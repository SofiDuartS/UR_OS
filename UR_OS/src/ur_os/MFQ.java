/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author prestamour
 */
public class MFQ extends Scheduler{

    int currentScheduler;
    private ArrayList<Scheduler> schedulers;
    //This may be a suggestion... you may use the current sschedulers to create the Multilevel Feedback Queue, or you may go with a more tradicional way
    //based on implementing all the queues in this class... it is your choice. Change all you need in this class.
    
    MFQ(OS os){
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList();
    }
    
    MFQ(OS os, Scheduler... s){ //Received multiple arrays
        this(os);
        schedulers.addAll(Arrays.asList(s));
    }
    
    
    @Override
    public void addProcess(Process p){
       //Overwriting the parent's addProcess(Process p) method may be necessary in order to decide what to do with process coming from the CPU. Another option is to uncomment and use
       // the method CPUReturningProcess(boolean cpuEmpty) on the Scheduler class and use that to manage what happens with the process. It is your choise.
    }
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        
        
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //It's empty because it is Non-preemptive

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //It's empty because it is Non-preemptive
    
}
