#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <errno.h>

#include "mastermind.h"

int main(){
	int error = -1;
	
	int fd = open("/dev/mastermind", O_RDWR);
	error = ioctl(fd, MMIND_ENDGAME);

	if(error == -1)
		perror("There is an error.\n");
	else
		printf("Successfully executed.\n");
	return 0;
}
