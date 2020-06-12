/**********************************************************************
cs1713p4Driver.c
Purpose:
    This program reads book information and a command file.   It 
    processes the commands against the book information.
    This is the driver code for the routines written by the students.
Command Parameters:
    p4 -b bookFileName -c commandFileName
Input:
    Book	same as Programming Assignment #3 although there may be different data.

    Command	This is is in the form of commands: 
        CUSTOMER BEGIN szCustomerId  dFeeBalance   szEmailAddr  szFullName  
            specifies the beginning of customer request and includes all the identification information from program 2.
        CUSTOMER ADDRESS szStreetAddress,szCity,szStateCd,szZipCd
            specifies the address for a customer (separated by commas)
        CUSTOMER TRANS cTransType    szBookId   szTransDt
            specifies a single book transaction.  Steps:
            -	Print the Transaction Type, Book Id, and Transaction Date 
            -	Lookup the book ID using a binary search.  If not found, print a warning (but do not terminate your program) and return.
            -	If the transaction date is invalid, show a message stating "invalid date", but do not terminate.  Use the validateDate  function.
            -	If the transaction type is C (meaning checkout):
                o	If the book is already checked out, show a message stating "already checked out", but do not terminate.
                o	Otherwise, check out the book to this customer, setting the book's customer ID.  The book's checked out date needs to be set to the transaction's date.
            -	If the transaction type is R (meaning return):
                o	Use dateDiff to subtract the book's szCheckOutDt from the transaction's szTransDt
                o	If the difference is more than 14:
                    --	Determine the number of days late by subtracting 14 from that difference.  
                    --	Compute the late fee by multiplying the number of days late by the book's dLateFeePerDay.
                    --	If that late fee is greater than the book's dMaxLateFee, change it to dMaxLateFee.
                    --  Print the late fee.
                    --	Add the computed late fee to the customer's dFeeBalance
                o	Set the book's customer ID to "NONE".
                o	Set the book's check out date to "0000-00-00".
        CUSTOMER COMPLETE
            specifies the completion   of a customer.  Print the total fees for this customer.
        BOOK CHANGE szBookId dLateFeePerDay dMaxLateFee
            change the Late Fee Per Day and Max Late Fee to the specified values
        BOOK SHOW szBookId    
            requests a display of a particular book.  Show all of its information.
        BOOK NEW szBookId szCustomerId szCheckedOutDt dLateFeePerDay dMaxLateFee szTitle 
                 9s        6s             10s           lf            lf          40s (may contain spaces)         
Results:
    Prints the Books prior to processing transactions.
    Processes the commands (see above) and shows any errors.
    Prints the resulting Books
Returns:
    0  normal
    -1 invalid command line syntax
    -2 show usage only
    -3 error during processing, see stderr for more information

Notes:
    p4 -?       will provide the usage information.  In some shells,
                you will have to type p4 -\?

**********************************************************************/
// If compiling using visual studio, tell the compiler not to give its warnings
// about the safety of scanf and printf
#define _CRT_SECURE_NO_WARNINGS 1

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "cs1713p4.h"

// prototypes for this file
void processCommandSwitches(int argc, char *argv[], char **ppszBookFileName
    , char **ppszCommandFileName);
void processCommands(Node **ppHead, char *pszCommandFileName);
int getSimpleToken(char szInput[], int *piBufferPosition, char szToken[]);

#define MAX_TOKEN_SIZE 20

int main(int argc, char *argv[])
{
    Node      *pHead;                       // points to an ordered linked list of books
    char      *pszBookFileName = NULL;      // Book file name
    char      *pszCommandFileName = NULL;   // Command file name

    processCommandSwitches(argc, argv, &pszBookFileName, &pszCommandFileName);

    // get the book, print it, sort it, and print the sorted
    pHead = getBooks(pszBookFileName);
    printBooks("Initial Books", pHead);
    
    printf("\n");

    // process the command input file
    processCommands(&pHead, pszCommandFileName);

    // print the book after processing the command file
    printBooks("Resulting Books", pHead);
    printf("\n");
    return 0;
}

