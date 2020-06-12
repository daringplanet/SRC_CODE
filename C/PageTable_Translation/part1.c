



#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <errno.h>



int main(int argc, char *argv[])
{
    if(argc != 3 && argc != 4)
    {
        perror("Invaild number of command arguments.");
        return -1;
    }
    
    //printf("Hello World. I am launched by command %s\n", argv[0]);
    //printf("input file = %s, output file = %s\n", argv[1], argv[2]);

    int dashV = 0;
    int count = 0;
    int input = -1;
    int output = -1;
    unsigned long buf = 0;
    unsigned long temp = 0;
    unsigned long outbuf = 0;
    unsigned long displacement = 0;
    unsigned long disAnder = 0x7F;
    int pageTable[12];
    int bytesread;
    int byteswritten = 0;
    int totalbytes = 0;

    if((input = open(argv[1], O_RDONLY)) == -1)
    {
        perror("Input file is NULL. Can not read file");
        return -1;
    }
    
    if((output = open(argv[2], O_WRONLY)) == -1)
    {
        perror("Output file is NULL. Can not write to file");
        return -1;
    }
    
    if(argc == 4)
    {
        if(strcmp(argv[3], "-v"))
        {
            perror("Not a valid flag");
            return -1;
        }
        dashV = 1;
    }

    pageTable[0] = 2;
    pageTable[1] = 4;
    pageTable[2] = 1;
    pageTable[3] = 7;
    pageTable[4] = 3;
    pageTable[5] = 5;
    pageTable[6] = 6;



    while(1)
    {
        //reades 8 bytes at a time and stores them into the buf. This also
        //counts the number of bytes read as well to know how many to write.
        while ((( bytesread = read(input, &buf, 8)) == -1) && (errno == EINTR));

        //if we are at the end off the file
        if (bytesread <= 0)
            break;
        

        //printf("current bytesread = %d, buf=%x\n", bytesread, buf);

        //copying over the buffer
        outbuf = buf;
        
        //printf("Current buf = %x\n", buf);
        //getting the displacement, correct
        displacement = buf & disAnder;
        //printf("displacement = %x\n", displacement);

        buf = buf >> 7;
        temp = buf;
        //printf("AFTER shift buf = %x\n", buf);
        if (buf < 0 || buf > 6)
        {
            perror("Out of range for page table after shifting.");
            return -1;
        }

        buf = pageTable[buf];

        buf = buf << 7;

        buf = buf + displacement;

        if(dashV)
            printf("%d. Logical: %x\tPage#: %d\tPhysical: %x\n", count, outbuf, (unsigned int )temp, buf);

        //printf("PHYSICAL address = %x\n", buf);

        outbuf = buf;
        count++;

        //while there are more bytes to write
        while(bytesread > 0)
        {

            //write some amout of bytes
            while(((byteswritten = write(output, &outbuf, bytesread)) == -1) && (errno == EINTR));

            //if we have completed writting
            if(byteswritten < 0)
                break;

            //keeps track of total number of bytes
            totalbytes += byteswritten;
            //keeps track of how many bytes left to write for the current buffer
            bytesread -= byteswritten;
            //advances the buffer location.
            outbuf += byteswritten;
        }
        
        //if there are no more bytes break TRUE loop.
        if (byteswritten == -1)
            break;

        buf = 0;
        outbuf = 0;
    }

    close(input);
    close(output);
    //printf("REaching\n");
    return 0;
}



