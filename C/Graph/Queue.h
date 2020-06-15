/************************************************************************
Queue.h

Purpose:
    Struct definitions for a Queue.
    Define function prototypes used by Queues.

	This implementation of a Queue will store each value as a Linked List, with the oldest element in the list at the head of the list and the newest element in the list at the foot of the list.
************************************************************************/

#include <stdio.h>
#include <string.h>
#include <stdarg.h>
#include <stdlib.h>


//#define constant values
#define TRUE 1
#define FALSE 0

//typedef for the Vertex which is simply an integer to store the ID of a vertex in a graph in this project.
typedef int Vertex;


//Typedef for a node in the queue.
typedef struct NodeQ
{
    Vertex v;
    struct NodeQ *pNext;

} NodeQ;

//Typedef for a Queue implementation.
//pHead points to the node waiting in the Queue the longest (first to be removed).
//pFoot points to the node waiting in the Queue the shortest (most recently added node).
typedef struct
{
    NodeQ *pHead;
	NodeQ *pFoot;
} QueueImp;

typedef QueueImp *Queue;


/*****Prototypes*******/

//Malloc a new QueueImp and return it's address.
Queue newQueue();


//Free the Queue and any nodes that still exist.
void freeQueue(Queue q);


//Allocate a new node and store "v" as the vertex in the node.  Return the address of the node.
NodeQ *allocateNodeQ(Vertex v);


//Create a node to store the given Element and add it to the end of the Queue.
void insertQ(Queue q, Vertex v);

//Remove the vertex at pHead in q and return it via *v (passed by reference).
//Functionally return FALSE if the queue is empty and return TRUE otherwise.
int removeQ(Queue q, Vertex *v);


/*******************************************
This function creates a new que and sets the head and foot pointer to null then returns
**********************************************/
Queue newQueue()
{
  Queue q = (Queue) malloc(sizeof(QueueImp));
  q->pHead=NULL;
  q->pFoot=NULL;
  return q;
}

/****************************************
This function frees a the nodes and the que itself
****************************************/
void freeQueue(Queue q)
{
  NodeQ *temp;
  while(q->pHead !=NULL)
  {
    temp = q->pHead;
    q->pHead = q->pHead->pNext;
    free(temp);
  }

  free(q);
  return;
}

/**********************************************
This function creates a new node and sets its vertex
*********************************************/
NodeQ *allocateNodeQ(Vertex v)
{
  NodeQ *newNode = (NodeQ *)malloc(sizeof(NodeQ));
  newNode->v = v;
  newNode->pNext = NULL; //sets the new node to null
  return newNode; //then returns it
}

/******************************************
This function inserts a vertex into a que
******************************************/
void insertQ(Queue q, Vertex v)
{
  NodeQ *newNode = allocateNodeQ(v); //create a new node
  if(q->pHead == NULL)
  { //sees if anything is in que if not this is the first and last thing in que
	   q->pHead=newNode;
	   q->pFoot = newNode;
	   return;
  }

  q->pFoot->pNext = newNode; //set the foot node from null to the new node
  q->pFoot = newNode; //set the foot pointer to the new node
  return;
}

/***************************************
This function removes a node from a que and returns the vertex by refrence
This function returns a true and false value
**************************************/
int removeQ(Queue q, Vertex *v)
{
  if(q->pHead == NULL)
	 return 0;

  NodeQ *temp;

  temp = q->pHead;
  q->pHead = q->pHead->pNext;
  *v = temp->v;
  free(temp);

  return 1;
}


/***************************************
This function prints the que
************************************/
void printQue(Queue q)
{
  NodeQ *temp = q->pHead;
  if(temp==NULL)
  {
  	printf("Queue is Empty\n");
    return;
  }

  printf("Que: ");
  while(temp != NULL)
  {
  	printf("%d ", temp->v);
  	temp=temp->pNext;
  }
  printf("\n");

  return;
}
