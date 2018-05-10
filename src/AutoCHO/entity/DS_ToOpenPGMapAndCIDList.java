package AutoCHO.entity;
import java.util.List;
import java.util.Map;

public class DS_ToOpenPGMapAndCIDList {
    public List<Integer>  CIDList;                 //To record nodes that are out of subtree
    public Map<String, List<DS_PGRecord>> ToOpenPGMap;  //To record which protecting groups should be opened? <PGType, PGRecordList>
    
    public DS_ToOpenPGMapAndCIDList(List<Integer> CIDList, Map<String, List<DS_PGRecord>> ToOpenPGMap){
        this.CIDList = CIDList;
        this.ToOpenPGMap = ToOpenPGMap;
    }
}
