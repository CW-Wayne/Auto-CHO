package AutoCHO.entity;
import java.util.*;

public class DS_Candidate {
    public int Steps;
    public double Yield;
    public String DeprotectingType;
    public List<DS_BuildingBlock> BBList;
    public List<List<DS_BuildingBlock>> FragBBList;    
    
    public DS_Candidate(){
        this.Steps = 0;
        this.Yield = 0;
        this.DeprotectingType = "";
        this.BBList = new ArrayList<>();
        this.FragBBList = new ArrayList<>();
    }
    
    public DS_Candidate(List<DS_BuildingBlock> BBList, double yield){
        this.Steps = 0;
        this.Yield = yield;
        this.DeprotectingType = "";
        this.BBList = BBList;
        this.FragBBList = new ArrayList<>();
    }
    
    public int getSteps(){
        return this.Steps;
    }
    public double getYield(){
        return this.Yield;
    }
    public String getDeProtType(){
        return this.DeprotectingType;
    }
    public List<DS_BuildingBlock> getBBList(){
        return this.BBList;
    }
    public List<List<DS_BuildingBlock>> getFragBBList(){
        return this.FragBBList;
    }
    
    public void setSteps(int steps){
        this.Steps = steps;
    }
    public void setYield(double yield){
        this.Yield = yield;
    }
    public void setDeProtType(String deProtType){
        this.DeprotectingType = deProtType;
    }
    public void setBBList(List<DS_BuildingBlock> BBList){
        this.BBList = BBList;
    }
    public void setFragBBList(List<List<DS_BuildingBlock>> fragBBList){
        this.FragBBList = fragBBList;
    }
}
