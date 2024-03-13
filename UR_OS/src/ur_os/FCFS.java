/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

/**
 *
 * @author prestamour
 */
public class FCFS extends Scheduler{

    boolean mfq;

    FCFS(OS os){
        super(os);
        mfq = false;
    }

    FCFS(OS os, boolean mfq){
        this(os);
        this.mfq = mfq;
    }
   
    @Override
    public void getNext(boolean cpuEmpty) {
        if(!processes.isEmpty() && cpuEmpty)
        {        
            Process p = processes.get(0);
            processes.remove();
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
        }
        if(!mfq){
            //Update waiting time
            for(Process proc: processes){
                if(proc.state == ProcessState.READY){proc.waitingTime ++;};
            }
        }
    }

    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive
    
    
    
}
