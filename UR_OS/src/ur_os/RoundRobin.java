/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

/**
 *
 * @author prestamour
 */
public class RoundRobin extends Scheduler{

    int q;
    int cont;
    
    RoundRobin(OS os){
        super(os);
        q = 5;
        cont=0;
    }
    
    RoundRobin(OS os, int q){
        this(os);
        this.q = q;
    }
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        if(!processes.isEmpty()){
            
            if (cpuEmpty) {
                cont=0;
                Process p = processes.get(0);
                processes.remove();
                os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
                cont++;
                
            }else{
                Process proc = os.cpu.getProcess();
                if(cont <q){
                    cont++;
                }else{
                    
                    if (proc.getRemainingTimeInCurrentBurst() != 0){
                        cont=0;
                        Process p = processes.get(0);
                        processes.remove();
                        os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, p);
                        cont++;
                    }else{
                        cont=0;
                        Process p = processes.get(0);
                        processes.remove();
                        os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, p);
                        if(processes.contains(proc)){
                            processes.remove(proc);
                        }
                        cont++;
                    }
                }
                
                
            }
        }else{
            if(cont <q){cont ++;}
            else{
                cont=0;
                os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
            }
        }
        
        
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) {
        //the process goes to the ready queue
        /*if(!cpuEmpty){
            Process p2 =  os.cpu.getProcess();
            os.cpu.removeProcess();
            os.rq.addProcess(p2);
        }*/
    } 

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        /*if(!cpuEmpty){
            Process p3 =  os.cpu.getProcess();
            os.cpu.removeProcess();
            os.rq.addProcess(p3);
        }*/
    } 
    
}
