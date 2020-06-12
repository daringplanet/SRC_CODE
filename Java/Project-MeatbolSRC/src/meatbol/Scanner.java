/**
    Scanner
    Written by William Lippard, aju722

    Special Notes:
        There are many print statements left in the code
        that could potentially be utilize at a later date,
        so I have choose to leave them in for submission.
 */

package meatbol;

import enums.ClassStruct;
import statement.STIdentifier;
import statement.STEntry;
import statement.STFunction;
import statement.STControl;
import enums.SubClassif;
import enums.Classif;
import java.io.*;
import java.util.*;



public class Scanner
{
    public final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n\032"; // terminate a token
    
    public final static String unarySeps = "(,[~;";



    public Token currentToken;
    public Token nextToken;
    public SymbolTable symbolTable;
    public java.util.Scanner scan;
    public int iColPos;
    public int ibColPos;
    public char[] textCharM;
    public int iSourceLineNr;
    public int ibLineNr;
    public String sourceFileNm;
    public List sourceLineM;
    public int iLen, iTotalLines;
    public String sTokenStr;
    public boolean bGetLine;
    public boolean bEndToken;
    public boolean bShowToken;
    public boolean bShowExpr;
    public boolean bShowAssign;
    public boolean bShowDeclare;
    public boolean bShowStmt;


