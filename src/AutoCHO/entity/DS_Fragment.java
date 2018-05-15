package AutoCHO.entity;
import AutoCHO.MainProcessor;
import java.util.*;

public class DS_Fragment {
    public double Yield;
    public double RRV;
    public int ParentBBLID;             //Parent fragment's BBL RootID
    public int ParentFragID;            //Parent fragment's root RootID
    public int RootID;                  //Fragment's root RootID
    public List<Integer> CIDList;       //Fragment's branch IDs
    public List<DS_BuildingBlockSTol> BBLList;
    public List<Integer> ResIDList;
    public Map<String, List<DS_PGRecord>> DePGMap;
    public boolean HasStableProductAnomer;
    public boolean IsSTolEnd;
    
    public DS_Fragment(){
        this.Yield = 0.0;
        this.RRV = 0.0;
        this.ParentBBLID = 0;
        this.ParentFragID = 0;
        this.RootID = 0;
        this.CIDList = new ArrayList<>();
        this.BBLList = new ArrayList<>();
        this.ResIDList = new ArrayList<>();
        this.DePGMap = new HashMap<>();
        this.HasStableProductAnomer = true;
        this.IsSTolEnd = true;
    }
    public DS_Fragment Copy(){
        DS_Fragment Fragment = new DS_Fragment();
        Fragment.Yield = this.Yield;
        Fragment.RRV = this.RRV;
        Fragment.ParentBBLID = this.ParentBBLID;
        Fragment.ParentFragID = this.ParentFragID;
        Fragment.RootID = this.RootID;
        Fragment.CIDList = new ArrayList<>(this.CIDList);
        Fragment.BBLList = new ArrayList<>(this.BBLList);
        Fragment.ResIDList = new ArrayList<>(this.ResIDList);
        Fragment.DePGMap = new HashMap<>(this.DePGMap);
        Fragment.HasStableProductAnomer = this.HasStableProductAnomer;
        Fragment.IsSTolEnd = this.IsSTolEnd;
        return Fragment;
    }
    public int GetBBLIDwithLargestRRV(){
        if(this.BBLList.size() > 0)
            return this.BBLList.get(0).BBLIdx;
        else
            return -1;
    }
    public int GetBBLIDwithSmallestRRV(){
        if(this.BBLList.size() > 0)
            return this.BBLList.get(this.BBLList.size() - 1).BBLIdx;
        else
            return -1;
    }
    public void UpdateRRV(){
        int BBID = this.GetBBLIDwithSmallestRRV();
        if(BBID >= 0){
            //this.RRV = MainProcessor.GetInstance().Lib.BBLList.get(BBID).RRV;
            this.RRV = MainProcessor.GetInstance().CombinedBBLList.get(BBID).RRV;
        }
    }
    public void CheckStableProductAnomer(List<DS_OptResidue> ResList){
        for(String PG: this.DePGMap.keySet()){
            //DS_BuildingBlock FragRootBBL = MainProcessor.GetInstance().Lib.BBLList.get(this.GetBBLIDwithSmallestRRV());
            DS_BuildingBlock FragRootBBL = MainProcessor.GetInstance().CombinedBBLList.get(this.GetBBLIDwithSmallestRRV());
            int RootGlycanID = FragRootBBL.Opt_Glycan.GetRootID();
            DS_OptResidue RootGlycan = FragRootBBL.Opt_Glycan.node.get(RootGlycanID);

            for(DS_OptResidue r: ResList){
                if(this.RootID == r.ID){
                    if(r.CID.containsKey(2) && RootGlycan.PG.containsKey(2) && RootGlycan.PG.get(2).equals(PG)){
                        this.HasStableProductAnomer = false;
                        break;
                    }
                }
            }
        }
    }
    public boolean ContainPG(List<String> ToOpenPGList){
        for(String ToOpenPG: ToOpenPGList){
            for(String DePG: DePGMap.keySet()){
                if(ToOpenPG.equalsIgnoreCase(DePG)){
                    return true;
                }
            }
        }
        return false;
    }
    public static void DFSOrder(List<DS_Fragment> FragmentList){
        Map<Integer, DS_Fragment> FragmentMap = new HashMap<>();
        int RootFragmentID = Integer.MAX_VALUE;
        for(DS_Fragment fragment: FragmentList){
            FragmentMap.put(fragment.RootID, fragment);
            if(fragment.RootID < RootFragmentID){
                RootFragmentID = fragment.RootID;
            }
        }
        DFSOrder(FragmentMap, RootFragmentID, 0);
    }
    private static Map<Integer, DS_Fragment> DFSOrder(Map<Integer, DS_Fragment> FragmentMap, int ID, int ParentFragID){
        DS_Fragment fragment = FragmentMap.get(ID);
        fragment.ParentFragID = ParentFragID;
        for(int CID: fragment.CIDList){
            DFSOrder(FragmentMap, CID, ID);
        }
        //System.out.println(fragment.RootID + " + " + fragment.ParentFragID);
        return FragmentMap;
    }
    
    //To Get Fragment Donor-Acceptor Index Map
    static List<DS_FragmentPair> FragmentDonorAcceptorIDPairList;
    public static List<DS_FragmentPair> DFSOrder2(List<DS_Fragment> FragmentList){
        //FragmentID<->Fragment
        Map<Integer, DS_Fragment> FragmentMap = new HashMap<>();
        
        //FragmentID<->FragmentListIdx
        Map<Integer, Integer> FragmentListIDIdxMap = new HashMap<>();
        
        //DonorFragmentID<->AcceptorFragmentID
        FragmentDonorAcceptorIDPairList = new ArrayList<>();
        
        int RootFragmentID = Integer.MAX_VALUE;
        for(int FragIdx = 0; FragIdx < FragmentList.size(); FragIdx++){
            DS_Fragment fragment = FragmentList.get(FragIdx);
            FragmentMap.put(fragment.RootID, fragment);
            FragmentListIDIdxMap.put(fragment.RootID, FragIdx);
            if(fragment.RootID < RootFragmentID){
                RootFragmentID = fragment.RootID;
            }
        }
        DFSOrder2(FragmentMap, RootFragmentID, 0);
        List<DS_FragmentPair> FragmentDonorAcceptorIdxPairList = new ArrayList<>();
        for(DS_FragmentPair IDPair: FragmentDonorAcceptorIDPairList){
            DS_FragmentPair IdxPair = new DS_FragmentPair();
            IdxPair.Donor = FragmentListIDIdxMap.get(IDPair.Donor);
            if(IDPair.Acceptor != 0)
                IdxPair.Acceptor = FragmentListIDIdxMap.get(IDPair.Acceptor);
            else
                IdxPair.Acceptor = -1;
            FragmentDonorAcceptorIdxPairList.add(IdxPair);
        }
        return FragmentDonorAcceptorIdxPairList;
    }
    private static void DFSOrder2(Map<Integer, DS_Fragment> FragmentMap, int ID, int ParentFragID){
        DS_Fragment fragment = FragmentMap.get(ID);
        fragment.ParentFragID = ParentFragID;
        for(int CID: fragment.CIDList){
            DFSOrder2(FragmentMap, CID, ID);
        }
        DS_FragmentPair pair = new DS_FragmentPair();
        pair.Donor = fragment.RootID;
        pair.Acceptor = fragment.ParentFragID;
        FragmentDonorAcceptorIDPairList.add(pair);
        return;
    }
}
