/***********************Prog.c**********************
Operating Systems Assignment 2
Written by William Lippard, aju722

Purpose:
    To simulate queue algrithems for handling
    process schudleing and calculate some meta
    data about the cpu scheduling.
*************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include "LinkedList.h"


/***************main***************************
Purpose:
    To validate commandline args and call the 
    correct alg from the commandline to simulate
    a cpu scheduling.
Params:
    argc - int that contains the number of commandline
            args
    argv - *char[] that contains the commandline arguments
Return:
    int the program's end behavior.
***********************************************/
int main(int argc, char *argv[])
{

    FILE *inFile = NULL; //to capture the input File
    char *alg = NULL; //to capture the algorthem type
    char *input = NULL; //to cpature the name of the input File
    int quant = 0; //to cature the quamtum if RR alg
    
    if(argc < 5 || argc > 7)//checks the number of arguments is in the 
    {                       //valid range
        printf("**Invalid number of arguments\n");
        printf("Usage:prog -alg FIFO -input input1.txt\n");    
        return -1;
    }

    
    //This first set of if is to validate
    //the first two arguments and assign
    //their values
    if(strcmp(argv[1], "-alg") == 0)
        alg = argv[2];
    else if (strcmp(argv[1], "-input") == 0)
        input = argv[2];
    else if (strcmp(argv[1], "-quantum") == 0)
        sscanf(argv[2], "%d", &quant);
    else
    {
        printf("Invalid arguments\n");
        printf("Usage:prog -alg FIFO -input input1.txt\n");
        return -1;
    }


    //This set of if statements is to validate the
    //second two arguements and assign their values.
    if(strcmp(argv[3], "-input") == 0)
        input = argv[4];
    else if(strcmp(argv[3], "-quantum") == 0)
        sscanf(argv[4], "%d", &quant);
    else if(strcmp(argv[3], "-alg") == 0)
        alg = argv[4];
    else
    {
        printf("Invalid arguments\n");
        printf("Usage:prog -alg FIFO -input input1.txt\n");
            return -1;
    }


    if(argc == 7) //if we have 7 arguemtn (the quant)
     {
        //validate the third set of arguemnts
        if(strcmp(argv[5], "-input") == 0)
            input = argv[6];
        else if (strcmp(argv[5], "-quantum") == 0)
            sscanf(argv[6], "%d", &quant);
        else if(strcmp(argv[5], "-alg") == 0)
            alg = argv[6];
        else
        {
            printf("Invalid arguments\n");
            printf("Usage:prog -alg FIFO -input input1.txt\n");
            return -1;
        }
    }

    //print out the name, input file, and alg type.
    printf("Student Name: William Lippard\n");
    printf("Input File Name : %s\n", input);
    printf("CPU Scheduling Alg : %s\n", alg);

    inFile = fopen(input, "r"); //open the file to read

    if(inFile == NULL) //if it did not open 
    {
        //error exit with message
        printf("File cannot be opened for reading: '%s'\n", input);
        return -1;
    }
   

    //check the alg type and call the
    //appropate alg type.
    if(strcmp(alg, "FIFO") == 0)
        FIFOalg(inFile);
    else if(strcmp(alg, "SJF") == 0)
        SJFalg(inFile);
    else if(strcmp(alg, "PR") == 0)
            PRalg(inFile);
    else if(strcmp(alg, "RR") == 0)
        if(quant <=  0) //checking for valid quantum
        {
            printf("ERROR: Not a valid quantum: '%d'\n", quant);
            exit(-1);
        }
        else
            RRalg(inFile, quant);
    else //if the alg is not found let the user know
    {
        printf("Invalid alg type: %s\n", alg);
        printf("Options: 'FIFO' 'SJF' 'RR' 'PR'\n");
        return -1;
    }

    fclose(inFile); //close the file

    return 0;
}

