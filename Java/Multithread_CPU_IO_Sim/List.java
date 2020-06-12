/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign5;

import java.util.ArrayList;

/**
 *
 * @author darin
 */
public class List 
{
    
    //ArrayList<NodeDL> list;
    NodeDL head;
    NodeDL tail;
    int size;
    String alg;
    
    
    public List(String alg)
    {
        //list = new ArrayList<NodeDL>();
        head = null;
        tail = null;
        size = 0;
        this.alg = alg;
    }
    
    
    
    
    public void appendList(NodeDL value, boolean isCpu) throws Exception
    {
        
        
        switch(this.alg)
        {
            case "FIFO":
                append(value);
                break;
            case "SJF":
                SJFappend(value, isCpu);
                break;
            case "RR":
                append(value);
                break;
            case "PR":
                PRappend(value);
                break;
            default:
                System.out.println("Not a valid alg. Recieved: '" + alg + "'");
                throw new Exception();

        }
        
        
    }
    
    public void append(NodeDL value)
    {
        if(head == null)
                {
                    head = value;
                    tail = value;
                    size++;
                    return;
                }
                size++;

                this.tail.nextNode = value;
                value.prevNode = this.tail;
                this.tail = value; 
    }
    
    
    public NodeDL getNode(LineItem lItem, int procID)
    {
        NodeDL newNode = new NodeDL();
        
        newNode.procID = procID;
        newNode.procPR = lItem.pr;
        newNode.CPUburst = lItem.cpuBurst;
        newNode.IOburst = lItem.ioBurst;
        newNode.numCPUburst = lItem.numOfCPUburst;
        newNode.numIOburst = lItem.numOfIOburst;
       
        
        return newNode;
    
    }
    
    
    
     private void SJFappend(NodeDL value, boolean isCpuList) 
    {
        this.size++;
        
        if(this.head == null)
        {
            this.head = value;
            this.tail = value;
            return;
        }
        
        NodeDL temp;
        
        temp = this.head;
        if(isCpuList)
        {
            while(temp != null)
                if(value.CPUburst[value.cpuIndex] > temp.CPUburst[temp.cpuIndex])
                {
                    //System.out.println(value.CPUburst[value.cpuIndex] + " > " + temp.CPUburst[temp.cpuIndex]);
                    temp = temp.nextNode;
                }
                else
                    break;
                    
                
            
            //tailCase
                if(temp == null)
                {
                    
                    this.tail.nextNode = value;
                    
                    value.prevNode = this.tail;
                    
                    this.tail = value;
                    
                    return;
                }
                
                
                //headCase
                if(temp.prevNode == null)
                {
                    
                    this.head.prevNode = value;
                    
                    value.nextNode = this.head;
                    
                    this.head = value;
                    
                    return;
                }
                
                //temp is 5 in the sanrio on the right.
                //middleCase
                temp.prevNode.nextNode = value;
                value.prevNode = temp.prevNode;
                value.nextNode = temp;
                temp.prevNode = value;
                return;
        }
        
         while(temp != null)
                if(value.IOburst[value.ioIndex] > temp.IOburst[temp.ioIndex])
                {
                    //System.out.println(value.IOburst[value.ioIndex] + " > " + temp.IOburst[temp.ioIndex]);
                    temp = temp.nextNode;
                }
                else
                    break;
                    
                
            
            //tailCase
                if(temp == null)
                {
                    
                    this.tail.nextNode = value;
                    
                    value.prevNode = this.tail;
                    
                    this.tail = value;
                    
                    return;
                }
                
                
                //headCase
                if(temp.prevNode == null)
                {
                    
                    this.head.prevNode = value;
                    
                    value.nextNode = this.head;
                    
                    this.head = value;
                    
                    return;
                }
                
                //temp is 5 in the sanrio on the right.
                //middleCase
                temp.prevNode.nextNode = value;
                value.prevNode = temp.prevNode;
                value.nextNode = temp;
                temp.prevNode = value;
                return;
        

    }
     
     
     
     
     
     private void PRappend(NodeDL value) 
    {
        this.size++;
        
        if(this.head == null)
        {
            this.head = value;
            this.tail = value;
            return;
        }
        
        NodeDL temp;
        
        temp = this.head;

        while(temp != null)
            if(value.procPR > temp.procPR)
            {
                //System.out.println(value.CPUburst[value.cpuIndex] + " > " + temp.CPUburst[temp.cpuIndex]);
                temp = temp.nextNode;
            }
            else
                break;



        //tailCase
            if(temp == null)
            {

                this.tail.nextNode = value;

                value.prevNode = this.tail;

                this.tail = value;

                return;
            }


            //headCase
            if(temp.prevNode == null)
            {

                this.head.prevNode = value;

                value.nextNode = this.head;

                this.head = value;

                return;
            }

            //temp is 5 in the sanrio on the right.
            //middleCase
            temp.prevNode.nextNode = value;
            value.prevNode = temp.prevNode;
            value.nextNode = temp;
            temp.prevNode = value;
            return;
        
    }
    
    
    public void printListOrder()
    {
        NodeDL temp;
        temp = this.head;
        while(temp != null)
        {
            System.out.print(temp.procPR + "->");
            temp = temp.prevNode;
        }
    }
    
    
    public void printList()
    {
        NodeDL temp;
        temp = this.head;
        System.out.println("___________List Print__________");
        while(temp != null)
        {
            
            System.out.printf("procID=%d:\n", temp.procID);
            System.out.println("\t\t...pr="  + temp.procPR);
            int i;
            for(i=0; i<temp.CPUburst.length; i++)
                System.out.println("\t\t...cpu[" + i + "] = " + temp.CPUburst[i]);
            for(i=0; i<temp.IOburst.length; i++)
                System.out.println("\t\t...io[" + i + "] = " + temp.IOburst[i]);
            
            
            temp = temp.nextNode;
        }
    }
    
    
    
    public NodeDL popTop()
    {
        NodeDL top = this.head;
        this.size--;
        
        if(top.nextNode == null)
        {
            this.head = null;
            this.tail = null;
            return top;
        }
        
        
        
        this.head.nextNode.prevNode = null;
        this.head = this.head.nextNode;
        
        top.prevNode = null;
        top.nextNode = null;
        
        return top;
    }
    
    
    public boolean isEmpty()
    {
        if(this.head == null)
            return true;
        else
            return false;
    }

   
    
}
