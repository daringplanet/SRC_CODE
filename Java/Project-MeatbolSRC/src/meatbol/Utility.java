/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meatbol;

//import STEntries.STEntry;
//import STEntries.STIdentifier;
import enums.ClassStruct;
import enums.Classif;
import enums.SubClassif;
import java.util.ArrayList;

/**
 *
 * @author William
 */
public class Utility 
{

    /**
     * 
     * Converts a result obj into 
     * a numeric object.
     * <p>
     * Also needs  the parser for
     * error checking and 
     * the position the numeric obj
     * is relative to related numeric objs
     *
     * @param parse parser used for debugging and error handling.
     * @param res result object used to convert to numeric object
     * @param sOperandPos the new numeric object's position in the arguments 
     *                    
     * @return newNum Numeric Object that is created filled out.
     * 
     *  @author William
     */
    public static Numeric numericConverter(Parser parse, ResultValue res, String sOperandPos)
    {
        //creates space for numeric obj
        Numeric newNum = new Numeric();
        
        //assigns values to the new numeric obj
        newNum.nParse = parse;
		newNum.valueStr = res.value;
        newNum.sOperandPos = sOperandPos;
        newNum.res = res;
		
        return newNum;
    }
    
    /**
     * returns a result value with 
     * the two numeric numbers added.
     * <p>
     * This function also determines
     * based off the left operand the 
     * data type of the object based on 
     * its tokenStr value. 
     * @param parse for error messages
     * @param nOp1 the left operand
     * @param nOp2 the right operand
     * @return res Result value containing the new number
     * @throws Error 
     * 
     *  @author William
     */
    public static ResultValue numericAdd(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
        
        if(valueL.type == SubClassif.IDENTIFIER)
            valueL = parse.storage.getValue(parse, valueL.value);
        
        if(valueR.type == SubClassif.IDENTIFIER)
            valueR = parse.storage.getValue(parse, valueR.value);
        
        //creating the new result value
        ResultValue res = new ResultValue();
        int left;
        int right;
        int total;
        double dLeft;
        double dRight;
        double dTotal;
       // boolean isFloat = false;
        
        
        res.structure = ClassStruct.PRIMITIVE;
        res.type = SubClassif.INTEGER;
       
        if(valueL.type == SubClassif.FLOAT || valueR.type == SubClassif.FLOAT)
        {
            if(valueL.value.charAt(0) == '-')
            valueL.value = Utility.unaryOperationStr(parse, valueL.value);
        
        if(valueR.value.charAt(0) == '-')
            valueR.value = Utility.unaryOperationStr(parse, valueR.value);
        
            dLeft = Double.valueOf(valueL.value);
            dRight = Double.valueOf(valueR.value);
            dTotal = dLeft + dRight;
            res.structure = ClassStruct.PRIMITIVE;
            res.type = SubClassif.FLOAT;
            res.value = Double.toString(dTotal);
            return res;
        }
        
        if(valueL.type != SubClassif.INTEGER || valueR.type != SubClassif.INTEGER)
            Utility.utilError("numericAdd", "ValueL or valueR is not a valid subclass.");
        
        if(valueL.value.charAt(0) == '-')
            valueL.value = Utility.unaryOperationStr(parse, valueL.value);
        
        if(valueR.value.charAt(0) == '-')
            valueR.value = Utility.unaryOperationStr(parse, valueR.value);
        
        left = Integer.valueOf(valueL.value);
        right = Integer.valueOf(valueR.value);
        total = left + right;
        res.structure = ClassStruct.PRIMITIVE;
        res.type = SubClassif.INTEGER;
        res.value = Integer.toString(total);
    
        return res;
    }
    
    
    /**
     * Takes two resultValues with
     * two strings and concatenate
     * and returns the new result in
     * a result value obj
     * <p>
     * @param parse used for errors messages
     * @param left ResultValue containing the left string.
     * @param right ResultValue containing the right string.
     * @return newRes resultValue containing the new string.
     * @throws Error 
     * 
     *  @author William
     */
    public static ResultValue StringConcat(Parser parse, ResultValue left, ResultValue right) throws Error
    {
        if(left.value == null || right.value == null)
            utilError("StringConcat", "left.value or right.value value is null");
        
        ResultValue newRes = new ResultValue();
        newRes.type = SubClassif.STRING;
        newRes.structure = ClassStruct.PRIMITIVE;

        if(left.value == null || right.value == null)
            Utility.utilError("StringConcat", "Either the left or right operand's value is null");
	
	//gets both the strings combined together.
	String newString = Utility.StrConHelper(left.value, right.value);
	
	//stores the value
	newRes.value = newString;
        return newRes;
    }
    
    /**
     * converts a string to a
     * char[] and returns the 
     * new char[] 
     * <p>
     * @param str the string to convert to char []
     * @return cStr the char[] representation of the string
     * 
     *  @author William
     */
    public static char[] StringToCharArray(String str) throws Error
    {
        if(str == null)
            utilError("StringToCharArray", "str is null");
        
        //start position
        int iColPos = 0;
        //lenght of the string
        int iLen = str.length();
        //new char[]
        char[] cStr = new char[iLen];
        
        while(iColPos < iLen) //while there are more chars
        {
            //assigning the char
            cStr[iColPos] = str.charAt(iColPos);
            iColPos++;
        }

        return cStr;
    }
    
    public static void PrintTokenList(ArrayList<Token> list) {
        for (Token token: list) {
            System.out.print(token.toString() + ", ");
        }
        System.out.println(":::End of array");
    }

