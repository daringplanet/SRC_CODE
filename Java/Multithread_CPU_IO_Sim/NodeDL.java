/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign5;

/**
 *
 * @author darin
 */
public class NodeDL 
{
    
    
    int procID;
    int procPR;
    int reg[] = new int[8];
    long enterClock;
    long endClock;
    long totalClock;
    long enterWaitTime;
    long endWaitTime;
    long totalWaitTime;
    
    long procTimeArrival;
    long procTimeFinish;
    
    
    int numCPUburst;
    int numIOburst;
    int CPUburst[];
    int IOburst[];
    int cpuIndex = 0;
    int ioIndex = 0;
    
    boolean isCpuTurn;
     
    
    NodeDL prevNode;
    NodeDL nextNode;
    
    
    
    
    public NodeDL()
    {
        
        enterClock = 0;
        endClock = 0;
        totalClock = 0;
        enterWaitTime = 0;
        endWaitTime = 0;
        totalWaitTime = 0;
        
        procTimeArrival = 0;
        procTimeFinish = 0;
            
        
        this.isCpuTurn = true;
        
        this.prevNode = null;
        this.nextNode = null;
    }
    
    
}
