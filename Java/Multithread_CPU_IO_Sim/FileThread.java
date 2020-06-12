/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign5;

import java.util.*;
import java.io.*;
import assign5.LineItem;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author William
 */
public class FileThread implements Runnable
{
    Scanner scan;
    ArrayList<NodeDL> overFlowList;
    List cpuList;
    
    Thread cpuThread;
    Thread ioThread;
    
    //Clock clock;
    
    Condition isCPUEmpty;
    
    
    Lock cpuLock;
    
    boolean sleep = false;

    
    String alg; 
    
    public FileThread(String input, List cpuList, String alg) throws FileNotFoundException
    {
        this.scan = new Scanner(new File(input));
        this.cpuList = cpuList;
        this.alg = alg;


        
        
//this.overFlowList = new ArrayList<NodeDL>();
        
    }
    

    @Override
    public void run() 
    {
        
        try
        {
            
        
            processFile();
   
            
           
                
            //printFile();


            
            //wait for all the IO to be done processing.
            //this.ioThread.join();
            
            //System.out.println("Leaving FileThread");
             
            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();        
        }
    }
    
    
    public void processFile() throws InterruptedException, Exception
    {
    
        
        LineItem lItem;
        NodeDL node;
        int idCount = 0;
        
        while(this.scan.hasNextLine())
        {
            
           
            
                idCount++;
                lItem = processLine();
                
                if(lItem == null)
                {
                    if(sleep)
                    {
                        sleep = false;
                        continue;
                    }        
                    return;
                }
                if(lItem!=null)
                {
                    this.cpuLock.lock();
                        node = this.cpuList.getNode(lItem, idCount);
                    this.cpuLock.unlock();
                    
                    //rechord the start time of the cpu waiting list
                    node.procTimeArrival = System.currentTimeMillis();
                    node.enterWaitTime = node.procTimeArrival;
                    
                    this.cpuLock.lock();
                        this.cpuList.appendList(node, true);
                        //this.cpuList.printList();
                        this.isCPUEmpty.signalAll();
                    this.cpuLock.unlock();
                
                    
                }

                //this.cpuList.printList();
                
            
        }
        
       
            
        
        
    }
    
    public LineItem processLine() throws InterruptedException
    {
        LineItem line= new LineItem();
        
        line.command = this.scan.next();
        if(line.command.equals("stop"))
            return null;
        
        if(line.command.equals("sleep"))
        {
            int waitTime = scan.nextInt();
            Thread.sleep(waitTime);
            sleep=true;
            return null;
        }
        line.pr = this.scan.nextInt();
        
        line.numOfBurst = this.scan.nextInt();
        
        line.numOfCPUburst = line.numOfBurst/2+1;
        line.numOfIOburst = line.numOfBurst/2;
        
        line.cpuBurst = new int[line.numOfCPUburst];
        line.ioBurst = new int[line.numOfIOburst];
        int cpuIndex = 0;
        int ioIndex = 0;
        
        int i=0;
        for(i=0; i<line.numOfBurst; i++)
        {
            if((i%2) == 0)
            {
                line.cpuBurst[cpuIndex] = this.scan.nextInt();
                cpuIndex++;
            }
            else
            {
                line.ioBurst[ioIndex] = this.scan.nextInt();
                ioIndex++;
            }
                
        }
        
        
       return line; 
    }
    
    
    public void printFile()
    {
        while(this.scan.hasNextLine())
            System.out.println(this.scan.nextLine());
        
        return;
    }
    
}