    /**
     * converts a char[] to a string
     * <p>
     * @param cStr the char[] representation of the string
     * @return newStr the string representation of the char[]
     * 
     *  @author William
     */
    public static String CharArrayToString(char[] cStr) throws Error
    {
        if(cStr == null)
            utilError("CharArrayToString", "cStr is null");
        
        //create new string space
        StringBuilder newStr = new StringBuilder(cStr.length);
        int iColPos = 0;
        while(iColPos < cStr.length) //while there are more chars
        {
            //append to the end of the newStr
            newStr.append(cStr[iColPos]);
            iColPos++;
        }

        return newStr.toString();
    }
    
    
    /**
     * converts a single char 
     * into a string.
     * @param cStr the char that will be converted to string.
     * @return newStr String that will contain a single char.
     * 
     *  @author William
     */
    public static String CharToString(char cStr)
    {
        
        //creeates a new string of size one
        StringBuilder newStr = new StringBuilder(1);
        //append the new char
        newStr.append(cStr);
        //return the string.
        return newStr.toString();
    }
    
    /**
     * Takes in two strings and 
     * concatenate them together.
     * <p>
     * @param sLeft String the left string
     * @param sRight String the right string
     * @return newStr String the new string with both the string
     * @throws Error 
     *  @author William
     */
    public static String StrConHelper(String sLeft, String sRight) throws Error
    {
        if(sLeft == null || sRight == null)
            utilError("StrConHelper", "left or right str is null");
        //position in the current string (left or right)
        int iColPos = 0;
        //position in the new string
        int iStrPos = 0;
        
        //newString
        String newStr;
        
        //create spaces for the char[]s
        char cLeft[] = new char[sLeft.length()];
        char cRight[] = new char[sRight.length()];
        char cNewStr[] = new char[cLeft.length + cRight.length];
        
        //get left and right into the char[]
        cLeft =  Utility.StringToCharArray(sLeft);
        cRight = Utility.StringToCharArray(sRight);


        while(iColPos<cLeft.length) //loop while there is still chars lef string
        {
            //assignment to the new string
            cNewStr[iColPos] = cLeft[iColPos];
            //advancement ofd the positions
            iColPos++;
            iStrPos++;
        }

        //reset the current string position
        iColPos = 0;
         
        while(iColPos < cRight.length) //loop while there is still chars right string
        {
            //assignment to the new string
            cNewStr[iStrPos] = cRight[iColPos];
            //advancement ofd the positions
            iColPos++;
            iStrPos++;    
        }
        
        //converts the charArray to string.
        newStr = Utility.CharArrayToString(cNewStr);
        
        return newStr;

    }
    
    /**
     * takes in two numeric objs
     * and preforms a subtraction
     * on the two numeric objs.
     * <p>
     * @param parse for error handling
     * @param nOp1 Numeric left operand
     * @param nOp2 Numeric right operand
     * @return res ResultValue the new result after the subtraction.
     * @throws Error 
     *  @author William
     */
    public static ResultValue numericSub(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
      
        
        //creating the new result value
        ResultValue res = new ResultValue();
        int left;
        int right;
        int total = 0;
        double dLeft;
        double dRight;
        double dTotal = 0.0;
        boolean isFloat = false;
        
        
        res.structure = ClassStruct.PRIMITIVE;
        res.type = SubClassif.INTEGER;
       
        if(valueL.type == SubClassif.FLOAT || valueR.type == SubClassif.FLOAT)
        {
            dLeft = Double.valueOf(valueL.value);
           // System.out.println("***********dLeft = " + dLeft);
            dRight = Double.valueOf(valueR.value);
            //System.out.println("***********dRight = " + dRight);
            dTotal = dLeft - dRight;
            res.structure = ClassStruct.PRIMITIVE;
            res.type = SubClassif.FLOAT;
            //System.out.println("***********dTotal = " + dTotal);
            res.value = Double.toString(dTotal);
            
            return res;
        }
        
        if(valueL.type != SubClassif.INTEGER || valueR.type != SubClassif.INTEGER)
            Utility.utilError("numericSub", "ValueL or valueR is not a valid subclass.");
        
        left = Integer.valueOf(valueL.value);
        right = Integer.valueOf(valueR.value);
        total = left - right;
        res.structure = ClassStruct.PRIMITIVE;
        res.type = SubClassif.INTEGER;
        //System.out.println("***********total = " + total);
        res.value = Integer.toString(total);
    
        return res;
    }
    
    /**
     * takes in two numeric objs
     * and preforms a multiplication
     * on the two numeric objs.
     * <p>
     * @param parse for error handling
     * @param nOp1 Numeric left operand
     * @param nOp2 Numeric right operand
     * @return res ResultValue the new result after the subtraction.
     * @throws Error 
     * 
     * SPECIAL NOTES:
     *      Some code will not be comment as 
     *      heavily due to it doing the same
     *      thing as the previous code.
     * 
     *  @author William
     */
    public static ResultValue numericMult(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
      
        
        //creating the new result value
        ResultValue res = new ResultValue();
        int left;
        int right;
        int total = 0;
        double dLeft;
        double dRight;
        double dTotal = 0.0;
        
        
        
        res.structure = ClassStruct.PRIMITIVE;
        res.type = SubClassif.INTEGER;
       
        if(valueL.type == SubClassif.FLOAT || valueR.type == SubClassif.FLOAT)
        {
            dLeft = Double.valueOf(valueL.value);
           // System.out.println("***********dLeft = " + dLeft);
            dRight = Double.valueOf(valueR.value);
            //System.out.println("***********dRight = " + dRight);
            dTotal = dLeft * dRight;
            res.structure = ClassStruct.PRIMITIVE;
            res.type = SubClassif.FLOAT;
            //System.out.println("***********dTotal = " + dTotal);
            res.value = Double.toString(dTotal);
            
            return res;
        }
        
        if(valueL.type != SubClassif.INTEGER || valueR.type != SubClassif.INTEGER)
            Utility.utilError("numericMult", "ValueL or valueR is not a valid subclass.");
        
        if(valueL.value.charAt(0) == '-')
            valueL.value = Utility.unaryOperationStr(parse, valueL.value);
        
        if(valueR.value.charAt(0) == '-')
            valueR.value = Utility.unaryOperationStr(parse, valueR.value);
        
        left = Integer.valueOf(valueL.value);
        
        right = Integer.valueOf(valueR.value);
        total = left * right;
        res.structure = ClassStruct.PRIMITIVE;
        res.type = SubClassif.INTEGER;
        //System.out.println("***********total = " + total);
        res.value = Integer.toString(total);
    
        return res;
    }
    
