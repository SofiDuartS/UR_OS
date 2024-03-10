/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

/**
 *
 * @author prestamour
 */
public class SJF_P extends Scheduler{

    
    SJF_P(OS os){
        super(os);
    }
    
    @Override
    public void newProcess(boolean cpuEmpty){// When a NEW process enters the queue, process in CPU, if any, is extracted to compete with the rest
        if(!cpuEmpty){
            Process p2 =  os.cpu.getProcess();
            os.cpu.removeProcess();
            os.rq.addProcess(p2);
        }
    } 

    @Override
    public void IOReturningProcess(boolean cpuEmpty){// When a process return from IO and enters the queue, process in CPU, if any, is extracted to compete with the rest
        if(!cpuEmpty){
            Process p3 =  os.cpu.getProcess();
            os.cpu.removeProcess();
            os.rq.addProcess(p3);
        }
               
    } 
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        if(!processes.isEmpty()){
            Process pMinCycles = null;
            double minCycles = Double.POSITIVE_INFINITY;
            if(cpuEmpty){
                for(Process proc : processes){ //iterando sobre la lista de procesos
                
                    if(proc.getRemainingTimeInCurrentBurst() < minCycles){ //comparar remainingTime del burst actual
                        
                        pMinCycles = proc;
                        minCycles = proc.getRemainingTimeInCurrentBurst();
                        
                    }
                }
                
                os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, pMinCycles);
                processes.remove(pMinCycles);

            }else{
                Process procCPU = os.cpu.getProcess();
                minCycles = procCPU.getRemainingTimeInCurrentBurst();
                for(Process p1 : processes){
                    if( p1.getRemainingTimeInCurrentBurst() < minCycles){
                        pMinCycles = p1;
                        minCycles = p1.getRemainingTimeInCurrentBurst();
                    }
                }
                if(minCycles != procCPU.getRemainingTimeInCurrentBurst()){
                    os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, procCPU);
                    
                }
            }



        }
       
    }
    
}
