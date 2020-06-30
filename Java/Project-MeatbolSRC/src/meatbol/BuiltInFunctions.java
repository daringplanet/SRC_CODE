/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meatbol;

import enums.ClassStruct;
import enums.Classif;
import enums.SubClassif;
import java.util.Stack;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 *
 * @author William
 */
public class BuiltInFunctions
{
    private Scanner scan;
    private Parser parser;

    public BuiltInFunctions(Scanner scan, Parser parser)
    {
        this.scan = scan;
        this.parser = parser;
    }
    /**
     * Functions used to print to the terminal
     * Assuming the current token is the "print" of the functions
     */
    public ResultValue print(boolean bExec) throws Error
    {
        ResultValue resResult = new ResultValue();
        resResult.value = "print";
        resResult.type = SubClassif.EMPTY;
        int saveLineNr = this.scan.currentToken.iSourceLineNr;
        //resResult.terminatingStr = "";

		scan.getToken();

		// The next token needs to be a (
        if (!scan.currentToken.tokenStr.equals("("))
            scan.errorWithLine("Expecting a left paren after a function call");

		if(!bExec) //if not executing
		{
            //skip to the end of the statement.
			this.scan.skipTo(";");

			if(!this.scan.currentToken.tokenStr.equals(";"))
				scan.errorWithCurrent("Expecting a ';' at the end of print statement.");
			//skip over the ';'
            scan.getToken();

			return resResult;
		}



        // First parameter of function if any
        //scan.getToken();

        if(this.scan.nextToken.tokenStr.equals(")"))
        {
            System.out.println();

            this.scan.getToken();
            this.scan.getToken();

            if(!this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ';' for the 'print' on line " + (saveLineNr+1));

            this.scan.getToken();

            return resResult;
        }

        if(this.scan.nextToken.primClassif != Classif.OPERAND && this.scan.nextToken.primClassif != Classif.FUNCTION && this.scan.nextToken.primClassif != Classif.OPERATOR)
            this.scan.errorWithCurrent("Expecting a Operand to start the print arguments");

        ResultValue resTemp = parser.expr(true);

        System.out.print(resTemp.value + " ");


        // If "," we are expecting more arguments to evaluate
        while (scan.currentToken.tokenStr.equals(","))
        {
            ResultValue res = new ResultValue();


            // Token should be an variable or primitive
            switch (scan.nextToken.primClassif)
            {
                case OPERAND:


                    //if it is a string, print it and continue.
                    if(this.scan.nextToken.subClassif == SubClassif.STRING)
                    {
                        int iLineNr = this.scan.currentToken.iSourceLineNr;
                        int iColPos = this.scan.currentToken.iColPos;


                          //loads the string into current
                        this.scan.getToken();
                        if(this.scan.nextToken.primClassif == Classif.OPERATOR || this.scan.nextToken.primClassif == Classif.FUNCTION)
                        {
                            //resets it to the token before the firs operand in the expr.
                             //jump back down to the endwhile
                            this.scan.setPosition(iColPos, iLineNr);



                             // Advance through the function until a ")" is hit
                            res = parser.expr(true);           // True expression within a function call



                            System.out.printf("%s ", res.value);

                             break;

                        }

                        System.out.print(this.scan.currentToken.tokenStr+" ");



                        //loads the ',' or ')'
                        this.scan.getToken();

                        continue;

                    }
                    // Advance through the function until a ")" is hit
                    res = parser.expr(true);           // True expression within a function call



                    System.out.printf("%s ", res.value);
                    //might have to save off orginal value.
                    break;
                case OPERATOR:

                	if (scan.nextToken.subClassif == SubClassif.UNARY_M)
                	{

                		res = parser.expr(true);

                		System.out.printf("%s ", res.value);

                	}

                	break;
                case FUNCTION:

                		res = parser.expr(true);

                		System.out.printf("%s ", res.value);

                    break;
                default:
                    scan.errorWithCurrent("Expecting an operand for function argument");
            }


            // if the next token is a ")" exit loop
            if (scan.currentToken.tokenStr.equals(")"))
            	break;


            if (!scan.currentToken.tokenStr.equals(","))
            	scan.errorWithCurrent("Expecting a ',' or ')' to end the print statement.");


        }

        System.out.println();

        if(!this.scan.curIsValue(")"))
            scan.errorWithCurrent("Expected a ')' before the ending with a ';'");



        if (!scan.nextToken.tokenStr.equals(";"))
            scan.errorWithCurrent("Expecting a ';'");

        //loads the ';' to current
        scan.getToken();
        //loads the Start of next statement.
		scan.getToken();

        return resResult;

    }

    /**
     * Purpose:
     *      Returns the number of populated values in an array.
     * @param stack
     * @param tk
     * @return
     * @throws Error
     */
    public Stack<Token> elem(Stack<Token> stack, Token tk) throws Error
    {
        Token tkIdentifier = stack.pop();

        if(tkIdentifier.classStruct != ClassStruct.FIXED_ARRAY)
            parser.error("Expecting an Array for ELEM function. Recieved: '" + tkIdentifier.tokenStr + "' on line " + tkIdentifier.iSourceLineNr);

        ResultValue resArray = parser.storage.getValue(parser, tkIdentifier.tokenStr);

        int iElemLength = 0;

        if(resArray.arrayVals != null)
            iElemLength = resArray.arrayVals.size();

        tk = tkIdentifier;
        tk.primClassif = Classif.OPERAND;
        tk.subClassif = SubClassif.INTEGER;
        tk.tkPrec = 0;
        tk.tokenStr = Integer.toString(iElemLength);

        stack.push(this.scan.copyTokens(tk));

        return stack;
    }

    public Stack<Token> length(Stack<Token> stack, Token tk) throws Error
    {
        Token tkString = stack.pop();
        ResultValue resString;
        ResultValue resR;
        if(tkString.subClassif == SubClassif.IDENTIFIER)
            resString = parser.storage.getValue(parser, tkString.tokenStr);
        else
            resString = parser.scan.tokenToResultV(tkString);

        if(resString.type != SubClassif.STRING)
            resString = Utility.stringCoherce(parser, resString);

        if(resString.value == null)
            parser.error("Declared '" + tk.tokenStr + "' but not Initilized on line " + (tk.iSourceLineNr+1));

        resR = Utility.LENGTH(resString.value);

        tk.primClassif = Classif.OPERAND;
        tk.subClassif = SubClassif.INTEGER;
        tk.tkPrec = 0;
        tk.tokenStr = resR.value;

        stack.push(this.scan.copyTokens(tk));
        return stack;
    }

    /**
     * Purpose:
     *      Will return the total number of elements the array can have in a fixed array.
     *
     * @param stack
     * @param tk
     * @return
     * @throws Error
     */
    public Stack<Token> maxelem(Stack<Token> stack, Token tk) throws Error
    {
        Token tkArray = stack.pop();
        ResultValue resA;
        ResultValue resRe = new ResultValue();

        if(tkArray.subClassif != SubClassif.IDENTIFIER)
            parser.error("Expecting an Identifier for the array for MAXELEM function on line " + (1+tk.iSourceLineNr));

        tk.primClassif = Classif.OPERAND;
        tk.subClassif = SubClassif.INTEGER;
        tk.tkPrec = 0;

        resA = parser.storage.getValue(parser, tkArray.tokenStr);

        if(resA.arrayVals == null)
            resRe.value = "0";
        else
            resRe.value = Integer.toString(resA.arraySize);


        tk.tokenStr = resRe.value;

        stack.push(this.scan.copyTokens(tk));
        return stack;
    }

    public Stack<Token> spaces(Stack<Token> stack, Token tk) throws Error
    {
        Token tkS = stack.pop();
        ResultValue resS;
        ResultValue resRet;
        if(tkS.subClassif == SubClassif.IDENTIFIER)
            resS = parser.storage.getValue(parser, tkS.tokenStr);
        else
            resS = parser.scan.tokenToResultV(tkS);

        if(resS.type != SubClassif.STRING)
            resS = Utility.stringCoherce(parser, resS);

        if(resS.value == null)
            parser.error("Declared '" + tk.tokenStr + "' but not Initilized on line " + (tk.iSourceLineNr+1));

        resRet = Utility.SPACES(resS.value);

        tk.primClassif = Classif.OPERAND;
        tk.subClassif = SubClassif.INTEGER;
        tk.tkPrec = 0;
        tk.tokenStr = resRet.value;

        stack.push(this.scan.copyTokens(tk));
        return stack;
    }

	/*
	 *returns an Int representing the difference in days
	 */
	public Stack<Token> dateDiff(Stack<Token> stack, Token tk) throws Error
	{
		Token tkD1 = stack.pop(); //first date perameter
		Token tkD2 = stack.pop(); //second date perameter

        ResultValue resA, resB;
        ResultValue resRe = new ResultValue();

		if(tkD1.subClassif != SubClassif.IDENTIFIER && tkD1.subClassif != SubClassif.DATE)
            parser.error("Expected a Date for first perameter for dateDiff function on line " + (1+tk.iSourceLineNr));

		if(tkD2.subClassif != SubClassif.IDENTIFIER && tkD2.subClassif != SubClassif.DATE)
            parser.error("Expected a Date for second perameter for dateDiff function on line " + (1+tk.iSourceLineNr));

		if(tkD1.subClassif == SubClassif.IDENTIFIER)
      {
         resA = parser.storage.getValue(parser, tkD1.tokenStr);
         if(resA.type != SubClassif.DATE)
            parser.error("Expected a Date for first perameter for dateDiff function on line " + (1+tk.iSourceLineNr));

         if(resA.value == null)
            parser.error("Uninitialized Date for first perameter for dateDiff function on line " + (1+tk.iSourceLineNr));
         else
            resA.value = "" + Date.utsaDateToUtsaJulian(resA.value);
      }
      else
      {
         resA = scan.tokenToResultV(tkD1);
         resA.value = "" + Date.utsaDateToUtsaJulian(resA.value);
      }

      if(tkD2.subClassif == SubClassif.IDENTIFIER)
		{
			resB = parser.storage.getValue(parser, tkD2.tokenStr);

			if(resB.type != SubClassif.DATE)
			    parser.error("Expected a Date for second perameter for dateDiff function on line " + (1+tk.iSourceLineNr));

			if(resB.value == null)
				parser.error("Uninitialized Date for second perameter for dateDiff function on line " + (1+tk.iSourceLineNr));
			else
				resB.value = "" + Date.utsaDateToUtsaJulian(resB.value);
		}
		else
		{
			resB = scan.tokenToResultV(tkD2);
			resB.value = "" + Date.utsaDateToUtsaJulian(resB.value);
		}

        int max = Math.max(Integer.parseInt(resA.value),Integer.parseInt(resB.value));
        int min = Math.min(Integer.parseInt(resA.value),Integer.parseInt(resB.value));
        resRe.value ="" + (max-min);

        tk.primClassif = Classif.OPERAND;
        tk.subClassif = SubClassif.INTEGER;
        tk.tkPrec = 0;
        tk.tokenStr = resRe.value;

        stack.push(this.scan.copyTokens(tk));
        return stack;
	}

	/**
	 * Date adjustment
	 * Assume: stack has parameters on it
	 * @return date
	 */
	public Stack<Token> dateAdj(Stack<Token> stack, Token tk) throws Error
	{
		int year, month, day;

		Calendar calendar;

		String dateStr = new String();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Token tkDayAdj = stack.pop();
		Token tkOldDate = stack.pop();

		ResultValue resDate = null, resDay = null;
        ResultValue resRe = new ResultValue();

		if(tkDayAdj.subClassif == SubClassif.IDENTIFIER)
		{
			resDay = parser.storage.getValue(parser, tkDayAdj.tokenStr);
			if(resDay.type != SubClassif.INTEGER)
				parser.error("Expected an Integer for second perameter for dateAdj function on line " + (1+tk.iSourceLineNr)
					+ ".\n Received " + resDay.value);
			if(resDay.value == null)
				parser.error("Uninisialized Integer for second perameter for dateAdj function on line " + (1+tk.iSourceLineNr));
			else
				resDay.value = tkDayAdj.tokenStr;
		}
		else if(tkDayAdj.subClassif == SubClassif.INTEGER)
			resDay = scan.tokenToResultV(tkDayAdj);
		else
			parser.error("Expected an Integer for second perameter for dateAdj function on line " + (1+tk.iSourceLineNr));


		if(tkOldDate.subClassif == SubClassif.IDENTIFIER)
		{
			resDate = parser.storage.getValue(parser, tkOldDate.tokenStr);
			if(resDate.type != SubClassif.DATE)
				parser.error("Expected a Date for first perameter for dateAdj function on line " + (1+tk.iSourceLineNr));

			if(resDate.value == null)
				parser.error("Uninisialized Date for first perameter for dateAdj function on line " + (1+tk.iSourceLineNr));
			else
			{
				dateStr = resDate.value.trim();
			    year = Integer.parseInt(dateStr.substring(0,4));
				month = Integer.parseInt(dateStr.substring(5,7))-1; //subtract 1 because gregorian calendar starts from 0 instead of 1
				day = Integer.parseInt(dateStr.substring(8));

				calendar = new GregorianCalendar(year,month,day);
				calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(resDay.value));


				resDate.value = sdf.format(calendar.getTime());
			}
		}
		else if(tkOldDate.subClassif == SubClassif.DATE)
		{
			resDate = scan.tokenToResultV(tkOldDate);

			dateStr = resDate.value.trim();
			year = Integer.parseInt(dateStr.substring(0,4));
			month = Integer.parseInt(dateStr.substring(5,7))-1;//subtract 1 because gregorian calendar starts from 0 instead of 1
			day = Integer.parseInt(dateStr.substring(8));

			calendar = new GregorianCalendar(year,month,day);
			calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(resDay.value));

			resDate.value = sdf.format(calendar.getTime());
		}
		else
			parser.error("Expected a Date for first perameter for dateAdj function on line " + (1+tk.iSourceLineNr));

        tk.primClassif = Classif.OPERAND;
        tk.subClassif = SubClassif.DATE;
        tk.tkPrec = 0;
        tk.tokenStr = resDate.value;

        stack.push(this.scan.copyTokens(tk));

		return stack;
	}

	public Stack<Token> dateAge(Stack<Token> stack, Token tk) throws Error
	{
      int year1, year2, years=0, max, min, julDays;
      String date1Str, date2Str;
      Token tkD1, tkD2;
      ResultValue resDate1, resDate2;

      stack = dateDiff(stack, tk);
      //*
      julDays = Integer.parseInt(stack.pop().tokenStr);
      years = julDays/365;

     //tk.primClassif = Classif.OPERAND;
    // tk.subClassif = SubClassif.DATE;
    // tk.tkPrec = 0;
     tk.tokenStr = Integer.toString(years);

     stack.push(this.scan.copyTokens(tk));
      //*/
		return stack;
	}
}
