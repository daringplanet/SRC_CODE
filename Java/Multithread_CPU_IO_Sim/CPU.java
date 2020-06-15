/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign5;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



/**
 *
 * @author William Lippard
 */
public class CPU implements Runnable
{
    List cpuList;
    List ioList;


    Lock ioLock;
    Condition ioSignal;


    int cpuReg[];
    boolean fileFinished = false;


    long clockStartTime;
    long clockEndTime;
    long clockTotalTime;






    long cpuUtil;


   public String alg;


    int cpuWaitTime;
    int totalQueWait;

    int quant;

    //Clock clock;

    public ArrayList<NodeDL> finsihList;
    public Lock lock  = new ReentrantLock();
    public Condition isCPUempty = lock.newCondition();

    //Lock mainLock;
    //Condition mainCond;


    int PCB;

    public CPU(List cpuList, String alg)
    {

        this.cpuList = cpuList;
        this.PCB = 0;
        this.cpuUtil = 0;
        this.finsihList = new ArrayList<NodeDL>();
        this.alg = alg;
        this.cpuReg = new int[8];

    }


    @Override
    public void run()
    {


        try
        {
            if(alg.equals("RR"))
                rrScheduler();
            else
                cpuScheduler();

            this.clockEndTime = System.currentTimeMillis();
            this.clockTotalTime = clockEndTime - clockStartTime;

        }
        catch (InterruptedException ex)
        {

            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //System.out.println("In the CPU Thread!!");


        return;


    }



    public void cpuScheduler() throws InterruptedException, Exception
    {

        this.clockStartTime = System.currentTimeMillis();

        while(true)
        {
            this.lock.lock();
                while(this.cpuList.size <= 0)
                    this.isCPUempty.await();


                    NodeDL pop;

                    pop = this.cpuList.popTop();


                this.lock.unlock();

                    //System.out.println("\t\t...In the CPU Schedular");


                //if it is the cpu turn, and we have a valid cpu inde
                if(pop.cpuIndex < pop.numCPUburst)
                {

                    for(int i=0; i<8; i++)
                        this.cpuReg[i] = pop.reg[i];

                    pop.endWaitTime = System.currentTimeMillis();
                    pop.totalWaitTime += pop.endWaitTime - pop.enterWaitTime;

                    //sleep for the given time
                    Thread.sleep(pop.CPUburst[pop.cpuIndex]);

                    //add the given time in ms to total cpu clock
                    this.cpuUtil += pop.CPUburst[pop.cpuIndex];


                    //increment the cpuIndex
                    pop.cpuIndex++;

                    //check to see if we need to append the process on ioList
                    if(pop.cpuIndex < pop.numCPUburst)
                    {

                        pop.enterWaitTime = System.currentTimeMillis();

                        this.ioLock.lock();
                            //append to the IO list
                            this.ioList.appendList(pop, false);
                            this.ioSignal.signalAll();

                        this.ioLock.unlock();
                    }
                    else
                    {

                        pop.procTimeFinish = System.currentTimeMillis();
                        this.finsihList.add(pop);
                         if(fileFinished)
                         {
                             this.ioLock.lock();
                             this.lock.lock();
                                    if(this.ioList.size <= 0 && this.cpuList.size <= 0 && Main.ioPopped == false)
                                    {
                                        this.lock.unlock();
                                        this.ioLock.unlock();

                                        return;
                                    }
                            this.lock.unlock();
                            this.ioLock.unlock();

                         }
                    }
                }
                else
                {
                    pop.procTimeFinish = System.currentTimeMillis();
                    this.finsihList.add(pop);
                }




            //this.cpuList.printList();

        }
        //return;
    }



    public void rrScheduler() throws InterruptedException, Exception
    {

        boolean ioReady = false;
        this.clockStartTime = System.currentTimeMillis();

        while(true)
        {
            this.lock.lock();
                while(this.cpuList.size <= 0)
                    this.isCPUempty.await();


                    NodeDL pop;

                    pop = this.cpuList.popTop();


                this.lock.unlock();


                if(pop.cpuIndex < pop.numCPUburst)
                {

                    //context Switch
                    for(int i=0; i<8; i++)
                        this.cpuReg[i] = pop.reg[i];

                    pop.endWaitTime = System.currentTimeMillis();
                    pop.totalWaitTime += pop.endWaitTime - pop.enterWaitTime;


                    if(pop.CPUburst[pop.cpuIndex] > this.quant)
                    {
                        pop.CPUburst[pop.cpuIndex] -= this.quant;

                        //add the given time in ms to total cpu clock
                        this.cpuUtil += this.quant;

                        Thread.sleep(this.quant);

                        pop.enterWaitTime = System.currentTimeMillis();

                        this.lock.lock();
                            cpuList.appendList(pop, true);
                            this.isCPUempty.signal();
                        this.lock.unlock();

                        continue;

                    }
                    else
                    {

                        this.cpuUtil += pop.CPUburst[pop.cpuIndex];
                        Thread.sleep(pop.CPUburst[pop.cpuIndex]);
                        pop.cpuIndex++;
                    }


                    //check to see if we need to append the process on ioList
                    if(pop.cpuIndex < pop.numCPUburst)
                    {
                        pop.enterWaitTime = System.currentTimeMillis();

                        this.ioLock.lock();
                            //append to the IO list
                            this.ioList.appendList(pop, false);
                            this.ioSignal.signalAll();

                        this.ioLock.unlock();
                    }
                    else
                    {

                        pop.procTimeFinish = System.currentTimeMillis();
                        this.finsihList.add(pop);
                         if(fileFinished)
                         {
                             this.ioLock.lock();
                             this.lock.lock();
                                    if(this.ioList.size <= 0 && this.cpuList.size <= 0 && Main.ioPopped == false)
                                    {
                                        this.lock.unlock();
                                        this.ioLock.unlock();

                                        return;
                                    }
                            this.lock.unlock();
                            this.ioLock.unlock();

                         }
                    }

                }
                else
                {
                    pop.procTimeFinish = System.currentTimeMillis();
                    this.finsihList.add(pop);
                }




            //this.cpuList.printList();

        }

    }

}
