

#include <stdio.h>
#include <errno.h>



typedef struct
{
    unsigned int phyAddress;
    int iFrameTime;
} PhysicalFrame;

typedef struct 
{
    int iFrameSize;
    int *freeFrames;
    PhysicalFrame *phyFrames;
    int maxFrames;
    int iClock;
    int tfFull;

} PhysicalMgr;


void printPhyMgr(PhysicalMgr *phyMgr)
{
    unsigned long i;
    for(i=0; i<phyMgr->maxFrames; i++)
    {
        if(phyMgr->freeFrames[i] == 1)
            printf(">>>>>>>>>>frameIndex = [%x], phyAddr = [%x], frameTime = [%d]\n", 
            i, phyMgr->phyFrames[i].phyAddress, phyMgr->phyFrames[i].iFrameTime);
    }
}

PhysicalMgr* createPhyMgr(int max)
{
        //printf("CREATEphyMgr:\n");
        //create space fo the physical page manager
        PhysicalMgr* phyMgr = (PhysicalMgr*) malloc(1 * sizeof(PhysicalMgr));

        //create space for the freeFrames validator
        phyMgr->freeFrames = malloc(max * sizeof(int));
        //create space for each frame
        phyMgr->phyFrames = malloc(max * sizeof(PhysicalFrame));
        
        //set some preset values
        phyMgr->maxFrames = max;
        //phyMgr->iFrameSize = frameSize;
        phyMgr->iClock = 0;
        phyMgr->tfFull = 0;

        //intilizes all the frames and freeFrames
        int i;
        for(i=1; i<max; i++)
        {
            phyMgr->freeFrames[i] = 0;
            phyMgr->phyFrames[i].phyAddress = 0x0;
            phyMgr->phyFrames[i].iFrameTime = 0;
        }
        
        //initilizes the os frame spot
        phyMgr->iClock++;
        phyMgr->freeFrames[0] = 1;
        phyMgr->phyFrames[0].phyAddress = 0x0;
        
        //return the new structure.
        return phyMgr;
}





void freePhyMgr(PhysicalMgr *phyMgr)
{
    //free the free frames
    free(phyMgr->freeFrames);
    //free the frames
    free(phyMgr->phyFrames);
    //free the phyMgr
    free(phyMgr);

    return;
}



/*************************
Purpose:
    Goes and finds a free spot to put
    the physical address and returns 
    the spot in the physical address array.
    If the array is full, it will
    eject the LRU frame, and replace 
    it with the new one and return
    that spot in the array.
Params:
    phyMgr - PhysicalMgr* which physical memeory 
            manager to inser the frame
    index - unsigned long page
**************************/
int insertPhy(PhysicalMgr *phyMgr, unsigned long dis)
{


    unsigned long i;
    
    unsigned long phyAddress = 0;
    
    //if all the frames are not full
    if(phyMgr->tfFull == 0)
    {
        //Loops through all the frames
        for(i=1; i<phyMgr->maxFrames; i+=1)
        {
            //if that frame is free
            if(phyMgr->freeFrames[i] == 0)
            {
                //calculate physical address
                phyAddress = i << 7;
                phyAddress += dis;
                
               // printf("\t\t...about to insert, i=%x\n", i);
                //insert it
                insertFrame(phyMgr, i, phyAddress);
                //printf("\t\t...Frame has been inserted\n");
                //set that spot to be used
                phyMgr->freeFrames[i] = 1;
                //return the index location
                return i;
            } //end frames if
        } //end for loop
        
        //printf("\t\t...There are no more free spots\n");
        //if we reach this part, the list is now full
        phyMgr->tfFull = 1;
    } //end full if

    /*
    int newIndex;

    newIndex = LRUinsert(phyMgr, phyAddress);
    */
    
    //returns -1 if not free frames were found.
    return -1;


}




void insertFrame(PhysicalMgr *phyMgr, unsigned long index, unsigned long phyAddress)
{

    //set that frame to not be free anymore.
    phyMgr->freeFrames[index] = 1;
    //assign the address
    phyMgr->phyFrames[index].phyAddress = phyAddress;
    //assign the current clock
    phyMgr->phyFrames[index].iFrameTime = phyMgr->iClock;
    return;

}


unsigned long LRUsearch(PhysicalMgr *phyMgr)
{
   // printf("START LRUsearch\n");
    unsigned long i;
    //set the index at the start of the frames
    unsigned long newIndex = 1;
    //sets the min at the start of the frames time
    int min = phyMgr->phyFrames[1].iFrameTime;
   // printf("start index = %x, minTime = %d\n", newIndex,  min);
    //for the remaining of the frames
    for(i=2; i<phyMgr->maxFrames; i++)
    {
        //if there is a new min
        if(min > phyMgr->phyFrames[i].iFrameTime)
        {
            //update min
            min = phyMgr->phyFrames[i].iFrameTime;
            //update location
            newIndex = i;
     //       printf("changed to index = %x, minTime = %d\n", newIndex,  min);
        }
    }

    //insert into new location
    //insertFrame(phyMgr, newIndex, phyAddress);
   // printf("final index = %x, minTime = %d\n", newIndex,  min);
   // printf("END LRUsearch\n");
    //return location.
    return newIndex;
    
}


































