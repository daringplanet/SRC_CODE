package meatbol;

import statement.STIdentifier;
import statement.STEntry;
import statement.STFunction;
import statement.STControl;
import enums.SubClassif;
import enums.Classif;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Hashtable;

public class SymbolTable
{

    public int VAR_ARGS=0;


    public HashMap <String, STEntry> hashMap;


    public SymbolTable ()
    {
        this.hashMap = new HashMap<String, STEntry>();
        
        initGlobal();
    }

/**
 * This function gets a entry from the 
 * symbol table
 * <p>
 * @param symbol the key for the table
 * @return STEntry an entry in the symbol table 
 */
    public STEntry getSymbol(String symbol)
    {

        return this.hashMap.get(symbol);

    }
    /**
     * This function puts a new entry in the symbol
     * table
     * <p>
     * @param symbol the key for the table
     * @param entry an entry of STEntry type to put in the table
     * @return void
     */
    public void putSymbol(String symbol, STEntry entry)
    {
        this.hashMap.put(symbol, entry);
    }

    /**
     * This function puts predefined values
     * into the symbol table.
     * <p>
     * @return void
     */
    private void initGlobal()
    {

        this.hashMap.put("def", new STControl("def", Classif.CONTROL, SubClassif.FLOW));
        this.hashMap.put("enddef", new STControl("enddef", Classif.CONTROL, SubClassif.END));
        this.hashMap.put("if", new STControl("if", Classif.CONTROL, SubClassif.FLOW));
        this.hashMap.put("endif", new STControl("endif", Classif.CONTROL, SubClassif.END));
        this.hashMap.put("else", new STControl("else", Classif.CONTROL, SubClassif.END));
        this.hashMap.put("for", new STControl("for", Classif.CONTROL, SubClassif.FLOW));
        this.hashMap.put("endfor", new STControl("endfor", Classif.CONTROL, SubClassif.END));
        this.hashMap.put("while", new STControl("while", Classif.CONTROL, SubClassif.FLOW));
        this.hashMap.put("endwhile", new STControl("endwhile", Classif.CONTROL, SubClassif.END));
        this.hashMap.put("select", new STControl("select", Classif.CONTROL, SubClassif.FLOW));
        this.hashMap.put("endselect", new STControl("endselect", Classif.CONTROL, SubClassif.END));
        this.hashMap.put("when", new STControl("when", Classif.CONTROL, SubClassif.END));
        this.hashMap.put("default", new STControl("default", Classif.CONTROL, SubClassif.END));
        

        this.hashMap.put("print", new STFunction("print", Classif.FUNCTION, SubClassif.BUILTIN, SubClassif.VOID, VAR_ARGS));

        this.hashMap.put("Int", new STControl("Int", Classif.CONTROL, SubClassif.DECLARE));
        this.hashMap.put("Float", new STControl("Float", Classif.CONTROL, SubClassif.DECLARE));
        this.hashMap.put("String", new STControl("String", Classif.CONTROL, SubClassif.DECLARE));
        this.hashMap.put("Bool", new STControl("Bool", Classif.CONTROL, SubClassif.DECLARE));
        this.hashMap.put("Date", new STControl("Date", Classif.CONTROL, SubClassif.DECLARE));

        this.hashMap.put("LENGTH", new STFunction("LENGTH", Classif.FUNCTION, SubClassif.INTEGER, SubClassif.BUILTIN, VAR_ARGS));
        this.hashMap.put("MAXLENGTH", new STFunction("MAXLENGTH", Classif.FUNCTION, SubClassif.INTEGER, SubClassif.BUILTIN, VAR_ARGS));
        this.hashMap.put("SPACES", new STFunction("SPACES", Classif.FUNCTION, SubClassif.INTEGER, SubClassif.BUILTIN, VAR_ARGS));
        this.hashMap.put("ELEM", new STFunction("ELEM", Classif.FUNCTION, SubClassif.INTEGER, SubClassif.BUILTIN, VAR_ARGS));
        this.hashMap.put("MAXELEM", new STFunction("MAXELEM", Classif.FUNCTION, SubClassif.INTEGER, SubClassif.BUILTIN, VAR_ARGS));
		
		this.hashMap.put("dateDiff", new STFunction("dateDiff", Classif.FUNCTION, SubClassif.INTEGER, SubClassif.BUILTIN, VAR_ARGS));
		this.hashMap.put("dateAdj", new STFunction("datAdj", Classif.FUNCTION, SubClassif.DATE, SubClassif.BUILTIN, VAR_ARGS));
		this.hashMap.put("dateAge", new STFunction("dateAge", Classif.FUNCTION, SubClassif.INTEGER, SubClassif.BUILTIN, VAR_ARGS));

/*
        this.hashMap.put("and", new STIdentifier("and", Classif.OPERATOR, SubClassif.IDENTIFIER));
        this.hashMap.put("or", new STIdentifier("or", Classif.OPERATOR, SubClassif.IDENTIFIER));
        this.hashMap.put("not", new STIdentifier("not", Classif.OPERATOR, SubClassif.IDENTIFIER));
        this.hashMap.put("in", new STIdentifier("in", Classif.OPERATOR, SubClassif.IDENTIFIER));
        this.hashMap.put("notin", new STIdentifier("notin", Classif.OPERATOR, SubClassif.IDENTIFIER));
       
        this.hashMap.put("AND", new STIdentifier("AND", Classif.OPERATOR, SubClassif.IDENTIFIER));
        this.hashMap.put("OR", new STIdentifier("OR", Classif.OPERATOR, SubClassif.IDENTIFIER));
        this.hashMap.put("NOT", new STIdentifier("NOT", Classif.OPERATOR, SubClassif.IDENTIFIER));
        this.hashMap.put("IN", new STIdentifier("IN", Classif.OPERATOR, SubClassif.IDENTIFIER));
        this.hashMap.put("NOTIN", new STIdentifier("NOTIN", Classif.OPERATOR, SubClassif.IDENTIFIER));
        */
        this.hashMap.put("T", new STIdentifier("T", Classif.OPERAND, SubClassif.BOOLEAN));
        this.hashMap.put("F", new STIdentifier("F", Classif.OPERAND, SubClassif.BOOLEAN));
    
        this.hashMap.put("debug", new STIdentifier("debug", Classif.DEBUG, SubClassif.EMPTY));
        this.hashMap.put("Expr", new STIdentifier("Expr", Classif.DEBUG, SubClassif.EXPRdbug));
        this.hashMap.put("Assign", new STIdentifier("Assign", Classif.DEBUG, SubClassif.ASSIGNdbug));
        this.hashMap.put("Stmt", new STIdentifier("Stmt", Classif.DEBUG, SubClassif.STATEMENTdbug));
        this.hashMap.put("Token", new STIdentifier("Token", Classif.DEBUG, SubClassif.TOKENdbug));
        
        
        
        
        this.hashMap.put("not", new STIdentifier("not", Classif.OPERATOR, SubClassif.EMPTY, 6, 6) );
        this.hashMap.put("or", new STIdentifier("or", Classif.OPERATOR, SubClassif.EMPTY, 4, 4) );
        
        
        this.hashMap.put("break", new STIdentifier("break", Classif.CONTROL, SubClassif.END, 0, 0));
        this.hashMap.put("continue", new STIdentifier("continue", Classif.CONTROL, SubClassif.END, 0, 0));
        
        
        
        this.hashMap.put("on", new STIdentifier("on", Classif.DEBUG, SubClassif.ONdbug));
        this.hashMap.put("off", new STIdentifier("off", Classif.DEBUG, SubClassif.OFFdbug));
        
    }

    
    
    
    
    
    
    /*
                            
    
                            Token popped = stack.pop();
                            
                            if(popped.subClassif != SubClassif.BOOLEAN)
                                error("Expecing a Boolean result for 'not' recieved '" + popped.tokenStr + 
                                        "' on line " + popped.iSourceLineNr);
                            
                            if(popped.tokenStr.equals("true"))
                                popped.tokenStr = "false";
                            else
                                popped.tokenStr = "true";
                            
                            stack.push(popped);
    */
    
    
    
    
    
    
    
}

