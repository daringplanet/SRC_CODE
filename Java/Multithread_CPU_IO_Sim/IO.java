/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author William
 */
public class IO implements Runnable
{
    
    Lock cpuLock;
    Condition cpuCond;
    List cpuList;
    
    
    String alg;
    
    int quant;
    
    List ioList;

    
    //Clock clock;
    
    public Lock lock  = new ReentrantLock();
    public Condition isIOEmpty = lock.newCondition();
    
    
    
    
    
    
    public IO(List ioList, String alg)
    {
        this.ioList = ioList;
        this.alg = alg;
        
    }
    

    @Override
    public void run() 
    {
        try
        {
        
        if(this.alg.equals("RR"))
            rrScheduler();
        else
            ioScheduler();
        }
        catch (InterruptedException ex)
        {
            
            return;
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        return;
        
        
    }
    
    
    
    
    public void ioScheduler() throws InterruptedException, Exception
    {
        
        
        while(true)
        {
        
            this.lock.lock();
                while(this.ioList.size <= 0)
                    this.isIOEmpty.await();
                
               
                NodeDL pop;
                
                
            
                pop = this.ioList.popTop();
                Main.ioPopped = true;
                
             this.lock.unlock();
             
              //System.out.println("\t...In the IO Scheduler");

                if(pop.ioIndex < pop.numIOburst && pop.ioIndex>=0)
                {
                    //pop.endWaitTime = System.currentTimeMillis();
                    //pop.totalWaitTime += pop.endWaitTime - pop.enterWaitTime;

                    Thread.sleep(pop.IOburst[pop.ioIndex]);
                   
                    pop.ioIndex++;
                    
                    this.cpuLock.lock();
                        pop.enterWaitTime = System.currentTimeMillis();
                        this.cpuList.appendList(pop, true); //appending to the cpu list
                        this.cpuCond.signal(); //signal cpu thread
                        Main.ioPopped = false; //set the pop back to false
                    this.cpuLock.unlock();

                    
                }
                
                
        }
        
        
    }
    
     public void rrScheduler() throws InterruptedException, Exception
    {
        
        boolean ioReady = false;
        
        while(true)
        {
            this.lock.lock();
                while(this.ioList.size <= 0)
                    this.isIOEmpty.await();

                    
                    NodeDL pop;

                    pop = this.ioList.popTop();
                    
                    
                this.lock.unlock();
                    
                    //System.out.println("\t\t...In the CPU Schedular");
                    
                    
                //if it is the cpu turn, and we have a valid cpu inde
                if(pop.ioIndex < pop.numIOburst)
                {

                    
                    if(pop.IOburst[pop.ioIndex] > this.quant)
                    {
                        pop.IOburst[pop.ioIndex] -= this.quant;
                        
                        
                        Thread.sleep(this.quant);
                        
                        pop.enterWaitTime = System.currentTimeMillis();
                       
                        this.lock.lock();
                            ioList.appendList(pop, false);
                        this.lock.unlock();
                        
                        continue;
                    
                    }
                    else
                    {
                        Thread.sleep(pop.IOburst[pop.ioIndex]);
                        pop.ioIndex++;
                    }
                   
                        
                    

                    //check to see if we need to append the process on ioList
                    if(pop.cpuIndex < pop.numCPUburst)
                    {
                        
                        pop.enterWaitTime = System.currentTimeMillis();
                        
                        this.cpuLock.lock();
                            //append to the IO list
                            this.cpuList.appendList(pop, true);
                            this.cpuCond.signalAll();

                        this.cpuLock.unlock();
                    }
                    
                }
                    
            
        }
        
    }
    
}
