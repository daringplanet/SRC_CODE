/*******************************************************************
cs3723p1.c by William Lippard, aju722
Purpose: 
    Contains functions that is used for a refrence count storage managment
*******************************************************************/
#include <stdio.h>
#include "cs3723p1.h"
#include <string.h>



/*************************updateFreeList******************************
    Void * updateFreelist(StorageManager *pMgr, short shNodeType)
Purpose:
    Return a freeNode of type shNodeType from the head of the free list in pMgr.
Parameters: 
    I StorageManager *pMgr Provides the freeList of free nodes
    
    I short shNodeType Provides the type of freeNode
Returns:
    - a freeNode of type shNodeType
*********************************************************************/
void * updateFreelist(StorageManager *pMgr, short shNodeType)
{
    FreeNode *pFreeN = pMgr->pFreeHeadM[shNodeType]; //save the current freeHead
    pMgr->pFreeHeadM[shNodeType] =  pMgr->pFreeHeadM[shNodeType]->pNextFree; //set the current freeHead to its pNext
    pFreeN->cAF = 'A'; //set the freeNode->cAF  we took off the free list to allocated
    return pFreeN; //return the freeNode
}



/****************************userAllocate****************************
void * userAllocate  (StorageManager *pMgr, short shUserDataSize, short shNodeType, char sbUserData[], SMResult *psmResult)
Purpose:
    Allocate a new AllocNode and return the user data portion
Parameters:
    I StorageManager *pMgr used to check the freeList, and calculate total size

    I short shUserDataSize the total size of the user data 

    I shNodeType the type of AllocNode/UserData

    I char sbUserData[] contains the user's data in binary

    I/O SMResult *psmResult stores the results if the function was successful or not
Returns:
    - a void pointer to the user data portion of the AllocNode


*********************************************************************/
void * userAllocate  (StorageManager *pMgr, short shUserDataSize, short shNodeType, char sbUserData[], SMResult *psmResult)
{
    AllocNode *pNewNode;  //a new node pointer
   
    //var of totla size
    int iTotalSize = shUserDataSize + pMgr->shPrefixSize;
   
    if(pMgr->pFreeHeadM[shNodeType] != NULL) //if there is a node of the same type in the free list
        pNewNode = (AllocNode *) updateFreelist(pMgr, shNodeType); //update the free list and set the newFree node pointer to a allocNode pointer
    else //if the is no node in the free list, make a new node
        pNewNode =  utilAlloc(pMgr, iTotalSize); //makes a new allocNode node
        
    pNewNode->shAllocSize = iTotalSize; //Setting the size of the whole node in allocNode
    pNewNode->cAF = 'A'; //Setting AllocNode to being used
    pNewNode->shRefCount = 1;  //has one pointer to it
    pNewNode->shNodeType = shNodeType; //setting the data type  
    memcpy(pNewNode->sbData, sbUserData, shUserDataSize); //copying the userData to the allocNode data
    psmResult->rc = 0; //sets a corret result
    
    void *pUserPointer = (void *) (&pNewNode->sbData); //gets the userData portion as a pointer
    return pUserPointer;    



}


/*****************************userRemoveRef*********************************
 userRemoveRef(StorageManager *pMgr, void *pUserData, SMResult *psmResult)
 Purpose:
    To remove a refrence from an AllocNode, and free the node if the refCount reaches 0
Prameters: 
    I StorageManager *pMgr Used to calculate size and access the metaData
    I void *pUserData the User portion data the needs to decrease its refrence count
    I/O SMResult *psmResult stores the results if the function was successful or not
Returns:
    
****************************************************************************/
void userRemoveRef(StorageManager *pMgr, void *pUserData, SMResult *psmResult)
{
    int iN; //int var
    MetaAttr *pMeta; //MetaAttr pointer
   
   AllocNode *pCurrentNode =  (AllocNode *) (((char *) pUserData) -  pMgr->shPrefixSize); //get the current node


    if(pCurrentNode->shRefCount > 0) //if the current node's refCount is greater than 0 
        pCurrentNode->shRefCount--; //decrease it by one
    else //else we have a error
    { 
        psmResult->rc = -1; //set error code
        strcpy(psmResult->szErrorMessage, "The pointer has nothing pointing at it already. No Decrement."); //error message
        return; //leave
    } //end else
   

    if(pCurrentNode->shRefCount == 0) //once we decreased the currentNode's refCount by one, check to see if it zero
    {
        //if it is zero, loop through all the attrubtes to check for ponters
        for(iN = pMgr->nodeTypeM[pCurrentNode->shNodeType].shBeginMetaAttr; pMgr->metaAttrM[iN].shNodeType == pCurrentNode->shNodeType; iN++)
        {
            
            pMeta = &(pMgr->metaAttrM[iN]); //assign the metaAttr to the local pointer MetaAttr varable

            if (pMeta->cDataType == 'P') //if that metaAttr is a pointer
            {
                void **pNode = (void **) &(pCurrentNode->sbData[pMeta->shOffset]);
                if(*pNode != NULL)
                    userRemoveRef(pMgr, *pNode, psmResult); //remove one of its references
            }
        } //end for loop
            
        //once we go through all the attrubutes free the current node. 
        memFree(pMgr, pCurrentNode, psmResult);
            
        
    }//end if(pCurrentNode == 0)

        
    psmResult->rc = 0; //set the result to true
    return; //leave function

}