    /**
     * Initializes the variables and returns a Scanner Object if.<p>
     * @param arg String of the file name
     * @param symbolTable SymbolTable to understand scope and place holder.
     * @retun Scanner object
     */
    public Scanner(String arg, SymbolTable symbolTable) {
        try {

            this.symbolTable = symbolTable;
            this.iSourceLineNr = -1;
            this.sTokenStr = "";
            this.sourceFileNm = arg;
            this.currentToken = new Token();
            this.nextToken = new Token();
            //this one for windows intellj
            //this.scan = new java.util.Scanner(new FileReader(new File("src\\" + arg)));
            //this one for linux AND netbeans
            this.scan = new java.util.Scanner(new FileReader(new File(arg)));
            this.sourceLineM = new ArrayList<String>();
            this.iTotalLines = 0;
            this.bGetLine = true;
            this.bEndToken = true;
            this.bShowToken = false;
            this.bShowAssign = false;
            this.bShowExpr = false;
            this.bShowDeclare = false;
            this.bShowStmt = false;
            this.ibColPos = 0;
            this.ibLineNr = 0;

            process(); //puts all lines into String[] of lines


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    


    /**
     * This function process all the lines in the text file
     * and populates them into a string array list.
     * <p>
     * process also loads the first line and set up getToken
     * @return void
     */
    public void process() throws Error
    {
        
        //While there are lines
        while(this.scan.hasNextLine())
        {
            //add the line to the array and increase the line counter
            this.sourceLineM.add(Utility.rmNonPrintChars(this.scan.nextLine()));
            this.iTotalLines++;
        }
        //once we are done reading the lines
        //add in our own end of the line symbol
        //this.sourceLineM.add("\032");
        //this.iTotalLines++;
        //reflect this change in the line count as well by
        //increasing iTotalLines.




        getNextLine(); //loading line
        getToken(); //loading token
      
        return;
    }




    /**
     * Copies the nextToken to the currentToken.
     * <p>
     * @return void
     */
    public void copyTokens()
    {
        
        this.ibLineNr = this.currentToken.iSourceLineNr;
        this.ibColPos = this.currentToken.iColPos;
        
        this.currentToken.bArrayBracket = this.nextToken.bArrayBracket;
        this.currentToken.tokenStr = this.nextToken.tokenStr;
        this.currentToken.subClassif = this.nextToken.subClassif;
        this.currentToken.primClassif = this.nextToken.primClassif;
        this.currentToken.iColPos = this.nextToken.iColPos;
        this.currentToken.iSourceLineNr = this.nextToken.iSourceLineNr;
        this.currentToken.classStruct = this.nextToken.classStruct;
        if(this.nextToken.stPrec > 0 && this.nextToken.stPrec > 0)
        {
            this.currentToken.stPrec = this.nextToken.stPrec;
            this.currentToken.tkPrec = this.nextToken.tkPrec;
        }
        else
        {
            this.currentToken.stPrec = 0;
            this.currentToken.tkPrec = 0;
        }
        //System.out.println(this.currentToken.toString());
        return;
    }
    
    
    /**
     * Copies the nextToken to the currentToken.
     * <p>
     * @return void
     */
    public Token copyTokens(Token tk)
    {
        
        Token tkNew = new Token();
        
        
        tkNew.tokenStr = tk.tokenStr;
        tkNew.subClassif = tk.subClassif;
        tkNew.primClassif = tk.primClassif;
        tkNew.iColPos = tk.iColPos;
        tkNew.iSourceLineNr = tk.iSourceLineNr;
        tkNew.classStruct = tk.classStruct;
        if(tk.stPrec > 0 && tk.stPrec > 0)
        {
            tkNew.stPrec = tk.stPrec;
            tkNew.tkPrec = tk.tkPrec;
        }
        else
        {
            tkNew.stPrec = 0;
            tkNew.tkPrec = 0;
        }
        tkNew.bArrayBracket = tk.bArrayBracket;
        
        //System.out.println(this.currentToken.toString());
        return tkNew;
    }



    /**
     * removes all the white space until it reaches
     * a char that is not white space.
     * <p>
     * This function will automatically get the next
     * line if it runs out of chars in the current line.
     * rmWhiteSpace also looks for the bGetLine boolean value,
     * which indicates if we have reached the end of the String Array
     * of lines.
     * @return void
     */
    public void rmWhiteSpace()
    {
        
        //used to capture the current char
        char c;

        if(!this.bGetLine)
            return;
        
        while(true)
        {

            //if we are ate the end of the line
            if(this.iColPos == this.iLen)
                getNextLine(); //get the next line of text

            if(!this.bGetLine) //check to see if we didn't get a line of text
            {
                //sets the end next token to no string
                this.nextToken.tokenStr = "";
                return;
            }

            c = this.textCharM[this.iColPos]; //set the char



            //if the char is white space
            if(Character.isWhitespace(c))
                this.iColPos++; //skip over it
            else
                return; //we found something other than white space
            

        }

    }

    /**
     * gets the next line of text from
     * the array list of lines
     * <p>
     * This function also checks to see if there are no
     * more lines to get and sets a boolean indicator
     * for other functions to know there are no more lines
     * called bGetLine
     * @return void
     */
    public void getNextLine()
    {
        

        if(!bGetLine) //if there are no more lines
            return;

        //this is how to capture and set up a new line form the
        //sourceLineM
        //increase the line count
        

        this.iLen=0; //reset the length to make statement true


        while(iLen==0) //skip newlines
        {
            this.iSourceLineNr++;
            
            if(this.iSourceLineNr == this.iTotalLines) //if we have no more lines
        {
            this.bGetLine = false; //set signal to let other function know.
            return;
        }
            //copy the line into token string, and copy and rest everything else
            this.sTokenStr = this.sourceLineM.get(this.iSourceLineNr).toString();
            this.textCharM = this.sTokenStr.toCharArray();
            this.iLen = this.textCharM.length;
            this.iColPos=0;
            
            
            //then print the line
            //printCurrentLine(); //print the current line
            
        }

        return;
    }
    
    /**
     * gets the next line of text from
     * the array list of lines
     * <p>
     * This function also checks to see if there are no
     * more lines to get and sets a boolean indicator
     * for other functions to know there are no more lines
     * called bGetLine
     * @return void
     */
    public void setPosition(int iColPos, int iSourceLineNm) throws Error
    {
        
        

        if(bGetLine == false)
        {
            this.bGetLine = true;
            this.bEndToken = true;
        }
        

        this.iSourceLineNr = iSourceLineNm-1;
        
        getNextLine();
        this.iColPos = iColPos;
        getToken();
        getToken();




        return;
    }

    
    public void backToken() throws Error
    {
        
        setPosition(this.ibColPos, this.ibLineNr);
        
       
        return;
    }

    /**
     * prints the current line if it has not already
     * been printed.
     * <p>
     *     checks to see if we have printed this line
     *     and if we have NOT we will print it, ensuring it
     *     to print once and before the tokens are printed.
     * </p>
     *
     * @return void
     */
    public void printCurrentLine()
    {
        
        System.out.println("  " + this.iSourceLineNr + " " + this.sTokenStr);
        
        return;
    }


    /**
     * Returns a String of the currentToken but calculates the next
     * Token.
     * <p>
     * This function calculates the nextToken and its classifications
     * and transfers the previous nextToken to currentToken with all the
     * classifications to return the current token. This function also stores
     * important tokens into the symbol table for reuse of the token's properties.
     *
     * @return String of the currentToken to be used by the parser.
     */
    public String getToken() throws Error
    {

       

        //Some local vars to keep track of
        //the current char and start position.
        char c;
        int start;

        //if we are at the end of the line
        if(this.iColPos >= this.iLen)
            getNextLine(); //get the next line



        if (!bGetLine) //if there are no more tokens to be read
            if(this.bEndToken)
            {
                copyTokens();
                this.bEndToken = false;
                return this.currentToken.tokenStr;
            }
            else
            {
                this.currentToken.tokenStr = "";
                return "";
            }

        
        copyTokens(); //copies the previous nextToken to the
                      //currentToken
        
        if (this.bShowToken)
        {
            System.out.print("\t\t...Token: ");
            this.currentToken.printToken();
        }
        rmWhiteSpace(); //function to remove the whitespace
		
		//Recognize arrays
        if(this.currentToken.subClassif == SubClassif.IDENTIFIER)
        {
            if(this.textCharM[this.iColPos] == '['){
                //if (this.currentToken.classStruct != ClassStruct.PRIMITIVE)
                    this.currentToken.classStruct = ClassStruct.FIXED_ARRAY;
                this.currentToken.tkPrec = 16;
                this.currentToken.stPrec = 0;
                this.currentToken.bArrayBracket = true;
                
                
                STEntry entry = this.symbolTable.hashMap.get(this.currentToken.tokenStr);
                if (entry instanceof STIdentifier)
                {
                    STIdentifier idEntry = (STIdentifier) entry;
                    idEntry.structure = ClassStruct.FIXED_ARRAY;
                    idEntry.stPrec = 0;
                    idEntry.tkPrec = 16;
                   this.symbolTable.putSymbol(this.currentToken.tokenStr, idEntry);
                }
                
                this.iColPos++;
                rmWhiteSpace();
                
                
                
            }
        }
   
        start = iColPos; //save the start position
        

        while (true)
        {
            //System.out.printf("sourceLine#=%s iColPos=%d iLen=%d\n", this.iSourceLineNr, this.iColPos, this.iLen);
            //System.out.printf("currentLine=[%s]\n", this.sTokenStr);

            if(!this.bGetLine) //if we run out of line, return the currentToken.
                return this.currentToken.tokenStr;

            if(this.iColPos >= this.iLen) //if we are at the end of the line
            { //the token is the starting to the end

                
                //save the tokens attributes.
                this.nextToken.tokenStr = this.sTokenStr.substring(start, iColPos);
                this.nextToken.primClassif = Classif.OPERAND;
                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                this.nextToken.stPrec = 0;
                this.nextToken.tkPrec = 0;
                 this.nextToken.classStruct = ClassStruct.PRIMITIVE;
                //get the entry form the symbol table and see what type of entry it is
                STEntry entry = this.symbolTable.hashMap.get(this.nextToken.tokenStr);
                        if (entry instanceof STIdentifier)
                        {
                            STIdentifier idEntry = (STIdentifier) entry;
                            this.nextToken.primClassif = idEntry.primClassif;
                            this.nextToken.subClassif = idEntry.dclType;
                          
                        } else if (entry instanceof STFunction)
                        {
                            STFunction idEntry = (STFunction) entry;
                            this.nextToken.primClassif = idEntry.primClassif;
                            this.nextToken.subClassif = idEntry.type;
                        } else if (entry instanceof STControl)
                        {
                            STControl idEntry = (STControl) entry;
                            this.nextToken.primClassif = idEntry.primClassif;
                            this.nextToken.subClassif = idEntry.subClassif;
                        } 
                        //once the next token's attrubutes are restored
                
               
                //return the current token's string
                return this.currentToken.tokenStr;

            }

            c = this.textCharM[iColPos]; //save the current char.
            //System.out .println("c=[" + c + "]");

            if (delimiters.indexOf(c) != -1) //if we found delimiter
            {
                if (this.iColPos == start) //if the token is the delimiter
                {

                    //Set the nextToken string value
                    this.nextToken.tokenStr = Character.toString(this.textCharM[iColPos]);
                    this.nextToken.stPrec = 0;
                    this.nextToken.tkPrec = 0;
                    this.nextToken.iColPos = this.iColPos;
                    this.nextToken.iSourceLineNr = this.iSourceLineNr;
                     this.nextToken.classStruct = ClassStruct.PRIMITIVE;

                    switch (this.nextToken.tokenStr) //determine what type of delimiter it is
                    {
                        case "/": //if we found a comment
                            if(this.sTokenStr.charAt(this.iColPos+1) == '/')
                            {
                                comment(); //do the comment routine
                                this.iColPos--; 
                            }
                            else
                            {
                                if(this.sTokenStr.charAt(this.iColPos+1) == '=')
                                {
                                    this.iColPos++;
                                    //assign the new operator to the token string
                                    this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos+1);
                                }
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.EMPTY;
                                this.nextToken.tkPrec = 9;
                                this.nextToken.stPrec = 9;
                            }
                            break;
                        case "\"": //if we found a string literal,
                            stringDouble(); //do the double quotes routine
                            break;
                        case "\'": //if we found a string literal,
                            stringSingle(); //do the single quotes routine
                            break;
                        case "+": //the rest of these cases just set the proper classifications.
                            if(this.sTokenStr.charAt(this.iColPos+1) == '=')
                                {
                                    this.iColPos++;
                                    //assign the new operator to the token string
                                    this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos+1);
                                }
                            this.nextToken.primClassif = Classif.OPERATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            this.nextToken.tkPrec = 8;
                            this.nextToken.stPrec = 8;
                            break;
                        case "-":
                            this.nextToken.primClassif = Classif.OPERATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            if(this.sTokenStr.charAt(this.iColPos+1) == '=')
                                {
                                    this.iColPos++;
                                    //assign the new operator to the token string
                                    this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos+1);
                                }
                            else
                                unaryMcheck();
                                this.nextToken.tkPrec = 8;
                                this.nextToken.stPrec = 8;
                                break;
                        case "*":
                            if(this.sTokenStr.charAt(this.iColPos+1) == '=')
                                {
                                    this.iColPos++;
                                    //assign the new operator to the token string
                                    this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos+1);
                                }
                            this.nextToken.primClassif = Classif.OPERATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            this.nextToken.tkPrec = 9;
                            this.nextToken.stPrec = 9;
                            break;
                        case "<": 
                            this.nextToken.tkPrec = 6;
                            this.nextToken.stPrec = 6;
                            
                            //checks to see if it is a compound operator
                            if (this.iColPos+1 != this.iLen &&  this.textCharM[this.iColPos+1] == '=')
                            {
                                this.iColPos++;
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.EMPTY;
                                //assign the new operator to the token string
                                this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos+1);
                            }
                            else //normal operator
                            {
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.EMPTY;
                            }
                            break;
                        case ">":
                            
                            this.nextToken.tkPrec = 6;
                            this.nextToken.stPrec = 6;
                            //checks to see if it is a compound operator
                            if (this.iColPos+1 != this.iLen &&  this.textCharM[this.iColPos+1] == '=')
                            {
                                this.iColPos++;
                             
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.EMPTY;
                                this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos+1);
                                
                            }
                            else
                            {
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.EMPTY;
                            }
                            break;
                        case "!":
                            this.nextToken.tkPrec = 6;
                            this.nextToken.stPrec = 6;
                            //checks to see if it is a compound operator
                            if (this.iColPos+1 != this.iLen &&  this.textCharM[this.iColPos+1] == '=')
                            {
                                this.iColPos++;
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.EMPTY;
                                //assign the new operator to the token string
                                this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos+1);
                                
                            }
                            else 
                            {
                                
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.EMPTY;
                            }
                            break;
                        case "=":
                            
