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
    int contQ0; //number of processes currently con Q0
    int contQ1; //number of processes currently con Q1
    int contQ2; //number of processes currently con Q2
    int it = 1; //only 1 at first iteration
    
    MFQ(OS os){
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList();
        contQ0 = processes.size();
        contQ1 = 0;
        contQ2 = 0;
    }
    
    MFQ(OS os, Scheduler... s){ //Received multiple arrays
        this(os);
        schedulers.addAll(Arrays.asList(s));
    }
    
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        /*cpuBefore and cpuAfter to know if the process in CPU has changed
        to update contQi. It shouldn't be updated unless the process in CPU
        changes.*/
        Process cpuBefore;
        Process cpuAfter;
        
        if(contQ0 !=0){
           currentScheduler = 0;
        }else if(contQ1 != 0){
            currentScheduler = 1;
        }else if(contQ2 != 0){
            currentScheduler = 2;
        }
        
        System.out.println("BEFORE");
        System.out.println(contQ0);
        System.out.println(contQ1);
        System.out.println(contQ2);
        
        cpuBefore = os.cpu.p;
        Scheduler s1 = schedulers.get(currentScheduler);
        s1.processes = processes;
        s1.getNext(cpuEmpty);
        cpuAfter = os.cpu.p;
        
        /*it != 1 because the first process went automatically to Q2 on first iteration*/
        if(it != 1 && cpuBefore != cpuAfter){
            switch(currentScheduler){
                case 0 -> {
                    contQ0 = contQ0 - 1;
                    contQ1 = contQ1 + 1;
                }
                case 1 -> {
                    contQ1 = contQ1 - 1;
                    contQ2 = contQ2 + 1;
                }
                case 2 -> contQ2 = contQ2 - 1;
            }
         
        }
        
        System.out.println("AFTER");
        System.out.println(contQ0);
        System.out.println(contQ1);
        System.out.println(contQ2);
        
        if(it == 1){
            it = -1;
        }
        
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) {
        contQ0 = contQ0 + 1; //every new process goes to Q0
    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        /*If a process goes to IO and comes back, it will go to the
        queue following the one it came from*/
        if(currentScheduler == 0){
            contQ1 = contQ1 + 1;
        } else if(currentScheduler == 1 || currentScheduler == 2){
            /*If it came from Q1, it will return to Q2. If it came
            from Q2, it will return to Q2*/
            contQ2 = contQ2 + 1;
        }
    }
    
}
