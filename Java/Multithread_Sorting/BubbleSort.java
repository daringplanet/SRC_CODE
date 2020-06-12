/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package part2;

/**
 *
 * @author lippa
 */
public class BubbleSort implements Runnable
{
    private double dSort[];
    private int start;
    private int end;
    private int start2;
    private int end2;
    
    public BubbleSort(double a[], int start, int end)
    {
        this.dSort = a;
        this.start = start;
        this.end = end;
        this.start2 = -1;
        this.end2 = -1;
    }
    

    @Override
    public void run() 
    {
        //System.out.println("*******RUN:");
        //sort the array using bubble sort.
        int i, j;
        double temp;
        
        for(i=this.start; i <= this.end; i++)
        {
            for(j = this.start; j< this.end; j++)
            {
                if(this.dSort[j] > this.dSort[j+1])
                {
                    temp = this.dSort[j];
                    this.dSort[j] = this.dSort[j+1];
                    this.dSort[j+1] = temp;
                }
            }
        }
        
        return;
    }
    
}
