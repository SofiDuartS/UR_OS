/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author prestamour
 */
public class MFQ extends Scheduler{

    int currentScheduler;
    private ArrayList<Scheduler> schedulers;

    LinkedList<Process> pQ0; //list of processes for queue 0
    boolean pQ0LastRunning = false;
    LinkedList<Process> pQ1; //list of processes for queue 1
    boolean pQ1LastRunning = false;
    LinkedList<Process> pQ2; //list of processes for queue 2
    boolean pQ2LastRunning = false;
    
    MFQ(OS os){
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList();
        pQ0 = new LinkedList();
        pQ1 = new LinkedList();
        pQ2 = new LinkedList();
    }
    
    MFQ(OS os, Scheduler... s){ //Received multiple arrays
        this(os);
        schedulers.addAll(Arrays.asList(s));
        if(s.length > 0)
            currentScheduler = 0;
    }
    
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        System.out.println("BEFORE");
        System.out.println("pQ0:");
        System.out.println(pQ0);
        System.out.println("pQ1:");
        System.out.println(pQ1);
        System.out.println("pQ2:");
        System.out.println(pQ2);

        this.defineCurrentScheduler(); //updates currentScheduler
        
        
        Scheduler s1 = schedulers.get(currentScheduler);
        Process prevCPU = os.getProcessInCPU();
        
        switch(currentScheduler){
            case 0:

                s1.processes = pQ0;
                s1.getNext(cpuEmpty); //Here s1.processes gets updated, so we should update pQ0 accordingly
                if(!cpuEmpty &&  os.isCPUEmpty()){ //the scheduler preempted the process that was initially there, we insert case 1
                    s1 = schedulers.get(1);
                    s1.processes = pQ1;
                    s1.getNext(os.isCPUEmpty());
                    
                    break;
                }
                
            break;

            case 1:
                
                s1.processes = pQ1;
                s1.getNext(cpuEmpty); //Here s1.processes gets updated, so we should update pQ1 accordingly
                if(!os.isCPUEmpty()){
                    if(os.getProcessInCPU() != prevCPU){
                        if(!pQ0.isEmpty()){
                            Process prev = os.getProcessInCPU();
                            os.removeProcessFromCPU();
                            s1.returnProcess(prev);
                            s1 = schedulers.get(0);
                            s1.processes = pQ0;
                            s1.getNext(os.isCPUEmpty());
                        }
                    }
                    if(os.isCPUEmpty()){
                        s1 = schedulers.get(2);
                        s1.processes=pQ2;
                        s1.getNext(os.isCPUEmpty());
                        break;
                    }
                }
                
                if((!cpuEmpty &&  os.isCPUEmpty())){ //the scheduler preempted the process that was initially there, we insert case 2
                    if(!pQ0.isEmpty()){
                        //ACÁ DEBERÍA HABER ALGO AAAAAA
                    }else{
                        s1 = schedulers.get(2);
                        s1.processes=pQ2;
                        s1.getNext(os.isCPUEmpty());
                        break;
                    }
                    
                }
                
            break;

            case 2: 

                s1.processes = pQ2;
                s1.getNext(cpuEmpty); //Here s1.processes gets updated, so we should update pQ2 accordingly
                if(!os.isCPUEmpty()){
                    if(os.getProcessInCPU()!=prevCPU){
                        if(!pQ0.isEmpty()){
                            Process prev = os.getProcessInCPU();
                            os.removeProcessFromCPU();
                            s1.returnProcess(prev);
                            s1 = schedulers.get(0);
                            s1.processes = pQ0;
                            s1.getNext(os.isCPUEmpty());
                        }else if(!pQ1.isEmpty()){
                            Process prev = os.getProcessInCPU();
                            os.removeProcessFromCPU();
                            s1.returnProcess(prev);
                            s1 = schedulers.get(1);
                            s1.processes = pQ1;
                            s1.getNext(os.isCPUEmpty());
                        }
                    }
                }
                break;
        }

        if(!os.isCPUEmpty()){
            System.out.println(os.getProcessInCPU());
            pQ0LastRunning = false; //assume last process running is not from pQ0
            pQ1LastRunning = false; //assume last process running is not from pQ1
            pQ2LastRunning = false; //assume last process running is not from pQ2
            
            switch(os.getProcessInCPU().getCurrentScheduler()){
                case 0:
                    pQ0LastRunning = true;
                    pQ1LastRunning = false;
                    pQ2LastRunning = false;
                break;
                case 1:
                    pQ0LastRunning = false;
                    pQ1LastRunning = true;
                    pQ2LastRunning = false;
                break;
                case 2:
                    pQ0LastRunning = false;
                    pQ1LastRunning = false;
                    pQ2LastRunning = true;
                break;
                
            }
        } else{
            if(pQ0LastRunning){
                pQ0LastRunning = false;
                pQ1LastRunning = true;
                pQ2LastRunning = false;
            }else if(pQ1LastRunning || pQ2LastRunning){
                pQ0LastRunning = false;
                pQ1LastRunning = false;
                pQ2LastRunning = true;
            }
            
        }

        for(Process proc: pQ0){
            //update waiting time
            if(proc.state == ProcessState.READY){proc.waitingTime++;}
        }
        for(Process proc: pQ1){
            //update waiting time
            if(proc.state == ProcessState.READY){proc.waitingTime++;}
        }
        for(Process proc: pQ2){
            //update waiting time
            if(proc.state == ProcessState.READY){proc.waitingTime++;}
        }
        
        System.out.println("AFTER");
        System.out.println("pQ0:");
        System.out.println(pQ0);
        System.out.println("pQ1:");
        System.out.println(pQ1);
        System.out.println("pQ2:");
        System.out.println(pQ2);
        
    }
    
    @Override
    public void addProcess(Process p){
        
        if(p.getState() == ProcessState.NEW){
            pQ0.add(p); //every new process goes to queue 0
            p.setCurrentScheduler(0); //for queue 0, scheduler 0
        }else if(p.getState() == ProcessState.IO){
            //depending on which queue the process left in scheduling, it should be sent to the next
            pQ0.add(p);
            p.setCurrentScheduler(0);
        }else if(p.getState() == ProcessState.CPU){
            //if process came from CPU, return it to the next queue
            switch(currentScheduler){
                case 0:
                    pQ1.add(p);
                    p.setCurrentScheduler(1);
                    break;
                case 1:
                    pQ2.add(p);
                    p.setCurrentScheduler(2);
                    break;
                case 2:
                    pQ2.add(p); //if it exits last queue, return to same one
                    p.setCurrentScheduler(2);
                    break;
            }
            os.removeProcessFromCPU();
        }
        
        p.setState(ProcessState.READY);
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) {
        //this is handled on addProcess
    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        //this is handled on addProcess
    }

    void defineCurrentScheduler(){
        /*Qi must be empty for Q(i+1) to begin*/
        if(!os.isCPUEmpty()){
            currentScheduler = os.getProcessInCPU().currentScheduler;
        }else{
            if(!pQ0.isEmpty() ){//|| pQ0LastRunninghay un error en esta condición
                currentScheduler = 0;
            }else if(!pQ1.isEmpty() ){//|| pQ1LastRunning
                currentScheduler = 1;
            }else if(!pQ2.isEmpty() ){//|| pQ2LastRunning
                currentScheduler = 2;
            }
        }
        
    }
    
}
