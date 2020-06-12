/******************** getLibrary *****************************
int getLibrary(Book bookM[])
Purpose:
    Retrieves the books using the FILE pointer, pFileBook, that 
    you defined above the main function and returns a count of the number of books.
Parameters:
    O   Book bookM[]    an array which is used for returning the books.
Returns:
    A count of the number of read.
Notes:
    1. Uses pFileBook as the FILE pointer for reading the file.  It must
       have been opened with fopen prior to calling this function.
    2. The data in the data file is in this format
    szBookId szCustomerId szCheckedOutDt dLateFeePerDay dMaxLateFee szTitle
    9s        6s             10s           lf            lf          40s (may contain spaces)
**************************************************************************/
int getLibrary(Book bookM[])
{
    int i = 0;
    char szInputBuffer[100];
    int iScanfCnt = 0;
    // Read the books until EOF
    while (fgets(szInputBuffer, 100, pFileBook) != NULL)
    {
        // check for too many books to fit in the bookM array
        if (i >= MAX_BOOKS)
            exitError(ERR_TOO_MANY_BOOKS, szInputBuffer);
        iScanfCnt = sscanf(szInputBuffer, "%9s %6s %10s %lf %lf %[^\n]\n"
            , bookM[i].szBookId
            , bookM[i].szCustomerId
            , bookM[i].szCheckedOutDt
            , &bookM[i].dLateFeePerDay
            , &bookM[i].dMaxLateFee
            , bookM[i].szTitle);
        
        // It should have populated all six variables, if not exit with
        // an error.
        if (iScanfCnt < 6)
            exitError(ERR_BOOK_DATA, szInputBuffer);
        i++;
    }
    return i;
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
