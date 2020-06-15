/************************************************************
LinkedList.h

Author: William Trace Lippard

Purpose:
    Define constants used in the project
    Struct definitions for a general Linked List.
    Define function prototypes used by general Linked Lists.
************************************************************************/
#include <stdio.h>
#include <string.h>
#include <stdarg.h>
#include <stdlib.h>
#include <time.h>


//#define constant values
#define MAX_URL_LENGTH 50

#define TRUE 1
#define FALSE 0


//typedef for the Element struct which constains a c string to store a URL in the BrowserList
typedef struct
{
    int procID;
    int procPR;
    int CPUburst;
    int reg[8];
    int queueEnterClock;
    int waitTime;
} PCB_st;


//Typedef for a node in the doubly linked list (has next and previous pointers).
typedef struct NodeDL
{
    PCB_st pcb;
    struct NodeDL *pNext;
    //struct NodeDL *pPrev;
} NodeDL;

//Typedef for a doubly linked list implementation.
//Contains a pointer to the first node in the list and the last node in the list (pHead and pFoot respectively).
typedef struct
{
    NodeDL *pHead;
    NodeDL *pFoot;
    int CLOCK;
    int CPUreg[8];
    int total_wating_time;
    int total_turnaround_time;
    int total_job;
} LinkedListImp;

typedef LinkedListImp *LinkedList;


/*****Prototypes*******/

//Malloc a new LinkedListImp and return it's address.
LinkedList newLinkedList();


//Free the LinkedList and any nodes that still exist in the list
void freeLinkedList(LinkedList list);


//Allocate a new node and store "value" as the Element in the node.  Return the address of the node.
NodeDL *allocateNode(PCB_st value);


//Create a node to store the given Element and add it to the end of the LinkedList.
void append(LinkedList list, PCB_st value);





/************************************************
LinkedList
Purpose: Creates a new Double linked list

*************************************************/
LinkedList newLinkedList()
{
    LinkedList newList = (LinkedList)malloc(sizeof(LinkedListImp)); //make room for the list
    newList->pHead=NULL; //set the pointers to null
    newList->pFoot=NULL;
    newList->CLOCK = 0;
    newList->total_wating_time = 0;
    newList->total_turnaround_time = 0;
    newList->total_job = 0;
    return newList; //return the list
}




/*************************************************
freeLinkedList
Purpose: frees double linked list
***************************************************/
void freeLinkedList(LinkedList list)
{
    NodeDL *p = list->pHead; //creates a temp node to move through the list
    while(p!=NULL)
    {
        NodeDL *temp; //creates another temp node to free nodes
	temp=p; //set the temp node to p
	p=p->pNext; //update p to the next item in the list
	free(temp); //free temp
    }
    free(list); //free the list
    return;
}

/********************************************
*allocateNode

Purpose: allocate new node with value

*********************************************/
NodeDL *allocateNode(PCB_st value)
{
    NodeDL *newNode = (NodeDL *)malloc(sizeof(NodeDL));//creates the space
                                                //for the node and call the
                                                //node pointer newNode

    newNode->pcb.procID = value.procID;
    newNode->pcb.procPR = value.procPR;
    newNode->pcb.CPUburst = value.CPUburst;
    int i;
    for(i=0; i<8; i++)
        newNode->pcb.reg[i] = value.reg[i];

    newNode->pcb.queueEnterClock = 0;
    newNode->pcb.waitTime = 0;

    newNode->pNext=NULL; //sets the nodes pNext to NULL
    //newNode->pPrev = NULL; //sets the nodes pPrev to NULL
    return newNode; //returns the newNode

}

/******************************************
append
Purpose: insert a element into the linked list

******************************************/
void append(LinkedList list, PCB_st value)
{
    NodeDL *newNode = allocateNode(value); //creates a new node and copies
                                           //the value int the nodes element

    if(list->pHead == NULL)
    { //checks to see if there is anything in the list if not
        list->pHead=newNode; //set all the pointers to the new node
        list->pFoot=newNode;
        return;
    }
    list->pFoot->pNext = newNode; //otherwise make the end of the
                                  //list point to the new node
    list->pFoot = newNode; //then update the end of the list to the
                            //newNode since it is at the end now

    return;
}







