#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <errno.h>

#include "mastermind.h"

int main(){
	int remaining = -1;
	
	int fd = open("/dev/mastermind", O_RDWR);
	remaining = ioctl(fd, MMIND_REMAINING);

	if(remaining == -1)
		perror("There is an error.\n");
	else
		printf("%d\n",remaining);
	return 0;
}
