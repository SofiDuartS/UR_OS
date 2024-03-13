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
        
        switch(currentScheduler){
            case 0:

                s1.processes = (LinkedList) pQ0.clone();
                s1.getNext(cpuEmpty); //Here s1.processes gets updated, so we should update pQ0 accordingly
                
                for(Process proc: pQ0){
                    if(!s1.processes.contains(proc)){ //if proc no longer in s1.processes, it went to CPU
                        pQ0.remove(proc);//it shouldn't be on pQ0 since it moved to CPU
                        if(!proc.isFinished()){ //if proc still has time left
                            proc.setCurrentScheduler(0);; //it will return at any moment, so keep track of where the process came from
                        }
                        break;
                    }
                }
                for(Process proc: s1.processes){
                    if(!pQ0.contains(proc)){
                        /*If there's a process on s1 that's not in pQ0, it means the process has gone back to processes
                        due to the scheduler's progress. It could happen in RoundRobin*/
                        pQ1.add(proc); //if the process returns to s1.processes, it should go to the next queue
                        proc.setCurrentScheduler(1); //since it's in pQ1, scheduler changes
                    }
                }
            break;

            case 1:
                
                s1.processes = (LinkedList) pQ1.clone();
                s1.getNext(cpuEmpty); //Here s1.processes gets updated, so we should update pQ1 accordingly
                for(Process proc: pQ1){
                    if(!s1.processes.contains(proc)){ //if proc no longer in s1.processes, it went to CPU
                        pQ1.remove(proc);//it shouldn't be on pQ1 since it moved to CPU
                        if(!proc.isFinished()){ //if proc still has time left
                            proc.setCurrentScheduler(1); //it will return at any moment, so keep track of where the process came from
                        }
                        break;
                    }
                }
                for(Process proc: s1.processes){
                    if(!pQ1.contains(proc)){
                        /*If there's a process on s1 that's not in pQ1, it means the process has gone back to processes
                        due to the scheduler's progress. It could happen in RoundRobin*/
                        pQ2.add(proc); //if the process returns to s1.processes, it should go to the next queue
                        proc.setCurrentScheduler(2); // since it's in pQ2, scheduler changes
                    }
                }
            break;

            case 2: 

                s1.processes = (LinkedList) pQ2.clone();
                s1.getNext(cpuEmpty); //Here s1.processes gets updated, so we should update pQ2 accordingly
                for(Process proc: pQ2){
                    if(!s1.processes.contains(proc)){ //if proc no longer in s1.processes, it went to CPU
                        pQ2.remove(proc); //it shouldn't be on pQ2 since it moved to CPU
                        if(!proc.isFinished()){ //if proc still has time left
                            proc.setCurrentScheduler(2);; //it will return at any moment, so keep track of where the process came from
                        }
                        break;
                    }
                }
                for(Process proc: s1.processes){
                    if(!pQ2.contains(proc)){
                        /*If there's a process on s1 that's not in pQ2, it means the process has gone back to processes
                        due to the scheduler's progress. It could happen in RoundRobin*/
                        pQ2.add(proc); //since this is the last queue, the process should return to the same queue
                        proc.setCurrentScheduler(2); //maybe this is not necessary
                    }
                }
                break;
        }

        if(!os.isCPUEmpty()){
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
            if(p.getCurrentScheduler() == 0){
                pQ1.add(p);
                p.setCurrentScheduler(1);
            }else if(p.getCurrentScheduler() == 1){
                pQ2.add(p);
                p.setCurrentScheduler(2);
            }else if(p.getCurrentScheduler() == 2){
                pQ2.add(p); //if p left the last queue, it will return to the same one
                p.setCurrentScheduler(2);
            }
        }else if(p.getState() == ProcessState.CPU){
            //if process came from CPU, return it to the next queue
            switch(currentScheduler){
                case 0:
                    pQ1.add(p);
                    break;
                case 1:
                    pQ2.add(p);
                    break;
                case 2:
                    pQ2.add(p); //if it exits last queue, return to same one
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
        if(!pQ0.isEmpty() || pQ0LastRunning){//hay un error en esta condición
            currentScheduler = 0;
        }else if(!pQ1.isEmpty() || pQ1LastRunning){
            currentScheduler = 1;
        }else if(!pQ2.isEmpty() || pQ2LastRunning){
            currentScheduler = 2;
        }
    }
    
}
