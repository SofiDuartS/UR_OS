/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory;

import ur_os.process.ProcessMemoryManager;
import ur_os.process.ProcessMemoryManagerType;

/**
 *
 * @author super
 */
public class PMM_Paging extends ProcessMemoryManager{
    
    PageTable pt;

    public PMM_Paging(int processSize) {
        super(ProcessMemoryManagerType.PAGING,processSize);
        pt = new PageTable(processSize);
    }

    public PMM_Paging(PageTable pt) {
        this.pt = pt;
    }
    
    public PMM_Paging(PMM_Paging pmm) {
        super(pmm);
        if(pmm.getType() == this.getType()){
            this.pt = new PageTable(pmm.getPt());
        }else{
            System.out.println("Error - Wrong PMM parameter");
        }
    }

    public PageTable getPt() {
        return pt;
    }
    
    public void addFrameID(int frame){
        pt.addFrameID(frame);
    }
    
    public MemoryAddress getPageMemoryAddressFromLocalAddress(int locAdd){
        //Include the code to calculate the corresponding page and offset for a logical address
        return null;
    }
    
    public MemoryAddress getFrameMemoryAddressFromLogicalMemoryAddress(MemoryAddress m){
        //Include the code to calculate the corresponding frame and offset for a logical address
        return null;
    }
    
   @Override
    public int getPhysicalAddress(int logicalAddress){
        
        //Include the code to calculate the physical address here
        
        return -1;
    }
    
     @Override
    public String toString(){
        return pt.toString();
    }
    
}