/******************** processCommands **************************************
    void processCommands(Node **ppHead
        , char *pszCommandFileName)
Purpose:
    Reads the Command file to process commands.  There are two major
    types of commands:  CUSTOMER and BOOK.
Parameters:
    I/O Node **ppHead                 Address of the pointer to the head of
                                      an ordered linked list of books
    I   char  **ppszCommandFileName   command file name
Notes:
    This opens and closes the Command file.
**************************************************************************/
void processCommands(Node **ppHead
     , char *pszCommandFileName)
{
    FILE *pfileCommand;                     // stream Input for Customer Customer data
                                            // variables for command processing
    char szInputBuffer[100];                // input buffer for a single text line
    char szCommand[MAX_TOKEN_SIZE+1];       // command
    char szSubCommand[MAX_TOKEN_SIZE+1];    // subcommand
    int bGotToken;                          // TRUE if getSimpleToken got a token
    int iBufferPosition;                    // This is used by getSimpleToken to 
                                            // track parsing position within input buffer
 
    // variables for customer information (except Book Transaction)
    Customer customer;
    
    // open the Command stream data file
    if (pszCommandFileName == NULL)
        exitError(ERR_MISSING_SWITCH, "-c");

    pfileCommand = fopen(pszCommandFileName, "r");
    if (pfileCommand == NULL)
        exitError(ERR_COMMAND_FILENAME, pszCommandFileName);
    
    /* get command data until EOF
    ** fgets returns null when EOF is reached.
    */
    while (fgets(szInputBuffer, 100, pfileCommand) != NULL)
    {
        printf("%s", szInputBuffer);        // includes \n in the data
        iBufferPosition = 0;                // reset buffer position

        // get the command
        bGotToken = getSimpleToken(szInputBuffer, &iBufferPosition, szCommand);

        // see what the command is
        if (bGotToken && strcmp(szCommand, "CUSTOMER") == 0)
        {   // CUSTOMER command
            // get the sub comand
            bGotToken = getSimpleToken(szInputBuffer, &iBufferPosition, szSubCommand);
            if (bGotToken)
                processCustomerCommand(ppHead
                , szSubCommand
                , &szInputBuffer[iBufferPosition]           // address past the subcommand
                , &customer);
            else exitError(ERR_CUSTOMER_SUB_COMMAND, " missing subcommand");
        }
        else  if (bGotToken && strcmp(szCommand, "BOOK") == 0)
        {   // BOOK command
            bGotToken = getSimpleToken(szInputBuffer, &iBufferPosition, szSubCommand);
            if (bGotToken)
                processBookCommand(ppHead, szSubCommand
                    , &szInputBuffer[iBufferPosition]);
            else exitError(ERR_BOOK_SUB_COMMAND, " missing subcommand");
        }
        else 
            exitError(ERR_INVALID_COMMAND, szCommand);
    }
    fclose(pfileCommand);
}
/******************** getBooks **************************************
    Node *getBooks(char * pszBookFileName)
Purpose:
    Retrieves the books from the file specified.
Parameters:
    I   char *pszBookFileName name of the file containing the book data
Returns:
    A pointer to the head of a linked list.
Notes:
    1. This opens the file based on the specified file name. 
    2. The data in the data file is in this format
    szBookId szCustomerId szCheckedOutDt dLateFeePerDay dMaxLateFee szTitle
    9s        6s             10s           lf            lf          40s (may contain spaces)
**************************************************************************/