    /**
     * takes in two numeric objs
     * and preforms a division
     * on the two numeric objs.
     * <p>
     * @param parse for error handling
     * @param nOp1 Numeric left operand
     * @param nOp2 Numeric right operand
     * @return res ResultValue the new result after the subtraction.
     * @throws Error 
     * 
     * SPECIAL NOTES:
     *      Some code will not be comment as 
     *      heavily due to it doing the same
     *      thing as the previous code.
     * 
     *  @author William
     */
     public static ResultValue numericDivide(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
      
        
        //creating the new result value
        ResultValue res = new ResultValue();
        int left;
        int right;
        int total = 0;
        double dLeft;
        double dRight;
        double dTotal = 0.0;
        boolean isFloat = false;
        
        
        res.structure = ClassStruct.PRIMITIVE;
        res.type = SubClassif.INTEGER;
       
        if(valueL.type == SubClassif.FLOAT || valueR.type == SubClassif.FLOAT)
        {
            
              if(valueL.value.charAt(0) == '-')
            valueL.value = Utility.unaryOperationStr(parse, valueL.value);
        
        if(valueR.value.charAt(0) == '-')
            valueR.value = Utility.unaryOperationStr(parse, valueR.value);
        
            dLeft = Double.valueOf(valueL.value);
           
            dRight = Double.valueOf(valueR.value);
            
            if(dRight == 0)
                Utility.utilError("numericDivide", "Can not divide by 0.");
            
            dTotal = dLeft / dRight;
            res.structure = ClassStruct.PRIMITIVE;
            res.type = SubClassif.FLOAT;
            
            res.value = Double.toString(dTotal);
            
            return res;
        }
        
        if(valueL.type != SubClassif.INTEGER || valueR.type != SubClassif.INTEGER)
            Utility.utilError("numericDivide", "ValueL or valueR is not a valid subclass.");
        
          if(valueL.value.charAt(0) == '-')
            valueL.value = Utility.unaryOperationStr(parse, valueL.value);
        
        if(valueR.value.charAt(0) == '-')
            valueR.value = Utility.unaryOperationStr(parse, valueR.value);
        
        left = Integer.valueOf(valueL.value);
        right = Integer.valueOf(valueR.value);
        
        if(right == 0)
                Utility.utilError("numericDivide", "Can not divide by 0.");
        
        total = left / right;
        res.structure = ClassStruct.PRIMITIVE;
        res.type = SubClassif.INTEGER;
        
        res.value = Integer.toString(total);
    
        return res;
    }
    
	
	
    /**
     * takes in two numeric objs
     * and preforms a exponentiation
     * using the left as the base and the 
     * right as the exponent.
     * <p>
     * @param parse for error handling
     * @param nOp1 Numeric left operand
     * @param nOp2 Numeric right operand
     * @return res ResultValue the new result after the subtraction.
     * @throws Error 
     * 
     * SPECIAL NOTES:
     *      Some code will not be comment as 
     *      heavily due to it doing the same
     *      thing as the previous code.
     * 
     *  @author William
     */
    public static ResultValue numericExpon(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
        //checking to see if either tokenStr is empty.
        if(valueL.value.isEmpty() || valueR.value.isEmpty())
            utilError("numericExpon", "valueR/L.tokenStr is empty.");
        
       ResultValue res = new ResultValue();
  
        
        
        if(valueL.type == SubClassif.FLOAT)
        {   //add float 
            
              if(valueL.value.charAt(0) == '-')
            valueL.value = Utility.unaryOperationStr(parse, valueL.value);
        
        if(valueR.value.charAt(0) == '-')
            valueR.value = Utility.unaryOperationStr(parse, valueR.value);
         
            res.type = SubClassif.FLOAT;
            res.structure = ClassStruct.PRIMITIVE;
            //convert both values into doubles
            double left = Double.valueOf(valueL.value);
            
            if(valueR.type == SubClassif.INTEGER)
                valueR = Utility.floatCoherce(parse, Utility.resValueToToken(parse, valueR));
            
            double right = Double.valueOf(valueR.value);
	
           //set total to 0
            double total = 0;
            
            //prefore the exponentiation
            total = Math.pow(left, right);
            //store the result in a string
            res.value = Double.toString(total);		
            return res;

        } 
        else if (valueL.type == SubClassif.INTEGER)
        {
	      if(valueL.value.charAt(0) == '-')
            valueL.value = Utility.unaryOperationStr(parse, valueL.value);
        
        if(valueR.value.charAt(0) == '-')
            valueR.value = Utility.unaryOperationStr(parse, valueR.value);
        
            res.type = SubClassif.FLOAT;
            res.structure = ClassStruct.PRIMITIVE;
            //convert both values into doubles
            int left = Integer.valueOf(valueL.value);
            
            //left still needs to be a double for fractional expos.
            if(valueR.type != SubClassif.FLOAT)
                valueR = Utility.floatCoherce(parse, Utility.resValueToToken(parse, valueR));
            
            double right = Double.valueOf(valueR.value);
	
           //set total to 0
            double total = 0;
            
            //prefore the exponentiation
            total = Math.pow(left, right); 
            //store the result in a string
            res.value = Double.toString(total);	
            
            //convert the result to an integer value.
            res = Utility.intCoherce(parse, Utility.resValueToToken(parse, res));
            
            return res;
        }
        
        //else it must be an error, not a numeric operand
        utilError("numericExpon", "valueL is not classified as a number.", valueL.value);
        return null; //unreachable, for java compiler
    }
    