/********************buildPRQueue******************
Purpose:
    To build a queue based on  priority.
Params:
    input - *FILE to a file that is already in read mode
    list - LinkedList to insert values to
Return:
    void
**************************************************/
void buildPRQueue(FILE *input, LinkedList list)
{
    
     PCB_st pcb; //program control block
     int i; //i is used for two purposes
     char line[16]; //to get the line of file

    //while there are more lines in the file
    while(fgets(line, 16, input) != NULL) 
    {
        i = sscanf(line, "%d %d %d", &pcb.procID, &pcb.procPR, &pcb.CPUburst); 
        //save the amout of scanned result into i for error checking

        if(i != 3) //if not valid scan, let the user know
        {
            printf("Error with input format.\n");
            return;
        }

        for(i=0; i<8; i++) //coping the registers value as the procID.
            pcb.reg[i] = pcb.procID;
        
        PRappend(list, pcb); //do a Priorty append


    }


    return;
}


/************************buildSJFQueue************
Purpose:    
    to build a SJF queue
Params:
    input - *FILE that is open to read
    list - LinkedList to insert values
Returns:
    Void
Special Notes:
    The same as buildPRQueue but uses SJFappend
**************************************************/
void buildSJFQueue(FILE *input, LinkedList list)
{
    PCB_st pcb;
    int i;
    char line[16];

    while(fgets(line, 16, input) != NULL)
    {
        i = sscanf(line, "%d %d %d", &pcb.procID, &pcb.procPR, &pcb.CPUburst);

        if(i != 3)
        {
            printf("Error with input format.\n");
            exit(-1);
        }
        for(i=0; i<8; i++)
            pcb.reg[i] = pcb.procID;

        SJFappend(list, pcb); //do a SJFappend
    }


    return;
}






/************buildQueue*********************
Purpose:
    to build a FIFO queue
Params:
    input - *FILE that is open to read
    list - LinkedList to insert values
Return:
    void
Special Notes:
    The same as buildPRQueue but uses append
********************************************/
void buildQueue(FILE *input, LinkedList list)
{
    PCB_st pcb;
    int i;
    char line[16];
    
    while(fgets(line, 16, input) != NULL)
    {
        i = sscanf(line, "%d %d %d", &pcb.procID, &pcb.procPR, &pcb.CPUburst);

        if(i != 3)
        {
            printf("Error with input format.\n");
            exit(-1);
        }
        
        for(i=0; i<8; i++)
            pcb.reg[i] = pcb.procID;
        
        append(list, pcb); //regular append


    }
    return;
}

/*****************FIFOalg****************
Purpose:
    to simulate a FIFO cpu scheduling alg
Param:
    input - *FILE that is open to read
Return:
    void
*****************************************/
void FIFOalg(FILE *input)
{
    LinkedList list;
    
    list = newLinkedList(); //makes new LinkedList and assing it to list
    printf("\n"); //new line for formating

    buildQueue(input, list); //building the FIFO queue

    processQueue(list); //process the queue
    printf("\n"); //new line for formating

    //calculating meta data and printing it out.
    printf("Average Waiting time = %0.2f ms\n", (double) list->total_wating_time/list->total_job);
    printf("Average Turnaround time = %0.2f ms\n", (double) list->CLOCK/list->total_job);
    printf("Throughput = %0.2f jobs per ms\n",(double) list->total_job/list->CLOCK);

    //free the list
    freeLinkedList(list);
    return;
}

/*****************processQueueRR*****************
Purpose:
    to process a Queue using Round Robin alg with
    a quantum.
Params:
    list - LinkedList to access values and process thier
            cpu times
    quant - int for the amout of processing time 
Return:
    void
************************************************/
void processQueueRR(LinkedList list, int quant)
{
    int i;
    NodeDL *current;
    while(list->pHead != NULL) //while there are more nodes
    {
        current = list->pHead; //set the current node
        list->pHead = list->pHead->pNext; //advance the head

        for(i=0; i<8; i++) //copying the registers over
            list->CPUreg[i] = current->pcb.reg[i];

        
        //if the quantum is bigger than current cpu burst
        if(quant > current->pcb.CPUburst)
        {
            //just use the cpu burst size of cpu time
            list->CLOCK += current->pcb.CPUburst;
            current->pcb.CPUburst = 0;
        }
        else 
        {
            //use the quant for the cpu burst time
            list->CLOCK = list->CLOCK + quant;
            current->pcb.CPUburst -= quant;
        }
        
        
        //updating the current wating time.
        current->pcb.waitTime = current->pcb.waitTime + list->CLOCK + current->pcb.queueEnterClock;

        //updating the total turnaround time
        list->total_turnaround_time = list->total_turnaround_time + list->CLOCK;
        if(current->pcb.CPUburst == 0) //checks to see if the job is done
        {
            list->total_job += 1; //increase total job count
            printf("Process %d is completed at %d ms\n", current->pcb.procID, list->CLOCK);
            free(current); //free the job
            addQueWait(list); //update the other nodes wait times
        } 
        else
        {
            addQueWait(list); //update the other nodes wait time
            append(list, current->pcb); //append the job to the end of the list
        }

    }

    return;
}