Node *getBooks(char * pszBookFileName)
{
    char szInputBuffer[100];		// input buffer for reading data
    int iScanfCnt;                  // returned by sscanf
    FILE *pFileBook;                // Stream Input for Books data. 
    Book book;                      // Local for taking data from the input
    Node *pHead = NULL;             // Pointer to the beginning of an 
                                    // ordered linked list of book nodes.
    Node *pFind;                    // Pointer returned by insertLL to
                                    // where it found the book Id or 
                                    // the new node it inserted.                                  

    /* open the Books stream data file */
    if (pszBookFileName == NULL)
        exitError(ERR_MISSING_SWITCH, "-b");

    pFileBook = fopen(pszBookFileName, "r");
    if (pFileBook == NULL)
        exitError(ERR_BOOK_FILENAME, pszBookFileName);

    /* get book information until EOF
    ** fgets returns null when EOF is reached.
    */
    while (fgets(szInputBuffer, 100, pFileBook) != NULL)
    {
        iScanfCnt = sscanf(szInputBuffer, "%9s %6s %10s %lf %lf %[^\n]\n"
            , book.szBookId
            , book.szCustomerId
            , book.szCheckedOutDt
            , &book.dLateFeePerDay
            , &book.dMaxLateFee
            , book.szTitle);

        // It should have populated all six variables, if not exit with
        // an error.
        if (iScanfCnt < 6)
            exitError(ERR_BOOK_DATA, szInputBuffer);

        // Insert the book into the linked list.  Since insertLL might
        // change the beginning of the linked list, we must pass the
        // address of pHead
        pFind = insertLL(&pHead, book);
    }
    fclose(pFileBook);
    return pHead;
}
/******************** allocateNode **************************************
    Node * allocateNode(Book book)
Purpose:
    Allocates a new node, placing the parameter in the node.
Parameters:
    I   Book book    Book information for one book.
Returns:
    A pointer to the newly allocated node.
Notes:
    If there isn't enough memory available, this function terminates.
**************************************************************************/
Node * allocateNode(Book book)
{
    Node *pNew;
    // to allocate a new node
    pNew = malloc(sizeof(Node));
    if (pNew == NULL)
        exitError("Memory allocation error", "");
    pNew->book = book;
    pNew->pNext = NULL;
    return pNew;
}

 /******************** getSimpleToken **************************************
 int getSimpleToken(char szInput[], int *piBufferPosition, char szToken[])
 Purpose:
    Returns the next token in a string.  The delimiter for the token is a 
    space, a newline or the end of the string.  To help with a 
    subsequent call, it also returns the next position in the buffer.
Parameters:
    I   char szInput[]          input buffer to be parsed
    I/O int *piBufferPosition   Position to begin looking for the next token.
                                This is also used to return the next 
                                position to look for a token (which will
                                follow the delimiter).
    O   char szToken[]          Returned token.  
Returns:
    Functionally:
        TRUE - a token is returned
        FALSE - no more tokens
    iBufferPosition parm - the next position for parsing
    szToken parm - the returned token.  If not found, it will be an empty string.
Notes:
    - If the token is larger than the szToken parm, we return a truncated value.
**************************************************************************/

int getSimpleToken(char szInput[], int *piBufferPosition, char szToken[]) 
{
    int iDelimPos;                      // found position of delim
    int iCopy;                          // number of characters to copy
    char szDelims[20] = " \n";          // delimiters
    
    // check for past the end of the string
    if (*piBufferPosition >= (int) strlen(szInput))
    {
        szToken[0] = '\0';              // mark it empty
        return FALSE;                   // no more tokens
    }

    // get the position of the first delim, relative to the iBufferPosition
    iDelimPos = strcspn(&szInput[*piBufferPosition], szDelims);

    // see if we have more characters than target token, if so, trunc
    if (iDelimPos > MAX_TOKEN_SIZE)
        iCopy = MAX_TOKEN_SIZE;             // truncated size
    else
        iCopy = iDelimPos;
    
    // copy the token into the target token variable
    memcpy(szToken, &szInput[*piBufferPosition], iCopy);
    szToken[iCopy] = '\0';              // null terminate

    // advance the position
    *piBufferPosition += iDelimPos + 1;  
    return TRUE;
}