    public static String floatToInt(Parser parse, String num) 
    {
    	int i;
    	String newNum = new String();
    	for(i=0; i<num.length(); i++) 
    	{
    		if(num.charAt(i) == '.')
    			break;
    	}
    	
    	newNum = num.substring(0, i);
    	
    	return newNum;
    }
    
    public static String unaryOperationStr(Parser parse, String num) throws Error
    {
        String newNum = new String();
        ResultValue res = new ResultValue();
    	int countOfUnary = 0;
    	int i;
    	for (i = 0; i < num.length(); i++)
    	{
    		if (num.charAt(i) == '-')
    			countOfUnary++;
    		else
    			break;
    	}
    	
		
		
		
    	newNum = num.substring(countOfUnary, num.length());
        if(parse.storage.checkKey(parse, newNum))
            newNum = parse.storage.getValue(parse, newNum).value;
        
        if(countOfUnary % 2 == 0)
        {
            return newNum;
            
        }
        return '-' + newNum;
    }
    
    
    public static ResultValue unaryOperation(Parser parse, String num) throws Error
    {
    	ResultValue res = new ResultValue();
    	int countOfUnary = 0;
    	int i;
    	for (i = 0; i < num.length(); i++)
    	{
    		if (num.charAt(i) == '-')
    			countOfUnary++;
    		else
    			break;
    	}
    	
		
		
		
    	String key = num.substring(countOfUnary, num.length());
	
        
    	// value has to return the actual value
    	res = parse.storage.getValue(parse, key);
		//System.out.println("res.value: " + res.value);
		if(countOfUnary % 2 == 0)
		{
			//System.out.println("EVEN number of '-'");
			return res;
		}
		//System.out.println("ODD number of '-'");
    	// multiple by -1 however many unary minuses
    	// (Parser aThis, ResultValue res1, String string, String nd_operand) 
    	Numeric valNum = new Numeric(parse, res, res.value, "");
    	
    	if(valNum.res.type == SubClassif.FLOAT)
        {    //add float 
            res.type = SubClassif.FLOAT;
            double left = Double.valueOf(valNum.valueStr);
            double right = (-1.0);
            double total = 0;
            //multiply the two and store the total
            total = left * right;
            res.value = Double.toString(total);
            //System.out.println("VALUE: " + res.value);
            return res;
        }//else nOp1 is an integer
        else if(valNum.res.type == SubClassif.INTEGER)
        {
        
            res.type = SubClassif.INTEGER;
            int left = Integer.valueOf(valNum.valueStr);
            int right = -1;
            int total = 0;
            //multiply the two and store the total
            total = left * right;
            res.value = Integer.toString(total);
			//System.out.println("VALUE: " + res.value);
            return res;
        }
    	
    	utilError("numericSub", "valNum is not classified as a number.", valNum.valueStr);
    	
    	return null;  // Unreacheable return
    }
    
    public static Token resValueToToken(Parser parse, ResultValue res)
    {
        
        Token newToken = new Token();
        newToken.primClassif = Classif.OPERAND;
        newToken.subClassif = res.type;
        newToken.tokenStr = res.value;
        newToken.classStruct = res.structure;
        
        return newToken;
    }
	
    
    
    public static int countUnary(String num)
    {
        int countOfUnary = 0;
    	int i;
    	for (i = 0; i < num.length(); i++)
    	{
    		if (num.charAt(i) == '-')
    			countOfUnary++;
    		else
    			break;
    	}
        
        return countOfUnary;
    }
    
    
    
	public static String unaryStringOperation(Parser parse, String num) throws Error
    {
		//System.out.println("START: num = " + num);
    	ResultValue res = new ResultValue();
    	int countOfUnary = 0;
    	int i;
    	for (i = 0; i < num.length(); i++)
    	{
    		if (num.charAt(i) == '-')
    			countOfUnary++;
    		else
    			break;
    	}
    	
		String key = num.substring(countOfUnary, num.length());
		System.out.println("key = " + key);
    	// value has to return the actual value
    	res = parse.storage.getValue(parse, key);
		
		if(countOfUnary % 2 == 0)
			return res.value;
    	
    	// multiple by -1 however many unary minuses
    	// (Parser aThis, ResultValue res1, String string, String nd_operand) 
    	Numeric valNum = new Numeric(parse, res, res.value, "");
    	
    	if(valNum.res.type == SubClassif.FLOAT)
        {    //add float 
            res.type = SubClassif.FLOAT;
            double left = Double.valueOf(valNum.valueStr);
            double right = (-1.0);
            double total = 0;
            //multiply the two and store the total
            total = left * right;
            res.value = Double.toString(total);
            
            return res.value;
        }//else nOp1 is an integer
        else if(valNum.res.type == SubClassif.INTEGER)
        {
        
            res.type = SubClassif.INTEGER;
            int left = Integer.valueOf(valNum.valueStr);
            int right = -1;
            int total = 0;
            //multiply the two and store the total
            total = left * right;
            res.value = Integer.toString(total);

            return res.value;
        }
    	
    	utilError("numericSub", "valNum is not classified as a number.", valNum.valueStr);
    	
    	return null;  // Unreacheable return
    }
    
