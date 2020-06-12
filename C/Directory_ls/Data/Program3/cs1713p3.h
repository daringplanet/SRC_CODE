/**********************************************************************
cs1713p3.h
Purpose:
    Defines typedefs for
        Customer - includes email address, full name, birthdate, 
                    postal address, and fee balance.
        Transaction - either a Checkout or Return of a book
        Book - Book Information
        UtsaDate - used in determining date differences
    Defines constants for
        boolean values
        error messages
        program return codes
    Prototypes
Notes:
    
**********************************************************************/

/**** typedefs ****/

// Customer information 
typedef struct
{
    char        szCustomerId[7];            // 6 character customer ID is used
                                            // to uniquely define a customer
    char        szEmailAddr[51];            // customer's email address 
    char        szFullName[31];             // customer full name
    char        szStreetAddress[31];        // street number, street name, and 
                                            // (optional) apartment number
    char        szCity[21];                 // address city
    char        szStateCd[3];               // address state code
    char        szZipCd[6];                 // address zip code
    double      dFeeBalance;                // amount this customer owes in fees
} Customer;

// Customer Transaction
typedef struct
{
    char cTransType;                       // C - check out, R - return
    char szBookId[10];                     // Individual Book Id (not ISBN since
                                           // multiple copies have the same value)
    char szTransDt[11];                    // Date for the transaction
} Transaction;

// Book Information 
typedef struct
{
    char szBookId[10];                     // Individual Book Id (not ISBN since
                                           // multiple copies have the same value)
    char szTitle[40];                      // Title
    char szCustomerId[7];                  // ID of customer who checked it out.
                                           // If not checked out, it will be "NONE".
    char szCheckedOutDt[11];               // Date the book was checked out.  
                                           // If not checked out, it will be "0000-00-00"
    double dLateFeePerDay;                 // Fee per day late 
    double dMaxLateFee;                    // Maximum late fee
} Book;

// UTSA Date
typedef struct
{
    int iYear;				   // Year 
    int iMonth;				   // Month - 1 is Jan, 2 is Feb, etc.
    int iDay;				   // Day - between 1 and the max days for the month
} UtsaDate;

/**** constants ****/
// Maximum sizes 
#define MAX_BOOKS 30

// boolean
#define FALSE           0
#define TRUE            1

/*  Error Messages */
#define ERR_MISSING_SWITCH          "missing switch"
#define ERR_EXPECTED_SWITCH         "expected switch, found"
#define ERR_MISSING_ARGUMENT        "missing argument for"
#define ERR_COMMAND_FILENAME        "invalid command file name "
#define ERR_CUSTOMER_ID_DATA        "invalid customer id data "
#define ERR_CUSTOMER_ADDRESS_DATA   "invalid customer address data "
#define ERR_TRANSACTION_DATA        "invalid customer transaction data "
#define ERR_BOOK_FILENAME           "invalid book file name "
#define ERR_BOOK_DATA               "invalid book data "
#define ERR_TOO_MANY_BOOKS          "attempted to put more than MAX_BOOKS in the book array, found "
#define ERR_BOOK_NOT_FOUND          "Book ID not found "
#define ERR_CUSTOMER_SUB_COMMAND    "invalid CUSTOMER subcommand "
#define ERR_BOOK_SUB_COMMAND        "invalid BOOK subcommand "
#define ERR_INVALID_COMMAND         "invalid command "
#define ERR_CHANGE_SUB_COMMAND      "invalid CHANGE subcommand "
#define ERR_SHOW_SUB_COMMAND        "invalid SHOW subcommand "

/* program return codes */
#define ERR_COMMAND_LINE_SYNTAX     -1      // invalid command line syntax
#define USAGE_ONLY                  -2      // show usage only
#define ERROR_PROCESSING            -3

/* prototypes */

/* functions you must code  */
int getBooks(Book bookM[], char * pszBookFileName);
void printBooks(char *pszHeading, Book bookM[], int iBookCnt);
void sortBooks(Book bookM[], int iBookCount);
void processCustomerCommand(Book bookM[], int iBookCnt
    , char *pszSubCommand, char *pszRemainingInput
    , Customer *pCustomer);
void processBookCommand(Book bookM[], int iBookCnt
    , char *pszSubCommand, char *pszRemainingInput);
int searchBooks(Book bookM[], int iBookCount, char szMatchBookId[]);
double processTransaction(Book bookM[], int iBookCnt, Customer customer, Transaction transaction);

/* Functions Larry provided in the driver that you might call */
int dateDiff(char szDate1[], char  szDate2[]);
int validateDate(char szDate[], UtsaDate *pDate);
int utsaDateToUtsaJulian(UtsaDate date);          // used by dateDiff
void exitError(char *pszMessage, char *pszDiagnosticInfo);
void exitUsage(int iArg, char *pszMessage, char *pszDiagnosticInfo);