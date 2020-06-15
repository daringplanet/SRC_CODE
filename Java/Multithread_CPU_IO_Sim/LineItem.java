/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign5;

/**
 *
 * @author William Lippard
 */
public class LineItem
{
    String command;
    int pr;
    int numOfBurst;
    int numOfCPUburst;
    int numOfIOburst;
    int cpuBurst[];
    int ioBurst[];



    public LineItem()
    {

    }


    public void printLineItem()
    {
        System.out.println("command = " + command);

        System.out.printf("numOfBurst=%d\nnumOfCPUburst=%d\n", numOfBurst, numOfCPUburst);
        System.out.printf("numOfIOburst=%d\n", numOfIOburst);

        int i;
        for(i=0; i<cpuBurst.length; i++)
            System.out.printf("cpuBurst[%d] = %d\n", i, cpuBurst[i]);

        for(i=0; i<ioBurst.length; i++)
            System.out.printf("ioBurst[%d] = %d\n", i, ioBurst[i]);
    }
}
