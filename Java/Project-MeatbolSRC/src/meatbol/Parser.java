/**
 * Parser.java
 * 
 * Purpose:
 *      To use the scanner to parse
 *      through a file, and validate
 *      and execute the contents of
 *      the file assuming the file is
 *      Meatbol source code.
 * 
 * Authors: William, Katherine, Luis
 */
package meatbol;

import enums.ClassStruct;
import enums.Classif;
import enums.SubClassif;
import static enums.SubClassif.INTEGER;
import static enums.SubClassif.OFFdbug;
import static enums.SubClassif.ONdbug;
import java.util.ArrayList;
import java.util.Stack;
import java.lang.String;
import java.util.logging.Level;
import java.util.logging.Logger;
import statement.STEntry;
import statement.STIdentifier;

/**
 * Analyzes language sentences giving a structural representation of the source
 * while checking for correct syntax based on a grammar
 * 
 * Also responsible for error checking during translation
 */
public class Parser 
{
    
    public Scanner scan;
    private char charLookAhead;
    private boolean bExec;
    
    public StorageMgr storage;
    public BuiltInFunctions functions;
    
    /**
     * Assuming cursor is at the start of the line. No grab yet.
     */
    public Parser(Scanner scan) 
    {
        this.scan = scan;
        this.storage = new StorageMgr();
        this.functions = new BuiltInFunctions(scan, this);
        this.bExec = true;
    }
    
    /**
     * Determines what kind of statement it is
     * and calls the specific routine
     * and will continue until there is no 
     * more file.
     * <p>
     * 
     * Should never see a separator here
     * 
     * Should start at the beginning of a statement
     * 
     * 
     * 
     * @throws Exception 
     */
    public ResultValue exec(boolean bExec) throws Error
    {
        ResultValue resTemp = new ResultValue();
        switch(this.scan.currentToken.primClassif)
        {
     
            case CONTROL: //declear, flows, 
                
                //if the token is a end token
                if(this.scan.currentToken.subClassif == SubClassif.END)
                {
                    resTemp.terminatingStr = this.scan.currentToken.tokenStr;
                    resTemp.type = SubClassif.END;
                    return resTemp;
                }
                
                //if it is a declare statement.
                if(scan.currentToken.subClassif == SubClassif.DECLARE)
                {
                    if(this.storage.checkKey(this, this.scan.nextToken.tokenStr))
                    {
                        
                        this.scan.errorWithCurrent("'" + this.scan.nextToken.tokenStr + "' has already been declared.");
                    }
                    resTemp = declareStmt(bExec);
                    
                }//type of flow statement.
                else if(scan.currentToken.subClassif == SubClassif.FLOW)
                {
                    switch(scan.currentToken.tokenStr)
                    {
                        case "if":
                            resTemp = ifStmt(bExec);
                            break;
                        case "while":
                            resTemp = whileStmt(bExec);
                            break;
                        case "for":
                            resTemp = forStmt(bExec);
                            break;
                        case "select":
                            
                            resTemp = selectStmt(bExec);
                            
                            break;
                        default:
                            String msg = String.format("Expecting a Control, Function, or Operand. Recieved \'%s\' subClassif = %s\n", this.scan.currentToken.tokenStr
                                            ,this.scan.currentToken.subClassif);
                            
                        scan.errorWithCurrent(msg);
                    }
                }
                break;
                
            case FUNCTION: //built in and user
                switch(this.scan.currentToken.tokenStr)
                {
                    case "print":
                        resTemp = functions.print(bExec);
                        break;
                    default:
                        scan.errorWithCurrent("Functions call doesn't exists with the built in library");
                }
                break;
                
            case OPERAND: //assignment statements
                
                resTemp = assignmentStmt(bExec);

                if(!this.scan.curIsSemi())
                    this.scan.errorWithCurrent("Expecting a ';'");

                //takes the ';' off current.
                scan.getToken();
                
                if(this.scan.currentToken.subClassif == SubClassif.END)
                    resTemp.terminatingStr = this.scan.currentToken.tokenStr;
        
                break;
                
            case DEBUG:   // Handle debug control operands
                resTemp = debugStmt(bExec);
                
                if(!this.scan.curIsSemi())
                    this.scan.errorWithCurrent("Expecting a ';'");
                
                scan.getToken();
                
                if(this.scan.currentToken.subClassif == SubClassif.END)
                    resTemp.terminatingStr = this.scan.currentToken.tokenStr;
                break;
            
                
            default:          // Either a comment or invalid operand
                switch(this.scan.currentToken.tokenStr)
                {
                    case "/":
                        scan.errorWithCurrent("expecting another '/' to start the comments");
                    default:
                        String msg = String.format("Expecting a Control, Function, or Operand. Recieved \'%s\' subClassif = %s\n", this.scan.currentToken.tokenStr
                                            ,this.scan.currentToken.subClassif);
                            
                        scan.errorWithCurrent(msg);
                }
        }
        return resTemp;
    }
    
    /**
     * declareStmt
     * Purpose:
     *      to declare a variable.
     * 
     * @param bExec boolean to tell us if we are executing
     *              or if we are just checking syntax.
     * 
     * Assume: The cursor is on the declaration type.
     *
     * Example:    Int two; Int a = 2 * "3";
     */
    