                            //checks to see if it is a compound operator
                             if (this.iColPos+1 != this.iLen && this.textCharM[this.iColPos+1] == '=')
                            {
                                this.nextToken.tkPrec = 6;
                                this.nextToken.stPrec = 6;
                                //System.out.println("next is not space but textM=" + this.textCharM[this.iColPos+1]);
                                this.iColPos++;
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.EMPTY;
                                this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos+1);
                                
                            }
                            else
                            {
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.EMPTY;
                            }
                            break;
                        case "#":
                            this.nextToken.tkPrec = 7;
                            this.nextToken.stPrec = 7;
                            this.nextToken.primClassif = Classif.OPERATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            break;
                        case "^":
                            if(this.sTokenStr.charAt(this.iColPos+1) == '=')
                                {
                                    this.iColPos++;
                                    //assign the new operator to the token string
                                    this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos+1);
                                }
                            this.nextToken.tkPrec = 11;
                            this.nextToken.stPrec = 10;
                            this.nextToken.primClassif = Classif.OPERATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            break;
                        case "(":
                            this.nextToken.tkPrec = 15;
                            this.nextToken.stPrec = 2;
                            this.nextToken.primClassif = Classif.SEPARATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            break;
                        case ")":
                            this.nextToken.primClassif = Classif.SEPARATOR;
                            this.nextToken.subClassif = SubClassif.RT_PAREN;
                            break;
                        case ":":
                            this.nextToken.primClassif = Classif.SEPARATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            break;
                        case ";":
                            this.nextToken.primClassif = Classif.SEPARATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            break;
                        case "[":
                            this.nextToken.primClassif = Classif.SEPARATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            break;
                        case "]":
                            this.nextToken.primClassif = Classif.SEPARATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            this.nextToken.bArrayBracket = true;
                            break;
                        case ",":
                            this.nextToken.primClassif = Classif.SEPARATOR;
                            this.nextToken.subClassif = SubClassif.EMPTY;
                            break;
                       
                    }

                    this.nextToken.iColPos = this.iColPos; //save the current iColPos to the token.
                    this.nextToken.iSourceLineNr = this.iSourceLineNr; //save the lineNumber to the token
                    this.iColPos++; //load the next char into the iColPos
                    return this.currentToken.tokenStr; //returns the current token string

                }
                else //if the nextToken is not a delimiter.
                {

                   

                    //save the token string in nextToken
                    this.nextToken.tokenStr = this.sTokenStr.substring(start, iColPos);
                    this.nextToken.stPrec = 0;
                    this.nextToken.tkPrec = 0;
                    this.nextToken.iColPos = this.iColPos;
                    this.nextToken.iSourceLineNr = this.iSourceLineNr;
                    this.nextToken.classStruct = ClassStruct.PRIMITIVE;
                    this.nextToken.bArrayBracket = false;


                   //if Token does not exist in the symbolTable
                    if(!this.symbolTable.hashMap.containsKey(this.nextToken.tokenStr))
                    { 

                        switch (this.currentToken.tokenStr) //switches on current token
                        { //if the current case is an Declaration, the nextToken will be of that type
                            case "Int": //each case save the correct classificaton of both currentToken and nextToken.
                                this.currentToken.primClassif = Classif.CONTROL;
                                this.currentToken.subClassif = SubClassif.DECLARE;
                                this.nextToken.primClassif = Classif.OPERAND;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif));
                                //also stores them in the symbol table for later reference.
                                break;
                            case "Float":
                                this.currentToken.primClassif = Classif.CONTROL;
                                this.currentToken.subClassif = SubClassif.DECLARE;
                                this.nextToken.primClassif = Classif.OPERAND;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif));
                                break;
                            case "String":
                                this.currentToken.primClassif = Classif.CONTROL;
                                this.currentToken.subClassif = SubClassif.DECLARE;
                                this.nextToken.primClassif = Classif.OPERAND;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif));
                                break;
							case "Date":
                                this.currentToken.primClassif = Classif.CONTROL;
                                this.currentToken.subClassif = SubClassif.DECLARE;
                                this.nextToken.primClassif = Classif.OPERAND;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif));
                                break;
                            case "Boolean":
                                this.currentToken.primClassif = Classif.CONTROL;
                                this.currentToken.subClassif = SubClassif.DECLARE;
                                this.nextToken.primClassif = Classif.OPERAND;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif));
                                break;
                            default: //it is not an a decleration, set the nextToken's classifications to empty
                                this.nextToken.primClassif = Classif.OPERAND;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif));
                                break;

                        }
                        
                        switch(this.nextToken.tokenStr)
                        {
                            case "IN":
                                
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;
                                this.nextToken.stPrec = 6;
                                this.nextToken.tkPrec = 6;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif, this.nextToken.stPrec, this.nextToken.tkPrec));
                                break;
                            case "NOTIN":
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;
                                this.nextToken.stPrec = 6;
                                this.nextToken.tkPrec = 6;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif, this.nextToken.stPrec, this.nextToken.tkPrec));
                                break;
                            case "NOT":
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;
                                this.nextToken.stPrec = 5;
                                this.nextToken.tkPrec = 5;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif, this.nextToken.stPrec, this.nextToken.tkPrec));
                                break;
                            case "AND":
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;
                                this.nextToken.stPrec = 4;
                                this.nextToken.tkPrec = 4;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif, this.nextToken.stPrec, this.nextToken.tkPrec));
                                break;
                            case "OR":
                                //System.out.print("reaching\n");
                                this.nextToken.primClassif = Classif.OPERATOR;
                                this.nextToken.subClassif = SubClassif.IDENTIFIER;
                                this.nextToken.iSourceLineNr = this.iSourceLineNr;
                                this.nextToken.iColPos = this.iColPos;
                                this.nextToken.stPrec = 4;
                                this.nextToken.tkPrec = 4;

                                this.symbolTable.hashMap.put(this.nextToken.tokenStr, new STIdentifier(this.nextToken.tokenStr, this.nextToken.primClassif, this.nextToken.subClassif, this.nextToken.stPrec, this.nextToken.tkPrec));
                                break;
                        }
                        
                        
                        
                    } else //if it is already in the symbol table
                    {
                        //System.out.printf("NewToken [%s] is int the symboleTable\n", this.nextToken.tokenStr);
                        
                        this.nextToken.iColPos = this.iColPos;
                        this.nextToken.iSourceLineNr = this.iSourceLineNr;
                        
                        STEntry entry = this.symbolTable.hashMap.get(this.nextToken.tokenStr);
                        if (entry instanceof STIdentifier)
                        {
                            STIdentifier idEntry = (STIdentifier) entry;
                            this.nextToken.primClassif = idEntry.primClassif;
                            this.nextToken.subClassif = idEntry.dclType;
                            if(idEntry.stPrec > 0 && idEntry.tkPrec > 0)
                            {
                                this.nextToken.stPrec = idEntry.stPrec;
                                this.nextToken.tkPrec = idEntry.tkPrec;
                                this.nextToken.bArrayBracket = false;
                            }
                            //this.nextToken.classStruct = idEntry.structure;
                            
                            if(idEntry.structure != null)
                                this.nextToken.classStruct = idEntry.structure;
                          
                        } else if (entry instanceof STFunction)
                        {
                            STFunction idEntry = (STFunction) entry;
                            this.nextToken.primClassif = idEntry.primClassif;
                            this.nextToken.subClassif = idEntry.type;
                            this.nextToken.stPrec = idEntry.stPrec;
                            this.nextToken.tkPrec = idEntry.tkPrec;
                            
                        } else if (entry instanceof STControl)
                        {
                            STControl idEntry = (STControl) entry;
                            this.nextToken.primClassif = idEntry.primClassif;
                            this.nextToken.subClassif = idEntry.subClassif;
                        } 
                        
                        
                        if(this.nextToken.primClassif == Classif.FUNCTION)
                        {
                            this.nextToken.tkPrec = 16;
                            this.nextToken.stPrec = 2;
                        }

                    }
      
                    return this.currentToken.tokenStr;

                }

            }


            //if the char is a digit
            if(Character.isDigit(this.textCharM[start]))
            { //sub-routine to validate digits
               
                //System.out.printf("\t\t...start=%d, iColPos=%d\n", start, this.iColPos);
                //System.out.printf("\t\t...iColPos=%d\n", this.iColPos);
               //System.out.println("alsjdflaksjdflkajds;kjfkja;sldjfja;sldjkf");
                int decPos;
                this.nextToken.stPrec = 0;
                this.nextToken.tkPrec = 0;
                this.nextToken.classStruct = ClassStruct.PRIMITIVE;
                boolean bDecimal=false; //a decimal indicator
                while (iColPos < iLen) { //while the there is more line left
                    //System.out.printf("\t\t...iColPos=%d\n", this.iColPos);

                    if (this.delimiters.indexOf(this.textCharM[this.iColPos]) != -1) //if it is a delimiter.
                    {
                        //get the string of numbers and save it to the nextToken String.
                        this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos);


                        if(this.nextToken.tokenStr.indexOf('.') != -1) //check if there is a decimal
                        {
                            //if there is decimal, the nextToken is a float.
                            this.nextToken.primClassif = Classif.OPERAND;
                            this.nextToken.subClassif = SubClassif.FLOAT;

                            return this.currentToken.tokenStr;
                        }
                        else //nextToken is an Int
                        {
                            this.nextToken.primClassif = Classif.OPERAND;
                            this.nextToken.subClassif = SubClassif.INTEGER;

                            return this.currentToken.tokenStr;
                        }

                    }

                    if(!Character.isDigit(this.textCharM[this.iColPos])) //checks to see if the current char is not a digit
                    {
                        
                        this.nextToken.tkPrec = 0;
                        this.nextToken.stPrec = 0;

                        if(this.textCharM[this.iColPos] == '.') //if it is a decimal
                        {
                            //check to see if there is a char that is a digit after the decimal and if we have see a deciaml already
                            if(this.iColPos + 1 == this.iLen || !Character.isDigit(this.textCharM[this.iColPos+1]) || bDecimal)
                                errorWithLine("Expecting digits after '.'");
                            
                            bDecimal = true;  //set the decimal signal to ture.
                        }
                        else
                            break; //if it is not a decimal, than error
                    }

                    this.iColPos++; //increase the col position
                }
                //throw new Exception("ERROR Line %d: %s\nFILE: %s\nInvalid number declaration\n", this.iSourceLineNr, this.sTokenStr, this.sourceFileNm);
                //System.out.printf("\t\t...textCharM[%d] = %c\n", this.iColPos-1, this.textCharM[this.iColPos - 1]);
                
                this.nextToken.primClassif = Classif.OPERAND;
                this.nextToken.subClassif = SubClassif.INTEGER;
                this.nextToken.tokenStr = this.sTokenStr.substring(start, this.iColPos);
                getNextLine();
                return this.currentToken.tokenStr;
                //throw new Error("Line " + this.iSourceLineNr + " Invalid Number: " + this.sTokenStr);
                
                
            }
            this.iColPos++; //increase the col position

        }

    }

    
    
    
    
    