/******************** validateDate ***********************************
int validateDate(char szDate[], UtsaDate *pDate);
Purpose:
    Validates the date and returns (via the second parameter) the 
    UtsaDate.   
Parameters:
    I char szDate[]         Date as a string in the form "yyyy-mm-dd"
    O UtsaDate *pDate       Address of a UtsaDate structure for 
                            returning the date.
Notes:
    1. The length must be 10 characters.
    2. The date must be in the form "yyyy-mm-dd".
    3. The month must be 01-12.
    4. The day must be between 1 and the max for each month
               Mar 31 Aug 31 Jan 31
               Apr 30 Sep 30 Feb 29
               May 31 Oct 31
               Jun 30 Nov 30
               Jul 31 Dec 31
    5. If Feb 29 was specified, validate that the year is a leap year.
Return Value:
    0     date is valid
    1     year is invalid
    2     month is invalid
    3     day is invalid
    4     invalid length or format
**********************************************************************/
int validateDate(char szDate[], UtsaDate *pDate)
{
    static int iDaysPerMonth[] = 
       { 0, 31, 29, 31
          , 30, 31, 30
          , 31, 31, 30
          , 31, 30, 31 };
    int iCharCnt = 0;
    int iScanfCnt;

    // Check for too few characters for the yyy-mm-dd format
    if (strlen(szDate) != 10)
        return 4;  // invalid format due to length
    
    // The year should be 4 characters 
    iCharCnt = strspn(szDate, "0123456789");
    if (iCharCnt != 4)
        return 1;  // invalid year
    
    // Use sscanf to pluck out the year, month, and day
    iScanfCnt = sscanf(szDate, "%4d-%2d-%2d"
        , &pDate->iYear
        , &pDate->iMonth
        , &pDate->iDay);
    // If sscanf returned less than 3, then something was bad
    if (iScanfCnt < 3)
        return 4;  // invalid format
    
    // Validate Month
    if (pDate->iMonth < 1 || pDate->iMonth > 12)
        return 2;  // month invalid
    
    // Validate day based on max days per month 
    if (pDate->iDay < 1 || pDate->iDay > iDaysPerMonth[pDate->iMonth])
        return 3;  // day invalid

    // if the 29th of Feb, check for leap year
    if (pDate->iDay == 29 && pDate->iMonth == 2)
    {
        if (pDate->iYear % 4 == 0 && (pDate->iYear % 100 != 0 || pDate->iYear % 400 == 0))
            return 0;    // it is a leap year
        else return 3;   // not a leap year, so the day is invalid
    }
    return 0;
}
/******************** dateDiff ***********************************
int dateDiff(char szDate1[], char szDate2[])
Purpose:
    Validates the dates and returns the difference in days of the 
    first date minus the second date.   
Parameters:
    I char szDate1[]        First date as a string in the form "yyyy-mm-dd"
    I char szDate2[]        Second date as a string in the form "yyyy-mm-dd"
Notes:
    1. We validate/convert the days to UtsaDate format.  If the date is
       invalid, we exit and show a message.
    2. For each of the dates, we determine the number of days since
       "0000-03-01" by starting the count at 1 for 0000-03-01. Using
       March 1st eliminates some leap day issues. 
    3. Return the difference in days
Return Value:
    the difference in dates
**********************************************************************/
int dateDiff(char szDate1[], char szDate2[])
{
    UtsaDate date1;
    UtsaDate date2;
    int iJulian1;
    int iJulian2;
  
    if (validateDate(szDate1, &date1) != 0)
        exitError("Invalid 1st date for dateDiff: ", szDate1);

    if (validateDate(szDate2, &date2) != 0)
        exitError("Invalid 2nd date for dateDiff: ", szDate2);

    iJulian1 = utsaDateToUtsaJulian(date1);
    iJulian2 = utsaDateToUtsaJulian(date2);

    return iJulian1 - iJulian2;
}

/******************** utsaDateToUtsaJulian ***********************************
int utsaDateToUtsaJulian(UtsaDate date)
Purpose:
    Converts a date to a UTSA Julian Days value.  This will start numbering
    at 1 for 0000-03-01. Making dates relaive to March 1st helps eliminate
    some leap day issues. 
Parameters:
    I char szDate1[]        First date as a string in the form "yyyy-mm-dd"
    I char szDate2[]        Second date as a string in the form "yyyy-mm-dd"
Notes:
    1 We replace the month with the number of months since March.  
      March is 0, Apr is 1, May is 2, ..., Jan is 10, Feb is 11.
    2 Since Jan and Feb are before Mar, we subtract 1 from the year
      for those months.
    3 Jan 1 is 306 days from Mar 1.
    4 The days per month is in a pattern that begins with March
      and repeats every 5 months:
           Mar 31 Aug 31 Jan 31
           Apr 30 Sep 30
           May 31 Oct 31
           Jun 30 Nov 30
           Jul 31 Dec 31
       Therefore:
           Mon  AdjMon  NumberDaysFromMarch (AdjMon*306 + 5)/10
           Jan    10      306
           Feb    11      337
           Mar     0        0
           Apr     1       31
           May     2       61
           Jun     3       92
           Jul     4      122 
           Aug     5      153
           Sep     6      184
           Oct     7      214
           Nov     8      245
           Dec     9      275
    5 Leap years are 
           years that are divisible by 4 and
           either years that are not divisible by 100 or 
           years that are divisible by 400
Return Value:
    the number of days since 0000-03-01 beginning with 1 for 
    0000-03-01.
**********************************************************************/
int utsaDateToUtsaJulian(UtsaDate date)
{
    int iCountDays;
    // Calculate number of days since 0000-03-01

    // If month is March or greater, decrease it by 3.
    if (date.iMonth > 2)
        date.iMonth -= 3;
    else
    {
        date.iMonth += 9;  // adjust the month since we begin with March
        date.iYear--;      // subtract 1 from year if the month was Jan or Feb
    }
    iCountDays = 365 * date.iYear                                // 365 days in a year
        + date.iYear / 4 - date.iYear / 100 + date.iYear / 400   // add a day for each leap year
        + (date.iMonth * 306 + 5) / 10                           // see note 4
        + (date.iDay );                                          // add the days
    return iCountDays;
}

