
package meatbol;

import enums.SubClassif;

/**
 *
 * @author katherine
 */
public class Date {
	
	static int iDaysPerMonth[] = 
       { 0, 31, 29, 31
          , 30, 31, 30
          , 31, 31, 30
          , 31, 30, 31 };
	
	/* Checks if a date is valid
	 * Returns 
	 *		0 if valid
	 *		1 if year is invalid
	 *		2 if month is invalid
	 *		3 if day is invalid
	 *		4 if invalid format (not yyyy-mm-dd)
	 */
	public static int validate(String str)
	{
		str = str.trim();
		
		int i;
		int iScanfCnt;
		int year, month, day;

		// Check for too few characters for the yyyy-mm-dd format
		if (str.length() != 10)
			return 4;  // invalid format due to length
		
		 
		for(i=0; i<10; i++)
		{
			if(i == 4 || i==7)
			{
				if(str.charAt(i) != '-')
					return 4;
			}
			else if(!Character.isDigit(str.charAt(i)))
				return 4;
		}
		
		year = Integer.parseInt(str.substring(0,4));
		month = Integer.parseInt(str.substring(5,7));
		day = Integer.parseInt(str.substring(8));

		// Validate Month
		if (month < 1 || month > 12)
		{
			//System.out.printf("Month: %d\n", month);
			return 2;  // month invalid
		}
			
		// Validate day based on max days per month 
		if (day < 1 || day > iDaysPerMonth[month])
			return 3;  // day invalid

		// if the 29th of Feb, check for leap year
		if (day == 29 && month == 2)
		{
			if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))
			{
				//System.out.println("Year: " + year + " Month: " + month + " Day:" + day);
				return 0;    // it is a leap year
			}
				
			else return 3;   // not a leap year, so the day is invalid
		}
		//System.out.println("Year: " + year + " Month: " + month + " Day:" + day);
		return 0;
	}
	
	/*
	 * Converts a date string into a julian date
	 * Returns the number of days since 0000-03-01
	 */
	public static int utsaDateToUtsaJulian(String date)
	{
		date = date.trim();
		
		int year = Integer.parseInt(date.substring(0,4));
		int month = Integer.parseInt(date.substring(5,7));
		int day = Integer.parseInt(date.substring(8));
     
		int iCountDays;
		// Calculate number of days since 0000-03-01

		// If month is March or greater, decrease it by 3.
		if (month > 2)
			month -= 3;
		else
		{
			month += 9;  // adjust the month since we begin with March
			year--;      // subtract 1 from year if the month was Jan or Feb
		}
		iCountDays = 365 * year                                // 365 days in a year
			+ year / 4 - year / 100 + year / 400   // add a day for each leap year
			+ (month * 306 + 5) / 10                           // see note 4
			+ (day );                                          // add the days
		return iCountDays;
	}
	

}
