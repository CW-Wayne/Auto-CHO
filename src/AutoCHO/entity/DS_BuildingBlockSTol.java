package AutoCHO.entity;

public class DS_BuildingBlockSTol {
    public int BBLIdx;
    public boolean STol;
    
    public DS_BuildingBlockSTol(){
        this.BBLIdx  = -1;
        this.STol = true;
    }
    
    public DS_BuildingBlockSTol(int BBLIdx){
        this.BBLIdx = BBLIdx;
        this.STol = true;
    }
}
