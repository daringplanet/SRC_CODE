/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package part2;

/**
 *
 * @author William Lippard
 */
public class Merge implements Runnable
{

    private double dSort[];
    private int lCur;
    private int leftMax;
    private int rightMax;
    private int rCur;
    private double finalA[];


    public Merge(double dSort[], int lMax, int rStart, int rMax, double finalA[])
    {
        this.dSort = dSort;
        this.leftMax = lMax;
        this.rightMax = rMax;
        this.rCur = rStart;
        this.finalA = finalA;
        this.lCur = 0;
    }


    @Override
    public void run()
    {
        int i;
        for(i=0; i<this.dSort.length; i++)
        {
            if(this.lCur > this.leftMax)
            {
                this.finalA[i] = this.dSort[this.rCur];
                this.rCur++;
            }
            else if(this.rCur > this.rightMax)
            {
                this.finalA[i] = this.dSort[this.lCur];
                this.lCur++;
            }
            else if(this.dSort[this.lCur] < dSort[this.rCur])
            {
                this.finalA[i] = this.dSort[this.lCur];
                this.lCur++;
            }
            else
            {
                this.finalA[i] = dSort[this.rCur];
                this.rCur++;
            }
        }

        return;
    }

}
