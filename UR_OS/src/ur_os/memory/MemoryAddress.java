/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory;

/**
 *
 * @author super
 */
public class MemoryAddress {
    
    int page_frame;
    int offset;

    public MemoryAddress() {
        this(0,0);
    }
    
    public MemoryAddress(int page_frame, int offset) {
        this.page_frame = page_frame;
        this.offset = offset;
    }

    public int getPage_frame() {
        return page_frame;
    }

    public void setPage_frame(int page_frame) {
        this.page_frame = page_frame;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public int getAddress(){
        return this.page_frame+this.offset;
    }
    
}