/**********************processQueue******
Purpose:
    to process a FIFO queue
Params:
    list - LinkedList that contains nodes to be processed
*****************************************/
void processQueue(LinkedList list)
{
    int i;
    NodeDL *current;
    while(list->pHead != NULL) //while there are more nodes
    {
        current = list->pHead; //get the current node
        list->pHead = list->pHead->pNext; //advance head
        for(i=0; i<8; i++) //copying the registers over context swithch
            list->CPUreg[i] = current->pcb.reg[i];


        list->CLOCK = list->CLOCK + current->pcb.CPUburst; //update the clock
        //update the wait time
        current->pcb.waitTime = current->pcb.waitTime + list->CLOCK + current->pcb.queueEnterClock;

        //update turn around time
        list->total_turnaround_time = list->total_turnaround_time + list->CLOCK;
        //update total job
        list->total_job += 1;
        printf("Process %d is completed at %d ms\n", current->pcb.procID, list->CLOCK);
        //free the current node
        free(current);
        //update the other nodes wait time
        addQueWait(list);
    }

    return;
}

/*****************SJFalg***********
Purpose:
    to simulate a SJF cpu shceduling
Params:
    in - *FILE input file in read mode
Return:
    void
Special Note:
    Similar to FIFO alg
**********************************/
void SJFalg(FILE *in)
{
    LinkedList list = newLinkedList();
    printf("\n");
    buildSJFQueue(in, list); //build a sjf queue
    processQueue(list);

    printf("\n");
    printf("Average Waiting time = %0.2f ms\n", (double) list->total_wating_time/list->total_job);
    printf("Average Turnaround time = %0.2f ms\n", (double) list->CLOCK/list->total_job);
    printf("Throughput = %0.2f jobs per ms\n",(double) list->total_job/list->CLOCK);

    return;
}

/*********************PRalg*************
Purpose:
    to simulate a Piority cpu scheduling
Params:
    in - *FILE input file in read mode
Retrun:
    void
Special Notes:
    Similar to SJFalg 
****************************************/
void PRalg(FILE *in)
{
    LinkedList list = newLinkedList();
    printf("\n");

    buildPRQueue(in, list); //build a Pirorty queue
    processQueue(list);
    
    printf("\n");

    printf("Average Waiting time = %0.2f ms\n", (double) list->total_wating_time/list->total_job);
    printf("Average Turnaround time = %0.2f ms\n", (double) list->CLOCK/list->total_job);
    printf("Throughput = %0.2f jobs per ms\n",(double) list->total_job/list->CLOCK);


    return;
}


/*************RRalg*****************
Purpose:
    to simulate a round robin cpu 
    scheduling alg
Params:
    input - *FILE input file that is in
            read mode
    quant - int a quantum for cpu burst
Return:
    void
***********************************/
void RRalg(FILE *input, int quant)
{

    LinkedList list;

    list = newLinkedList();
    printf("\n"); //new line for formating

    buildQueue(input, list);
    processQueueRR(list, quant); //process the queue in RR alg
    printf("\n"); //new line for formating.

    printf("Average Waiting time = %0.2f ms\n", (double) list->total_wating_time/list->total_job);
    printf("Average Turnaround time = %0.2f ms\n", (double) list->CLOCK/list->total_job);
    printf("Throughput = %0.2f jobs per ms\n",(double) list->total_job/list->CLOCK);


    freeLinkedList(list);
    return;

}


