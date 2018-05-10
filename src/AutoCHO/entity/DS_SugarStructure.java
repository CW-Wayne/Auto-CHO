package AutoCHO.entity;
import java.util.*;

public class DS_SugarStructure {
    public DS_OptGlycan TargetGlycan;          
    public DS_OptGlycan CurrentGlycan;
    public DS_OptGlycan ReducingEndGlycan;
    public List<DS_BuildingBlock> BBList;
    public List<DS_Candidate> CandidateList;
    
    public DS_SugarStructure(){
        this.TargetGlycan = new DS_OptGlycan();
        this.CurrentGlycan = new DS_OptGlycan();
        this.ReducingEndGlycan = new DS_OptGlycan();
        this.BBList = new ArrayList<>();
        this.CandidateList = new ArrayList<>();
    }
    
    public DS_SugarStructure Clone(){
        DS_SugarStructure SS = new DS_SugarStructure();
        
        SS.TargetGlycan = this.TargetGlycan.Copy();
        SS.CurrentGlycan = this.TargetGlycan.Copy();
        if(this.ReducingEndGlycan.glycan != null){
            SS.ReducingEndGlycan = this.ReducingEndGlycan.Copy();
        }
        for(DS_BuildingBlock BB: this.BBList){
            SS.BBList.add(BB);
        }
        for(DS_Candidate candidate: this.CandidateList){
            SS.CandidateList.add(candidate);
        }
        return SS;
    }
}