    /**
     * Format an error message by passing int
     * the function name and the message as a string
     * to throw and error. 
     * <p>
     * @param funcName String the function from that had the error
     * @param msg String the error message
     * @throws Error 
     * 
     *  @author William
     */
    public static void utilError(String funcName, String msg) throws Error
    {
        String err = String.format("Utility:\nFunction: %s:\nerrorMsg:%s\n", funcName, msg);
        throw new Error(err);
        
    }
    
    /**
     * Format an error message by passing int
     * the function name and the message as a string
     * to throw and error. 
     * <p>
     * @param funcName String the function from that had the error
     * @param msg String the error message
     * @param token String the token string that contained the error.
     * @throws Error 
     * 
     *  @author William
     */
    public static void utilError(String funcName, String msg, String token) throws Error
    {
        String err = String.format("Utility:\nFunction: %s:\nerrorMsg:%s\nToken:%s", funcName, msg, token);
        throw new Error(err);
        
    }

    
   
    public static ResultValue greater(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
        
        ResultValue newRes = new ResultValue();
        newRes.type = SubClassif.BOOLEAN;
		
        //System.out.println("numLeft token lenght = " + numLeft.valueStr.length());
        switch(valueL.type)
        {
            case INTEGER:
                int iLeft;
                int iRight;
                
                valueR = Utility.intCoherce(parse, Utility.resValueToToken(parse, valueR));
                iRight = Integer.valueOf(valueR.value);
                
                iLeft = Integer.valueOf(valueL.value);
				
                if(iLeft > iRight)
                    newRes.value = "true";
		else
                    newRes.value = "false";
		return newRes;
				            
            case FLOAT:
                double dLeft;
                double dRight;
                
                valueR = Utility.floatCoherce(parse, Utility.resValueToToken(parse, valueR));
                dRight = Double.valueOf(valueR.value);
                
                dLeft = Double.valueOf(valueL.value);
				
                if(dLeft > dRight)
                    newRes.value = "true";
                else
                    newRes.value = "false";
		return newRes;
                
            case STRING: 
                //determine what data type to try and coerce to.
                switch(valueR.type)
                {
                    case INTEGER:
                        valueL = Utility.intCoherce(parse, Utility.resValueToToken(parse, valueL));
                        int iL = Integer.valueOf(valueL.value);
                        int iR = Integer.valueOf(valueR.value);
                        
                        if(iL > iR)
                            newRes.value = "true";
                        else
                            newRes.value = "false";
                        break;
                    case FLOAT:
                        valueL = Utility.floatCoherce(parse, Utility.resValueToToken(parse, valueL));
                        double dL = Double.valueOf(valueL.value);
                        double dR = Double.valueOf(valueR.value);
                        
                        if(dL > dR)
                            newRes.value = "true";
                        else
                            newRes.value = "false";
                        break;
                    default:
                        Utility.utilError("greater", "Not a valid operand for '>'", valueR.value);
                }
                
                return newRes;
            default:
                String msg = String.format("Not a valid comparison type. %s is %s", valueL.value, valueR.type);
                parse.scan.errorWithLine(msg);
        }
		
		Utility.utilError("greater", "Reacing unreachable statement.");
        return null;
    }

    public static Numeric numIntConverter(Parser parse, Numeric numRight) throws Error 
    {
        
        int iColPos = 0;
        
        if(!Character.isDigit(numRight.valueStr.charAt(iColPos)))
            Utility.utilError("numIntConverter", "numRight does not start with digit.");
        
        while(Character.isDigit(numRight.valueStr.charAt(iColPos)) && iColPos < numRight.valueStr.length())
        {
            iColPos++;
        }
        
        numRight.valueStr = numRight.valueStr.substring(0, iColPos);
        return numRight;
    }
    
    public static Numeric numDoubleConverter(Parser parse, Numeric numRight) throws Error 
    {
        
        int iColPos = 0;
        boolean foundDec = false;
        char c;
        if(!Character.isDigit(numRight.valueStr.charAt(iColPos)))
            Utility.utilError("numIntConverter", "numRight does not start with digit.");
        
        c = numRight.valueStr.charAt(iColPos);
        while((Character.isDigit(c) || c == '.') && iColPos < numRight.valueStr.length())
        {
            if(c == '.')
            {
                if(foundDec)
                    Utility.utilError("numDoubleConverter", "Two decmial points are found.", numRight.valueStr);
                else
                    foundDec = true;
            }
            iColPos++;
        }
        
        numRight.valueStr = numRight.valueStr.substring(0, iColPos);
        return numRight;
    }

