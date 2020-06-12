/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meatbol;

/**
 *
 * @author Luis
 */
public class Numeric {

    public Parser nParse;
    public ResultValue res;
    public String valueStr;
    public String sOperandPos;
    
    
    
    public Numeric(Parser aThis, ResultValue res1, String value, String nd_operand) 
    {
        this.nParse = aThis;
        this.res = res1;
        this.valueStr = value;
        this.sOperandPos = nd_operand;
    }

    public Numeric() 
    {
     
    }
    
}
