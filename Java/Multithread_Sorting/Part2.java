/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package part2;

import java.util.*;

/**
 *
 * @author William Lippard
 */
public class Part2
{


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            int n = Integer.valueOf(args[0]);
            //System.out.printf("arg[0] = %d\n", n);

            Date date = new Date();

            double[] a = new double[n];
            double[] finalA = new double[n];
            double[] b = new double[n];

            Random r = new Random();

            long endTime;
            long startTime;
            long msSec;
            long msDec;

            int i;


            //System.out.println("Orginal:");
            for(i=0; i<n; i++)
            {

                a[i] = 100 * r.nextDouble();
                b[i] = a[i];
                //System.out.println("\tA[" + i +"] = " + a[i]);
                //System.out.println("\t...B[" + i +"] " + b[i]);
            }



            //create the 3 threads
            BubbleSort r1 = new BubbleSort(a, 0, n/2);
            Thread t1 = new Thread(r1);
            BubbleSort r2 = new BubbleSort(a, n/2+1, n-1);
            Thread t2 = new Thread(r2);
            Merge r3 = new Merge(a, n/2, n/2+1, n-1, finalA);
            Thread t3 = new Thread(r3);

            //capture the current time in ms
            startTime = java.lang.System.currentTimeMillis();
            //start the two threads.
            t1.start();
            t2.start();
            //wait until both half of the array has been sorted.
            t1.join();
            t2.join();

            t3.start();
            t3.join();

            endTime = java.lang.System.currentTimeMillis();
            //print out the total time in ms of how long it took.
            System.out.println("Sorting is done in " + (endTime - startTime) + "ms when two threads are used.");


            startTime = 0;
            endTime = 0;
            /*
            System.out.println("START: Orginal B");
            for(i=0; i<n; i++)
                System.out.println("\t" + i +". " + b[i]);
            //*/

            BubbleSort r4 = new BubbleSort(b, 0, n-1);
            Thread t4 = new Thread(r4);

            startTime = java.lang.System.currentTimeMillis();
            t4.start();
            t4.join();
            endTime = java.lang.System.currentTimeMillis();

            System.out.println("Sorting is done in " + (endTime - startTime) + "ms when one thread is used.");

            /*
            System.out.println("Final B:");
            for(i=0; i<n; i++)
                System.out.println("\t" + i +". " + b[i]);
            */

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return;
    }

}
