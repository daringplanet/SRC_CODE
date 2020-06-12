




#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>


double *dSort;
double *dFinal;
double *dSortOne;

typedef struct
{
    int startIndex;
    int endIndex;
    int iCur;
}MyStruct;


typedef struct
{
    MyStruct *left;
    MyStruct *right;
}FinalStruct;



void *sort(void *arg);
void *merge(void *arg);
void *singleSort(void *args);




int main(int argc, char *argv[])
{
    
    if(argc != 2)
    {
        printf("ERROR: Invalid number of arguments.\n");
        printf("USAGE: > prog int a\n");

        exit(-1);
    }


   
    pthread_t tid, tid2, tid3;


    int n = 0;

    struct timeval start, stop;

    MyStruct leftStruct;
    MyStruct rightStruct;
    
    MyStruct oneStruct;

    FinalStruct final;
    final.left = &leftStruct;
    final.right = &rightStruct;

    sscanf(argv[1], "%d", &n);

    
    //creates the space for the []
    dSort = (double *)malloc(n * sizeof(double));
    dFinal = (double *)malloc(n * sizeof(double));
    dSortOne = (double*)malloc(n * sizeof(double));

    int i;
    for(i=0; i<n; i++)
    {
        dSort[i] = rand() % 100;
        dSortOne[i] = dSort[i];
    }


    oneStruct.startIndex = 0;
    oneStruct.endIndex = n-1;

    leftStruct.startIndex = 0;
    leftStruct.endIndex = n/2 - 1;
    leftStruct.iCur = 0;


    rightStruct.startIndex = n/2;
    rightStruct.endIndex = n-1;
    rightStruct.iCur = rightStruct.startIndex;
    
  //  printf("RIGHT: start = %d end = %d, iCur = %d\n", rightStruct.startIndex, rightStruct.endIndex, rightStruct.iCur);

    gettimeofday(&start, NULL);
    //printf("start = %lu\n", start);
    pthread_create(&tid, NULL, sort,  &leftStruct);
    pthread_create(&tid2, NULL, sort, &rightStruct);

    pthread_join(tid, NULL);
    pthread_join(tid2, NULL);
    gettimeofday(&stop, NULL);   

    unsigned long msSec = (stop.tv_sec - start.tv_sec) * 1000000 + ((stop.tv_usec - start.tv_usec) / 1000);
    unsigned long msDec = (stop.tv_usec - start.tv_usec) % 1000;
    /*
    printf("Left half sorted:\n");
    for(i=leftStruct.startIndex; i<=leftStruct.endIndex; i++)
        printf(".\t\t..dSort[%d] = %lf\n", i, dSort[i]);

    printf("Right half sorted:\n");
    for(i=rightStruct.startIndex; i<=rightStruct.endIndex; i++)
        printf(".\t\t..dSort[%d] = %lf\n", i, dSort[i]);
    */
    
    pthread_create(&tid3, NULL, merge, &final);

    pthread_join(tid3, NULL);

    
    printf("Sorting is done in %lu.%lums when two threads are used\n", msSec, msDec);
    
    gettimeofday(&start, NULL);
    pthread_create(&tid, NULL, singleSort, &oneStruct);
    pthread_join(tid, NULL);
    gettimeofday(&stop, NULL);
    msSec = (stop.tv_sec - start.tv_sec) * 1000000 + ((stop.tv_usec - start.tv_usec) / 1000);
    msDec = (stop.tv_usec - start.tv_usec) % 1000;

    printf("Sorting is done in %lu.%lums when one threads are used\n", msSec, msDec);

    /*
    printf("FINAL sorted:\n");
    for(i=0; i<=rightStruct.endIndex; i++)
        printf(".\t\t..dSort[%d] = %lf\n", i, dFinal[i]);
    */

    free(dSortOne);
    free(dFinal);
    return 0;

}






void *sort(void *args)
{
    //printf("withdraw created:");
    MyStruct *sortA = (MyStruct*) args;
    
    int i, j;
    double temp;
    for(i = sortA->startIndex; i <= sortA->endIndex; i++)
    {
        //printf("CURRENT: i = %d\n", i);
        for(j = sortA->startIndex; j < sortA->endIndex; j++)
        {
            //printf("j = %d\n", j);
            
            if(dSort[j] > dSort[j+1])
            {
                //printf("dSort[%d] = %lf >>>>> dSort[%d] = %lf\n", j, dSort[j], j+1, dSort[j+1]);
                temp = dSort[j];
                dSort[j] = dSort[j+1];
                dSort[j+1] = temp;
            }
        }
    }

    return NULL;
}





void *singleSort(void *args)
{
    MyStruct *sortA = (MyStruct*) args;

    int i, j;
    double temp;

    for(i = sortA->startIndex; i <= sortA->endIndex; i++)
    {
        for(j = sortA->startIndex; j < sortA->endIndex; j++)
        {
            if(dSortOne[j] > dSortOne[j+1])
            {
                temp = dSortOne[j];
                dSortOne[j] = dSortOne[j+1];
                dSortOne[j+1] = temp;
            }
        }
    }
}





void *merge(void *args)
{
    FinalStruct *final = (FinalStruct*) args;
    int i;
    
    
    for(i=0; i<=final->right->endIndex; i++)
    {
        if(final->left->iCur > final->left->endIndex)
        {
            dFinal[i] = dSort[final->right->iCur];
            final->right->iCur++;
        }
        else if(final->right->iCur > final->right->endIndex)
        {
            dFinal[i] = dSort[final->left->iCur];
            final->left->iCur++;
        }
        else if(dSort[final->left->iCur] < dSort[final->right->iCur])
        {
            dFinal[i] = dSort[final->left->iCur];
            final->left->iCur++;
        }
        else
        {
            dFinal[i] = dSort[final->right->iCur];
            final->right->iCur++;
        }
    }
    
    free(dSort);
    return NULL;
}































