#include <linux/module.h>
#include <linux/moduleparam.h>
#include <linux/init.h>

#include <linux/kernel.h>	
#include <linux/slab.h>		
#include <linux/fs.h>		
#include <linux/errno.h>	
#include <linux/types.h>	
#include <linux/proc_fs.h>
#include <linux/fcntl.h>	
#include <linux/seq_file.h>
#include <linux/cdev.h>

#include <asm/switch_to.h>	
#include <asm/uaccess.h>	

#include "mastermind.h"

#ifndef __ASM_ASM_UACCESS_H
    #include <linux/uaccess.h>
#endif


#define DEVICE_MAJOR 0
#define NUMBER_OF_DEVS 4

int device_major = DEVICE_MAJOR;
int device_minor = 0;
int number_of_devs = NUMBER_OF_DEVS;
char *mmind_number = "0000";
int mmind_max_guesses = 10;

module_param(device_major, int, S_IRUGO);
module_param(device_minor, int, S_IRUGO);
module_param(number_of_devs, int, S_IRUGO);
module_param(mmind_number, charp,0);
module_param(mmind_max_guesses, int,0);

MODULE_AUTHOR("Alessandro Rubini, Jonathan Corbet");
MODULE_LICENSE("Dual BSD/GPL");


struct mmind_dev {
    char **data;
    int guess_number;
    unsigned long size;
    struct semaphore sem;
    struct cdev cdev;
};

struct mmind_dev *mmind_devices;

bool is_valid_guess(char * guess){
	int counter=0;
	while(guess[counter]!='\0'){
		counter++;
	}
	printk(KERN_ALERT "count is %d\n\n", counter);
	if(counter == 4){
		return true;
	}
	return false;
}

int mastermind_trim(struct mmind_dev *dev)
{
    int i;
    if (dev->data) {
        for (i = 0; i < dev->guess_number; i++) {
            if (dev->data[i])
                kfree(dev->data[i]);
        }
        kfree(dev->data);
    }
    dev->data = NULL;
    dev->guess_number=0;
    dev->size = mmind_max_guesses;
    return 0;
}


int mastermind_open(struct inode *inode, struct file *filp)
{
    struct mmind_dev *dev;

    dev = container_of(inode->i_cdev, struct mmind_dev, cdev);
    filp->private_data = dev;

    /* trim the device if open was write-only */
    if ((filp->f_flags & O_APPEND) == 0 && (filp->f_flags & O_ACCMODE) == O_WRONLY) {
        if (down_interruptible(&dev->sem))
            return -ERESTARTSYS;
        mastermind_trim(dev);
        up(&dev->sem);
    }
    return 0;
}

int mastermind_release(struct inode *inode, struct file *filp)
{
    return 0;
}

ssize_t mastermind_read(struct file *filp, char __user *buf, size_t count,
                   loff_t *f_pos)
{
    struct mmind_dev *dev = filp->private_data;
    char *line_data;
    int guess_pos = (long)*f_pos;
    ssize_t retval = 0;
    int line_index = 0;
    
    char *local_buffer;

    if (down_interruptible(&dev->sem))
        return -ERESTARTSYS;

    if (*f_pos >= dev->guess_number)
        goto out;

    if (dev->data == NULL || !dev->data[guess_pos])
        goto out;
//guess_pos ======= f_pos

    local_buffer = kmalloc((count + 30) * sizeof(char), GFP_KERNEL);
    memset(local_buffer, 0, (count + 30) * sizeof(char));

	line_data = dev->data[guess_pos];

	line_index = 0;
	while (line_data[line_index]){
		local_buffer[line_index] = line_data[line_index];
		line_index++;
	}
   
    if (copy_to_user(buf, local_buffer, line_index)) {
        retval = -EFAULT;
        goto out;
    }
    *f_pos += 1;
    retval = line_index;

  out:
    up(&dev->sem);
    return retval;
}


