/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meatbol;

import java.util.*;
import enums.*;

/**
 *
 * @author Luis
 */

public class StorageMgr 
{
    
    public HashMap <String, ResultValue> values;
    /**
     * @author William
     */
    public StorageMgr()
    {
        values = new HashMap<String, ResultValue>();
	
    }

        // Type of new value is saved baded on subclassif passed in
        public void putValue(Parser parse, String key, Classif prim, SubClassif sub, String value) throws Error
        {

                if(key == null)   
                        parse.scan.errorWithCurrent("The key is null");

                    //need to add the class structures here when there are any such as char[],
                //or non primitive sysmbols
            ResultValue newValue = new ResultValue();
            newValue.value = value;
            newValue.structure = ClassStruct.PRIMITIVE;
            newValue.type = sub;

            this.values.put(key, newValue);
            return;	
        }
        
     // Type of new value is saved baded on subclassif passed in
        public void putValue(Parser parse, String key, Classif prim, SubClassif sub, ClassStruct classStruct, String value) throws Error
        {

                if(key == null)   
                        parse.scan.errorWithCurrent("The key is null");

                    //need to add the class structures here when there are any such as char[],
                //or non primitive sysmbols
            ResultValue newValue = new ResultValue();
            newValue.value = value;
            newValue.structure = classStruct;
            newValue.type = sub;

            this.values.put(key, newValue);
            return;	
        }
        
        
        // Type of new value is saved baded on subclassif passed in
        public void putValue(Parser parse, String key, ResultValue resValue) throws Error
        {     
            
            if(key == null)   
                parse.scan.errorWithCurrent("The key is null");

            
            if(resValue == null)
                parse.error("Function: putValue\nMessage: resValue = null");
            
            ResultValue newValue = new ResultValue();
            
            newValue = Utility.getCopy(resValue);

            this.values.put(key, newValue);
            return;	
        }
        
        
	
        public boolean checkKey(Parser parse, String key)
        {
            
            
                if(key.charAt(0) == '-')
                {
                        int i;
                        for(i=0; i<key.length(); i++)
                                if(key.charAt(i) != '-')
                                        break;
                        key = key.substring(i, key.length());

                }

                if(this.values.containsKey(key))
                        return true;

                return false;
        }
    
        public void setValue(Parser parse, String key, ResultValue value) throws Error
        {
            // Check if value already exists
            if (!values.containsKey(key))
                parse.error("StorageMgr:\nvaraible couldn't be assigned since variables has not been initialized.");

            values.put(key, value);
        } 
    
        public ResultValue getValue(Parser parse, String key) throws Error
        {
            ResultValue resReturn = new ResultValue();
            ResultValue temp = null;
            
            int i;
            
            if(this.values.isEmpty())
                parse.error("StorageMgr:\nvalues have only initialized, so they are null.");

            if(key == null)
                parse.error("SRC code function getValue: key var is null.");

            if(key.charAt(0) == '-')
            {

                for(i=0; i<key.length(); i++)
                        if(key.charAt(i) != '-')
                                break;
                
                key = key.substring(i);
                
               
                if(values.containsKey(key))
                resReturn = values.get(key);
            else
                parse.error("'" + key + "' has not been declared.");
            
                resReturn = Utility.getCopy(resReturn);
                
             while(i!=0)
            {
                resReturn.value = '-' + resReturn.value;
                i--;
            }
                
           
            return resReturn;
            }
            
            //printValuesMap(parse);
            
            
            if(values.containsKey(key))
                resReturn = values.get(key);
            else
                parse.error("'" + key + "' has not been declared.");
            
            
            
            return  Utility.getCopy(resReturn);
        }
    
        public void printValuesMap(Parser parse) throws Error
        {
            ResultValue resValue;
             if(this.values.isEmpty())
                 return;
            for(String key: this.values.keySet())
            {
                resValue = this.getValue(parse, key);
                System.out.printf("Key: %s Value: %s\n", key, resValue.value);
            }
            return;
        }
    
        
        
	
    
}