    public static ResultValue greaterEql(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
        
        ResultValue newRes = new ResultValue();
        newRes.type = SubClassif.BOOLEAN;
		
        //System.out.println("numLeft token lenght = " + numLeft.valueStr.length());
        switch(valueL.type)
        {
            case INTEGER:
                int iLeft;
                int iRight;
                
                valueR = Utility.intCoherce(parse, Utility.resValueToToken(parse, valueR));
                iRight = Integer.valueOf(valueR.value);
                
                iLeft = Integer.valueOf(valueL.value);
				
                if(iLeft >= iRight)
                    newRes.value = "true";
		else
                    newRes.value = "false";
		return newRes;
				            
            case FLOAT:
                double dLeft;
                double dRight;
                
                valueR = Utility.floatCoherce(parse, Utility.resValueToToken(parse, valueR));
                dRight = Double.valueOf(valueR.value);
                
                dLeft = Double.valueOf(valueL.value);
				
                if(dLeft >= dRight)
                    newRes.value = "true";
		else
                    newRes.value = "false";
		return newRes;
                
            case STRING:
                if(valueR.type != SubClassif.STRING)
                    parse.error("String must be compared to other Strings. Found: " + valueR.value + " type: " + valueR.type);
                if(valueL.value.length() >= valueR.value.length())
                    newRes.value = "true";
                else
                    newRes.value = "false";
                
                return newRes;
            default:
                String msg = String.format("Not a valid comparison type. %s is %s", valueL.value, valueL.type);
                parse.scan.errorWithLine(msg);
        }
		
		Utility.utilError("greater", "Reacing unreachable statement.");
        return null;
    }

    public static ResultValue less(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
        
        ResultValue newRes = new ResultValue();
        newRes.structure = ClassStruct.PRIMITIVE;
        newRes.type = SubClassif.BOOLEAN;
		
        //System.out.println("numLeft token lenght = " + numLeft.valueStr.length());
        switch(valueL.type)
        {
            case INTEGER:
                int iLeft;
                int iRight;
                
                valueR = Utility.intCoherce(parse, Utility.resValueToToken(parse, valueR));
                iRight = Integer.valueOf(valueR.value);
                
                iLeft = Integer.valueOf(valueL.value);
				
                if(iLeft < iRight)
                    newRes.value = "true";
		else
                    newRes.value = "false";
		return newRes;
				            
            case FLOAT:
                double dLeft;
                double dRight;
                
                valueR = Utility.floatCoherce(parse, Utility.resValueToToken(parse, valueR));
                dRight = Double.valueOf(valueR.value);
                
                dLeft = Double.valueOf(valueL.value);
				
                if(dLeft < dRight)
                    newRes.value = "true";
		else
                    newRes.value = "false";
		return newRes;
                
            case STRING:
                
                if(valueR.type != SubClassif.STRING)
                    parse.error("String must be compared to other Strings. Found: " + valueR.value + " type: " + valueR.type);
                
                if(valueL.value.length() < valueR.value.length())
                    newRes.value = "true";
                else
                    newRes.value = "false";
                
                return newRes;
            default:
                String msg = String.format("Not a valid comparison type. %s is %s", valueL.value, valueL.type);
                parse.scan.errorWithLine(msg);
        }
		
		Utility.utilError("greater", "Reacing unreachable statement.");
        return null;
    }

    public static ResultValue lessEql(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
        
        ResultValue newRes = new ResultValue();
        newRes.type = SubClassif.BOOLEAN;
		
        //System.out.println("numLeft token lenght = " + numLeft.valueStr.length());
        switch(valueL.type)
        {
            case INTEGER:
                int iLeft;
                int iRight;
                
                valueR = Utility.intCoherce(parse, Utility.resValueToToken(parse, valueR));
                iRight = Integer.valueOf(valueR.value);
                
                iLeft = Integer.valueOf(valueL.value);
				
                if(iLeft <= iRight)
                    newRes.value = "true";
				
				else
					newRes.value = "false";
					
				return newRes;
				            
            case FLOAT:
                double dLeft;
                double dRight;
                
                valueR = Utility.floatCoherce(parse, Utility.resValueToToken(parse, valueR));
                dRight = Double.valueOf(valueR.value);
                
                dLeft = Double.valueOf(valueL.value);
				
                if(dLeft <= dRight)
                    newRes.value = "true";
				
				else
					newRes.value = "false";
					
				return newRes;
            case STRING:
                
                if(valueR.type != SubClassif.STRING)
                    parse.error("String must be compared to other Strings. Found: " + valueR.value + " type: " + valueR.type);
                
                if(valueL.value.length() <= valueR.value.length())
                    newRes.value = "true";
                else
                    newRes.value = "false";
                
                return newRes;
            default:
                String msg = String.format("Not a valid comparison type. %s is %s", valueL.value, valueL.type);
                parse.scan.errorWithLine(msg);
        }
		
		Utility.utilError("greater", "Reacing unreachable statement.");
        return null;
    }

    public static ResultValue equal(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
        
        ResultValue newRes = new ResultValue();
        newRes.type = SubClassif.BOOLEAN;
		
        //System.out.println("numLeft token lenght = " + numLeft.valueStr.length());
        switch(valueL.type)
        {
            case INTEGER:
                int iLeft;
                int iRight;
                
                valueR = Utility.intCoherce(parse, Utility.resValueToToken(parse, valueR));
                iRight = Integer.valueOf(valueR.value);
                
                iLeft = Integer.valueOf(valueL.value);
				
                if(iLeft == iRight)
                    newRes.value = "true";
				
				else
					newRes.value = "false";
					
				return newRes;
				            
            case FLOAT:
                double dLeft;
                double dRight;
                
                valueR = Utility.floatCoherce(parse, Utility.resValueToToken(parse, valueR));
                dRight = Double.valueOf(valueR.value);
                
                dLeft = Double.valueOf(valueL.value);
				
                if(dLeft == dRight)
                    newRes.value = "true";
				
				else
					newRes.value = "false";
					
				return newRes;
            case STRING:
                
                if(valueR.type != SubClassif.STRING)
                    parse.error("String must be compared to other Strings. Found: " + valueR.value + " type: " + valueR.type);
                
                valueL.value = Utility.rmNonPrintChars(valueL.value);
                valueR.value = Utility.rmNonPrintChars(valueR.value);
                
                if(valueL.value.equals(valueR.value))
                    newRes.value = "true";
                else
                    newRes.value = "false";
                
                return newRes;
            default:
                String msg = String.format("Not a valid comparison type. %s is %s", valueL.value, valueL.type);
                parse.scan.errorWithLine(msg);
        }
		
		Utility.utilError("greater", "Reacing unreachable statement.");
        return null;
    }

