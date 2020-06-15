
/*Written by William Trace Lippard*/
#include <stdarg.h>
#include <stdlib.h>
#include <stdio.h>
#include <dirent.h>
#include <sys/stat.h>
#include <time.h>
#include <fcntl.h>
#include <string.h>
#define ERROR_PROCESSING 99



int main( int argc, char *argv[]){


    char *temp, *temp2;

    if(argc < 2  )
        errExit2("Usage:xdic \"directory\" -\"option1\" -\"option2\"\n");
    if(argv[2] != NULL){
        temp=argv[2];
        temp2 = argv[3];
    }else{
        temp = "00";
        temp2 = "00";
    }

    if(argc == 2)
        regLS(argv[1]);
   else if( argc == 4){
        if(temp[1] == 'a' && temp2[1] == 'l')
             alPrint(argv[1]);
        else if(temp[1] == 'l' && temp2[1] == 'a')
            alPrint(argv[1]);
        else
            errExit2("Incorret flags\n");


    } else if(temp[1] == 'a')
        allPrint(argv[1]);

    else if(temp[1] == 'l'){
      longPrint(argv[1]);
   } else
         errExit2("Incorret flags\n");


   return 0;
    }


void longPrint(char *directory){

    DIR *pDir;
    struct dirent *pDirent;
    pDir = opendir(directory);
    char dirName[64];

    if(pDir == NULL)
        errExit2("Could not open '%s'\n", directory);

    printf("%s :\n", directory);

     char *temp;
     char *file;
    while((pDirent = readdir(pDir)) != NULL){

        temp = pDirent->d_name;
        if(temp[0] != '.'){
            strcpy(&dirName, directory);
           char* temp2 = "/";
            temp2 = strcat(dirName, temp2);
            file = strcat(temp2, pDirent->d_name);
            struct stat statsBuf;
            int check = stat(file, &statsBuf);
            if(check < 0 )
                errExit2("Problem reading '%s' stats\n", file);


        if(S_ISLNK(statsBuf.st_mode))
            printf("    %s L %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);
       else if (S_ISFIFO(statsBuf.st_mode))
           printf("    %s P %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);
        else if(S_ISREG(statsBuf.st_mode))
            printf("    %s F %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);
        else if(S_ISDIR(statsBuf.st_mode))
            printf("    %s D %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);
        else
            printf("    %s L %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);

        *file = NULL;

        }

        }

    closedir(pDir);
    return;



    }



void alPrint(char *directory){

     DIR *pDir;
     struct dirent *pDirent;
     pDir = opendir(directory);
     char dirName[64];

     if(pDir == NULL)
         errExit2("Could not open '%s'\n", directory);





    printf("%s :\n", directory);


    char *temp;
    char *file;
    while((pDirent = readdir(pDir)) != NULL){

        temp = pDirent->d_name;
        strcpy(&dirName, directory);
        char* temp2 = "/";
        temp2 = strcat(dirName, temp2);
        file = strcat(temp2, pDirent->d_name);
        struct stat statsBuf;
        int check = stat(file, &statsBuf);
         if(check < 0 )
             errExit2("Problem reading '%s' stats\n", file);

         if(S_ISLNK(statsBuf.st_mode))
              printf("    %s L %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);
        else if (S_ISFIFO(statsBuf.st_mode))
            printf("    %s P %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);
        else if(S_ISREG(statsBuf.st_mode))
            printf("    %s F %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);
        else if(S_ISDIR(statsBuf.st_mode))
            printf("    %s D %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);
        else
            printf("    %s L %ld blks %ld bytes\n", pDirent->d_name, statsBuf.st_blocks, statsBuf.st_size);


        *file = NULL;

        }

        closedir(pDir);
        return;

    }

void regLS(char *directory){


    DIR *pDir;
    struct dirent *pDirent;
    pDir = opendir(directory);


    if(pDir == NULL)
        errExit2("Could not open '%s'\n", directory);


    printf("%s :\n", directory);

    char *temp;
    while((pDirent = readdir(pDir)) != NULL){

        temp = pDirent->d_name;
        if(temp[0] != '.')
            printf("    %s\n", pDirent->d_name);
        }

        closedir(pDir);
        return;

    }






void allPrint(char *directory){

        DIR *pDir;
        struct dirent *pDirent;
        pDir = opendir(directory);


        if(pDir == NULL)
            errExit2("Could not open '%s'\n", directory);


        printf("%s :\n", directory);

         char *temp;
         while((pDirent = readdir(pDir)) != NULL){

               temp = pDirent->d_name;
                printf("    %s\n", pDirent->d_name);
         }

         closedir(pDir);
           return;
}










void errExit2(const char szFmt[], ... ) {
    va_list args;               // This is the standard C variable argument list type
    va_start(args, szFmt);      // This tells the compiler where the variable arguments
                                // begins.  They begin after szFmt.
    printf("ERROR: ");
    vprintf(szFmt, args);       // vprintf receives a printf format string and  a
                                // va_list argument
    va_end(args);               // let the C environment know we are finished with the
                                // va_list argument
    printf("\n");
    exit(ERROR_PROCESSING);
}