/***********************************userAssoc***********************************************
void userAssoc(StorageManager *pMgr, void *pUserDataFrom, char szAttrName[], void *pUserDataTo, SMResult *psmResult)
Purpose: To set a User pointer to another user pointer and update the refrence count
Parameters: 
    I StorageManager *pMgr Used to calculate size and to access metaData
    I void *pUserDataFrom the data that points other data
    I void *pUserDataTo the data that is being pointed at
    I/O SMResult *psmResult stores the result if the function was successful or not
Returns:

********************************************************************************************/
void userAssoc(StorageManager *pMgr, void *pUserDataFrom, char szAttrName[], void *pUserDataTo, SMResult *psmResult)
{

    AllocNode *pCurrentNode =  (AllocNode *) ((char *) pUserDataFrom - (char *) pMgr->shPrefixSize); //get the current node

    MetaAttr *pMeta; //MetaAttr pointer
    int iN; //int varable 

    for(iN = pMgr->nodeTypeM[pCurrentNode->shNodeType].shBeginMetaAttr; pMgr->metaAttrM[iN].shNodeType == pCurrentNode->shNodeType; iN++)
    {
        //for every metaAttr of that type of node
        pMeta = (&pMgr->metaAttrM[iN]); //assign the metaAttr to a pointer 
        if(strcmp(pMeta->szAttrName, szAttrName) == 0)
        { //if the attr is the same as the attr that was passed
            if(pMeta->cDataType != 'P')
            { //double check that it is type pointer
                psmResult->rc = RC_ASSOC_ATTR_NOT_PTR; //if not, error
                strcpy(psmResult->szErrorMessage, "Inside userAddRef: NULL pointer error\n"); //error message
            } //end if for error checking
            
            void **pDataNext;
               
            pDataNext = (void **) &(pCurrentNode->sbData[pMeta->shOffset]);
               
            if(*pDataNext == NULL)
            {
                *pDataNext = pUserDataTo; //set the pointer to the new data
                userAddRef(pMgr, pUserDataTo, psmResult); //update the new refCount
                psmResult->rc = 0; //no error
                return; 
            }
            else
            { //if we do need to decrease the refCount
                userRemoveRef(pMgr, pUserDataFrom, psmResult); //call userRemoveRef to decrease the refcount
                *pDataNext = pUserDataTo; //once the refCount is decreased, set the pointer to the new data
                userAddRef(pMgr, pUserDataTo, psmResult); //update the new refCount
                psmResult->rc = 0; //no error
                return;
                
            } //end if for pDataNext == NULL
            
        } //end if for string compare
         
    }//end for loop


    psmResult->rc = -1;
    strcpy(psmResult->szErrorMessage, "userAssoc:___________________no attr match\n");
    return; 
    
}


/****************************userAddRef********************************
void userAddRef(StorageManager *pMgr, void *pUserDataTo, SMResult *psmResult)
Purpose: To add one to the refrence count
Parameters: 
    I StorageManager *pMgr Used to calculate the AllocNode
    I void *pUserDataTo the User data portion
    I/O SMResult *psmResult stores the result if the function was successful or not
Returns: 

***********************************************************************/
void userAddRef(StorageManager *pMgr, void *pUserDataTo, SMResult *psmResult)
{

    if(pUserDataTo == NULL) //if there is nothing we have error
    {
        psmResult->rc = -1;
        strcpy(psmResult->szErrorMessage, "Inside userAddRef: NULL pointer error\n"); //error message
        return;
    }

    AllocNode *pCurrentNode =  (AllocNode *) ((char *) pUserDataTo - (char *) pMgr->shPrefixSize); //get the current node
    pCurrentNode->shRefCount+=1; //increase the refrence count by 1
    psmResult->rc = 0; //set successful error message
    return; 
}
    

/****************************memFree*************************************
void memFree(StorageManager *pMgr, AllocNode *pAlloc, SMResult *psmResult)
Purpose: to free an AllocNode
Parameters:
    I StorageManager *pMgr to access the freeList
    I AllocNode *pAlloc the alloc node that needs to be put on the free list
    I/O  SMResult *psmResult stores the result if the function was successful or not
Returns: 
    
************************************************************************/
void memFree(StorageManager *pMgr, AllocNode *pAlloc, SMResult *psmResult)
{
    FreeNode *pNewFree = (FreeNode *)pAlloc; //Type case from alloc to a free node
    pNewFree->pNextFree = pMgr->pFreeHeadM[pAlloc->shNodeType]; // set the new freeNode's pNext to the current head of the freeList
    pMgr->pFreeHeadM[pAlloc->shNodeType] = pNewFree; //set the head of the freeList to the new freeNode
    pNewFree->cAF = 'F'; //set the new freeNode's cAF to F which means free
    pNewFree->shAllocSize = pAlloc->shAllocSize; //set the freeNode's allocSize  to the total size of the allocNode
    return;
}

        
