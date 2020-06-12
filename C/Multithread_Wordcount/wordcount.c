/* //
    Assignment 1, Written by William Lippard, aju722
*/

#include <stdio.h>
#include <errno.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>





/**********main****************
Purpose:
    This is the main driving function for 
    wordcount.c. This will create a
    process for each file that prints
    out the word count for that file, 
    and once all the processes end,
    the driver will then let the user know 
    that all files were counted.
Parameters:
    argc - int that stores the number of command arguemtns
    
    argv - *char[] that store an array of string
           that represent command arguments.

******************************/
int main(int argc, char *argv[])
{
    if(argc < 2) //if there are no file provided
    {
        printf("ERROR: No files provided in arguments\n");
        return -1; 
    }
    
    pid_t cpid[argc-1]; //create an array of pid numbers
    int i; //loop var
    FILE *fp; //treverse file var

    for(i=1; i<argc; i++) //for the amount of files provided in the commandline
    {
        //create a new process
        cpid[i-1] = fork();
    
        if(cpid[i-1] < 0) //if the creating of the new process faild
        {
            printf("ERROR: did not create new fork\n");
            return -1;
        }
        if(cpid[i-1] == 0) //if we are the child process
        {
            fp = fopen(argv[i], "r"); //open the file
            countWords(fp, argv[i]); //count the amount of words
            fclose(fp); //close file
            return 0; //close program
        }
        
        if(cpid > 0) //if we are the parent
            wait(NULL); //wait until the child gets done

    }

    //once all the files have been counted, let the user know
    printf("All %d files have been counted!\n", argc-1);
    

    return 0;
}


/***********************countWords*************
Purpose:
    this function read a file and count the number of 
    words inside the given file.
Params:
    fp - FILE pointer to a file that is already in read mode

    fileName - char* to a the name of the file


*********************************************/
void countWords(FILE *fp, char *fileName)
{
    int num=0; //counter
    int charTF=0; //identifer for new words
    char c; //current char

    while((c = fgetc(fp)) != EOF) //while we are not at the end of the file
    {
        //check to see if we are some sort of white space
        if(c == ' ' || c=='\n' || c=='\t')
            charTF=0; //indicate we found whitespace

        else if(charTF == 0) //if the char is not whitespace and is the first char we have seen
        {
            charTF=1; //turn off the word locator
            num++; //increase teh word count
        }
    }

    //once we found all the words, let the user know the filex has num words.
    printf("Child process for %s: number of words is %d\n", fileName, num);

    return;
}




















