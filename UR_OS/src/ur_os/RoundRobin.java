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
            Process apuntador = null;
            if (cpuEmpty) {
                Process p = processes.get(0);
                processes.remove();
                os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
            }else{
                Process proc = os.cpu.getProcess();
                for(Process pr : processes){
                    if (cont < q) {
                        apuntador = pr;
                        os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, apuntador);
                        cont++;
                        processes.remove(apuntador);
                    }
                    if (proc.getRemainingTimeInCurrentBurst() != 0){
                        os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, apuntador);
                        processes.add(apuntador);
                    }
                }
                
            }
        }
        
        
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) {
        //the process goes to the ready queue
        Process p1 =  os.cpu.getProcess();
        os.cpu.removeProcess();
        os.rq.addProcess(p1);
    } 

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
    } 
    
}