    public ResultValue declareStmt(boolean bExec) throws Error
    {
        /******currentToken should be on a "Declare" SubClassif of a variable*******/
        if (scan.currentToken.subClassif != SubClassif.DECLARE)
            this.scan.errorWithCurrent("Expecting a declaration. Recieved: " + this.scan.currentToken.subClassif );
            
        if(this.scan.nextToken.subClassif != SubClassif.IDENTIFIER)
            this.scan.errorWithNext("Expecting an Identifier");
        
        //key = the operand's identifier.
        String key = scan.nextToken.tokenStr;
        
        ResultValue resReturn = new ResultValue();
       
        int iSaveLineNr = this.scan.currentToken.iSourceLineNr; 
    
        Token tk;   
        switch (scan.currentToken.tokenStr)
        {
            case "Int":
                //loades the identifier
                scan.getToken();
                //creates a copy of the identifier token
                tk = this.scan.currentToken.copyToken();
                
                
                //changes its SubClassif to the Declared datatype.
                tk.subClassif = SubClassif.INTEGER;
                
                if(this.scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                    this.scan.errorWithCurrent("Expecting a variable name after decleration 'Int'");
                
                //if we are declaring a fixed array, return the arrayAssign routine.
                if(scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
                {
                    ResultValue resInit = new ResultValue();
        
                    resInit.structure = ClassStruct.FIXED_ARRAY;
                    resInit.currentSize = 0;
                    resInit.arrayVals = new ArrayList<ResultValue>();
                    resInit.arraySize=0;
                    resInit.type = tk.subClassif;
                    
                    //storing initial values for an empty array
                    this.storage.putValue(this, key,resInit);
                    
                    //currentToken is on the variable, tk is the variable but with the correct data type
                    return arrayAssign(bExec, tk, iSaveLineNr);
                }
                
                //store a empty value for it.
                storage.putValue(this, scan.currentToken.tokenStr, scan.currentToken.primClassif, SubClassif.INTEGER, "");

                if(this.scan.nextToken.tokenStr.charAt(0) == ';')
                {
                    //loads the ';' into currentToken
                    this.scan.getToken();
                    //loads the beggining of the next statement.
                    this.scan.getToken();
                    return storage.getValue(this,key );
                }

                //if the next token is not an operator error
                if(this.scan.nextToken.primClassif != Classif.OPERATOR)
                    this.scan.errorWithCurrent("Expecting a ';'");
                
                resReturn = assignmentStmt(bExec);
                
                if(!this.scan.currentToken.tokenStr.equals(";"))
                    scan.errorWithCurrent("expectd a ';' Recieved Token.");
                
                //loads the next token after the ';'
                scan.getToken();
                break;
                
            case "Float":
                //loades the identifier
                scan.getToken();
               
                tk = this.scan.currentToken.copyToken();
                tk.subClassif = SubClassif.FLOAT;
                
                //if it is not an identifier
                if(this.scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                    this.scan.errorWithCurrent("Expecting Identifier. Recieve: " + this.scan.currentToken.subClassif);
                
                //if we are declaring a fixed array, return the arrayAssign routine.
                if(scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
                {
                    ResultValue resInit = new ResultValue();
        
                    resInit.structure = ClassStruct.FIXED_ARRAY;
                    resInit.currentSize = 0;
                    resInit.arrayVals = new ArrayList<ResultValue>();
                    resInit.arraySize=0;
                    resInit.type = tk.subClassif;
                    
                    //storing initial values for an empty array
                    this.storage.putValue(this, key,resInit);
                    
                    //currentToken is on the variable, tk is the variable but with the correct data type
                    return arrayAssign(bExec, tk, iSaveLineNr);
                }
                
                //store a empty value for it.
                storage.putValue(this, scan.currentToken.tokenStr, scan.currentToken.primClassif, SubClassif.FLOAT, "");

                //if there is no assignment statement
                if(this.scan.nextToken.tokenStr.equals(";"))
                {
                    //loads the ';' into currentToken
                    this.scan.getToken();
                    //loads the beggining of the next statement.
                    this.scan.getToken();
                    
                    return storage.getValue(this,key );
                }
                //if the next token is not a operator error
                if(this.scan.nextToken.primClassif != Classif.OPERATOR)
                    this.scan.errorWithNext("Expecting an operator");

                //do the assignment statement.
                resReturn = assignmentStmt(bExec);
                
                if(!this.scan.curIsSemi())
                    this.scan.errorWithCurrent("Expecting a ';'");
                //skip of ';'
                scan.getToken();
                break;
                
            case "Bool":
                //loades the identifier
                scan.getToken();
                
                tk = this.scan.currentToken.copyToken();
                tk.subClassif = SubClassif.BOOLEAN;
                
                //if it is not an identifier
                if(this.scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                    this.scan.errorWithCurrent("Expecting Identifier. Recieve: " + this.scan.currentToken.subClassif);

                //if we are declaring a fixed array, return the arrayAssign routine.
                if(scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
                {
                    ResultValue resInit = new ResultValue();
        
                    resInit.structure = ClassStruct.FIXED_ARRAY;
                    resInit.currentSize = 0;
                    resInit.arrayVals = new ArrayList<ResultValue>();
                    resInit.arraySize=0;
                    resInit.type = tk.subClassif;
                    
                    //storing initial values for an empty array
                    this.storage.putValue(this, key,resInit);
                    
                    //currentToken is on the variable, tk is the variable but with the correct data type
                    return arrayAssign(bExec, tk, iSaveLineNr);
                }
                
                //store a empty value for it.
                storage.putValue(this, scan.currentToken.tokenStr, scan.currentToken.primClassif, SubClassif.BOOLEAN, "");
                
                //if there is no assignment statement
                if(this.scan.nextToken.tokenStr.charAt(0) == ';')
                {
                    //loads the ';' into currentToken
                    this.scan.getToken();
                    //loads the beggining of the next statement.
                    this.scan.getToken();
                    
                    return storage.getValue(this,key );
                }

                //if the next token is not a operator error
                if(this.scan.nextToken.primClassif != Classif.OPERATOR)
                    this.scan.errorWithNext("Expecting an operator");

                //do the assignment statement.
                assignmentStmt(bExec);
                
                if(!this.scan.curIsSemi())
                    this.scan.errorWithCurrent("Expecting ';' after declaring '" + key + "'");
                
                this.scan.getToken();
                
                break;
                
            case "String":
                //loades the identifier
                scan.getToken();

                tk = this.scan.currentToken.copyToken();
                tk.subClassif = SubClassif.STRING;
        
                //if it is not an identifier
                if(this.scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                    this.scan.errorWithCurrent("Expecting Identifier. Recieve: " + this.scan.currentToken.subClassif);
                
                //if we are declaring a fixed array, return the arrayAssign routine.
                if(scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
                {
                    ResultValue resInit = new ResultValue();
        
                    resInit.structure = ClassStruct.FIXED_ARRAY;
                    resInit.currentSize = 0;
                    resInit.arrayVals = new ArrayList<ResultValue>();
                    resInit.arraySize=0;
                    resInit.type = tk.subClassif;
                    
                    //storing initial values for an empty array
                    this.storage.putValue(this, key,resInit);
                    
                    //currentToken is on the variable, tk is the variable but with the correct data type
                    return arrayAssign(bExec, tk, iSaveLineNr);
                }
                
                //store a empty value for it.
                storage.putValue(this, scan.currentToken.tokenStr, scan.currentToken.primClassif, SubClassif.STRING, "");

                //if there is no assignment statement
                if(this.scan.nextToken.tokenStr.charAt(0) == ';')
                {
                    //loads the ';' into currentToken
                    this.scan.getToken();
                    //loads the beggining of the next statement.
                    this.scan.getToken();
                    return storage.getValue(this,key );
                }

                //if the next token is not a operator error
                if(this.scan.nextToken.primClassif != Classif.OPERATOR)
                    this.scan.errorWithNext("Expecting an '=' operator");

                //do the assignment statement.
                assignmentStmt(bExec);
                if(!this.scan.curIsSemi())
                    this.scan.errorWithCurrent("Expecting a ';'");

                //skip of ';'
                scan.getToken();
                break;
				
			case "Date":
				//loades the identifier
                scan.getToken();

                tk = this.scan.currentToken.copyToken();
                tk.subClassif = SubClassif.DATE;
        
                //if it is not an identifier
                if(this.scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                    this.scan.errorWithCurrent("Expecting Identifier. Recieve: " + this.scan.currentToken.subClassif);
                
                //if we are declaring a fixed array, return the arrayAssign routine.
                if(scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
                {
                    ResultValue resInit = new ResultValue();
        
                    resInit.structure = ClassStruct.FIXED_ARRAY;
                    resInit.currentSize = 0;
                    resInit.arrayVals = new ArrayList<ResultValue>();
                    resInit.arraySize=0;
                    resInit.type = tk.subClassif;
                    
                    //storing initial values for an empty array
                    this.storage.putValue(this, key,resInit);
                    
                    //currentToken is on the variable, tk is the variable but with the correct data type
                    return arrayAssign(bExec, tk, iSaveLineNr);
                }
                
                //store a empty value for it.
                storage.putValue(this, scan.currentToken.tokenStr, scan.currentToken.primClassif, SubClassif.DATE, "");

                //if there is no assignment statement
                if(this.scan.nextToken.tokenStr.charAt(0) == ';')
                {
                    //loads the ';' into currentToken
                    this.scan.getToken();
                    //loads the beggining of the next statement.
                    this.scan.getToken();
                    return storage.getValue(this,key );
                }

                //if the next token is not a operator error
                if(this.scan.nextToken.primClassif != Classif.OPERATOR)
                    this.scan.errorWithNext("Expecting an operator");	

                //do the assignment statement.
                assignmentStmt(bExec);
                if(!this.scan.curIsSemi())
                    this.scan.errorWithCurrent("Expecting a ';'");

                //skip of ';'
                scan.getToken();
                break;
            
            default: //if no case match, not sure what the declaration type is.
                this.scan.errorWithCurrent("Not a valid declaration type.");
        }
        return resReturn;
    }
    
    
    
    /**
     * Assumes currentToken is on the array identifier.
     * @param tk
     * @param iSaveLineNr
     * @return
     * @throws Error 
     */
   public ResultValue arrayAssign(boolean bExec, Token tk, int iSaveLineNr) throws Error
   {
        String key = new String();
        key = scan.currentToken.tokenStr;
        ResultValue arrLength = new ResultValue();
        ArrayList<Token> post = new ArrayList<Token>();
        ResultValue resReturn = new ResultValue();
        
        if(!this.storage.checkKey(this, key))
            this.scan.errorWithCurrent("Array has not been declared.");
        
        if(this.scan.nextToken.tokenStr.equals("]"))
        {

            resReturn = arrayAssignmentExpandable(bExec, tk, iSaveLineNr);
            resReturn.structure = ClassStruct.FIXED_ARRAY;
            
            return resReturn;
        }
        
        post = convertToPostfix("]");
        arrLength = evalPostfixExpression(post);
        ResultValue resR = new ResultValue();
        resR.arraySize = Integer.valueOf(Utility.intCoherce(this, Utility.resValueToToken(this, arrLength)).value);
        resR.bSizeSet = true;
        resR.structure = ClassStruct.FIXED_ARRAY;
        resR.type = tk.subClassif;
        storage.putValue(this, key, resR); //stores an empty array
        
        scan.getToken(); //takes off ']'
                
        if(this.scan.curIsSemi())
        {
            scan.getToken();
            return resR;
        }
                
        resR = arrayAssignment(bExec, tk, iSaveLineNr);
        return resR;
    }
    
    public ResultValue evalPostfixExpression(ArrayList<Token> list) throws Error
    {
        ResultValue resReturn = new ResultValue();
        ResultValue res = new ResultValue();
        Stack<Token> stack = new Stack<Token>();
        Stack tfStack = new Stack();
        boolean currentTF = false;
        ResultValue temp = new ResultValue();
        
        if(list == null)
            return null;
        
        // If list is of length 1, evaluate that variable on its own
        if (list.size() == 1)
        {            
            res = this.scan.tokenToResultV(list.get(0));
            
            if(res.type == SubClassif.BOOLEAN)
                return res;
            
            if(res.type == SubClassif.IDENTIFIER)
                res = this.storage.getValue(this, res.value);
            
            if(this.scan.bShowExpr)
                System.out.printf("\t\t...Expr result is '%s'\n", res.value);
            
            return res;
        }
        // evaluate list of tokens based on postfix expressions
        if(this.scan.bShowExpr)
            System.out.printf("\t\t...Expr " + list.get(0).tokenStr + " ");
        
        int count=-1;
        for (Token tk: list)
        {
            count++;
            //tk.printToken();
            switch (tk.primClassif)
            {
                case OPERAND:      
                    if(tk.classStruct != null
                    && tk.classStruct == ClassStruct.FIXED_ARRAY)
                    {
                        if((count+1) < list.size())
                        {
                            
                            
                            if(list.get(count+1).tokenStr.equals("ELEM"))
                            {
                                stack.push(tk);
                                break;
                            }
                            else if(list.get(count+1).tokenStr.equals("MAXELEM"))
                            {
                                stack.push(tk);
                                break;
                            }
                            else if(list.get(count+1).tokenStr.equals("SPACES"))
                            {
                                 stack.push(tk);
                                 break;
                            }
                            else if(list.get(count+1).tokenStr.equals("LENGTH"))
                            {
                                 stack.push(tk);
                                 break;
                            }
                           
                        }
                            
                        boolean neg = false;
                        Token index = stack.pop();
                        ResultValue resIndex;
                        if(index.subClassif == SubClassif.IDENTIFIER)
                        {
                            resIndex = this.storage.getValue(this, index.tokenStr);
                            index = Utility.resValueToToken(this, resIndex);
                        }
                            
                        ResultValue array = this.storage.getValue(this, tk.tokenStr);
                        
                        
                        if(index.subClassif != SubClassif.INTEGER)
                            error("Expecting an Integer index. Recieved " + index.tokenStr + " of type " + index.subClassif);
                        
                        if(tk.tokenStr.charAt(0) == '-')
                            if(Utility.countUnary(tk.tokenStr) % 2 == 1)
                                neg = true;
                            
                        int iIndex = Integer.valueOf(index.tokenStr);
                        boolean isString = true;
                        ResultValue resElem = new ResultValue();
                        //Seperate logic when is a string and a primitive
                        if (array.type == SubClassif.STRING && array.structure == ClassStruct.PRIMITIVE)
                            resElem = array.getArrayElem(iIndex, isString);
                        else
                            resElem = array.getArrayElem(iIndex, !isString);
                        
                        tk = Utility.resValueToToken(this, resElem);
                        
                        if(neg)
                            tk.tokenStr = '-' + tk.tokenStr;
                    }
                    stack.push(tk);
                    break;
                    
                case OPERATOR:
                    if(tk.tokenStr.equals("not"))
                    {
                        Token pop = stack.pop();
                        
                        if(pop.subClassif == SubClassif.IDENTIFIER)
                        {
                            ResultValue resTem = new ResultValue();
                            resTem = this.storage.getValue(this, pop.tokenStr);
                            
                            pop = Utility.resValueToToken(this, resTem);
                        }
                        
                        if(pop.subClassif != SubClassif.BOOLEAN)
                            error("Expecting a Boolean type for the 'not' operator. Recieved: '" + pop.tokenStr + " on line " + pop.iSourceLineNr);
                        
                        if(pop.tokenStr.equals("true"))
                            pop.tokenStr = "false";
                        else
                            pop.tokenStr = "true";
                        
                        stack.push(pop);
                        continue; 
                    }
                    
                    Token operator = tk;
                    Token opRight = stack.pop();
                    Token opLeft = stack.pop();
                    ResultValue valueR = new ResultValue();
                    ResultValue valueL = new ResultValue();
                    Operations ops;
                    
                    if(this.scan.bShowExpr)
                        System.out.print(operator.tokenStr + " " + opRight.tokenStr + " ");
                    
                    switch (operator.tokenStr)
                    {
                        case "+":
                            valueL = handleBasicOperations(opLeft);
                            valueR = handleBasicOperations(opRight);
                            res = Utility.numericAdd(this, valueL, valueR);
                            break;
                            
                        case "-":
                            valueL = handleBasicOperations(opLeft);
                            valueR = handleBasicOperations(opRight);
                            res = Utility.numericSub(this, valueL, valueR);
                            break;
                            
                        case "*":
                            valueL = handleBasicOperations(opLeft);
                            valueR = handleBasicOperations(opRight);
                            res = Utility.numericMult(this, valueL, valueR);
                            break;
                            
                        case "/":
                            valueL = handleBasicOperations(opLeft);
                            valueR = handleBasicOperations(opRight);
                            res = Utility.numericDivide(this, valueL, valueR);
                            break;
                            
                        case "^":
                            valueL = handleBasicOperations(opLeft);
                            valueR = handleBasicOperations(opRight);
                            res = Utility.numericExpon(this, valueL, valueR);
                            break;
                            
                        case "#":
                            valueL = handleBasicOperations(opLeft);
                            valueR = handleBasicOperations(opRight);
                            res = Utility.strConcat(this, valueL, valueR);
                            break;
                            
                        case ">":
                            ops = handleCondOperations(opLeft, opRight, ">");
                            res = Utility.greater(this, ops.valueL, ops.valueR);
                            res.type = SubClassif.BOOLEAN;
                            break;
                            
                        case ">=":
                            ops = handleCondOperations(opLeft, opRight, ">=");
                            res = Utility.greaterEql(this, ops.valueL, ops.valueR);
                            res.type = SubClassif.BOOLEAN;
                            break;
                            
                        case "<":
                            ops = handleCondOperations(opLeft, opRight, "<");
                            res = Utility.less(this, ops.valueL, ops.valueR);
                            res.type = SubClassif.BOOLEAN;
                            break;
                            
                        case "<=":
                            ops = handleCondOperations(opLeft, opRight, "<=");
                            res = Utility.lessEql(this, ops.valueL, ops.valueR);
                            res.type = SubClassif.BOOLEAN;
                            break;
                            
                        case "==":
                            ops = handleCondOperations(opLeft, opRight, "==");
                            res = Utility.equal(this, ops.valueL, ops.valueR);
                            res.type = SubClassif.BOOLEAN;
                            break;
                            
                        case "!=":
                            ops = handleCondOperations(opLeft, opRight, "!=");
                            res = Utility.notEqual(this, ops.valueL, ops.valueR);
                            res.type = SubClassif.BOOLEAN;
                            break;
                        case "and":
                            if(opLeft.tokenStr.equals("true") && opRight.tokenStr.equals("true"))
                                res.value = "true";
                            else
                                res.value = "false";
                            break;
                        case "or":
                            if(opLeft.subClassif != SubClassif.BOOLEAN)
                                error("or compare on line " + (tk.iSourceLineNr+1) + " is expecting Boolean values to compare. "
                                    + "Recieved: " + opLeft.tokenStr );
                            if(opRight.subClassif != SubClassif.BOOLEAN)
                                    error("or compare on line " + (tk.iSourceLineNr+1) + " is expecting Boolean values to compare. "
                                        + "Recieved: " + opRight.tokenStr );
                            
                            if(opLeft.tokenStr.equals("true") || opRight.tokenStr.equals("true"))
                            {
                                res.value = "true";
                                res.structure = ClassStruct.PRIMITIVE;
                                res.type = SubClassif.BOOLEAN;
                            }    
                            else
                            {
                            
                                res.value = "false";
                                res.structure = ClassStruct.PRIMITIVE;
                                res.type = SubClassif.BOOLEAN;
                            }
                            break;
                        default:
                            return null;
                    }
                    
                    // Convert res back to a token to put in the stack
                    Token combValues = res.toToken();
                    
                    stack.push(combValues);
                    
                    break;
                case FUNCTION:
                    switch(tk.tokenStr)
                    {
                        case "ELEM":
                            stack = functions.elem(stack, tk);
                            break;
                            
                        case "LENGTH":
                            stack = functions.length(stack, tk);
                            break;
                            
                        case "MAXELEM":
                            stack = functions.maxelem(stack, tk);
                            break;
                            
                        case "SPACES":
                            stack = functions.spaces(stack, tk);
                            break;
							
                        case "dateDiff":
                            stack = functions.dateDiff(stack, tk);
                            break;
							
                        case "dateAdj":
                            stack = functions.dateAdj(stack, tk);
                            break;
							
                        case "dateAge":
                            stack = functions.dateAge(stack, tk);
                            break;
							
                        default:
                            error("Function '" + tk.tokenStr + "' has not been declared.");
                    }
                    break;
                default:
                    scan.errorWithCurrent("Token primary classification couldn't be identified");
            }
        }
        // At this point only one token is in the stack
        Token resTk = stack.pop();
        ResultValue resFinal = this.scan.tokenToResultV(resTk);
        
        if(resFinal.value.equals(""))
            return resFinal;
        
        if(resFinal.type == SubClassif.BOOLEAN)
            return resFinal;
        
        if(resTk.tokenStr.charAt(0) == '-')
            if(resTk.subClassif == SubClassif.IDENTIFIER)
            {
                ResultValue trueValue = this.storage.getValue(this, resTk.tokenStr);
                        
                trueValue.value = '-' + trueValue.value;
                
                
                if(this.scan.bShowExpr)
                    System.out.println(" = " + trueValue.value );
                
                return trueValue;
            }
                
        if(this.scan.bShowExpr)
            System.out.println(" = " + res.value );
        
        return resFinal;
    }
    
    /**
     * handleBasicOperations(Token operand)
     * Handles checking for if the operand passed in is in the storage manager
     * and converts the token into a result value if ti exists
     * @param operand    Token to convert to result value
     * @return   ResultValue     ResultValue after convertion
     * @throws Error  
     */
    public ResultValue handleBasicOperations(Token operand) throws Error {
        ResultValue temp;
        //opLeft is an identifier get its true value
        if(this.storage.checkKey(this,operand.tokenStr))
        {
            temp = this.storage.getValue(this, operand.tokenStr);
            operand.tokenStr = temp.value;
            operand.subClassif = temp.type;
        }
        return this.scan.tokenToResultV(operand);
    }
    
    /**
     * handleCondOperations(Token opLeft, Token opRight, String opeartionCond)
     * This functions is used to handle condition operations between 2 variables.
     * Takes in Integers, floats, booleans, and string
     * 
     * @param opLeft   Left operand of the evaluation
     * @param opRight  Right operand of the evaluation
     * @param operationCond   Type of operation used for error printing to user
     * @return    Operations object containing 2 results values
     *          ResultValue valueL
     *          ResultValue valueR
     * Used as a container to return 2 value at once
     * @throws Error    if the operant on the left operand sub-classification is unidentified
     */
    public Operations handleCondOperations(Token opLeft, Token opRight, String operationCond) throws Error {
        ResultValue temp;
        ResultValue valueL = new ResultValue();
        ResultValue valueR = new ResultValue();
        Operations res = new Operations();
        
        //opLeft is an identifier get its true value
        if(this.storage.checkKey(this,opLeft.tokenStr))
        {
            temp = this.storage.getValue(this, opLeft.tokenStr);
            opLeft.tokenStr = temp.value;
            opLeft.subClassif = temp.type;
        }
        //opRight is an identifier get its true value
        if(this.storage.checkKey(this,opRight.tokenStr))
         {
            temp = this.storage.getValue(this, opRight.tokenStr);
            opRight.tokenStr = temp.value;
            opRight.subClassif = temp.type;
        }

        switch(opLeft.subClassif)
        {
            case INTEGER:
                valueL = this.scan.tokenToResultV(opLeft);
                valueR = Utility.intCoherce(this, opRight);
                break;
            case FLOAT:
                valueL = this.scan.tokenToResultV(opLeft);
                valueR = Utility.floatCoherce(this, opRight);
                break;
            case BOOLEAN:
                valueL = Utility.intCoherce(this, opLeft);
                valueR = Utility.intCoherce(this, opRight);
                break;
            case STRING: //evaluates string based off of their string lengths. 
                //one == two = true, one != two = false
                valueL = this.scan.tokenToResultV(opLeft);
                valueR = this.scan.tokenToResultV(opRight);
                break;
            default:
                String errStr = "Not a valid operand for " + operationCond + ": " 
                        + opLeft.tokenStr + " " 
                        + opLeft.subClassif;
                error(errStr);
        }
        res.setValueL(valueL);
        res.setValueR(valueR);
        return res;
    }
    
    
    /*
     *Assume the current token is on the flow token
     *
     */
    
    public ResultValue evalCond() throws Error
    {
       ArrayList<Token> out = new ArrayList<Token>();
       ResultValue newRes = new ResultValue(); 
       int iSaveLine = this.scan.currentToken.iSourceLineNr;
       
       if(this.scan.currentToken.subClassif == SubClassif.DECLARE)
           out = convertToPostfix(";");
       else //must be flow.
           out = convertToPostfix(":");
      
       newRes = evalPostfixExpression(out);
       
       return newRes;
    }
    
    public ResultValue evalSingleExpr(String delim) throws Error
    {
       ArrayList<Token> out = new ArrayList<Token>();
       ResultValue newRes = new ResultValue(); 
       int iSaveLine = this.scan.currentToken.iSourceLineNr;
       
        out = convertToPostfix(delim);
      
       newRes = evalPostfixExpression(out);
       
       return newRes;
    }
    
    

    public ResultValue ifStmt(boolean bExec) throws Error
    {
        int saveLineNr = scan.currentToken.iSourceLineNr;
        ResultValue resReturn = new ResultValue();
        ResultValue resTemp = new ResultValue();

       if(bExec)
        {
            ResultValue resCond = evalCond();

            if(resCond == null)
                error("Expecting a ':' after condition for 'if' on line " + (saveLineNr+1));

           if(this.scan.bShowStmt)
                this.scan.debugMessage("if on line " + (saveLineNr+1) + " is " + resCond.value);

            if(resCond.value.equals("true"))
            {
                scan.getToken();
                //start executing true statements.
                while(this.scan.currentToken.subClassif != SubClassif.END)
                {
                    resTemp = exec(true);
                    if(resTemp.terminatingStr != null)
                        break;
                }
                if(this.scan.currentToken.tokenStr.equals("END"))
                    return resTemp;

                
                if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                {
                    

                    
                    while(!this.scan.curIsValue("endif"))
                    {
                        
                        breakRoutine();
                        
                        if(this.scan.curIsValue("else"))
                        {
                            this.scan.getToken();
                            
                            if(!this.scan.curIsColon())
                                this.scan.errorWithCurrent("Expecting a ':' after the 'else'");
                            
                            this.scan.getToken();
                            
                            continue;
                        }
                        
                        if(this.scan.curIsValue("continue"))
                        {
                            this.scan.getToken();
                            
                            if(!this.scan.curIsSemi())
                                this.scan.errorWithCurrent("Expecting a ';' after the 'continue'");
                            
                            this.scan.getToken();
                            
                            continue;
                        }
                    }
                    
                    this.scan.getToken();
                    
                    if(!this.scan.curIsSemi())
                        this.scan.errorWithCurrent("Expecting a ';' after the 'endif'");
                    
                    this.scan.getToken();
                    
                    
                    
                    resTemp.terminatingStr = "break";
                    
                    return resTemp;
                }
                
                
                if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                {
                    resTemp.terminatingStr = "continue";
                    breakContinue();
                    
                    if(this.scan.curIsValue("else"))
                    {
                        this.scan.getToken();
                        
                        if(!this.scan.curIsColon())
                            this.scan.errorWithCurrent("Expecting ':' after 'else'");
                        
                        this.scan.getToken();
                        
                        breakContinue();
                    }
                }
                
                if(this.scan.currentToken.tokenStr.equals("else"))
                {
                    if(!this.scan.nextToken.tokenStr.equals(":"))
                        this.scan.errorWithCurrent("expected a ':'");

                    scan.getToken();
                    scan.getToken();

                    while(this.scan.currentToken.subClassif != SubClassif.END)
                        resTemp = exec(false);
                    

                
                    if(this.scan.curIsValue("continue"))
                        continueRoutine();
                    
                    if(this.scan.curIsValue("break"))
                        breakRoutine();
                    
                }
                
                
                
                

                if(!this.scan.currentToken.tokenStr.equals("endif"))
                {
                    error("expected 'endif' for the 'if' beginning on line " + saveLineNr + ", found " 
                        + this.scan.currentToken.tokenStr);
                }

                //gets endif off the currentToken
                this.scan.getToken();

                if(!this.scan.curIsSemi())
                    error("expected a ';' after the 'endif' for the 'if' on line " + saveLineNr );

                //skip over colon.
                this.scan.getToken();

                resReturn = resTemp;
            }
            else     //Condition returned false, ignore execution
            { 
                if(!this.scan.curIsColon())
                    this.scan.errorWithCurrent("expected a ':' on Line");

                //loads the colone off current.
                scan.getToken();

                while(this.scan.currentToken.subClassif != SubClassif.END)
                    resTemp = exec(false); //is changing value somewhere

                //if there are no more lines to execute.
                if(this.scan.currentToken.tokenStr.equals("END"))
                    return resTemp;
                
                

                
                
                if(this.scan.curIsValue("break"))
                    breakRoutine();
                
                if(this.scan.curIsValue("continue"))
                    continueRoutine();
                
                //if there was an else
                if(this.scan.currentToken.tokenStr.equals("else"))
                {
                    this.scan.getToken();
                    if(!this.scan.currentToken.tokenStr.equals(":"))
                            scan.errorWithCurrent("expected ':' after 'else'");
                    //gets the ':' off current
                    scan.getToken();
                    //now execute the eles's true statements
                    while(this.scan.currentToken.subClassif != SubClassif.END)
                        resTemp = exec(true);
                    
                    if(this.scan.curIsValue("break"))
                    {
                        breakRoutine();
                        resTemp.terminatingStr = "break";
                    }
                    if(this.scan.curIsValue("continue"))
                    {
                        continueRoutine();
                        resTemp.terminatingStr = "continue";
                    }
                }
                    

                if(!this.scan.currentToken.tokenStr.equals("endif"))
                    error("expected 'endif' for the 'if' beginning on line " + saveLineNr + ", found " 
                    + this.scan.currentToken.tokenStr);

                    scan.getToken();
                    if(!this.scan.curIsSemi())
                        scan.errorWithCurrent("expect ';' after 'endif'");
                    //get the ':' off current
                    scan.getToken();

                resReturn = resTemp;
            }
        }
       else //not executing
        {
            this.scan.skipColon();
            //get the next token after the colon.
            this.scan.getToken();

            while(this.scan.currentToken.subClassif != SubClassif.END)
                resTemp = exec(false);

            if(this.scan.currentToken.tokenStr.equals("else"))
            {
                scan.getToken();
                if(!this.scan.curIsColon())
                    scan.errorWithCurrent("expected 'endif' for the begginning on line " + saveLineNr 
                                                                + ", found " + this.scan.currentToken.tokenStr);

                scan.getToken();
                
                while(this.scan.currentToken.subClassif != SubClassif.END)
                    resTemp = exec(false);
            }
            
            if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
            {
                while(!this.scan.curIsValue("endif"))
                {
                    breakRoutine();
                    
                    if(this.scan.curIsValue("continue"))
                    {
                        this.scan.getToken();
                        
                        if(!this.scan.curIsSemi())
                            this.scan.errorWithCurrent("Expecting a ';' after 'continue'");
                        
                        this.scan.getToken();
                        continue;
                    }
                    else if(this.scan.curIsValue("else"))
                    {
                        this.scan.getToken();

                        if(!this.scan.curIsColon())
                            this.scan.errorWithCurrent("Expecting a ';' after 'else'");

                        this.scan.getToken();
                        continue;
                    }
                    else
                        break;
                }
                
            }
            
            if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
            {
                while(!this.scan.curIsValue("endif"))
                {
                    continueRoutine();
                    
                    if(this.scan.curIsValue("break"))
                        breakRoutine();
                    else if(this.scan.curIsValue("else"))
                    {
                        this.scan.getToken();

                        if(!this.scan.curIsColon())
                            this.scan.errorWithCurrent("Expecting a ';' after 'else'");

                        this.scan.getToken();
                        continue;
                    }
                    else
                        break;
                }
            }

            if(!this.scan.currentToken.tokenStr.equals("endif"))
                scan.errorWithCurrent("expected 'endif' for the begginning on line " + saveLineNr 
                                                                    + ", found " + this.scan.currentToken.tokenStr);
            scan.getToken();
            if(!this.scan.curIsSemi())
                scan.errorWithCurrent("expect ‘;’ after ‘endif’");
            //skip over colon.
            scan.getToken();
            
            resReturn = resTemp;
        }
        
        return resReturn;
    }
    
    
    
    public ResultValue whileStmt(boolean bExec) throws Error
    {
            int saveLineNr = scan.currentToken.iSourceLineNr;
            int saveColPos = this.scan.currentToken.iColPos;
            int endLineNr = 0;
            int endColPos = 0;
            
            ResultValue resReturn = new ResultValue();
            ResultValue resTemp = new ResultValue();
            
            if(bExec)
            {
                ResultValue resCond = evalCond();
                
                if(resCond == null || !this.scan.curIsColon())
                    error("Expecting a ':' after condition for 'while' on line " + (saveLineNr+1));
                
                if(resCond.type != SubClassif.BOOLEAN)
                    error("EvalCond on line " + saveLineNr + " resultvalue is not of true or false");
                
                if(this.scan.bShowStmt)
                            this.scan.debugMessage("while on line " + (saveLineNr+1) + " is " + resCond.value);

                if(resCond.value.equals("true"))
                {
                    while(true)
                    {
                        //gets ':' off the currentToken
                        scan.getToken();
                        //execute statements until we find an end subclass token.
                        while(this.scan.currentToken.subClassif != SubClassif.END)
                        {
                            resTemp = exec(true);
                            if(resTemp.terminatingStr != null)
                                if(resTemp.terminatingStr.equals("break") || resTemp.terminatingStr.equals("continue"))
                                    break;
                        }
                        
                        
                            if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                            {
                                    breakRoutine();
                                    
                                    
                                if(!this.scan.currentToken.tokenStr.equals("endwhile"))
                                error("Expecting 'endwhile' for the condition on line " + saveLineNr);

                                this.scan.getToken();

                                if(!this.scan.curIsSemi())
                                    this.scan.errorWithCurrent("Expecting a ';' after the 'endwhile'");

                                this.scan.getToken();

                                return resTemp;
                                    
                            }
                            
                            
                            if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                            {
                                continueRoutine();
                                
                                if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                                    breakRoutine();
                                 
                            }
                        
                        
                        
                        
                        if(!this.scan.currentToken.tokenStr.equals("endwhile"))
                            this.scan.errorWithCurrent("Expecting 'endwhile' for the condition on line " + saveLineNr);
                        
                        endLineNr = this.scan.currentToken.iSourceLineNr;
                        endColPos = this.scan.currentToken.iColPos;
                        
                        //set it to the while cond
                        this.scan.setPosition(0, saveLineNr);
                        //this.scan.getToken();
                        
                        //evaluates the new condition
                        resCond = evalCond();

                        if(resCond == null)
                           error("Expecting a ':' after condition for 'while' on line " + (saveLineNr+1));

                        //makes sure the return is of T or F
                        if(resCond.type != SubClassif.BOOLEAN)
                           error("EvalCond on line " + saveLineNr + " resultvalue is not of true or false\n" 
                           + "Found: " + resCond.value);


                        if(this.scan.bShowStmt)
                           this.scan.debugMessage("while on line " + (saveLineNr+1) + " is " + resCond.value);


                        if(resCond.value.equals("false"))
                            break; //break out of loop
                         
                    }
                        //jump back down to the endwhile
                        this.scan.setPosition(endColPos, endLineNr);

                        if(!this.scan.curIsSemi())
                           error("Expecting a ';' after 'endwhile' on line " + (endLineNr+1) + ".");

                        this.scan.getToken();
                        
                        return resCond;
                    }
                    else//Condition returned false, ignore execution
                    { 
                        if(!this.scan.curIsColon())
                            error("Expecting ':' at the end of condition for 'while' on line " + saveLineNr);
                     
                        //loads the colone off current.
                        scan.getToken();

                        while(this.scan.currentToken.subClassif != SubClassif.END)
                            resTemp = exec(false);

                        //if there are no more lines to execute.
                        if(this.scan.currentToken.tokenStr.equals("END"))
                            return resTemp;

                        if(!this.scan.currentToken.tokenStr.equals("endwhile"))
                            error("expected 'endwhile' for the 'while' beginning on line " + saveLineNr + ", found " 
                            + this.scan.currentToken.tokenStr);

                        scan.getToken();

                        if(!this.scan.curIsSemi())
                            scan.errorWithCurrent("expect ‘;’ after ‘endwhile’");
                        //get the ';' off current
                        scan.getToken();

                        resReturn = resTemp;
                    }
            }
            else
            {
                this.scan.skipColon();
                //get the next token after the colon.
                this.scan.getToken();
                
                while(this.scan.currentToken.subClassif == SubClassif.END)
                    resTemp = exec(false);
                
                if(!this.scan.currentToken.tokenStr.equals("endwhile"))
                    scan.errorWithCurrent("expected 'endwhile' for the 'while' begginning on line " + saveLineNr 
                                                                        + ", found " + this.scan.currentToken.tokenStr);
                scan.getToken();
                
                if(!this.scan.curIsSemi())
                    scan.errorWithCurrent("expect ‘;’ after ‘endwhile’");
                //skip over semi-colon.
                scan.getToken();
                
                resReturn = resTemp;
            }
            
            return resReturn;
    }
        
        
        
        
    
    
    
    /**
     * Assumes on the 'Identifier' after the 'for'
     * //dont for get to implement the false bExec
     */
    public ResultValue forFromStmt(boolean bExec, int iStartLine, int iStartColPos) throws Error
    {
        ResultValue resString = new ResultValue();
        //int iSaveLine; 
       // int iSaveCol;
        
         if(this.scan.currentToken.subClassif != SubClassif.IDENTIFIER)
             this.scan.errorWithCurrent("Expecting a String Identifier.");
         
         
        Token key = this.scan.getCurrentToken();
        ResultValue resKey = this.storage.getValue(this, key.tokenStr);
        
        //gets the 'from' in current
        //checked previously
        this.scan.getToken();
        
        //gets the String or Identifier to terverse
        this.scan.getToken();
        
        Token keyStr = this.scan.getCurrentToken();
        
        if(keyStr.subClassif == SubClassif.IDENTIFIER)
        {
            resString = this.storage.getValue(this, keyStr.tokenStr);
            
        }
        else
            resString = this.scan.currentToResult(this);
        
        if(resString.type != SubClassif.STRING)
            this.scan.errorWithCurrent("Expecting a String after the 'from'. Recieved token");
        
        //gets the 'by' in the currentToken
        this.scan.getToken();
        
        if(!this.scan.curIsValue("by"))
            this.scan.errorWithCurrent("Expecting Identifier 'by'");
        
        this.scan.getToken();
        
        Token delim = this.scan.getCurrentToken();
        ResultValue resDelim = new ResultValue();
        
        if(delim.subClassif == SubClassif.STRING || delim.subClassif == SubClassif.IDENTIFIER)
        {
            if(delim.subClassif == SubClassif.IDENTIFIER)
            {
                resDelim = this.storage.getValue(this, delim.tokenStr);
            }
            else
                resDelim = this.scan.currentToResult(this);
        }
        else
            this.scan.errorWithCurrent("Expecting a String or String Identifier.");
        
        //loads the ':'  
        this.scan.getToken();
        
        if(!this.scan.curIsColon())
            this.scan.errorWithCurrent("Expecting a ':' after the delimiter" );
        
        //loads the start of the next instruction
        this.scan.getToken();
        
        resDelim.value = Utility.rmNonPrintChars(resDelim.value);
        resString.value = Utility.rmNonPrintChars(resString.value);

        int iCurStart = 0;
        ResultValue resTemp = new ResultValue();
        
        //iSaveLine = this.scan.currentToken.iSourceLineNr;
       // iSaveCol = this.scan.currentToken.iColPos;
        
        while(true)
        {
            
            iCurStart = Utility.getToken(resString.value, resDelim.value, iCurStart, resKey);
            
            

            //End case
            if(iCurStart >= resString.value.length())
            {
                if(resKey.value.equals(""))    
                    while(this.scan.currentToken.subClassif != SubClassif.END)
                        exec(false);
                else
                {
                    this.storage.putValue(this, key.tokenStr,resKey);
                     
                    while(this.scan.currentToken.subClassif != SubClassif.END)
                        exec(true);
                }
                if(this.scan.curIsValue("break")|| this.scan.curIsValue("continue"))
                    breakContinue();
                
                if(!this.scan.curIsValue("endfor"))
                    this.scan.errorWithCurrent("Expecting a 'endfor' for the 'for' on line " + (iStartLine+1) + "'");
                
                this.scan.getToken();
                
                if(!this.scan.curIsSemi())
                    this.scan.errorWithCurrent("Expecting a ';' for the 'endfor'");
                
                this.scan.getToken();
                
                return resTemp;
            }
               
            if(resKey.value.equals(""))
                continue;
            //iCurStart += resDelim.value.length();
            
            //store the new value in the 
            this.storage.putValue(this, key.tokenStr,resKey);
            
            while(this.scan.currentToken.subClassif != SubClassif.END)
                resTemp = exec(true);
            
            
            if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
            {
                    breakContinue();
                    
                    if(!this.scan.curIsValue("endfor"))
                        this.scan.errorWithCurrent("Expecting a 'endfor' for the 'for' on line " + (iStartLine+1));
                    
                    this.scan.getToken();
                    
                    if(!this.scan.curIsSemi())
                        this.scan.errorWithCurrent("Expecting a ';' after the 'endfor'");
                    
                    this.scan.getToken();
                    
                    return resTemp;
            }
            
            if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                breakContinue();
            
            if(!this.scan.curIsValue("endfor"))
                this.scan.errorWithCurrent("Expecting a 'endfor' for the 'for' on line " + (iStartLine+1));

                    
                    
            this.scan.setPosition(iStartColPos, iStartLine);
            this.scan.skipTo(":");
            this.scan.getToken();//':'
            
            
            
            
        }
        
        
    }
    
    
    
    
    
    
        
    /**
     * assumes currentToken is on the variable before 'in'
     * @param bExec
     * @return 
     */
    public ResultValue forInStmt(boolean bExec, int iStartLine, int iStartColPos) throws Error 
    {
        int iEndLine = -1;
        int iEndColPos = -1;
        int iSaveLineNr = this.scan.currentToken.iSourceLineNr;
        boolean checkType = false;
        String arrayKey = new String();

        ResultValue resTemp = new ResultValue();
        ResultValue resReturn = new ResultValue();

        if(!this.scan.nextToken.tokenStr.equals("in"))
            this.scan.errorWithNext("Not a valid for loop decleration. Expecting 'in'");


        if(!bExec)
        {
            this.scan.skipTo(":");
            this.scan.getToken();

            while(this.scan.currentToken.subClassif != SubClassif.END)
                exec(false);

            if(!this.scan.curIsValue("endfor"))
                this.scan.errorWithCurrent("Expecting an 'endfor' for the for loop starting on line " + iStartLine);


            //get the 'endfor' off currentToken.
            this.scan.getToken();

            if(!this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ';' after the 'endfor'");

            //gets the ';' off currentToken
            this.scan.getToken();


            resReturn.type = SubClassif.EMPTY;

            return resReturn;
        }


        //ResultValue resCurrent = new ResultValue();
        
        int iIndex = 0;
        Token key = this.scan.getCurrentToken();

        


        //initilizes the identfier to index 0
        if(!this.storage.checkKey(this, key.tokenStr))
            this.storage.putValue(this, arrayKey, new ResultValue());
 

        //gets the identifier off current.
        this.scan.getToken();

        //gets the 'in' off currentToken.
        this.scan.getToken();



        //currentToken is now on array or string to loop through
        if(this.scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
        {
            ResultValue array = Utility.getCopy(this.storage.getValue(this, this.scan.currentToken.tokenStr));
            arrayKey = this.scan.currentToken.tokenStr;
            
            
            if(array.arrayVals == null || array.arrayVals.size() <= 0)
            { //skip over the for statmement. Array is empty
                this.scan.skipTo(":");
                this.scan.getToken();
                
                while(this.scan.currentToken.subClassif != SubClassif.END)
                    exec(false);
                
                if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                {
                    
                    while(this.scan.curIsValue("break") || this.scan.curIsValue("continue"))
                    {
                        if(this.scan.curIsValue("break"))
                            breakRoutine();
                        else
                            continueRoutine();
                    } 
                }
                
                if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                {
                    
                    while(this.scan.curIsValue("break") || this.scan.curIsValue("continue"))
                    {
                        if(this.scan.curIsValue("break"))
                            breakRoutine();
                        else
                            continueRoutine();
                    } 
                }
                
                if(!this.scan.curIsValue("endfor"))
                    this.scan.errorWithCurrent("Expecting an 'endfor' for the for loop starting on line " + iStartLine);

                this.scan.getToken();
                
                if(!this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ';' after the 'endfor'");
                
                this.scan.getToken();
                return array;
            }

            
            this.scan.getToken();

            if(!this.scan.curIsColon())
                this.scan.errorWithCurrent("Expecting ':' to end the 'for' decleration");
            
            this.scan.getToken();
            
           

            while(true)
            {
                
                  if(iIndex > array.arraySize-1)
                    break;

                if(iIndex > array.arrayVals.size()-1)
                    break;
                
                 //update the new value in the array.
                this.storage.putValue(this, key.tokenStr, array.getArrayElem(iIndex, false));
                
                while(this.scan.currentToken.subClassif != SubClassif.END)
                    if(resTemp.terminatingStr != null)
                        if(resTemp.terminatingStr.equals("break") || resTemp.terminatingStr.equals("continue"))
                            break;
                        else
                            resTemp = exec(true);
                    else
                        resTemp = exec(true);
                
                
                if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                {
                    iIndex = array.arrayVals.size();
                    breakContinue();
                }
                
                if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                {
                   breakContinue();
                   resTemp.terminatingStr = null;
                }

                if(!this.scan.curIsValue("endfor"))
                    this.scan.errorWithCurrent("Expecting an 'endfor' for the for loop starting on line " + iStartLine);

                iEndLine = this.scan.currentToken.iSourceLineNr;
                iEndColPos = this.scan.currentToken.iColPos;
                
                //update the array for any changes.
                array = this.storage.getValue(this, arrayKey);
                
                //completed one iteration, increment the index
                iIndex++;

                
                

                this.scan.setPosition(iStartColPos, iStartLine);
                this.scan.skipTo(":");
                this.scan.getToken();//':'
            }
            
            
            
            this.scan.setPosition(iEndColPos, iEndLine);
            


            if(!this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ';' after the 'endfor'");

            //gets the ';' off currentToken
            this.scan.getToken();
            
            resReturn.type = SubClassif.EMPTY;

            return resReturn;
        }
        return null;
    }

    
    public ResultValue forStmt(boolean bExec) throws Error
    {
        int saveLineNr = scan.currentToken.iSourceLineNr;
        int saveColPos = this.scan.currentToken.iColPos;
        int endLineNr = 0;
        int endColPos = 0;
        
        ResultValue resReturn = new ResultValue();
        ResultValue resTemp = new ResultValue();
        
        
        //Boolean whileTrue = false;
        String curKey = new String();

        //gets the 'for' off
        this.scan.getToken();

        //checks to see if 'for _ in _:' for loop
        if(this.scan.nextToken.tokenStr.equals("in"))
            return forInStmt(bExec, saveLineNr, saveColPos);
        
        if(this.scan.nextToken.tokenStr.equals("from"))
            return forFromStmt(bExec, saveLineNr, saveColPos);

        if(this.scan.currentToken.subClassif != SubClassif.IDENTIFIER)
            this.scan.errorWithCurrent("Expecting an Identifier for the for loop on line " + saveLineNr );

        curKey = this.scan.currentToken.tokenStr;

        if(!this.scan.nextToken.tokenStr.equals("="))
        {
            
            this.scan.errorWithNext("Expecting an '=' for initializing '" + this.scan.currentToken.tokenStr + "' in for loop starting on line " + saveLineNr);
        }
        ResultValue resCurrentValue;
        ResultValue resEndValue;
        ResultValue resIncr = null;
        int iCur = -1;
        double dCur = -1;
        int iEnd = -1;
        double dEnd = -1;
        boolean bInt = true;
        //puts the '=' in the currentToken
        this.scan.getToken();

        //get the initial value
        resCurrentValue = expr("to");
        
        if(!this.scan.curIsValue("to"))
            this.scan.errorWithCurrent("Expecting 'to' after the initilization of '" + curKey + "'");

        //store it
        this.storage.putValue(this, curKey, resCurrentValue);

        switch(resCurrentValue.type)
        {
            case INTEGER:

                Token temp = Utility.resValueToToken(this, resCurrentValue);
                ResultValue resT = Utility.intCoherce(this, temp);

                iCur = Integer.valueOf(Utility.intCoherce(this, Utility.resValueToToken(this, resCurrentValue)).value);
                break;
            case FLOAT:
                bInt = false;
                dCur = Double.valueOf(Utility.floatCoherce(this, Utility.resValueToToken(this, resCurrentValue)).value);
            default:
                this.scan.errorWithLine("Not a valid type. Expected Int or Float. Recieved: " + resCurrentValue.type);

        }

        resEndValue = expr(":");

        if(bInt)
            iEnd = Integer.valueOf(Utility.intCoherce(this, Utility.resValueToToken(this, resEndValue)).value);
        else
            dEnd = Double.valueOf(Utility.floatCoherce(this, Utility.resValueToToken(this, resEndValue)).value);

        if(this.scan.curIsValue("by"))
            resIncr = expr(":");

        if(!this.scan.curIsColon())
            this.scan.errorWithCurrent("Expecting a ':'");

        //gets the colon off
        this.scan.getToken();
        ResultValue resT = new ResultValue();
        
        
        
        while(true)
        {
            while(this.scan.currentToken.subClassif != SubClassif.END)
                if(resT.terminatingStr != null)
                    if(resT.terminatingStr.equals("break") || resT.terminatingStr.equals("continue"))
                        break;
                    else
                    resT = exec(true);
                else
                    resT = exec(true);
             
            if(this.scan.curIsValue("break") || (resT.terminatingStr != null && resT.terminatingStr.equals("break")))
            {
                
                breakContinue();
                resT.terminatingStr = null;
                
                if(!this.scan.curIsValue("endfor"))
                    this.scan.errorWithCurrent("Expecting an 'endfor' for the for loop on line " + (saveLineNr+1));
                
                this.scan.getToken();
                
                if(!this.scan.curIsSemi())
                    this.scan.errorWithCurrent("Expecting a ';' after the 'endwhile'");
                
                this.scan.getToken();
                return resT;
            }
            
            if(this.scan.curIsValue("continue") || (resT.terminatingStr != null && resT.terminatingStr.equals("continue")))
            {
                   breakContinue();
                   resT.terminatingStr = null;
            }

            if(!this.scan.curIsValue("endfor"))
                this.scan.errorWithCurrent("Expecting an 'endfor' for the for loop on line " + (saveLineNr+1));
            
            
            if(resIncr==null)
            {
                if(bInt)
                {
                    iCur++;
                    resCurrentValue.value = Integer.toString(iCur);
                    this.storage.putValue(this, curKey, resCurrentValue);
                }
                else
                {
                    dCur++;
                    resCurrentValue.value = Double.toString(dCur);
                    this.storage.putValue(this, curKey, resCurrentValue);
                }
            }
            else
            {
                if(bInt)
                {
                    iCur+= Integer.valueOf(Utility.intCoherce(this, Utility.resValueToToken(this, resIncr)).value);
                    resCurrentValue.value = Integer.toString(iCur);
                    this.storage.putValue(this, curKey, resCurrentValue);
                }
                else
                {
                    dCur+= Double.valueOf(Utility.floatCoherce(this, Utility.resValueToToken(this, resIncr)).value);
                    resCurrentValue.value = Double.toString(dCur);
                    this.storage.putValue(this, curKey, resCurrentValue);this.storage.putValue(this, curKey, resCurrentValue);
                }
            }

            if(bInt)
            {
                if(iCur == iEnd)
                    break;

                this.scan.setPosition(saveColPos, saveLineNr);
                this.scan.skipTo(":");
                this.scan.getToken();
                continue;
            }
            else
            {
                if(dCur == dEnd)
                    break;

                this.scan.setPosition(saveColPos, saveLineNr);
                this.scan.skipTo(":");
                this.scan.getToken();
                continue;
            }
        }

        //loads the ';'
        this.scan.getToken();

        if(!this.scan.curIsSemi())
            this.scan.errorWithCurrent("Expecting a ';'");

        //get the ';' off currentToken
        this.scan.getToken();
        return resReturn;
    }

    
    public ResultValue arrayAssignment(boolean bExec, Token tk, int iSaveLineNr) throws Error
    {
        ResultValue temp = new ResultValue();
        ResultValue array = this.storage.getValue(this, tk.tokenStr);
        
        if(bExec == false)
        {
            this.scan.skipTo(";");
            this.scan.getToken();
            
            return array;
        }
            
        ArrayList<Token> post = convertToPostfixMulti(";");
        
        if(post == null)
            error("Expecting ';' on line " + (iSaveLineNr+1));

        temp = evalPostfixExpression(post);
        
        switch(tk.subClassif)
        {
            case INTEGER:
                temp = Utility.intCoherce(this, Utility.resValueToToken(this, temp));
                break;
            case FLOAT:
                temp = Utility.floatCoherce(this, Utility.resValueToToken(this, temp));
                break;
            case BOOLEAN:
                //temp = Utility.  .boolCoherce(this, Utility.resValueToToken(this, temp));
                break;
            case STRING:
                //temp = Utility.  floatCoherce(this, Utility.resValueToToken(this, temp));
                break;
            default:
                this.scan.errorWithCurrent("Invalid datatype recieved type of " + temp.type);
        }
        
        temp.structure = ClassStruct.ARRAY_ELEM;
        
        if(array.arrayVals == null)
            array.arrayVals = new ArrayList<ResultValue>();
        
        if(array.arrayVals.size() >= array.arraySize)
            error("On line " + (iSaveLineNr+1) + "array is full out of bounds exception");

        if(temp.type != tk.subClassif)
            error("On line " + (iSaveLineNr+1) + ". \nExpecting the datatype of " + tk.subClassif +
                ". Recieved '" + temp.value + "' of type " + temp.type);
        
        array.putArrayValue(this, Utility.getCopy(temp));
        
        while(this.scan.curIsValue(","))
        {
            post = convertToPostfixMulti(";");

            if(post == null)
                error("Expecting ';' on line " + (iSaveLineNr+1));

            temp = evalPostfixExpression(post);
            switch(tk.subClassif)
            {
                case INTEGER:
                    temp = Utility.intCoherce(this, Utility.resValueToToken(this, temp));
                    break;
                case FLOAT:
                    temp = Utility.floatCoherce(this, Utility.resValueToToken(this, temp));
                    break;
                case BOOLEAN:
                    //temp = Utility.  .boolCoherce(this, Utility.resValueToToken(this, temp));
                    break;
                case STRING:
                    //temp = Utility.  floatCoherce(this, Utility.resValueToToken(this, temp));
                    break;
                default:
                    this.scan.errorWithCurrent("Invalid datatype recieved type of " + temp.type);
            }
        
            temp.structure = ClassStruct.ARRAY_ELEM;

            
            
            //array = this.storage.getValue(this,key);
            if(array.arrayVals.size() >= array.arraySize)
                error("On line " + (iSaveLineNr+1) + "array is full out of bounds exception");

            if(temp.type != tk.subClassif)
                error("Expecting the datatype of " + tk.subClassif + ". Recieved " + temp.value);
            
            array.putArrayValue(this, Utility.getCopy(temp));
        }

        if(!this.scan.curIsSemi())
            this.scan.errorWithCurrent("Expecting ';'");
        
        this.storage.putValue(this, tk.tokenStr, Utility.getCopy(array));

        scan.getToken();

        return Utility.getCopy(array);

    }
    
    public ResultValue arrayAssignmentExpandable(boolean bExec, Token tk, int iSaveLineNr) throws Error
    {
        ResultValue temp = new ResultValue();
        ResultValue array = this.storage.getValue(this, tk.tokenStr);
        
        if(bExec == false)
        {
            this.scan.skipTo(";");
            this.scan.getToken();
            
            return array;
        }
        //loads the ']'
        this.scan.getToken();


        //loads the '='
        this.scan.getToken();

        if(!this.scan.curIsValue("="))
            this.scan.errorWithCurrent("Expecting '=' for array assignment.");
        
        ArrayList<Token> post = convertToPostfixMulti(";");
                
        if(post == null)
            error("Expecting ';' on line " + (iSaveLineNr+1));

        temp = evalPostfixExpression(post);
        
        switch(tk.subClassif)
        {
            case INTEGER:
                temp = Utility.intCoherce(this, Utility.resValueToToken(this, temp));
                break;
            case FLOAT:
                temp = Utility.floatCoherce(this, Utility.resValueToToken(this, temp));
                break;
            case BOOLEAN:
                //temp = Utility.  .boolCoherce(this, Utility.resValueToToken(this, temp));
                break;
            case STRING:
                //temp = Utility.  floatCoherce(this, Utility.resValueToToken(this, temp));
                break;
            default:
                this.scan.errorWithCurrent("Invalid datatype recieved type of " + temp.type);
        }
        
        temp.structure = ClassStruct.ARRAY_ELEM;
        
        if(array.arrayVals == null)
            array.arrayVals = new ArrayList<ResultValue>();
        
        if(array.arrayVals.size() >= array.arraySize)
            array.arraySize++;

        if(temp.type != tk.subClassif)
            error("On line " + (iSaveLineNr+1) + ". \nExpecting the datatype of " + tk.subClassif +
                ". Recieved '" + temp.value + "' of type " + temp.type);
        
        array.putArrayValue(this, temp);
        
        while(this.scan.curIsValue(","))
        {
            post = convertToPostfixMulti(";");

            if(post == null)
                error("Expecting ';' on line " + (iSaveLineNr+1));

            temp = evalPostfixExpression(post);
            switch(tk.subClassif)
            {
                case INTEGER:
                    temp = Utility.intCoherce(this, Utility.resValueToToken(this, temp));
                    break;
                case FLOAT:
                    temp = Utility.floatCoherce(this, Utility.resValueToToken(this, temp));
                    break;
                case BOOLEAN:
                    //temp = Utility.  .boolCoherce(this, Utility.resValueToToken(this, temp));
                    break;
                case STRING:
                    //temp = Utility.  floatCoherce(this, Utility.resValueToToken(this, temp));
                    break;
                default:
                    this.scan.errorWithCurrent("Invalid datatype recieved type of " + temp.type);
            }
        
            temp.structure = ClassStruct.ARRAY_ELEM;

            if(array.arrayVals.size() >= array.arraySize)
                array.arraySize++;

            if(temp.type != tk.subClassif)
                error("Expecting the datatype of " + tk.subClassif + ". Recieved " + temp.value);
            
            array.arrayVals.add(Utility.getCopy(temp));
        }

        if(!this.scan.curIsSemi())
            this.scan.errorWithCurrent("Expecting ';'");
        
                
        array.structure = ClassStruct.FIXED_ARRAY;
        this.storage.putValue(this, tk.tokenStr, array);
        ResultValue res = this.storage.getValue(this, tk.tokenStr);
        scan.getToken();
        
        return Utility.getCopy(array);
    }
    

    
    public ResultValue assignmentStmt(boolean bExec) throws Error
    {
        int iSaveLineNr = this.scan.currentToken.iSourceLineNr;
        int iSaveColPos = this.scan.currentToken.iColPos;
        
        ResultValue resReturn = new ResultValue();
        Token declare = new Token();
                
        String operatorStr;
        
        ResultValue res01 = this.scan.currentToResult(this);
        ResultValue res02 = new ResultValue();

        
        boolean bArray = false;
        boolean bScalar = false;
        ResultValue array = new ResultValue();
        int iIndex = -1;
        
        
        if(bExec == false)
        {
            if(this.scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                this.scan.errorWithCurrent("Expecing an Identifier on the left side of the equation");
            
            declare = this.scan.getCurrentToken();
            
            if(this.scan.currentToken.bArrayBracket)
                this.scan.skipTo("]");
            
            this.scan.getToken();
            
            if(this.scan.currentToken.primClassif != Classif.OPERATOR)
                this.scan.errorWithCurrent("Expecting an Assignment Operator for assignment");
            
            this.scan.getToken();
            
            this.scan.skipTo(";");
            return this.scan.tokenToResultV(declare);
            
        }
        
        
        
        
        if(this.scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
        {
            ResultValue index;
           
            bArray = true;
            declare = this.scan.getCurrentToken();
            array = this.storage.getValue(this, this.scan.currentToken.tokenStr);
     
            if (this.scan.currentToken.bArrayBracket == false)
            {
                int iRevertLine = this.scan.currentToken.iSourceLineNr;
                int iRevertCol = this.scan.currentToken.iColPos;
                
                this.scan.getToken();
                
                if(!this.scan.curIsValue("="))
                    this.scan.errorWithCurrent("Expecting an '=' assignment statemtent for scalers and array to array assignemnt statements");
                
                res02 = expr(";");
                if(res02.structure == ClassStruct.FIXED_ARRAY)
                {
                    ResultValue copyArray = res02;
                    
                    if(copyArray.arrayVals == null)
                        error("'" + res02.value + "' Array has been initilized but not declared for array to array assignment startign on line " + iRevertLine);
                    
                    int i;
                    if(array.bSizeSet)
                    {
                        for(i=0; i<copyArray.arrayVals.size(); i++)
                        {
                            if(i >= array.arraySize)
                                break;
                                
                            array.putArrayValue(this, copyArray.getArrayElem(i, false), i);
                        }
                        
                        this.storage.putValue(this, declare.tokenStr, array);
                        return array;
                    }
                    else
                    {
                        for(i=0; i<copyArray.arrayVals.size(); i++)
                        {                                
                           array.putArrayValue(this, copyArray.getArrayElem(i, false), i);
                        }
                        
                        this.storage.putValue(this, declare.tokenStr, array);
                        return array;
                    }
                    
                }
                else
                {
                    bScalar = true;
                    this.scan.setPosition(0, iSaveLineNr);
                    
                }
            } 
            else 
            {
                index = expr("]");
                index = Utility.intCoherce(this, Utility.resValueToToken(this,  index)); 
                iIndex = Integer.valueOf(index.value);
            }
        }
        
        if(!bArray)     //save the declaring identifier.
            declare = this.scan.getCurrentToken();
        
        //get the operator in the next token.
        scan.getToken();
        //saves the operator
        operatorStr = this.scan.currentToken.tokenStr;
        
        if(declare.subClassif == SubClassif.IDENTIFIER)
        {
            res01 = this.storage.getValue(this, declare.tokenStr);
            declare.subClassif = this.storage.getValue(this, declare.tokenStr).type;
        }
        
        if (operatorStr.equals("="))
        {   
            res02 = expr(false);
           /* 
            if(declare.subClassif == SubClassif.DATE)
            {
                
                if(Utility.dateValidate(this, res02) == false)
                    Utility.utilError("assignmentStatement", "Not a valid date for " + declare.tokenStr + " on line " + (declare.iSourceLineNr+1));
                
                res02.type = SubClassif.DATE;
                this.storage.putValue(this, declare.tokenStr, res02);
                return Utility.getCopy(res02);
            }
            */
            if(bArray && !bScalar)    // Assingments for a declared array only
            {
                //really a string[] access we treat as a fixed array, but really still
                //primitive
                if(res01.structure == ClassStruct.PRIMITIVE)
                {
                    if(iIndex > res01.value.length() || iIndex < 0)
                        error("'" + declare.tokenStr + "' index is out of range on line " + (declare.iSourceLineNr+1));

                    StringBuilder sb = new StringBuilder(res01.value);

                    int i=0;
                    res02.value = Utility.rmNonPrintChars(res02.value);
                    while(i < res02.value.length())
                    {
                        sb.setCharAt(iIndex, res02.value.charAt(i));
                        i++;
                        iIndex++;
                    }
                    res01.value = sb.substring(0);

                    this.storage.putValue(this, declare.tokenStr, res01);

                    return Utility.getCopy(res01);
                }

                // Array subscript assignment    i.e. A[2] = "x";
                if (iIndex != -1 && res01.value != null)
                {
                    String newString = res01.value.substring(0, iIndex) + res02.value + res01.value.substring(iIndex+1);
                    array.value = newString;
                }
                else
                    array.putArrayValue(this, res02, iIndex);

                resReturn = Utility.getCopy(array);

                this.storage.putValue(this, declare.tokenStr, Utility.getCopy(array));

                return resReturn;
            }

            // Array assignment for scalar arrays
            if (bScalar) 
            {
                //we must be inside a string to string assignment, after we got a char from the
                //previous string.
                if(res01.structure == ClassStruct.PRIMITIVE)
                {
                    if(res02.structure != ClassStruct.PRIMITIVE)
                        error("Expecting a Primitive string to assign '" + declare.tokenStr + "' on line " + (declare.iSourceLineNr+1));

                    //replace the current value with the new value
                    this.storage.putValue(this, declare.tokenStr, res02);
                    return Utility.getCopy(res02);

                }

                // Modify existing value if arrayvals is null
                if (array.arrayVals == null)
                    array.arrayVals = new ArrayList<ResultValue>();

                res02.structure = ClassStruct.ARRAY_ELEM;

                // Initialize are element of the array using scalar
                for (int i = 0; i < array.arraySize; i++)
                    array.putArrayValue(this, res02);

                resReturn = Utility.getCopy(array);
                this.storage.putValue(this, declare.tokenStr, Utility.getCopy(array));
                return resReturn;
            }

            // Delcaration base on subClassif types
            switch(declare.subClassif)
            {
                case INTEGER:
                    resReturn = Utility.intCoherce(this, res02.toToken());

                    if(bArray)
                        array.putArrayValue(this, array, iIndex);
                    else
                        this.storage.putValue(this, declare.tokenStr, resReturn);
                    break;

                case FLOAT:
                    resReturn = Utility.floatCoherce(this, res02.toToken());
                    this.storage.putValue(this, declare.tokenStr, resReturn);
                    break;

                case BOOLEAN: //true = 1, false = 0
                    //resReturn = Utility.intCoherce(this, res02.toToken());
                    if(res02.value.equals("T"))
                        res02.value = "true";
                    else
                        res02.value = "false";
                    resReturn = Utility.getCopy(res02);
                    this.storage.putValue(this, declare.tokenStr, res02);
                    break;

                case STRING:
                    this.storage.putValue(this, declare.tokenStr, res02);
                    resReturn = Utility.getCopy(res02);
                    break;
					
				case DATE:
					//if date is invalid error
					if(res02.type != SubClassif.DATE)
					{
						int err = Date.validate(res02.value);
						switch(err)
						{
							//TODO: Check if correct error method. could be error with line
							case 0: //this shouldn't happen because needs to be invalid date to go in here
								System.out.println("Something went wrong.");
								break;
							case 1:
								error("On line " + (iSaveLineNr-1) + ". Invalid year. Recieved " + res02.value.substring(0,4));
								break;
							case 2:
								error("On line " + (iSaveLineNr-1) + ". Invalid month. Recieved " + res02.value.substring(5,7));
								break;
							case 3:
								error("On line " + (iSaveLineNr-1) + ". Invalid day. Recieved " + res02.value.substring(8));
								break;
							case 4:
								error("On line " + (iSaveLineNr-1) + ". Invalid date format. Expected yyyy-mm-dd");
								break;
						}
					}
                    this.storage.putValue(this, declare.tokenStr, res02);
                    resReturn = Utility.getCopy(res02);
                    break;

                default:
                    error("On line " + iSaveLineNr + ". Not a valid operand for '=' assignment. Recieved: " + declare.tokenStr + " " + declare.subClassif);

            }

            if(resReturn.value == null)
                error("Value is null, look at stack trace to see where you forgot too assign value");

            if(this.scan.bShowAssign)
                this.scan.debugMessage("Assign result into '" + declare.tokenStr + "' is '" + resReturn.value + "'");

            return resReturn;
        }
        
        // Handle Numeric Assignment operations only valid (+, -, /, *, ^)
        resReturn = numericAssignment(res01, res02, operatorStr, declare);
        
        return resReturn;
    }
    
    ResultValue numericAssignment(ResultValue res01, ResultValue res02, String operatorStr, Token declare) throws Error
    {
        ResultValue resReturn = new ResultValue();
        
        if(res01.value.equals(""))
            error(declare + " is being declared. Can not use anthing other than '=' as the operator.\nLine: " + declare.iSourceLineNr+1);
                
        res02 = expr(false);
        
        
        // Convert expression result to its corresponding data type
        SubClassif type = this.storage.getValue(this, declare.tokenStr).type;
        if (type == SubClassif.INTEGER)
            res02 = Utility.intCoherce(this, res02.toToken());
        else if (type == SubClassif.FLOAT)
            res02 = Utility.floatCoherce(this, res02.toToken());
        else
            error("Invalid operation type on numeric assignment" + declare.tokenStr);
        
        // Do assingment operation based on numeric operator
        switch(operatorStr)
        {
            case "+=":
                resReturn = Utility.numericAdd(this, res01, res02);
                break;
            case "-=":
                resReturn = Utility.numericSub(this, res01, res02);
                break;
            case "*=":
                resReturn = Utility.numericMult(this, res01, res02);
                break;
            case "/=":
                resReturn = Utility.numericDivide(this, res01, res02);
                break;
            case "^=":
                resReturn = Utility.numericExpon(this, res01, res02);
                break;
            default:
                error("Not a valid operand assignment. Recieved:" + declare.tokenStr + " " + declare.subClassif);
        }
        
        // Store result of evaluation in the storage manager
        //this.storage.putValue(this, declare.tokenStr, Classif.OPERAND, SubClassif.INTEGER, resReturn.value);
        this.storage.values.put(declare.tokenStr, resReturn);

        
        return resReturn;
    }
    
    /***
     * expr() converts infix expressions to postfix to facilitate multiple 
     * variable operations
     * 
     * Assumes on the ','.
     * 
     * @return ResultValue
     * @throws Error 
     */
    public ResultValue expr(boolean inPrint) throws Error
    {
        ArrayList<Token> postfix = new ArrayList<Token>();
        ResultValue resReturn;
        int iSaveLine = this.scan.currentToken.iSourceLineNr;
        
        // Use ")" as a terminator when expression is used within a function
        if (inPrint)
            postfix = convertToPostfixMulti(")");     
        else // Use ";" terminator when expression independently
            postfix = convertToPostfix(";");
        
        if(postfix == null)
            error("Expecting at the end of statement ';' on line " + iSaveLine);
        
        resReturn = evalPostfixExpression(postfix);
        
        if(resReturn == null)
            error("Expecting a ';' on line " + (iSaveLine+1));
        
        return resReturn;
    }
    
    
    
    /***
     * expr() converts infix expressions to postfix to facilitate multiple 
     * variable operations
     * 
     * Assumes on first operand of the expression.
     * 
     * @return ResultValue
     * @throws Error 
     */
    public ResultValue expr(String delim) throws Error
    {
        ArrayList<Token> postfix = new ArrayList<Token>();
        ResultValue resReturn;
        int iSaveLine = this.scan.currentToken.iSourceLineNr;
        
            
        postfix = convertToPostfix(delim);
        
        if(postfix == null)
            error("Expecting a ';' on line " + (iSaveLine+1));
        
        resReturn = evalPostfixExpression(postfix);
        
        if(resReturn == null)
            error("Expecting a ';' on line " + (iSaveLine+1));
        
        return resReturn;
    }
    
    /**
     * Assumes on first operand of expr
     * @return
     * @throws Error
     * @throws Exception 
     */
    /*
    public ArrayList<Token> convertToPostfix() throws Error
    {
        //System.out.println("currentToken = " + this.scan.currentToken.tokenStr);
        // Store a list of out values to late process
        ArrayList<Token> out = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        boolean bFoundParen = false;
        boolean bUnaryOperation = false;
        
        // Get to the start of the assignment 
        while(!scan.getToken().equals(";")) //colon
        {
            switch(scan.currentToken.primClassif)
            {
                case OPERAND:
                    out.add(scan.getCurrentToken());
                    break;
                case OPERATOR:
                    // Handle unary operators
                    switch(scan.currentToken.subClassif)
                    {
                        case UNARY_M:
                            //TODO: add while loop
                            
                            
                            // Assuming current token is "-" before a variable
                            // get variable next to unary minus
                            scan.getToken();
                            
                            // Concatinate minus to the token variable
                            scan.currentToken.tokenStr = "-" + scan.currentToken.tokenStr;
                            
                            out.add(scan.getCurrentToken());
                            bUnaryOperation = true;
                            
                            break;
                            
                        default:
                            while(!stack.isEmpty())
                            {
                                // get precedence of top element in stack
                                if (scan.currentToken.tkPrec > stack.peek().tkPrec)
                                    break;
                               out.add(stack.pop());
                            }
                    }
                    
                    // The current token is an actual operator
                    if (bUnaryOperation == false)
                        stack.push(scan.getCurrentToken());
                    
                    break;
                case SEPARATOR:
                    switch(scan.currentToken.tokenStr)
                    {
                        case "(":
                            stack.push(scan.getCurrentToken());
                            break;
                        case ")": 
                            while(!stack.isEmpty())
                            {
                                Token popped = stack.pop();
                                if (popped.tokenStr.equals("("))
                                {
                                    bFoundParen = true;
                                    break;
                                }
                                out.add(popped);
                            }
                            if (!bFoundParen)
                                scan.errorWithLine("Missing a matching left paren in expression");
                            break;
                        default:
                            scan.errorWithLine("Invalid separator on line");
                    }
                default:
                    return null;
            }
        }
        
        
        while(!stack.isEmpty())
        {
            Token tk = stack.pop();
            if (tk.tokenStr.equals("("))
                scan.errorWithLine("Missing right paren in expression");
            
            out.add(tk);
        }
        
        //System.out.println("out: " + out);
        return out;
    }
    */
    public ArrayList<Token> convertToPostfix(String delim) throws Error
    {

        // Store a list of out values to late process
        ArrayList<Token> out = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        boolean bFoundParen = false;
        boolean bUnaryOperation = false;
        boolean bFoundBracket = false;
        boolean bFunction = false;
        int pernCount = 0;
        int bracketCount = 0;
        
        //Gets the ',' off
        this.scan.getToken();
        
        
        
        while(!this.scan.curIsValue(delim)) //value
        {
            
            //sd
            
            if(this.scan.curIsValue("by"))
                break;
            
            if(this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ')' before the ';'");
            
            
             if(this.scan.curIsValue("]"))
            {
                
                if(bracketCount <= 0)
                    this.scan.errorWithCurrent("No '[' for the currentToken.");
                
                if(bFoundBracket)
                {
                    bracketCount--;
                    bFoundBracket = false;
                }
                
                scan.getToken();
                
                
                
                
                Token popped = stack.pop();
                
                while(popped.classStruct != ClassStruct.FIXED_ARRAY || popped.classStruct == ClassStruct.ARRAY_STRING)
                {
                    out.add(popped);
                    
                    if(!stack.isEmpty())
                        popped = stack.pop();
                    else
                        break;
                }
                //link the expr to the array reference.
                out.add(popped);
                continue;
            }
            
            switch(scan.currentToken.primClassif)
            {
                case OPERAND:
                    
                    if (scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
                    {
                        this.scan.currentToken.tkPrec = 16;
                        this.scan.currentToken.stPrec = 0;

                        if(this.scan.currentToken.bArrayBracket)
                        {
                            bracketCount++;
                            bFoundBracket = true;
                        }
                        while(!stack.isEmpty())
                        {
                            // get precedence of top element in stack
                            if (scan.currentToken.tkPrec > stack.peek().stPrec)
                                break;
                            
                         
                            
                            out.add(stack.pop());
                        }
                        stack.push(scan.getCurrentToken());
                        //this.scan.currentToken.classStruct = ClassStruct.PRIMITIVE;
                        break;
                    }
                    out.add(scan.getCurrentToken());
                    break;
                    
                case OPERATOR:
                    // Handle unary operators
                    switch(scan.currentToken.subClassif)
                    {
                        case UNARY_M:
                            String value = new String();
                            while(this.scan.currentToken.subClassif == SubClassif.UNARY_M)
                            {
                                value =  value + '-';
                                // get variable next to unary minus
                                scan.getToken();
                            }

                            this.scan.currentToken.tokenStr = value + this.scan.currentToken.tokenStr;

                            if(this.scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
                            {
                                 if(this.scan.currentToken.bArrayBracket)
                                {
                                    bracketCount++;
                                    bFoundBracket = true;
                                }

                                while(!stack.isEmpty())
                                {
                                    // get precedence of top element in stack
                                    if (scan.currentToken.tkPrec > stack.peek().stPrec)
                                        break;
                                    out.add(stack.pop());
                                }
                                break;
                            }
                            out.add(scan.getCurrentToken());
                            bUnaryOperation = true;

                            break;
                        default:
                            while(!stack.isEmpty())
                            {
                                // get precedence of top element in stack
                                if (scan.currentToken.tkPrec > stack.peek().stPrec)
                                    break;

                                out.add(stack.pop());
                            }
                            
                    }
                    // The current token is an actual operator
                    if (bUnaryOperation == false)
                        stack.push(scan.getCurrentToken());

                    if(bUnaryOperation == true)
                        bUnaryOperation = false;
                    break;
                    
                case SEPARATOR:
                    switch(scan.currentToken.tokenStr)
                    {
                        case "(":
                            stack.push(scan.getCurrentToken());
                            pernCount++;
                            break;
                            
                        case ")": 
                            pernCount--;
                            while(!stack.isEmpty())
                            {
                                Token popped = stack.pop();
                                if (popped.tokenStr.equals("("))
                                {
                                    bFoundParen = true;
                                    break;
                                }
                                out.add(popped);
                            }
                            if (!bFoundParen)
                                scan.errorWithLine("Missing a matching left paren in expression");
                            
                            if(bFunction)
                            { //pop the function off too if we encounted a function.
                                out.add(stack.pop());
                                bFunction = false;
                            }
                            break;      
                        case "[":
                            bFoundBracket = true;
                            bracketCount++;
                            
                            Token tkStrA = out.get(out.size()-1);
                            out.remove(out.size()-1);
                            tkStrA.classStruct = ClassStruct.ARRAY_STRING;
                            tkStrA.tkPrec = 16;
                            tkStrA.stPrec = 0;
                            
                            while(!stack.isEmpty())
                            {
                                // get precedence of top element in stack
                                if (scan.currentToken.tkPrec > stack.peek().stPrec)
                                    break;

                                out.add(stack.pop());
                            }
                            stack.push(scan.getCurrentToken());
                            break;
                        case ",":
                            if(bFunction)
                                break;
                            
                        default:
                            scan.errorWithLine("Invalid separator on line");
                    }
                    break;
                    
                case FUNCTION:
                    bFunction = true;
                    while(!stack.isEmpty())
                    {
                        // get precedence of top element in stack
                        if (scan.currentToken.tkPrec > stack.peek().stPrec)
                            break;

                        out.add(stack.pop());
                    }
                    stack.push(scan.getCurrentToken());
                    break;
                    
                default:
                    scan.errorWithCurrent("Can only evaluate an expression using Operands and Operators");
            }
            scan.getToken();
        }
        
        if(bracketCount != 0)
            error("Unbalanced number of brackets in experssion ending on line " + (this.scan.currentToken.iSourceLineNr+1));
        
        while(!stack.isEmpty())
        {
            Token tk = stack.pop();
            if (tk.tokenStr.equals("("))
                scan.errorWithLine("Missing right paren in expression");
            
            out.add(tk);
        }
        
        return out;
    }
    
    /**
     * Assumes on token before 1st operand of expr
     * @return
     * @throws Error
     * @throws Exception 
     */
    public ArrayList<Token> convertToPostfixMulti(String delim) throws Error
    {
        //System.out.println("currentToken = " + this.scan.currentToken.tokenStr);
        // Store a list of out values to late process
        ArrayList<Token> out = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        boolean bFoundParen = false;
        boolean bUnaryOperation = false;
        int iFunc = 0;
        int pernCount = 0;
        int bracketCount = 0;
        
        
        
        //Gets the ',' off
        this.scan.getToken();
        

        while(!this.scan.curIsValue(",") || iFunc>0) //value
        {
            if(this.scan.curIsValue(",") && iFunc>0)
            {
                    scan.getToken(); //skip over the comma
                    continue;
            }
			
            if(this.scan.curIsValue(delim))
                if(pernCount==0)
                    break;
            
            if(this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ')' before the ';'");
            
            
            switch(this.scan.currentToken.tokenStr)
            {
                case"]":
                    if(bracketCount <= 0)
                        this.scan.errorWithCurrent("No '[' for the currentToken.");

                    if(this.scan.currentToken.bArrayBracket)
                        bracketCount--;

                    scan.getToken();

                    Token popped = stack.pop();

                    while(popped.classStruct != ClassStruct.FIXED_ARRAY)
                    {
                        out.add(popped);

                        if(!stack.isEmpty())
                            popped = stack.pop();
                        else
                            break;
                    }
                    //link the expr to the array reference.
                    out.add(popped);
                    continue;
            }
            
            switch(scan.currentToken.primClassif)
            {
                case OPERAND:
                    if (scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
                    {
                        if(this.scan.currentToken.bArrayBracket)
                            bracketCount++;
                        
                        this.scan.currentToken.tkPrec = 16;
                        this.scan.currentToken.stPrec = 0;
                    
                        while(!stack.isEmpty())
                        {
                            // get precedence of top element in stack
                            if (scan.currentToken.tkPrec > stack.peek().stPrec)
                                break;
                            out.add(stack.pop());
                        }
                        stack.push(scan.getCurrentToken());
                        break;
                    }

                    out.add(scan.getCurrentToken());
                    break;
                    
                case OPERATOR:
                    // Handle unary operators
                    switch(scan.currentToken.subClassif)
                    {
                        case UNARY_M:
                            String value = new String();

                            while(this.scan.currentToken.subClassif == SubClassif.UNARY_M)
                            {
                                value =  value + '-';
                                // get variable next to unary minus
                                scan.getToken();
                            }
                            this.scan.currentToken.tokenStr = value + this.scan.currentToken.tokenStr;

                            if(this.scan.currentToken.classStruct == ClassStruct.FIXED_ARRAY)
                            {
                                bracketCount++;

                                while(!stack.isEmpty())
                                {
                                    // get precedence of top element in stack
                                    if (scan.currentToken.tkPrec > stack.peek().stPrec)
                                        break;
                                    out.add(stack.pop());
                                }
                                break;
                            }
                            out.add(scan.getCurrentToken());
                            bUnaryOperation = true;
                            break;
                            
                        default:
                            while(!stack.isEmpty())
                            {
                                // get precedence of top element in stack
                                if (scan.currentToken.tkPrec > stack.peek().stPrec)
                                    break;

                                out.add(stack.pop());
                            }
                    }
                    // The current token is an actual operator
                    if (bUnaryOperation == false)
                        stack.push(scan.getCurrentToken());
                    
                    if(bUnaryOperation == true)
                        bUnaryOperation = false;
                    break;
                    
                case SEPARATOR:
                    switch(scan.currentToken.tokenStr)
                    {
                        case "(":
                            stack.push(scan.getCurrentToken());
                            pernCount++;
                            break;
                            
                        case ")": 
                            pernCount--;
                            while(!stack.isEmpty())
                            {
                                Token popped = stack.pop();
                                if (popped.tokenStr.equals("("))
                                {
                                    bFoundParen = true;
                                    break;
                                }
                                out.add(popped);
                            }
                            if (!bFoundParen)
                                scan.errorWithLine("Missing a matching left paren in expression");
                            
                            if(iFunc > 0)
                            {
                                iFunc--;
                                out.add(stack.pop());
                            }
                            break;
                            
                        case ",":
                            while(!stack.isEmpty())
                            {
                                Token tk = stack.pop();
                                if (tk.tokenStr.equals("("))
                                    scan.errorWithLine("Missing right paren in expression");

                                out.add(tk);
                            }
                            return out;
                            
                        default:
                            this.scan.errorWithCurrent("Invalid separator on line. Expecting '" + delim + "'");
                    }
                    break;
                    
                case FUNCTION:
                    //increase the function count
                    iFunc++;
                    
                    while(!stack.isEmpty())
                    {
                        // get precedence of top element in stack
                        if (scan.currentToken.tkPrec > stack.peek().stPrec)
                            break;
                        out.add(stack.pop());
                    }
                    stack.push(scan.getCurrentToken());
                    break;
                default:
                    scan.errorWithCurrent("Can only evaluate an expression using Operands and Operators");
            }
            scan.getToken();
        }
        
        if(bracketCount != 0)
            error("Unbalanced number of brackets in experssion ending on line " + (this.scan.currentToken.iSourceLineNr+1));
        
        
        while(!stack.isEmpty())
        {
            Token tk = stack.pop();
            if (tk.tokenStr.equals("("))
                scan.errorWithLine("Missing right paren in expression");
            out.add(tk);
        }
        
        
        //System.out.println("out: " + out);
        return out;
    }
    
    /**
     * Assign variable to its respected ResultValue given
     * @param var     String representing the variable
     * @param value   ResultValue used to assign that variable
     * @return        ResultValue passed in to that variable
     * @throws Error 
     */
    public ResultValue assign(String var, ResultValue value) throws Error
    {
        storage.setValue(this, var, value);
        return value;
    }
    
 
    
    private boolean isConstant(String str)
    {
        // Empty
        if (str.isEmpty())
            return false;
                
        // Actual numeric
        char first = str.charAt(0);
        if (Character.isDigit(first) || first == '.')
            return true;
        
        return false;
    }
    
    /**
     * debugStmt will turn off or on debug
     * statements for meatbol src code.
     * <p>
     * 
     * assumes on the debug token
     * assumes ending on the semicolon
     * 
     * @throws Error 
     */
    public ResultValue debugStmt(boolean bExec) throws Error
    {
        ResultValue resReturn = new ResultValue();
        resReturn.debugONoff = "off";
        
        //System.out.println("\t\t...DEBUG Statement");
        this.scan.getToken(); //loades the current and next tokens
        
        //System.out.printf("\t\t.. currentToken=%s nextToken=%s\n", this.scan.currentToken.tokenStr, this.scan.nextToken.tokenStr);
        
        STEntry cEntry = this.scan.symbolTable.hashMap.get(this.scan.currentToken.tokenStr);
        STEntry nEntry = this.scan.symbolTable.hashMap.get(this.scan.nextToken.tokenStr);
        if (cEntry instanceof STIdentifier && nEntry instanceof STIdentifier)
        {
            STIdentifier currentID = (STIdentifier) cEntry;
            STIdentifier nextID= (STIdentifier) nEntry;
            
            switch (currentID.dclType)
            {
                case EXPRdbug:
                    if(nextID.dclType == ONdbug)
                    {
                        resReturn.debugONoff = "on";
                        scan.bShowExpr = true;
                    }
                    else if (nextID.dclType == OFFdbug)
                        scan.bShowExpr = false;
                    else
                    {
                        String msg = String.format("Not a valid debug \'ON\' or \'OFF\' type: \'%s\'\n", this.scan.currentToken.tokenStr);
                    scan.errorWithCurrent(msg);
                    }
                    break;
                    
                case DECLAREbug:
                    if(nextID.dclType == ONdbug)
                    {
                        resReturn.debugONoff = "on";
                        scan.bShowDeclare = true;
                    }
                    else if (nextID.dclType == OFFdbug)
                        scan.bShowDeclare = false;
                    else
                    {
                        String msg = String.format("Not a valid debug \'ON\' or \'OFF\' type: \'%s\'\n", this.scan.currentToken.tokenStr);
                    scan.errorWithCurrent(msg);
                    }
                    break;
                    
                case ASSIGNdbug:
                    if(nextID.dclType == ONdbug)
                    {
                        resReturn.debugONoff = "on";
                        scan.bShowAssign = true;
                    }
                    else if (nextID.dclType == OFFdbug)
                        scan.bShowAssign = false;
                    else
                    {
                        String msg = String.format("Not a valid debug \'ON\' or \'OFF\' type: \'%s\'\n", this.scan.currentToken.tokenStr);
                    scan.errorWithCurrent(msg);
                    }
                    break;
                    
                case STATEMENTdbug:
                    if(nextID.dclType == ONdbug)
                    {
                        resReturn.debugONoff = "on";
                        scan.bShowStmt = true;
                    }
                    else if (nextID.dclType == OFFdbug)
                        scan.bShowStmt = false;
                    else
                    {
                        String msg = String.format("Not a valid debug \'ON\' or \'OFF\' type: \'%s\'\n", this.scan.currentToken.tokenStr);
                        scan.errorWithCurrent(msg);
                    }
                    break;
                    
                case TOKENdbug:
                    if(nextID.dclType == ONdbug)
                    {
                        resReturn.debugONoff = "on";
                        scan.bShowToken = true;
                    }
                    else if (nextID.dclType == OFFdbug)
                        scan.bShowToken = false;
                    else
                    {
                        String msg = String.format("Not a valid debug \'ON\' or \'OFF\' type: \'%s\'\n", this.scan.currentToken.tokenStr);
                    scan.errorWithCurrent(msg);
                    }
                    break;
                    
                default:
                    String msg = String.format("Not a valid debug type: \'%s\'\n", this.scan.currentToken.tokenStr);
                    scan.errorWithCurrent(msg);
                    
            }
            
        }
        this.scan.semiSkip();
        return resReturn;
    }
    
    
    public void breakRoutine() throws Error
    {
        if(this.scan.curIsValue("break"))
        {
            this.scan.getToken();

            if(!this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ';' after the 'continue'");

            this.scan.getToken();
        }

            while(this.scan.currentToken.subClassif != SubClassif.END)
                exec(false);

        
    }
    
    
    
    
    /**
     * 
     * @throws Error 
     */
    public void continueRoutine() throws Error
    {
        ResultValue resTemp;
        
        if(this.scan.curIsValue("continue"))
        {
            this.scan.getToken();

            if(!this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ';' after the 'continue'");

            this.scan.getToken();
        }

        while(this.scan.currentToken.subClassif != SubClassif.END)
            exec(false);
         
    }
    
    
    
    /**
     * Purpose:
     *      Combines both the break and continue
     *      routines since so similar. Can just call this
     *      and assume, you will continue to parse down until
     *      you reach and END type of token that is not 'break'
     *      or 'continue'
     * 
     * @throws Error 
     */
    public void breakContinue() throws Error
    {
        ResultValue resTemp;
        
        if(this.scan.curIsValue("continue") || this.scan.curIsValue("break"))
        {
            this.scan.getToken();

            if(!this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ';' after the 'continue'");

            this.scan.getToken();
        }
        
        
        
         while(this.scan.currentToken.subClassif != SubClassif.END || (this.scan.curIsValue("break") || this.scan.curIsValue("continue")))
            resTemp = exec(false);
        
        

        return;
         
    }

    
    public void error(String msg) throws Error
    {
        String err = String.format("Error Message:%s\n", msg);
        throw new Error(err);
    }

    /**
     * selectStmt
     * Purpose:
     *      to handle select when statements
     * 
     * Assumes on the 'select' 
     * 
     * @param bExec
     * @return 
     */
    private ResultValue selectStmt(boolean bExec) throws Error 
    {
        ResultValue resValue = new ResultValue();
        Token key = new Token();
        int iLineNr = this.scan.currentToken.iSourceLineNr;
        
        if(bExec == false)
        {
            this.scan.skipTo(":");
            this.scan.getToken();
            
            //execute all the other statements false until 'endselect' 
            while(true)
            {
                while(this.scan.currentToken.subClassif != SubClassif.END)
                     exec(false);

                if(this.scan.curIsValue("break")) //skip over the break
                {
                    this.scan.getToken();

                    if(!this.scan.curIsSemi())
                         this.scan.errorWithCurrent("Expecting a ';' after the 'break'");

                    this.scan.getToken();
                    continue;
                } else if(this.scan.curIsValue("continue")) //skip over the continue
                {
                    this.scan.getToken();

                    if(!this.scan.curIsSemi())
                         this.scan.errorWithCurrent("Expecting a ';' after the 'continue'");

                    this.scan.getToken();
                    continue;
                } else if(this.scan.curIsValue("when")) //skip over remaining whens
                {
                    this.scan.skipTo(":");
                    this.scan.getToken();
                    continue;
                } else if(this.scan.curIsValue("default"))
                {
                    this.scan.getToken();

                    if(!this.scan.curIsColon())
                         this.scan.errorWithCurrent("Expecting a ':' after the 'default'");

                    this.scan.getToken();
                    continue;

                }
                else //end value other than select statements inner end values
                    break;
            }
            
            if(!this.scan.curIsValue("endselect"))
                this.scan.errorWithCurrent("Expecting an 'endselect' for the 'select' on line " + (1+iLineNr));
            
            this.scan.getToken();

            if(!this.scan.curIsSemi())
                this.scan.errorWithCurrent("Expecting a ';' after the 'endselect'");

            this.scan.getToken();
            
            return resValue;
        }
        
        if(this.scan.nextToken.primClassif != Classif.OPERAND && this.scan.nextToken.primClassif != Classif.FUNCTION)
            this.scan.errorWithCurrent("Expecting an expression after the 'select' on line " + (this.scan.currentToken.iSourceLineNr + 1));
        
        resValue =  evalSingleExpr(":");

        if(!this.scan.curIsColon())
            this.scan.errorWithCurrent("Expecting a ':' after the 'value' for the 'select' on line: " + (iLineNr+1));
        
        //loads the when or default
        this.scan.getToken();
        

       
       ResultValue resTemp = new ResultValue();
       
       ArrayList<Token> post;
        
        while(true)
        {
            //if we are at the end of the select statement
            if(this.scan.curIsValue("endselect"))
                break;
            
            //determine what kind of statement it is
            switch(this.scan.currentToken.tokenStr)
            {
                case "when":
                    
                    //for each item to compare up to the ':'
                    while(!this.scan.curIsColon())
                    {
                        //gets the next value up to the ':' but will also
                        //stop on the ','
                        post = convertToPostfixMulti(":");
                        resTemp = evalPostfixExpression(post);
                        
                        //determine the key type to coerce the comparing value to that type if possable.
                        switch(resValue.type)
                        {
                            case INTEGER:

                                resTemp = Utility.intCoherce(this, Utility.resValueToToken(this, resTemp));
                                
                                //check to see if the two values are equal
                                resTemp = Utility.equal(this, resValue, resTemp);

                                if(resTemp.value.equals("true"))
                                {//if they are equal
                                    
                                    //skip over the rest of the values
                                    this.scan.skipTo(":");

                                    //get the ':' off current
                                    this.scan.getToken();

                                    //execute the statements that are true
                                    while(this.scan.currentToken.subClassif != SubClassif.END)
                                        resTemp = exec(true);
                                    
                                    if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                                    {
                                        resTemp.terminatingStr = "break";
                                        breakContinue();
                                    }
                                    if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                                    {
                                        resTemp.terminatingStr = "continue";
                                        breakContinue();
                                    }
                                    
                                   //execute all the other statements false until 'endselect' 
                                   while(true)
                                   {
                                       while(this.scan.currentToken.subClassif != SubClassif.END)
                                            exec(false);

                                       if(this.scan.curIsValue("break")) //skip over the break
                                       {
                                           this.scan.getToken();

                                           if(!this.scan.curIsSemi())
                                                this.scan.errorWithCurrent("Expecting a ';' after the 'break'");

                                           this.scan.getToken();
                                           continue;
                                       } else if(this.scan.curIsValue("continue")) //skip over the continue
                                       {
                                           this.scan.getToken();

                                           if(!this.scan.curIsSemi())
                                                this.scan.errorWithCurrent("Expecting a ';' after the 'continue'");

                                           this.scan.getToken();
                                           continue;
                                       } else if(this.scan.curIsValue("when")) //skip over remaining whens
                                       {
                                           this.scan.skipTo(":");
                                           this.scan.getToken();
                                           continue;
                                       } else if(this.scan.curIsValue("default"))
                                       {
                                           this.scan.getToken();
                                           
                                           if(!this.scan.curIsColon())
                                                this.scan.errorWithCurrent("Expecting a ':' after the 'default'");
                                           
                                           this.scan.getToken();
                                           continue;
                                           
                                       }
                                       else //end value other than select statements inner end values
                                           break;
                                   }
                                   
                                   
                                   //validate the 'endselect' and ';'
                                   if(!this.scan.curIsValue("endselect"))
                                       this.scan.errorWithCurrent("Expecting an 'endselect' for the 'select' on line " + (1+iLineNr));
                                   this.scan.getToken();

                                   if(!this.scan.curIsSemi())
                                       this.scan.errorWithCurrent("Expecting a ';' after the 'endselect'");

                                   this.scan.getToken();

                                   //return the last true executed result
                                   return resTemp;
                                } 
                                //if false
                                break;
                            case FLOAT:
                                resTemp = Utility.floatCoherce(this, Utility.resValueToToken(this, resTemp));

                                resTemp = Utility.equal(this, resValue, resTemp);

                                if(resTemp.value.equals("true"))
                                {
                                    this.scan.skipTo(":");

                                    //get the ':' off current
                                    this.scan.getToken();

                                    //execute the statements that are true
                                    while(this.scan.currentToken.subClassif != SubClassif.END)
                                    {
                                        resTemp = exec(true);
                                    }
                                    
                                    if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                                    {
                                        resTemp.terminatingStr = "break";
                                        breakContinue();
                                    }
                                    if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                                    {
                                        resTemp.terminatingStr = "continue";
                                        breakContinue();
                                    }

                                   //execute all the other statements false until 'endselect' 
                                   while(true)
                                   {
                                       while(this.scan.currentToken.subClassif != SubClassif.END)
                                            exec(false);

                                       if(this.scan.curIsValue("break")) //skip over the break
                                       {
                                           this.scan.getToken();

                                           if(!this.scan.curIsSemi())
                                                this.scan.errorWithCurrent("Expecting a ';' after the 'break'");

                                           this.scan.getToken();
                                           continue;
                                       } else if(this.scan.curIsValue("contiue")) //skip over the continue
                                       {
                                           this.scan.getToken();

                                           if(!this.scan.curIsSemi())
                                                this.scan.errorWithCurrent("Expecting a ';' after the 'continue'");

                                           this.scan.getToken();
                                           continue;
                                       } else if(this.scan.curIsValue("when")) //skip over remaining whens
                                       {
                                           this.scan.skipTo(":");
                                           this.scan.getToken();
                                           continue;
                                       } else if(this.scan.curIsValue("default"))
                                       {
                                           this.scan.getToken();
                                           
                                           if(!this.scan.curIsColon())
                                                this.scan.errorWithCurrent("Expecting a ':' after the 'default'");
                                           
                                           this.scan.getToken();
                                           continue;
                                           
                                       }
                                       else //end value other than select statements
                                           break;
                                   }

                                   if(!this.scan.curIsValue("endselect"))
                                       this.scan.errorWithCurrent("Expecting an 'endselect' for the 'select' on line " + (1+iLineNr));
                                   this.scan.getToken();

                                   if(!this.scan.curIsSemi())
                                       this.scan.errorWithCurrent("Expecting a ';' after the 'endselect'");

                                   this.scan.getToken();


                                   return resTemp;


                                }
                                break;
                            case STRING:
                                resTemp = Utility.stringCoherce(this, resTemp);

                                resTemp = Utility.equal(this, resValue, resTemp);

                                if(resTemp.value.equals("true"))
                                {
                                    this.scan.skipTo(":");

                                    //get the ':' off current
                                    this.scan.getToken();

                                    //execute the statements that are true
                                    while(this.scan.currentToken.subClassif != SubClassif.END)
                                        resTemp = exec(true);
                                    
                                    if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                                    {
                                        resTemp.terminatingStr = "break";
                                        breakContinue();
                                    }
                                    if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                                    {
                                        resTemp.terminatingStr = "continue";
                                        breakContinue();
                                    }

                                   //execute all the other statements false until 'endselect' 
                                   while(true)
                                   {
                                       while(this.scan.currentToken.subClassif != SubClassif.END)
                                            exec(false);

                                       if(this.scan.curIsValue("break")) //skip over the break
                                       {
                                           this.scan.getToken();

                                           if(!this.scan.curIsSemi())
                                                this.scan.errorWithCurrent("Expecting a ';' after the 'break'");

                                           this.scan.getToken();
                                           continue;
                                       } else if(this.scan.curIsValue("continue")) //skip over the continue
                                       {
                                           this.scan.getToken();

                                           if(!this.scan.curIsSemi())
                                                this.scan.errorWithCurrent("Expecting a ';' after the 'continue'");

                                           this.scan.getToken();
                                           continue;
                                       } else if(this.scan.curIsValue("when")) //skip over remaining whens
                                       {
                                           this.scan.skipTo(":");
                                           this.scan.getToken();
                                           continue;
                                       } else if(this.scan.curIsValue("default"))
                                       {
                                           this.scan.getToken();
                                           
                                           if(!this.scan.curIsColon())
                                                this.scan.errorWithCurrent("Expecting a ':' after the 'default'");
                                           
                                           this.scan.getToken();
                                           continue;
                                           
                                       }
                                       else //end value other than select statements
                                           break;
                                   }
                                   
                                   

                                   
                                   
                                   
                                   if(!this.scan.curIsValue("endselect"))
                                       this.scan.errorWithCurrent("Expecting an 'endselect' for the 'select' on line " + (1+iLineNr));
                                   this.scan.getToken();

                                   if(!this.scan.curIsSemi())
                                       this.scan.errorWithCurrent("Expecting a ';' after the 'endselect'");

                                   this.scan.getToken();


                                   return resTemp;


                                }
                                break;
                            default:
                                this.scan.errorWithCurrent("Not a valid type for 'select'.");
                        }   
                    }
                    
                    //gets the ':' off currentToken
                    this.scan.getToken();
                    
                    //skip over the false stuff
                    while(this.scan.currentToken.subClassif != SubClassif.END)
                        exec(false);
                    
                    if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                    {
                        resTemp.terminatingStr = "break";
                        breakContinue();
                    }
                    
                    if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                    {
                        resTemp.terminatingStr = "continue";
                        breakContinue();
                    }
                    
                    break;
                case "default":
                    //loads the ':'
                    this.scan.getToken();
                    
                    if(!this.scan.curIsColon())
                        this.scan.errorWithCurrent("Expecting a ':' after the 'default'");
                    //gets the start of the next statement
                    this.scan.getToken();
                    
                    while(this.scan.currentToken.subClassif != SubClassif.END)
                        resTemp = exec(true);
                    
                    
                    if(this.scan.curIsValue("break") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("break")))
                    {
                        resTemp.terminatingStr = "break";
                        breakContinue();
                    }
                    
                    if(this.scan.curIsValue("continue") || (resTemp.terminatingStr != null && resTemp.terminatingStr.equals("continue")))
                    {
                        resTemp.terminatingStr = "continue";
                        breakContinue();
                    }
                    
                    if(!this.scan.curIsValue("endselect"))
                        this.scan.errorWithCurrent("Expecting a 'endselect' for the 'select' on line " + (1+iLineNr));
                    
                    this.scan.getToken();
                    
                    if(!this.scan.curIsSemi())
                        this.scan.errorWithCurrent("Expecting a ';' after the 'endselect'");
                    this.scan.getToken();
                    
                    return resTemp;
                        
                default:
                    this.scan.errorWithCurrent("Not a valid select statement. " + (iLineNr+1));
            }
            
        }
        
        //gets the 'endselect' off currentToken
        this.scan.getToken();
        
        if(!this.scan.curIsSemi())
            this.scan.errorWithCurrent("Expecting ';' after 'endselect'");
        
        this.scan.getToken();
        
        return resTemp;
        
    }
    

    
    }
    
  


