





#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>




/*********************************
typedef for the Element struct i
which constains a c string to 
store pages

**********************************/



typedef struct
{
    unsigned long frameId;
    int vbit;

} Element;


typedef struct
{
    Element* pages;
    int iMax;
    
}PageMgr;


void printPageMgr(PageMgr *pageMgr)
{
    unsigned long i;
    for(i=0; i<pageMgr->iMax; i++)
    {
        //printf("i = %x\n", i);
       // printf("************* i = %x vbit = %d\n", i, pageMgr->pages[i].vbit);
        if(pageMgr->pages[i].vbit == 1)
            printf("\t\t...pageIndex = [%x],\tframeIndex = [%x],\tvbit = [%d]\n", i, 
                pageMgr->pages[i].frameId, pageMgr->pages[i].vbit);
    }
    return;
}




PageMgr* createPageMgr(int max)
{
    //printf("Initlizing the page Mgr.");
    //printf("CREATpageMgr:\n");
    PageMgr *newPageMgr = malloc(1 * sizeof(PageMgr));
    newPageMgr->pages = (Element *) malloc(max * sizeof(Element));
    
    int i;
    //set all the valid bits to 0
    for(i=0; i<newPageMgr->iMax; i++)
        newPageMgr->pages[i].vbit = 0;

    newPageMgr->iMax = max;    
    
    return newPageMgr;

}

void freePageMgr(PageMgr *pageMgr)
{
    //printf("FREEpageMgr:\n");
    free(pageMgr->pages);
    free(pageMgr);
    return;
}





void deValidatePageIndex(PageMgr *pageMgr, unsigned long frameIndex)
{
    //printf("START: deValidatePageIndex:\n");
    unsigned long i;
    for(i=0; i<pageMgr->iMax; i++)
    {
        if(pageMgr->pages[i].vbit == 1)
            if(pageMgr->pages[i].frameId == frameIndex)
            {
                //printf("\t\t...frameIndex = %x page[i].frameId = %x\n", frameIndex, pageMgr->pages[i].frameId);
                //printf("\t\t...pageIndex = %x\n", i);
                pageMgr->pages[i].vbit = 0;
                //printf("pageIndex[%x] vbit = %d\n", i, pageMgr->pages[i].vbit);
            
               // printf("END: deValidatePageIndex\n");
                return;
            }
    }

    //printf("\t\t...NO matching frame fround\n");
    //printf("END: deValidatePageIndex\n");
    return;
}

