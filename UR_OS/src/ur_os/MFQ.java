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
    LinkedList<Process> pQ0Past; //list of processes that just left queue 0
    boolean pQ0LastRunning = false;
    LinkedList<Process> pQ1; //list of processes for queue 1
    LinkedList<Process> pQ1Past; //list of processes that just left queue 1
    boolean pQ1LastRunning = false;
    LinkedList<Process> pQ2; //list of processes for queue 2
    LinkedList<Process> pQ2Past; //list of processes that just left queue 2
    boolean pQ2LastRunning = false;
    
    MFQ(OS os){
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList();
        pQ0 = new LinkedList();
        pQ0Past = new LinkedList();
        pQ1 = new LinkedList();
        pQ1Past = new LinkedList();
        pQ2 = new LinkedList();
        pQ2Past = new LinkedList();
    }
    
    MFQ(OS os, Scheduler... s){ //Received multiple arrays
        this(os);
        schedulers.addAll(Arrays.asList(s));
    }
    
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        System.out.println("BEFORE");
        System.out.println("pQ0:");
        System.out.println(pQ0);
        System.out.println("pQ0Past");
        System.out.println(pQ0Past);
        System.out.println("pQ1:");
        System.out.println(pQ1);
        System.out.println("pQ1Past:");
        System.out.println(pQ1Past);
        System.out.println("pQ2:");
        System.out.println(pQ2);
        System.out.println("pQ2Past:");
        System.out.println(pQ2Past);
        
        /*Qi must be empty for Q(i+1) to begin*/
        if(!pQ0.isEmpty() || pQ0LastRunning){//hay un error en esta condición
           currentScheduler = 0;
        }else if(!pQ1.isEmpty() || pQ1LastRunning){
            currentScheduler = 1;
        }else if(!pQ2.isEmpty() || pQ2LastRunning){
            currentScheduler = 2;
        }
        
        
        Scheduler s1 = schedulers.get(currentScheduler);
        
        switch(currentScheduler){
            case 0:

                s1.processes = (LinkedList) pQ0.clone();
                s1.getNext(cpuEmpty); //Here s1.processes gets updated, so we should update pQ0 and pQ0Past accordingly
                
                for(Process proc: pQ0){
                    if(!s1.processes.contains(proc)){ //if proc no longer in s1.processes, it went to CPU
                        pQ0.remove(proc);//it shouldn't be on pQ0 since it moved to CPU
                        if(!proc.isFinished()){ //if proc still has time left
                            pQ0Past.add(proc); //it will return at any moment, so keep track of where the process came from
                        }
                    }
                }
                for(Process proc: s1.processes){
                    if(!pQ0.contains(proc)){
                        /*If there's a process on s1 that's not in pQ0, it means the process has gone back to processes
                        due to the scheduler's progress. It could happen in RoundRobin*/
                        pQ1.add(proc); //if the process returns to s1.processes, it should go to the next queue
                    }
                }
                break;

            case 1:
                
                s1.processes = (LinkedList) pQ1.clone();
                s1.getNext(cpuEmpty); //Here s1.processes gets updated, so we should update pQ1 and pQ1Past accordingly
                for(Process proc: pQ1){
                    if(!s1.processes.contains(proc)){ //if proc no longer in s1.processes, it went to CPU
                        pQ1.remove(proc);//it shouldn't be on pQ1 since it moved to CPU
                        if(!proc.isFinished()){ //if proc still has time left
                            pQ1Past.add(proc); //it will return at any moment, so keep track of where the process came from
                        }
                        //System.out.println(pQ1);
                        break;
                    }
                }
                for(Process proc: s1.processes){
                    if(!pQ1.contains(proc)){
                        /*If there's a process on s1 that's not in pQ1, it means the process has gone back to processes
                        due to the scheduler's progress. It could happen in RoundRobin*/
                        pQ2.add(proc); //if the process returns to s1.processes, it should go to the next queue
                    }
                }
                break;

            case 2: 

                s1.processes = (LinkedList) pQ2.clone();
                s1.getNext(cpuEmpty); //Here s1.processes gets updated, so we should update pQ2 and pQ2Past accordingly
                for(Process proc: pQ2){
                    if(!s1.processes.contains(proc)){ //if proc no longer in s1.processes, it went to CPU
                        pQ2.remove(proc); //it shouldn't be on pQ2 since it moved to CPU
                        if(!proc.isFinished()){ //if proc still has time left
                            pQ2Past.add(proc); //it will return at any moment, so keep track of where the process came from
                        }
                    }
                }
                for(Process proc: s1.processes){
                    if(!pQ2.contains(proc)){
                        /*If there's a process on s1 that's not in pQ2, it means the process has gone back to processes
                        due to the scheduler's progress. It could happen in RoundRobin*/
                        pQ2.add(proc); //since this is the last queue, the process should return to the same queue
                    }
                }
                break;
        }
        
        pQ0LastRunning = false; //assume last process running is not from pQ0
        pQ1LastRunning = false; //assume last process running is not from pQ1
        pQ2LastRunning = false; //assume last process running is not from pQ2

        for(Process proc: pQ0Past){
            if(os.cpu.getProcess() == proc){
                pQ0LastRunning = true; //last process running is from pQ0
                pQ1LastRunning = false; //last process running isn't from pQ1
                pQ2LastRunning = false; //last process running isn't rom pQ2
            }
            //update waiting time
            if(proc.state == ProcessState.READY){proc.waitingTime++;}
        }
        for(Process proc: pQ1Past){
            if(os.cpu.getProcess()== proc){
                pQ0LastRunning = false; //last process running isn't from pQ0
                pQ1LastRunning = true; //last process running is from pQ1
                pQ2LastRunning = false; //last process running isn't from pQ2
            }
            //update waiting time
            if(proc.state == ProcessState.READY){proc.waitingTime++;}
        }
        for(Process proc: pQ2Past){
            if(os.cpu.getProcess() == proc){
                pQ0LastRunning = false; //last process running isn't from pQ0
                pQ1LastRunning = false; //last process running isn't from pQ1
                pQ2LastRunning = true; //last process running is from pQ2
            }
            //update waiting time
            if(proc.state == ProcessState.READY){proc.waitingTime++;}
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
        System.out.println("pQ0Past");
        System.out.println(pQ0Past);
        System.out.println("pQ1:");
        System.out.println(pQ1);
        System.out.println("pQ1Past:");
        System.out.println(pQ1Past);
        System.out.println("pQ2:");
        System.out.println(pQ2);
        System.out.println("pQ2Past:");
        System.out.println(pQ2Past);
        
    }
    
    @Override
    public void addProcess(Process p){
        
        if(p.getState() == ProcessState.NEW){
            pQ0.add(p); //every new process goes to queue 0
        }else if(p.getState() == ProcessState.IO){
            //depending on which queue the process left in scheduling, it should be sent to the next
            if(pQ0Past.contains(p)){
                pQ1.add(p);
                pQ0Past.remove(p);
            }else if(pQ1Past.contains(p)){
                pQ2.add(p);
                pQ1Past.remove(p);
            }else if(pQ2Past.contains(p)){
                pQ2.add(p); //if p left the last queue, it will return to the same one
                pQ2Past.remove(p);
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
            if(pQ0Past.contains(p)){
                pQ0Past.remove(p);
            }else if(pQ1Past.contains(p)){
                pQ1Past.remove(p);
            }else if(pQ2Past.contains(p)){
                pQ2Past.remove(p);
            }
            os.cpu.removeProcess();
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
    
}
