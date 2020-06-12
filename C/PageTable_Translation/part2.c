/***************************

File Name: part2.c
Class: Operating Systems
Assingment 3


********************************/



#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <errno.h>
#include "pagetable.c"
#include "phypage.c"

int main(int argc, char *argv[])
{
    if(argc != 3 && argc != 4)
    {
        perror("Invaild number of command arguments.");
        return -1;
    }
    
    
    int pageHit = 0;
    int pageFault = 0;
    int dashV = 0;
    int count = 0;
    int input = -1;
    int output = -1;
    unsigned long buf = 0;
    unsigned long phyAddress = 0;
    unsigned long pageIndex = 0;
    unsigned long frameIndex = 0;
    unsigned long outbuf = 0;
    unsigned long displacement = 0;
    unsigned long disAnder = 0x7F;
    int bytesread;
    int byteswritten = 0;
    int totalbytes = 0;
    int max = 32;
    Element value;
    PageMgr *pageMgr;
    PhysicalMgr *phyMgr;

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


    pageMgr = createPageMgr(32);
    phyMgr = createPhyMgr(8);

    while(1)
    {
        //update the clock.
        phyMgr->iClock++;
        


        //reades 8 bytes at a time and stores them into the buf. This also
        //counts the number of bytes read as well to know how many to write.
        while ((( bytesread = read(input, &buf, 8)) == -1) && (errno == EINTR));

        //if we are at the end off the file
        if (bytesread <= 0)
            break;
        


        //copying over the logical address for printing purposes
        outbuf = buf;
        //printf("------>buf = %x\n",  buf);
       // printf("------>disAnder = %x\n", disAnder);
        //getting the displacement
        displacement = buf & disAnder;
        //printf("------>displacement = %x\n", displacement);

        //getting the pNum(page number)
        pageIndex = buf >> 7;


        
        //range checking
        if (pageIndex < 0 || pageIndex > max)
        {
            perror("Out of range for page table after shifting.");
            return -1;
        }

        unsigned long newFrameIndex = pageMgr->pages[pageIndex].frameId;

        //if that page is not valid, we must validate it
        if(pageMgr->pages[pageIndex].vbit != 1)
        {
            pageFault+=1;
            
            //try and insert into free spot
            newFrameIndex = insertPhy(phyMgr, displacement); 
            //if there are no physical frames
            if(newFrameIndex == -1)
            {
                //get the LRU frame id
                newFrameIndex = LRUsearch(phyMgr);
                //deValidate old page index with newFrameIndex
                deValidatePageIndex(pageMgr, newFrameIndex);
                //calaculate physical address

                phyAddress = newFrameIndex;
                phyAddress = phyAddress << 7;
                phyAddress += displacement;
                //insert new frame.
                insertFrame(phyMgr, newFrameIndex, phyAddress); 
            }
            
            pageMgr->pages[pageIndex].frameId = newFrameIndex;
            //set the page valid bit to valid.
            pageMgr->pages[pageIndex].vbit = 1;
        } 
        else
        {
            pageHit+=1;
            phyMgr->phyFrames[newFrameIndex].iFrameTime = phyMgr->iClock;
        }

        

        if(dashV)
            printf("%d. Page Index: %x\tLogic: %x\tFrame Index: %x\tPhysic: %x\tDisplacement: %x\n", 
                (phyMgr->iClock -1), pageIndex, outbuf, pageMgr->pages[pageIndex].frameId, 
                phyMgr->phyFrames[newFrameIndex].phyAddress, displacement);


        //copying physical address into outbuf
        outbuf = phyMgr->phyFrames[newFrameIndex].phyAddress;
//        printf("MAIN: outbuf = %x\n", outbuf);
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
        phyAddress = 0;
        displacement = 0;
        if(dashV)
        {
            printPageMgr(pageMgr);
            printPhyMgr(phyMgr);
        }
    }

    printf("Page Hit: %d\nPage Fault: %d\n", pageHit, pageFault);

    freePhyMgr(phyMgr);
    freePageMgr(pageMgr);
    close(input);
    close(output);
    return 0;
}