ssize_t mastermind_write(struct file *filp, const char __user *buf, size_t count,
                    loff_t *f_pos)
{
	int i,k,guess_number;
	int index1 = 0;
	int in_place=0,out_place=0;
    struct mmind_dev *dev = filp->private_data;
    ssize_t retval = -ENOMEM;
    int guess_pos = dev->guess_number;
    char *local_buffer;
    char * guess;
    char temp_guess[4];
    char mastermind[4];

    if (down_interruptible(&dev->sem))
        return -ERESTARTSYS;

    if (guess_pos >= dev->size) {
        goto out;
    }

    if (!dev->data) {
        dev->data = kmalloc(dev->size * sizeof(char *), GFP_KERNEL);
        if (!dev->data)
            goto out;
        memset(dev->data, 0, dev->size * sizeof(char *));
    }
    if (!dev->data[guess_pos]) {
        dev->data[guess_pos] = kmalloc(sizeof(char), GFP_KERNEL);
        if (!dev->data[guess_pos])
            goto out;
    }

    local_buffer = kmalloc(5 * sizeof(char), GFP_KERNEL);

    if (copy_from_user(local_buffer, buf, 5)) {
        retval = -EFAULT;
        goto out;
    }
    //char is_valid[4];
    /*for(index1=0;index1<4;index1++){
		printk(KERN_ALERT"%c ",local_buffer[index1]);
	}*/
    /*for(index1=0;index1<4;index1++){
    	is_valid[index1] = local_buffer[index1];
	}*/
    /*if(!is_valid_guess(local_buffer)){
        retval = -EFAULT;
        goto out;
    }*/
    guess = kmalloc(17 * sizeof(char), GFP_KERNEL);

    for(index1=0;index1<4;index1++){
    	guess[index1] = local_buffer[index1];
    	temp_guess[index1] = local_buffer[index1];
	}
	for(i=0;i<4;i++){
		mastermind[i]=mmind_number[i];
	}
	for(i = 0 ; i < 4 ; i++)
    {
        if(mastermind[i] == temp_guess[i])
        {
            mastermind[i] = '?' ;
            temp_guess[i] = '?' ;
            in_place++ ;
        }
    }
    for(k = 0 ; k < 4  ; k++)
    {
        int j ;
        for(j = 0 ; j < 4 ; j++)
        {
			if(mastermind[k]!='?' && temp_guess[j]!='?'){
				if(mastermind[k] == temp_guess[j])
				{
					mastermind[k] = '?' ;
					temp_guess[j] = '?' ;
					out_place++ ;
				}
			}
        }
    }

	guess_number = dev->guess_number+1;
	if(dev->guess_number >= mmind_max_guesses){
		 retval = -EDQUOT;
		 goto out;
	 }
    guess[4] = ' ';
    guess[5] = in_place+'0';
    guess[6] = '+';
    guess[7] = ' ';
    guess[8] = out_place+'0';
    guess[9] = '-';
    guess[10] = ' ';
    guess[11] = (guess_number/1000)+'0';
    guess[12] = ((guess_number%1000)/100)+'0';
    guess[13] = ((guess_number%100)/10)+'0';
    guess[14] = (guess_number%10)+'0';
    guess[15] = '\n';
    guess[16] = '\0';

    dev->data[guess_pos] = guess;
    dev->guess_number++;
    
    *f_pos+=1;

    retval = count;
    kfree(local_buffer);
  out:
    up(&dev->sem);
    return retval;
}

