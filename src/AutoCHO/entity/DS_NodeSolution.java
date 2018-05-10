package AutoCHO.entity;
import java.util.*;

public class DS_NodeSolution {
    public int NodeID;
    public double AvgFragYield;
    public List<DS_Fragment> FragList; //Fragment list of the subtree when this node is regarded as a root
    
    public DS_NodeSolution(){
        NodeID = -1;
        AvgFragYield = 0;
        FragList = new ArrayList<>();
    }
    public void UpdateAvgFragYield(){
        for(DS_Fragment fragment: FragList){
            this.AvgFragYield += fragment.Yield;
        }
        this.AvgFragYield = this.AvgFragYield / this.FragList.size();
        if(this.AvgFragYield > 1)
            System.out.println();
    }
    public double getAvgFragYield(){
        return this.AvgFragYield;
    }
    public double getFragListSize(){
        return this.FragList.size();
    }
}
