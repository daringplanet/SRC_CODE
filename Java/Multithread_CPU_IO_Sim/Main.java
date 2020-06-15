/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign5;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



/**
 *
 * @author William Lippard
 */
public class Main
{

    public static boolean ioPopped = false;

    public static void main(String[] args)
    {

        Lock mainLock = new ReentrantLock();
        Condition mainWait = mainLock.newCondition();

        Lock cpuLock;
        Condition cpuCond;

        Lock ioLock;
        Condition ioCond;


        Condition isCPUEmpty;



        try
        {



            String alg = new String();
            String input = new String();
            int quant = 0;

            //System.out.println("num of args = " + args.length);


            if(args.length < 4 || args.length > 6)
            {
                System.out.println("Invalid number of arguments.");
                return;
            }


            switch(args.length)
            {
                case 4:

                    switch(args[0])
                    {
                        case "-alg":
                            alg = args[1];
                            break;
                        case "-input":
                            input = args[1];
                        default:
                            System.out.println("Invalid flag.");
                            return;
                    }

                    switch(args[2])
                    {
                        case "-alg":
                            alg = args[3];
                            break;
                        case "-input":
                            input = args[3];
                            break;
                        default:
                            System.out.println("Invalid flag.");
                            return;
                    }

                    break;

                case 6:

                    switch(args[0])
                    {
                        case "-alg":
                            alg = args[1];
                            break;
                        case "-input":
                            input = args[1];
                        case "-quantum":
                            quant = Integer.valueOf(args[1]);
                            break;
                        default:
                            System.out.println("Invalid flag.");
                            return;
                    }

                    switch(args[2])
                    {
                        case "-alg":
                            alg = args[3];
                            break;
                        case "-input":
                            input = args[3];
                        case "-quantum":
                            quant = Integer.valueOf(args[3]);
                            break;
                        default:
                            System.out.println("Invalid flag.");
                            return;
                    }

                    switch(args[4])
                    {
                        case "-alg":
                            alg = args[5];
                            break;
                        case "-input":
                            input = args[5];
                            break;
                        case "-quantum":
                            quant = Integer.valueOf(args[5]);
                            break;
                        default:
                            System.out.println("Invalid flag.");
                            return;
                    }

                    break;
                default:
                    System.out.println("Invalid number of arguments.");
                    return;
            }


            //System.out.println("alg = " + alg + ", input = " + input);

            List cpuList = new List(alg);
            List ioList = new List("FIFO");

            //Clock clock = Clock.systemDefaultZone();




            FileThread file = new FileThread(input, cpuList, alg);
            CPU cpu = new CPU(cpuList, alg);
            IO io = new IO(ioList, "FIFO");

            //file.clock = clock;


            io.cpuList = cpu.cpuList;
            //io.clock = clock;
            io.cpuLock = cpu.lock;
            io.cpuCond = cpu.isCPUempty;
            io.quant = quant;



            //cpu.clock = clock;
            cpu.ioList = io.ioList;
            cpu.ioLock = io.lock;
            cpu.ioSignal = io.isIOEmpty;
            cpu.quant = quant;


            Thread fileThread = new Thread(file);
            Thread cpuThread = new Thread(cpu);
            Thread ioThread = new Thread(io);


            file.cpuThread = cpuThread;
            file.ioThread = ioThread;
            file.cpuLock = cpu.lock;
            file.isCPUEmpty = cpu.isCPUempty;

            cpuLock = cpu.lock;
            isCPUEmpty = cpu.isCPUempty;


            cpuLock = cpu.lock;
            cpuCond = cpu.isCPUempty;

            ioLock = io.lock;
            ioCond = io.isIOEmpty;


            fileThread.start();
            cpuThread.start();
            ioThread.start();




            fileThread.join();

            cpu.fileFinished = true;


            cpuThread.join();

            ioThread.interrupt();

            long totalProcTime = 0;
            long totalWaitTime = 0;
            int i;
            for(i=0; i<cpu.finsihList.size(); i++)
            {
                totalProcTime += cpu.finsihList.get(i).procTimeFinish -
                                cpu.finsihList.get(i).procTimeArrival;

                totalWaitTime += cpu.finsihList.get(i).totalWaitTime;

            }




            double cpuUtil = (double) cpu.cpuUtil / cpu.clockTotalTime * 100;
            double throughPut = (double) cpu.finsihList.size() / cpu.clockTotalTime;
            double avgTurnTime = (double) totalProcTime / cpu.finsihList.size();
            double avgWaitTime = (double) totalWaitTime / cpu.finsihList.size();


            cpuUtil = Double.valueOf(decFormatter(Double.toString(cpuUtil)));
            throughPut = Double.valueOf(decFormatter(Double.toString(throughPut)));
            avgTurnTime = Double.valueOf(decFormatter(Double.toString(avgTurnTime)));
            avgWaitTime = Double.valueOf(decFormatter(Double.toString(avgWaitTime)));




            System.out.println("Input File Name\t\t\t: " + input);

            if(args.length == 4)
                System.out.println("CPU Scheduling Alg\t\t: " + alg);
            else
                System.out.println("CPU Scheduling Alg\t\t: " + alg + " quantum: " + quant);

            System.out.println("CPU utilization \t\t: " + cpuUtil + "%");
            System.out.println("Throughput\t\t\t: " + throughPut + " processes / ms");
            System.out.println("Avg. Turnaround time\t\t: " + avgTurnTime + "ms");
            System.out.println("Avg. Waiting time in R queue\t: " + avgWaitTime + "ms");


        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return;
    }


    public static String decFormatter(String value)
    {
        String returnSt = new String();
        boolean dec = false;
        int i;
        int count=0;

        for(i=0; i<value.length(); i++)
        {
            if(value.charAt(i)  == '.')
                dec = true;

            if(dec)
            {
                if(count > 3)
                    break;
                else
                    count++;
            }
        }


        return value.substring(0, i);

    }

}
