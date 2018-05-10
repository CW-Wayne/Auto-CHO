package AutoCHO.entity;
import java.util.*;
import org.eurocarbdb.application.glycanbuilder.*;

public class DS_SyntheticTarget {
    public List<DS_SugarStructure> sugarStuctList;
    public TreeMap<Integer, List<Integer>> newRootIDList;
    
    public DS_SyntheticTarget(Glycan glycan) throws Exception{
        sugarStuctList = new ArrayList<>();
        newRootIDList = new TreeMap<>();
        this.process(glycan);
    }
    
    //The function for dealing with reducing-end building block
    private void process(Glycan glycan) throws Exception{
        DS_OptGlycan target = new DS_OptGlycan(glycan);
        target.glycan = glycan;
        
        DS_SugarStructure SS = new DS_SugarStructure();
        SS.TargetGlycan = target;
        DS_SugarStructure sugarTree = new DS_SugarStructure();
        sugarTree.TargetGlycan = target.Copy();
        sugarStuctList.add(sugarTree);
    }
}