    public static ResultValue notEqual(Parser parse, ResultValue valueL, ResultValue valueR) throws Error 
    {
        
        ResultValue newRes = new ResultValue();
        newRes.type = SubClassif.BOOLEAN;
		
        //System.out.println("numLeft token lenght = " + numLeft.valueStr.length());
        switch(valueL.type)
        {
            case INTEGER:
                int iLeft;
                int iRight;
                
                valueR = Utility.intCoherce(parse, Utility.resValueToToken(parse, valueR));
                iRight = Integer.valueOf(valueR.value);
                
                iLeft = Integer.valueOf(valueL.value);
				
                if(iLeft != iRight)
                    newRes.value = "true";
                else
                    newRes.value = "false";
                
                return newRes;
				            
            case FLOAT:
                double dLeft;
                double dRight;
                
                valueR = Utility.floatCoherce(parse, Utility.resValueToToken(parse, valueR));
                dRight = Double.valueOf(valueR.value);
                
                dLeft = Double.valueOf(valueL.value);
				
                if(dLeft != dRight)
                    newRes.value = "true";				
                else
                    newRes.value = "false";

                return newRes;
            case STRING:
                
                if(valueR.type != SubClassif.STRING)
                    parse.error("String must be compared to other Strings. Found: " + valueR.value + " type: " + valueR.type);
                
                valueL.value = Utility.rmNonPrintChars(valueL.value);
                valueR.value = Utility.rmNonPrintChars(valueR.value);

                
                if(valueL.value.equals(valueR.value))
                    newRes.value = "false";
                else
                    newRes.value = "true";
                
                return newRes;
            default:
                String msg = String.format("Not a valid comparison type. %s is %s", valueL.value, valueL.type);
                parse.scan.errorWithLine(msg);
        }
		
		Utility.utilError("greater", "Reacing unreachable statement.");
        return null;
    }
    
    public static String rmNonPrintChars(String str)
    {
        StringBuilder sb = new StringBuilder();
        int i;
        char c;
        for(i = 0; i<str.length(); i++)
        {
            c = str.charAt(i);  
            if(c > 31 && c < 127)
                sb.append(c);
        }
        return sb.toString();
    }
    

	public static ResultValue intCoherce(Parser parse, Token cur) throws Error
	{
		ResultValue newRes = new ResultValue();
		//System.out.println("cur = " + cur.tokenStr);
		int i = 0;
		StringBuilder sb = new StringBuilder();
		
		switch(cur.subClassif)
		{
			case INTEGER:
				newRes = parse.scan.tokenToResultV(cur);
				return newRes;
			case FLOAT:
				
                            boolean bDec = false;
                
				while(i < cur.tokenStr.length())
				{
                                    if(!Character.isDigit(cur.tokenStr.charAt(i)))
                                        if(cur.tokenStr.charAt(i) != '-')
                                            break;
                    
                                    sb.append(cur.tokenStr.charAt(i));
                                    i++;
				}
				cur.tokenStr = sb.toString();
				cur.subClassif = SubClassif.INTEGER;
				newRes = parse.scan.tokenToResultV(cur);
				return newRes;	
			case STRING:

				while(i < cur.tokenStr.length())
				{
                    //System.out.printf("charAt(%d) = %c\n)", i, cur.tokenStr.charAt(i));
                    if(!Character.isDigit(cur.tokenStr.charAt(i)))
                        break;
					sb.append(cur.tokenStr.charAt(i));
					i++;
				}
                
				cur.tokenStr = sb.toString();
				cur.subClassif = SubClassif.INTEGER;
				newRes = parse.scan.tokenToResultV(cur);
				return newRes;
			case BOOLEAN:
				if(cur.tokenStr.equals("true"))
				{
					cur.tokenStr = "1";
					cur.subClassif = SubClassif.INTEGER;
					newRes = parse.scan.tokenToResultV(cur);
					return newRes;
				}
				
				cur.tokenStr = "0";
				cur.subClassif = SubClassif.INTEGER;
				newRes = parse.scan.tokenToResultV(cur);
				return newRes;
			default:
				Utility.utilError("intCoherce", "Not a valid Int type.", cur.tokenStr);
		}
		
		//reachable.
		return newRes;
	}