/******************** processCommandSwitches *****************************
    void processCommandSwitches(int argc, char *argv[]
        , char **ppszBookFileName
        , char **ppszCommandFileName)
Purpose:
    Checks the syntax of command line arguments and returns the filenames.
    If any switches are unknown, it exits with an error.
Parameters:
    I   int argc                        count of command line arguments
    I   char *argv[]                    array of command line arguments
    O   char **ppszBookFileName         book file name
    O   char **ppszCommandFileName      command file name
Notes:
    If a -? switch is passed, the usage is printed and the program exits
    with USAGE_ONLY.
    If a syntax error is encountered (e.g., unknown switch), the program
    prints a message to stderr and exits with ERR_COMMAND_LINE_SYNTAX.
**************************************************************************/

void processCommandSwitches(int argc, char *argv[], char **ppszBookFileName
    , char **ppszCommandFileName)
{
    int i;
    int rc = 0;
    int bShowCommandHelp = FALSE;

    for (i = 1; i < argc; i++)
    {
        // check for a switch
        if (argv[i][0] != '-')
            exitUsage(i, ERR_EXPECTED_SWITCH, argv[i]);
        // determine which switch it is
        switch (argv[i][1])
        {
        case 'b':                   // Book File Name
            if (++i >= argc)
                exitUsage(i, ERR_MISSING_ARGUMENT, argv[i - 1]);
            // check for too long of a file anme
            else
                *ppszBookFileName = argv[i];
            break;
        case 'c':                   // Command File Name
            if (++i >= argc)
                exitUsage(i, ERR_MISSING_ARGUMENT, argv[i - 1]);
            else
                *ppszCommandFileName = argv[i];
            break;
        case '?':
            exitUsage(USAGE_ONLY, "", "");
            break;
        default:
            exitUsage(i, ERR_EXPECTED_SWITCH, argv[i]);
        }
    }
}

/******************** exitError *****************************
    void exitError(char *pszMessage, char *pszDiagnosticInfo)
Purpose:
    Prints an error message and diagnostic to stderr.  Exits with
    ERROR_PROCESSING.
Parameters:
    I char *pszMessage              error message to print
    I char *pszDiagnosticInfo       supplemental diagnostic information
Notes:
    This routine causes the program to exit.
**************************************************************************/
void exitError(char *pszMessage, char *pszDiagnosticInfo)
{
    fprintf(stderr, "Error: %s %s\n"
        , pszMessage
        , pszDiagnosticInfo);
    exit(ERROR_PROCESSING);
}

/******************** exitUsage *****************************
    void exitUsage(int iArg, char *pszMessage, char *pszDiagnosticInfo)
Purpose:
    If this is an argument error (iArg >= 0), it prints a formatted message
    showing which argument was in error, the specified message, and
    supplemental diagnostic information.  It also shows the usage. It exits
    with ERR_COMMAND_LINE_SYNTAX.

    If this is just asking for usage (iArg will be -1), the usage is shown.
    It exits with USAGE_ONLY.
Parameters:
    I int iArg                      command argument subscript
    I char *pszMessage              error message to print
    I char *pszDiagnosticInfo       supplemental diagnostic information
Notes:
    This routine causes the program to exit.
**************************************************************************/

void exitUsage(int iArg, char *pszMessage, char *pszDiagnosticInfo)
{
    if (iArg >= 0)
        fprintf(stderr, "Error: bad argument #%d.  %s %s\n"
        , iArg
        , pszMessage
        , pszDiagnosticInfo);
    fprintf(stderr, "p3 -b bookFileName -c commandFileName\n");
    if (iArg >= 0)
        exit(-1);
    else
        exit(-2);
}