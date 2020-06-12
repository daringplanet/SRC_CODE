/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meatbol;

/**
 *  This Class is a place holder to be able to be use on operations as we need
 *  to return 2 value at the same time
 * @author Luis
 */
public class Operations {
    
    ResultValue valueL;
    ResultValue valueR;
    
    public Operations() 
    {
        valueL = new ResultValue();
        valueR = new ResultValue();
    }
    
   public void setValueL(ResultValue newValue)
   {
       valueL = newValue;
   }
   
   public void setValueR(ResultValue newValue)
   {
       valueR = newValue;
   }
}