	public static ResultValue floatCoherce(Parser parse, Token cur) throws Error
	{
		ResultValue newRes = new ResultValue();
		
		int i = 0;
		StringBuilder sb = new StringBuilder();
		
		switch(cur.subClassif)
		{
			case INTEGER:
				cur.tokenStr += ".0";
				cur.subClassif = SubClassif.FLOAT;
				newRes = parse.scan.tokenToResultV(cur);
				return newRes;
			case FLOAT:
				newRes = parse.scan.tokenToResultV(cur);
				return newRes;
			case STRING:
				boolean bDec = false;
                
				while(i < cur.tokenStr.length())
				{
                    //System.out.printf("charAt(%d) = %c\n)", i, cur.tokenStr.charAt(i));
                    if(!Character.isDigit(cur.tokenStr.charAt(i)))
                    {
                        if(cur.tokenStr.charAt(i) == '.')
                        {
                            if(bDec)
                                Utility.utilError("intCoherce", "Found more than one decimal", cur.tokenStr);
                            else
                                bDec = true;
                        }
                        else
                            break;
                    }
					sb.append(cur.tokenStr.charAt(i));
					i++;
				}
                
				cur.tokenStr = sb.toString();
				cur.subClassif = SubClassif.INTEGER;
				newRes = parse.scan.tokenToResultV(cur);
				return newRes;
			case BOOLEAN:
				if(cur.tokenStr.equals("true"))
				{
					cur.tokenStr = "1.0";
					cur.subClassif = SubClassif.INTEGER;
					newRes = parse.scan.tokenToResultV(cur);
					return newRes;
				}
				
				cur.tokenStr = "0.0";
				cur.subClassif = SubClassif.INTEGER;
				newRes = parse.scan.tokenToResultV(cur);
				return newRes;
			default:
				Utility.utilError("floatCoherce", "Not a valid Int type.", cur.tokenStr);
		}
		
		//reachable.
		return newRes;
	}
    
        
        
    public static ResultValue LENGTH(String str)
    {
        ResultValue resNew = new ResultValue();
        int i;
        int count=0;
        for(i = 0; i<str.length(); i++)
            if(str.charAt(i) > 31 && str.charAt(i) < 127)
                count++;
        
        resNew.structure = ClassStruct.PRIMITIVE;
        resNew.type = SubClassif.INTEGER;
        resNew.value = Integer.toString(count);
        
        return resNew;
    }
    
    /**
     * Counts the number of spaces in a string
     * and returns a ResultValue of integer type.
     * @param str String to count spaces
     * @return resNew ResultValue containing the count of spaces.
     */
    public static ResultValue SPACES(String str)
    {
        ResultValue resNew = new ResultValue();
        int i;
        int count=0;
        for(i = 0; i<str.length(); i++)
            if(str.charAt(i) == ' ')
                count++;
        
        resNew.structure = ClassStruct.PRIMITIVE;
        resNew.type = SubClassif.INTEGER;
        resNew.value = Integer.toString(count);
        
        return resNew;
    }
    
    
    
    
    public static ResultValue getCopy(ResultValue value)
    {
        ResultValue resNew = new ResultValue();
        
        resNew.debugONoff = value.debugONoff;
        resNew.structure = value.structure;
        resNew.terminatingStr = value.terminatingStr;
        resNew.type = value.type;
        resNew.value = value.value;
        resNew.arraySize = value.arraySize;
        
        if(value.structure == ClassStruct.FIXED_ARRAY)
        {
            if(value.arrayVals != null)
            {
                resNew.arrayVals = new ArrayList<ResultValue>();
                for(int i=0; i<value.arrayVals.size(); i++)
                {
                    resNew.arrayVals.add(i, value.arrayVals.get(i));
                }
            }
        }
        
        return resNew;
    }
    
    public static ResultValue stringCoherce(Parser parse, ResultValue value) throws Error
    {
        
        switch(value.type)
        {
            case INTEGER:
                value.type = SubClassif.STRING;
                value.value = Integer.toString(Integer.valueOf(value.value));
                break;
            case FLOAT:
                value.type = SubClassif.STRING;
                value.value = Double.toString(Double.valueOf(value.value));
                break;
            case STRING:
                break;
            case BOOLEAN:
                value.type = SubClassif.STRING;
                if(value.value.equals("true"))
                    value.value = "T";
                else
                    value.value = "F";
                
                break;
            default:
                Utility.utilError("stringCoherce", "'" + value.value + "' is not a valid data type to coerced int to String");
        }
        
        return value;
    }

    static ResultValue strConcat(Parser aThis, ResultValue valueL, ResultValue valueR) throws Error 
    {
        ResultValue resNew = new ResultValue();
        
        
        if(valueL.type != SubClassif.STRING)
            Utility.utilError("strConcat", "Expecting String with '#' operator.", valueL.value);
        
        if(valueR.type != SubClassif.STRING)
            Utility.utilError("strConcat", "Expecting String with '#' operator.", valueL.value);
        
       
        
        resNew.structure = ClassStruct.PRIMITIVE;
        resNew.type = SubClassif.STRING;
        resNew.value = valueL.value + valueR.value;
        
        return resNew;
    }

    /**
     *  Gets a token from a string using
     *  a delimiter. Increments the index of the
     *  string cursor, and stores the new subString
     *  int resKey.value.
     * 
     * @param str
     * @param delim
     * @param iStart
     * @param resKey
     * @return 
     */
    public static int getToken(String str, String delim, int iStart, ResultValue resKey) 
    {
        
        
        int i;
        int count = 0;

        for(i=iStart; i<str.length()-(delim.length()-1); i++)
        {
            if(str.substring(i, i+delim.length()).equals(delim))
                break;
            
            count++;
        }
        
        
        if(iStart == str.length()-(delim.length()-1))
            resKey.value = str.substring(iStart);
        else
            resKey.value = str.substring(iStart, i);
        
        if(count==0)
            iStart += delim.length();
        else
            iStart += count;
        
        return iStart;
    }

}