/**
 * This function is to skip over any comments.
 * <p>
 * 
 * @return void
 */
    public void comment()
    {
       //load the next line
        getNextLine();
        //save the current string
        String oldCurrentToken = this.currentToken.tokenStr;
        try 
        {
            //remove any whitespace if any
            rmWhiteSpace();
            //load the new next token which also advances the 
            //current token to '/' from the comment
            getToken();
          
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
        //restore the old current token
        this.currentToken.tokenStr = oldCurrentToken;
        return;
    }

   
    /**
     * ends on the semicolon or colon where exec should then 
     * try and get the next token which should be the first
     * token of the next statement. <p>
     * This also loads the new tokens into the current and next tokens
     * for the parser to take over calling getNext.
     * 
     * @throws Error
     * @throws Exception 
     */
	
     public void skipBoth() throws Error
    {
        boolean found = false;
        //while there is more line left
		while(this.iColPos <= this.iLen)
        {   
            //System.out.printf("Current textCharM[%d]=\'%c\'\n", this.iColPos, this.textCharM[this.iColPos]);
            if(this.textCharM[this.iColPos] == ';' || this.textCharM[this.iColPos] == ':')
            {
                found = true;
                break;
            }
            this.iColPos++;
			if(this.iColPos == this.iLen)
			{
				getNextLine();
				rmWhiteSpace();
				
			}
        }
        //if(!this.currentToken.tokenStr.equals(";" ) || !this.currentToken.tokenStr.equals(";"))
        //    System.out.printf("TRUE:::\n");
        
        if(found != true && this.currentToken.tokenStr.equals(";" ) || this.currentToken.tokenStr.equals(";"))
            errorWithCurrent("Missing \':\' or \';\' at the end of expression.");
        
        //System.out.println("before currentToken=" + this.currentToken.tokenStr);
        if(this.iColPos == this.iLen)
        {
            getNextLine();
            getToken();
            //System.out.println("new CurrentToken=" + this.currentToken.tokenStr);
            return;
        }
        
        getToken();
        getToken();
        
        //System.out.println("new CurrentToken=" + this.currentToken.tokenStr);
        
        return;
    }

    
  

    /**
     * This function is a process that checks a
     * single quote string to see if it is valid
     * and set the nextToken properties to appropriate
     * values.
     * <p>
     * @return void
     */
    private void stringSingle() throws Error
    {
        //create a new char[] to hold the new string
        char stringLit[] = new char[this.iLen];
        //counter is our position in the new char[]
        int counter=0;

        if(iColPos>=iLen) //if we are at the end of the line
        {
            throw new Error("Line " + this.iSourceLineNr + " Invalid string: " + this.sTokenStr);
        }

        this.iColPos++; //skip over the single quote

        while(iColPos < iLen) //loop while there is still line left
        {

             //range of ascii printable char.
            if(this.textCharM[this.iColPos] > 31 && this.textCharM[this.iColPos] < 127)
            {  
                if(this.textCharM[this.iColPos] == '\\') //handel the \ cases
                {
                    if(this.iColPos+1<iLen)
                    {
                        switch (this.textCharM[iColPos + 1])
                        {
                            case '\'':
                                this.iColPos += 2;
                                stringLit[counter] = '\'';
                                counter++;
                                break;
                            case '\"':
                                this.iColPos += 2;
                                stringLit[counter] = '\"';
                                counter++;
                                break;
                            case 'n':
                                this.iColPos += 2;
                                stringLit[counter] = 0x0A;
                                counter++;
                                break;
                            case 't':
                                this.iColPos += 2;
                                stringLit[counter] = 0x09;
                                counter++;
                                break;
                            case '\\':
                                this.iColPos += 2;
                                stringLit[counter] = '\\';
                                counter++;
                                break;
                            case 'a':
                                this.iColPos +=2;
                                stringLit[counter] = 0x07;
                                counter++;
                                break;
                        }
                        continue;
                    }
                    else
                    {
                        throw new Error("Line " + this.iSourceLineNr + " Invalid string: " + this.sTokenStr);

                    }
                }

                if(this.textCharM[this.iColPos] == '\'') //if we are at the end of the string
                {


                    this.nextToken.tokenStr = new String(stringLit); //copy new string
                    //set the attributes of the token
                    this.nextToken.primClassif = Classif.OPERAND;
                    this.nextToken.subClassif = SubClassif.STRING;
					
					if(Date.validate(this.nextToken.tokenStr) == 0)		//if a valid date set subclass to DATE
						this.nextToken.subClassif = SubClassif.DATE;
					
                    return;
                }
                else
                {
                    stringLit[counter] = this.textCharM[this.iColPos];

                    counter++;
                    this.iColPos++;
                }
            }
            else
                this.iColPos++;
            
             if(this.iColPos == this.iLen)
            {
                getNextLine();
                rmWhiteSpace();
            }
            
        }

        throw new Error("Line " + this.iSourceLineNr + " Invalid string:" + this.sTokenStr);

    }


    /**
     * This function is a process that checks a
     * double quote string to see if it is valid
     * and set the nextToken properties to appropriate
     * values.
     * <p>
     * @return void
     */
    private void stringDouble() throws Error
    {

        
        //create a new char[] to hold the new string
        char stringLit[] = new char[this.iLen];
        //counter is our position in the new char[]
        int counter=0;
        char c;
        

       
        
        this.iColPos++; //skip over the double quote
        
         if(this.iColPos == this.iLen)
        {
            getNextLine();

            rmWhiteSpace();

        }


         c = this.textCharM[iColPos];
         
        while(iColPos < iLen) //loop while there is still chars left
        {
            
            c = this.textCharM[iColPos];
            
           //range of ascii printable char.
            if(c > 31 && c < 127)
            {
                if(c == '\\') //handel the \ cases
                {
                    //makes sure there is something escaped.
                    if(this.iColPos == this.iLen)
                    {
                        String err = String.format("Line " + this.iSourceLineNr + " Invalid string:  " + this.sTokenStr + "\nExpected \'\"\'");
                        throw new Error(err);
                    }


                    if(this.iColPos+1<iLen)
                    {
                        switch (this.textCharM[iColPos + 1])
                        {
                            case '\'':
                                this.iColPos += 2;
                                stringLit[counter] = '\'';
                                counter++;
                                break;
                            case '\"':
                                this.iColPos += 2;
                                stringLit[counter] = '\"';
                                counter++;
                                break;
                            case 'n':
                                this.iColPos += 2;
                                stringLit[counter] = 0x0A;
                                counter++;
                                break;
                            case 't':
                                this.iColPos += 2;
                                stringLit[counter] = 0x09;
                                counter++;
                                break;
                            case '\\':
                                this.iColPos += 2;
                                stringLit[counter] = '\\';
                                counter++;
                                break;
                            case 'a':
                                this.iColPos +=2;
                                stringLit[counter] = 0x07;
                                counter++;
                                break;
                            default:
                               this.iColPos++;
                        }
                        
                        
                         
                    }
                    else
                    {
                        //System.out.printf("Error with  c=[%c]\n", this.textCharM[this.iColPos]);
                        throw new Error("Line " + (this.iSourceLineNr+1) + " Invalid string: " + this.sTokenStr);

                    }
                }
                
                
                

                if(this.textCharM[this.iColPos] == '\"') //if we are at the end of the string
                {
                    this.nextToken.tokenStr = new String(stringLit); //copy new string
                    //set the attributes of the token
                    this.nextToken.primClassif = Classif.OPERAND;
                    this.nextToken.subClassif = SubClassif.STRING;
					
					//String temp = new String();
					//temp = this.nextToken.tokenStr.trim();
					if(Date.validate(this.nextToken.tokenStr.trim())==0)		//if a valid date set subclass to DATE
						this.nextToken.subClassif = SubClassif.DATE;
                    
                    //this.iColPos++;
					
                    return;
                }
                else 
                {
                    stringLit[counter] = this.textCharM[this.iColPos];
                    counter++;
                    this.iColPos++;
                    
                 
                }
            }
            else
                this.iColPos++;
            
            if(this.iColPos == this.iLen)
            {
                getNextLine();

                rmWhiteSpace();

            }
            
            
            
        }
        
        String err = String.format("Line %d  Invalid string: %s\nMissing \'\"\'", this.iSourceLineNr+1, this.sTokenStr);
        throw new Error(err);
    }
    
    /**
     * Create a new copy of the current token to prevent unwanted alteration of current token
     * @return 
     */
    public Token getCurrentToken()
    {
        Token newToken = new Token();
        newToken.iColPos = this.currentToken.iColPos;
        newToken.iSourceLineNr = this.currentToken.iSourceLineNr;
        newToken.primClassif = this.currentToken.primClassif;
        newToken.stPrec = this.currentToken.stPrec;
        newToken.subClassif = this.currentToken.subClassif;
        newToken.tkPrec = this.currentToken.tkPrec;
        newToken.tokenStr = this.currentToken.tokenStr;
        newToken.classStruct = this.currentToken.classStruct;
        
        
        return newToken;
    }

    /**
     * check if the current token is empty
     * <p>
     *
     * @return boolean if it the current token string is
     *                 empty.
     */
    public boolean isEmpty()
    {
       return this.currentToken.isEmpty();
    }


    
    
    
    public void errorWithCurrent(String msg) throws Error
    {
        String err = String.format("Line %d: \'%s\'\nToken: '%s'\nMessage: %s\n", this.currentToken.iSourceLineNr+1, this.currentToken.tokenStr, this.currentToken.tokenStr, msg);
        throw new Error(err);
    }
    
    public void errorWithNext(String msg) throws Error
    {
        String err = String.format("Line %d: \'%s\'\nToken: %s\nMessage: %s\n", this.iSourceLineNr, Utility.CharArrayToString(textCharM), this.currentToken.tokenStr, msg);
        throw new Error(err);
    }
    
    public void errorWithLine(String msg) throws Error
    {
        String err = String.format("Line %d: \'%s\'\nMessage: %s\n", (this.iSourceLineNr+1), Utility.CharArrayToString(textCharM), msg);
        throw new Error(err);
    }
    
    
    public void debugMessage(String msg) throws Error
    {
        String fMsg = String.format("\t\t...%s", msg);
        System.out.println(fMsg);
    }
	public void skipTo(String value) throws Error
	{
		
		if(value.equals(";"))
		{
			semiSkip();
			return;
		}
			
		if(value.equals(":"))
		{
			skipColon();
			return;
		}
		
		//while the current token does not equal the delimeter
		while(!this.currentToken.tokenStr.equals(value))
		{   
			//if we have encountered a end statement
			if(this.currentToken.tokenStr.equals(";") || this.currentToken.tokenStr.equals(":"))

				errorWithLine("Did not locate '" + value + "' in the current statment");

			getToken();

		}
        
        return;
	}
	
	
	
	public void semiSkip() throws Error
	{
		while(this.currentToken.tokenStr.charAt(0) != ';')
		{   
			//if we have encountered a end statement
			if(this.currentToken.tokenStr.equals(":"))

				errorWithLine("Did not locate ';' in the current statment");

			getToken();

		}
	}
	
    
    
	
	
	public boolean curIsColon()
	{
		if(this.currentToken.tokenStr.equals(":"))
			return true;
		return false;
	}
	public boolean curIsSemi()
	{
		if(this.currentToken.tokenStr.equals(";"))
			return true;
		return false;
	}
	
	public boolean curIsValue(String value)
	{
		if(this.currentToken.tokenStr.equals(value))
			return true;
		return false;
	}
	
	
    public void skipColon() throws Error
	{
		while(this.currentToken.tokenStr.charAt(0) != ':')
		{   
			//if we have encountered a end statement
			if(this.currentToken.tokenStr.equals(";"))

				errorWithLine("Did not locate ':' in the current statment");

			getToken();

		}
	}

    /**
     * Set the subClass to unary minus of the 
     * nextToken if it is an unary minus.
     * <p> 
     * 
     * @return void
     */
    public void unaryMcheck() 
    {
       
        //if there was an opertor before the '-'
        if(this.currentToken.primClassif == Classif.OPERATOR)
        {
            this.nextToken.subClassif = SubClassif.UNARY_M;
            return;
        }
        //if there was an contol token before the '-'
        if(this.currentToken.primClassif == Classif.CONTROL)
        {
            this.nextToken.subClassif = SubClassif.UNARY_M;
            return;
        }
        //if it is an unary seperator
        if(unarySeps.contains(this.currentToken.tokenStr) || curIsValue("by") || curIsValue("to"))
            this.nextToken.subClassif = SubClassif.UNARY_M;
    
        
        return;
        
       
    }
	
	public ResultValue tokenToResultV(Token value)
	{
		ResultValue newRes = new ResultValue();
		
		newRes.structure = value.classStruct;
		newRes.type = value.subClassif;
		newRes.value = value.tokenStr;

        
		
		return newRes;		
	}
	
	
	public ResultValue currentToResult(Parser parse) throws Error
	{
		
            ResultValue newRes = new ResultValue();
            
            //if it is not an operand, there is no result value
            if(parse.scan.currentToken.primClassif != Classif.OPERAND)
                parse.scan.errorWithCurrent("Expecting an operand.");
	
            //if it is an identifier, get the stored ResultValue
            if(parse.scan.currentToken.subClassif == SubClassif.IDENTIFIER)
            {
                //if it has not been declared yet, error
                if(!parse.storage.checkKey(parse, this.currentToken.tokenStr))
                        parse.scan.errorWithCurrent("Value has not been declared yet.");
                //return the stored result value.
                return parse.storage.getValue(parse, this.currentToken.tokenStr);
            }
            
            
              
            newRes.type = this.currentToken.subClassif;
            newRes.value = this.currentToken.tokenStr;
            newRes.structure = ClassStruct.PRIMITIVE;
              
            return newRes;		
	}
    
    
    



}