/***********addQueWait****************
Purpose:
    to add wait time to the current queue
Params:
    list - LinkedList with nodes to add wait time
returns:
    void
*************************************/
void addQueWait(LinkedList list)
{
    NodeDL *currentNode = NULL; //treverser
    currentNode = list->pHead; //set it to head
    while(currentNode != NULL) //while there are more nodes
    {
        //add the wait time to each node
        currentNode->pcb.queueEnterClock += list->CLOCK - currentNode->pcb.queueEnterClock;
        //advance current
        currentNode = currentNode->pNext;
    }
    //log the new total wait time
    list->total_wating_time += list->CLOCK;

    return;
}











/**************SJFappend***************
Purpose:
    to append a new node in SJF priority
Params:
    list - LinkedList to append new node
    value - PCB_st that contains values for
            the new node
Returns:
    void
***************************************/
void SJFappend(LinkedList list, PCB_st value)
{
    NodeDL *newNode = allocateNode(value); //creates a new node and copies
    NodeDL *current = NULL; //the value int the nodes element
    NodeDL *prev = NULL;

    if(list->pHead == NULL)
    { //checks to see if there is anything in the list if not
        list->pHead=newNode; //set all the pointers to the new node
        list->pFoot=newNode;
        return;
    }

    //set current to head
    current = list->pHead;

    //while the current node is not null and the new cpu burst is
    //biggger than the current cpu burst, keep advancing through the list
    while(current != NULL && value.CPUburst > current->pcb.CPUburst)
    {
        prev = current; //keeps track of previous node
        current = current->pNext;
    }

    //if we are at the end of the list
    if(current == NULL)
    {
        //just append to foot
        list->pFoot->pNext = newNode;
        list->pFoot = newNode;
        return;
    }

    //if we are at the start of the list
    if(current == list->pHead)
    {
        //append to head
        newNode->pNext = current;
        list->pHead = newNode;
        return;
    }


    //otherwise we are in the middle of the list
    //to append
    newNode->pNext = current;

    //if there was a previous node before current
    if(prev != NULL)
        prev->pNext = newNode; //make it point to new node


    return;
}


/************printListID**************
Purpose:
    to print a linkedList by its proccess Id
Params:
    list - LinkedList to go and print
Return:
    void
**************************************/
void printListId(LinkedList list)
{
    NodeDL *current = NULL;
    current = list->pHead;
    while(current != NULL)
    {
        printf("id[%d] cpuBurst=%d\n", current->pcb.procID, current->pcb.CPUburst);
        current = current->pNext;
    }

}



/****************PRappend***********************
Purpose:
    to appendd a new node in current priority
Params:
    list - LinkedList to append new node
    value - PCB_st that contains values
            for a new node
Returns:
    void
Special Notes:
    This function is similar to SJFappend execpt this
    function append based off of priority than cpu burst
***********************************************/
void PRappend(LinkedList list, PCB_st value)
{

    NodeDL *newNode = allocateNode(value); //creates a new node and copies
    NodeDL *current = NULL;                                        //the value int the nodes element
    NodeDL *prev = NULL;


    if(list->pHead == NULL)
    { //checks to see if there is anything in the list if not
        list->pHead=newNode; //set all the pointers to the new node
        list->pFoot=newNode;
        return;
    }

    current = list->pHead;
    while(current != NULL && value.procPR <= current->pcb.procPR)
    {
        prev = current;
        current = current->pNext;

    }

    if(current == NULL)
    {
        list->pFoot->pNext = newNode;
        list->pFoot = newNode;
        return;
    }

    if(current == list->pHead)
    {
        newNode->pNext = current;
        list->pHead = newNode;
        return;
    }

    newNode->pNext = current;
    newNode->pNext = current;

    if(prev != NULL)
        prev->pNext = newNode;


    return;
}