long mastermind_ioctl(struct file *filp, unsigned int cmd, unsigned long arg)
{
	struct mmind_dev *dev= filp->private_data;
	int err = 0;
	int retval = 0;

	/*
	 * extract the type and number bitfields, and don't decode
	 * wrong cmds: return ENOTTY (inappropriate ioctl) before access_ok()
	 */
	if (_IOC_TYPE(cmd) != MMIND_IOC_MAGIC) return -ENOTTY;
	if (_IOC_NR(cmd) > MMIND_IOC_MAXNR) return -ENOTTY;

	/*
	 * the direction is a bitmask, and VERIFY_WRITE catches R/W
	 * transfers. `Type' is user-oriented, while
	 * access_ok is kernel-oriented, so the concept of "read" and
	 * "write" is reversed
	 */
	 
	if (_IOC_DIR(cmd) & _IOC_READ)
		err = !access_ok(VERIFY_WRITE, (void __user *)arg, _IOC_SIZE(cmd));
	else if (_IOC_DIR(cmd) & _IOC_WRITE)
		err =  !access_ok(VERIFY_READ, (void __user *)arg, _IOC_SIZE(cmd));
	if (err) return -EFAULT;
	
	switch(cmd) {
	  case MMIND_REMAINING:
		if (! capable (CAP_SYS_ADMIN))
			return -EPERM;
		retval = mmind_max_guesses-dev->guess_number;
		break;
		
	  case MMIND_ENDGAME: 
		if (! capable (CAP_SYS_ADMIN))
			return -EPERM;
		mastermind_trim(dev);
		break;

	  case MMIND_NEWGAME:
		if (! capable (CAP_SYS_ADMIN))
			return -EPERM;
		mastermind_trim(dev);
		mmind_number[0] = (arg/1000)+'0';
		mmind_number[1] = ((arg%1000)/100)+'0';
		mmind_number[2] = ((arg%100)/10)+'0';
		mmind_number[3] = (arg%10)+'0'; 
		retval = arg;
		break;
		
	  default: 
		return -ENOTTY;
	}
	
	return retval;
}



struct file_operations mastermind_fops = {
    .owner =    THIS_MODULE,
    .read =     mastermind_read,
    .write =    mastermind_write,
    .unlocked_ioctl =  mastermind_ioctl,
    .open =     mastermind_open,
    .release =  mastermind_release,
};


void mastermind_cleanup_module(void)
{
    int i;
    dev_t devno = MKDEV(device_major, device_minor);

    if (mmind_devices) {
        for (i = 0; i < number_of_devs; i++) {
            mastermind_trim(mmind_devices + i);
            cdev_del(&mmind_devices[i].cdev);
        }
    kfree(mmind_devices);
    }

    unregister_chrdev_region(devno, number_of_devs);
}


int mastermind_init_module(void)
{
    int result, i;
    int err;
    dev_t devno = 0;
    struct mmind_dev *dev;

    if (device_major) {
        devno = MKDEV(device_major, device_minor);
        result = register_chrdev_region(devno, number_of_devs, "mastermind");
    } else {
        result = alloc_chrdev_region(&devno, device_minor, number_of_devs,
                                     "mastermind");
        device_major = MAJOR(devno);
    }
    if (result < 0) {
        printk(KERN_WARNING "mastermind: can't get major %d\n", device_major);
        return result;
    }

    mmind_devices = kmalloc(number_of_devs * sizeof(struct mmind_dev),
                            GFP_KERNEL);
    if (!mmind_devices) {
        result = -ENOMEM;
        goto fail;
    }
    memset(mmind_devices, 0, number_of_devs * sizeof(struct mmind_dev));

    /* Initialize each device. */
    for (i = 0; i < number_of_devs; i++) {
        dev = &mmind_devices[i];
        dev->guess_number=0;
        dev->size=mmind_max_guesses;
        sema_init(&dev->sem,1);
        devno = MKDEV(device_major, device_minor + i);
        cdev_init(&dev->cdev, &mastermind_fops);
        dev->cdev.owner = THIS_MODULE;
        dev->cdev.ops = &mastermind_fops;
        err = cdev_add(&dev->cdev, devno, 1);
        if (err)
            printk(KERN_NOTICE "Error %d adding mastermind%d", err, i);
    }

    return 0; 

  fail:
    mastermind_cleanup_module();
    return result;
}

module_init(mastermind_init_module);
module_exit(mastermind_cleanup_module);
