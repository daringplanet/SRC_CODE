/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meatbol;

import enums.SubClassif;
import enums.ClassStruct;
import java.util.ArrayList;

/**
 *
 * @author darin
 */
public class ResultValue 
{
    SubClassif type;            // usually data type of the result
    String value;               // value of the result
    ClassStruct structure;      // primitive, fixed aray, unbounded array
    String terminatingStr;      // used for end of lists of things
    String debugONoff;
    ArrayList<ResultValue> arrayVals;
    int arraySize;
    int currentSize = 0;
    boolean bSizeSet = false;
    
    
    public ResultValue()
    {
        type=null;
        value=null;
        structure=null;
        terminatingStr=null;
    }
    
    
    
    
    public ResultValue(SubClassif type)
    {
        this.type = type;
        value=null;
        structure=null;
        terminatingStr=null;
    }
    
    public ResultValue( ClassStruct struct)
    {
        this.type = null;
        value=null;
        structure=struct;
        terminatingStr=null;
    }
    
    public ResultValue(SubClassif type, String value, ClassStruct struct, String termStr)
    {
        this.type = type;
        this.value = value;
        this.structure = struct;
        this.terminatingStr = termStr;
    }
    
    /***
     * This functions is mainly used to convert a Result Value back to a token
     * for minimal purposes. 
     * @return 
     */
    public Token toToken()
    {
        Token tk = new Token();
        
        tk.tokenStr = this.value;
        tk.subClassif = this.type;
        
        return tk;
    }
    
    
    
    ResultValue getArrayElem(int index, boolean isString) throws Error
    {
        if (isString)
        {
            ResultValue res = new ResultValue();
            res.structure = ClassStruct.PRIMITIVE;
            res.type = this.type;
            res.value = Character.toString(this.value.charAt(index));
            
            return res;
        }
        
        //if the array has not been initialized.
        if(this.arrayVals == null)
            Utility.utilError("getArrayElem", "Array has not been initialized after declared.");
        
        //if the index is not valid for the current index range
        if(index >=  this.arrayVals.size() || index < 0) 
            Utility.utilError("getArrayElem", "index bound error for array " + this.value 
                + ". Tried to access " + index + " when range is currently at " + this.arrayVals.size() 
                + " and a max range of " + this.arraySize);
        
       
        //get the array elem
        ResultValue res = this.arrayVals.get(index);
        
        //return a copy of it.
        return Utility.getCopy(res);
    }
    
    
    
    
    
    
    
    
    public ResultValue getNewValue()
    {
        return new ResultValue();
    }
    
    
    public void putArrayValue(Parser parse, ResultValue newArrayElem, int index) throws Error
    {
        //used to detect if we are creating entry until the index spot if no entry were populated

        boolean creating = false;
        
        //if the array is null, make a new array
        if(this.arrayVals == null)
            this.arrayVals = new ArrayList<ResultValue>();
        
        newArrayElem.structure = ClassStruct.ARRAY_ELEM;
        
        
        //if the size has already  been set, make sure index is in the correct max range
        if(this.bSizeSet)
            if(index > this.arraySize)
                Utility.utilError("getArrayElem", "index bound error for array " + this.value 
                    + ". Tried to access " + index + " when range is currently at " + this.arrayVals.size() 
                    + " and a max range of " + this.arraySize);
        
        //check to see if the index is below 0
        if(index < 0)
            Utility.utilError("getArrayElem", "index bound error for array " + this.value 
                    + ". Tried to access " + index + " when range is currently at " + this.arrayVals.size() 
                    + " and a max range of " + this.arraySize);

        //while the index is greater than the current size
        while(index >= this.arrayVals.size())
        {

            //create a new result value to store to grow the array to the index spot
            ResultValue r = new ResultValue();
            //set it to be an array element
            r.structure = ClassStruct.ARRAY_ELEM;
            //set its type to be the same
            r.type = this.type;
            //set its value to be empty due to not having a true value.
            r.value = "";
            
            //signal that we started creating entrys
            creating=true;
            
            //add it to the array, which will increase the array size
            this.arrayVals.add(r);
            //increase the currentSize count
            this.currentSize++;

        }
        
        //if the new data type does not match, try and coherce it to the same data type
        if(newArrayElem.type != this.type)
        {
            
            switch(this.type)
            {
                case INTEGER:
                  newArrayElem = Utility.intCoherce(parse, Utility.resValueToToken(parse, newArrayElem));
                    break;
                case FLOAT:
                    newArrayElem = Utility.floatCoherce(parse, Utility.resValueToToken(parse, newArrayElem));
                    break;
                case STRING:
                    newArrayElem = Utility.stringCoherce(parse, newArrayElem);
                    break;
                default:
                Utility.utilError("putArrayValue", "Not a valid data type for '" + this.type + "'. Recieved '" 
                        + newArrayElem.value + "' of type '" + newArrayElem.type);
            }
        }
        
        
            
            //replace its value at the index with 'set' instead of creating a new entry with the '.add'
            this.arrayVals.set(index, Utility.getCopy(newArrayElem));
        
            
        
        return;
    }
    
    
    
    
    
    public void putArrayValue(Parser parse, ResultValue newArrayElem) throws Error
    {
        
        //if the array is null, create the arraylist
        if(this.arrayVals == null)
            this.arrayVals = new ArrayList<ResultValue>();
        
        
        
        
        int i=0;
        //while i is lessthan the current array size
        while(i < this.arrayVals.size())
        {
            //look for empty entries to replace with the newArrayElem
            if(this.arrayVals.get(i).value.equals(""))
                break;//if we find an empty entry, break to preserve 'i' position(the index)
            
            i++;
        }
        
        //if there has been a size set, make sure that i < the array total size
        if(this.bSizeSet)
            if(i >= this.arraySize )
                Utility.utilError("putArrayValue", "Array is full");
        
        
         //if the new data type does not match, try and coherce it to the same data type
        if(newArrayElem.type != this.type)
        {
            
            switch(this.type)
            {
                case INTEGER:
                  newArrayElem = Utility.intCoherce(parse, Utility.resValueToToken(parse, newArrayElem));
                    break;
                case FLOAT:
                    newArrayElem = Utility.floatCoherce(parse, Utility.resValueToToken(parse, newArrayElem));
                    break;
                case STRING:
                    newArrayElem = Utility.stringCoherce(parse, newArrayElem);
                    break;
                default:
                Utility.utilError("putArrayValue", "Not a valid data type for '" + this.type + "'. Recieved '" 
                        + newArrayElem.value + "' of type '" + newArrayElem.type);
            }
        }
        
        
        //if index is one bigger than all the current indexes
        if(i == this.arrayVals.size())
        {
            //create a new index by adding the newArrayElem to the end of the arrayList
            this.arrayVals.add(Utility.getCopy(newArrayElem));
        }
        else
        {
            //set that index at i to be the newArrayElem(if we found a empty entire it will replace it).
            this.arrayVals.set(i, Utility.getCopy(newArrayElem));
        }
        
        
        return;
    }


    
    
}
