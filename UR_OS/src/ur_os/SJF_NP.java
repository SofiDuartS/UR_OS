/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

/**
 *
 * @author prestamour
 */
public class SJF_NP extends Scheduler{

    
    SJF_NP(OS os){
        super(os);
    }
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        if(!processes.isEmpty() && cpuEmpty){
            
            Process pMinCycles = null;
            double minCycles = Double.POSITIVE_INFINITY;
            
            for(Process proc : processes){ //iterando sobre la lista de procesos
                
                if(proc.getRemainingTimeInCurrentBurst() < minCycles){ //comparar remainingTime del burst actual
                    
                    pMinCycles = proc;
                    minCycles = proc.getRemainingTimeInCurrentBurst();
                    
                }
            }
            
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, pMinCycles);
            processes.remove(pMinCycles);
        }
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive
    
}
