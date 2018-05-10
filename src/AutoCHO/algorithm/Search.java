package AutoCHO.algorithm;
import AutoCHO.entity.DS_SugarStructure;
import AutoCHO.entity.DS_OptResidue;
import AutoCHO.entity.DS_BuildingBlock;
import AutoCHO.entity.DS_ProtectingGroup;
import AutoCHO.entity.DS_PGRecord;
import AutoCHO.entity.DS_Fragment;
import AutoCHO.entity.DS_ToOpenPGMapAndCIDList;
import AutoCHO.entity.DS_BuildingBlockSTol;
import AutoCHO.entity.DS_BuildingBlockTextVirtual;
import AutoCHO.entity.DS_OptGlycan;
import AutoCHO.entity.DS_NodeSolution;
import AutoCHO.MainFormController;
import AutoCHO.MainProcessor;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

public class Search extends Thread{
    private DS_SugarStructure SS;
    private Map<Integer, List<DS_NodeSolution>> NodeSolMap;
    private boolean IsFragSearchMode;
    private boolean IsTestMode;
    private boolean ToConsiderNonSTol;
    private double MinFragYield;
    private int MaxFragNum;
    private int MinBBLNumOfEachFrag;
    private int MaxBBLNumOfEachFrag;
    
    private double RRV_THR_High;
    private double RRV_THR_Medium;
    
    private int RandomBBLNum;
    
    public Search(){
        
    }
    
    public void run(){
        if(MainProcessor.GetInstance().IsTestMode == true)
            System.out.println("Thread starts.");
        this.SS = MainProcessor.GetInstance().TargetGlycan.sugarStuctList.get(0);
        this.NodeSolMap = new HashMap<>();
        
        this.IsFragSearchMode = MainProcessor.GetInstance().IsFragSearchMode;
        this.IsTestMode = MainProcessor.GetInstance().IsTestMode;
        this.MinFragYield = MainProcessor.GetInstance().MinFragYield;
        
        this.MaxFragNum = MainProcessor.GetInstance().MaxFragNum;
        this.MinBBLNumOfEachFrag = MainProcessor.GetInstance().MinBBLNumOfEachFrag;
        this.MaxBBLNumOfEachFrag = MainProcessor.GetInstance().MaxBBLNumOfEachFrag;
        
        this.RRV_THR_High = MainProcessor.GetInstance().RRV_THR_High;
        this.RRV_THR_Medium = MainProcessor.GetInstance().RRV_THR_Medium;
        
        this.RandomBBLNum = MainProcessor.GetInstance().RandomBBLNum;
        this.ToConsiderNonSTol = MainProcessor.GetInstance().ToConsiderNonSTol;
        
        try {
            this.Search();
        } catch (InterruptedException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void Search() throws InterruptedException{
        long StartTime = System.currentTimeMillis();
        
        DS_OptGlycan TargetGlycan = SS.TargetGlycan;
        TreeMap<Integer, DS_OptResidue> TargetGlycanNode = SS.TargetGlycan.node;
        
        List<DS_OptResidue> LeafResList = FindLeafNodes(TargetGlycanNode);
        List<DS_BuildingBlock> LibBBLList = new ArrayList<>();
        if(MainProcessor.GetInstance().LibMode == 0){
            LibBBLList = MainProcessor.GetInstance().Lib.BBLList;
            MainProcessor.GetInstance().CombinedBBLList = LibBBLList;
        }
        else if(MainProcessor.GetInstance().LibMode == 1){
            LibBBLList.addAll(MainProcessor.GetInstance().Lib.BBLList);
            for(int idx: MainFormController.GetInstance().GetSelectedVBBLIdx()){
                LibBBLList.add(MainProcessor.GetInstance().Lib_VBBL.BBLList.get(idx));
            }
            LibBBLList.sort((DS_BuildingBlock BBL1, DS_BuildingBlock BBL2) -> Double.compare(BBL1.RRV, BBL2.RRV));
            MainProcessor.GetInstance().CombinedBBLList = LibBBLList;
        }
        List<String> AllowedPGList = DS_ProtectingGroup.GetAllowedPGList();
        //List<Integer> ReversedOrderRandomBBLIdxList = this.GetReversedOrderRandemBBLIdx(LibBBLList.size(), this.RandomBBLNum);
        
        //<editor-fold defaultstate="collapsed" desc="Start from leaf node">
        try{
            for(int LeafResIdx = 0; LeafResIdx < LeafResList.size(); LeafResIdx++){
                List<DS_OptResidue> ResList = new ArrayList<>();
                ResList.add(LeafResList.get(LeafResIdx));
                
                for(int CurrentResIdx = 0; CurrentResIdx < ResList.size(); CurrentResIdx++){
                    //If subtrees have no solution yet, restart from another leaf node and find solutions for these subtrees
                    if(CheckSubtreesHaveSol(ResList, CurrentResIdx) == false)
                        break;
                    
                    //Find the current residue's parent, add it into the ResList
                    if(ResList.get(CurrentResIdx).PID != 0)
                        ResList.add(TargetGlycanNode.get(ResList.get(CurrentResIdx).PID));
                    
                    //Find the current glycan structure
                    DS_OptGlycan CurrentGlycan = TargetGlycan.GetSubtree(ResList.get(CurrentResIdx));
                    int CurrentResID = ResList.get(CurrentResIdx).ID;
                    List<DS_NodeSolution> NodeSolList = new ArrayList<>();
                    
                    //To check if fragment size in the node solution > max fragment size
                    boolean Bool_FragNumOfNodeSolLessTHR = this.CheckFragNumOfNodeSolLessTHR(NodeSolList);
                    if(Bool_FragNumOfNodeSolLessTHR == false)
                        break;
                                
                    if(IsTestMode)
                        System.out.println("CurrentResidueID=" + ResList.get(CurrentResIdx).ID + "(ChildNode#=" + ResList.get(CurrentResIdx).CID.size() + "):");
                    
                    boolean DoesResHaveMatchedBBL = false;
                    //BBL Selection
                    for(int BBLIdx = (LibBBLList.size() - 1); BBLIdx >= 0; BBLIdx--){
                    //for(int q = 0; q < ReversedOrderRandomBBLIdxList.size(); q++){
                        //int BBLIdx = ReversedOrderRandomBBLIdxList.get(q);
                        //DS_BuildingBlock BBL = LibBBLList.get(BBLIdx).Clone();
                        DS_BuildingBlock BBL = LibBBLList.get(BBLIdx);
                        //<editor-fold defaultstate="collapsed" desc="To record subtree IDs and match the BBL and the current glycan structure">
                        boolean IsPerfectMatchedBBL = false;
                        Map<Integer, Integer> SubtreeIDMap = CurrentGlycan.FindSubtreeFromCurrentID(CurrentResID, BBL.Opt_Glycan);
                        if(SubtreeIDMap != null){
                            IsPerfectMatchedBBL = true;
                        }
                        else if(IsFragSearchMode == true){
                            IsPerfectMatchedBBL = false;
                            SubtreeIDMap = CurrentGlycan.FindSubtreeFragFromCurrentID(CurrentResID, BBL.Opt_Glycan);
                        }
                        if(SubtreeIDMap == null)
                            continue;
                        
                        List<Integer> SubtreeIDList = new ArrayList<>(SubtreeIDMap.keySet());
                        //</editor-fold>
                        
                        DoesResHaveMatchedBBL = true;
                        
                        //To record branch IDs and de-protecting groups
                        DS_ToOpenPGMapAndCIDList obj = this.GetToOpenPGMapAndCIDList(SubtreeIDMap, CurrentGlycan, AllowedPGList, BBL);
                        if(obj == null)
                            continue;
                        List<Integer> CIDList = obj.CIDList;
                        Map<String, List<DS_PGRecord>> ToOpenPGMap = obj.ToOpenPGMap;
                        
                        //To check if all child nodes have solution(s) [CIDList values are not NULL]
                        if(CheckAllChildNodesHaveSol(CIDList) == false)
                            continue;
                        
                        //<editor-fold defaultstate="collapsed" desc="The node has no child: leaf node">
                        if(CIDList.isEmpty()){
                            //Since this is for target tree terminal, it should be a full protected building block
                            if(IsPerfectMatchedBBL == true && BBL.RRV > this.RRV_THR_High){
                            //if(IsPerfectMatchedBBL == true){
                                DS_Fragment Fragment = new DS_Fragment();
                                Fragment.RootID = CurrentResID;
                                //Fragment.ParentBBLID;      (TBD)
                                //Fragment.ParentFragID;     (TBD) 
                                //Fragment.CIDList;   (No CIDList)
                                //Fragment.DeGPList;  (No deprotecting group)
                                Fragment.Yield = 1.0;
                                Fragment.ResIDList = SubtreeIDList;
                                DS_BuildingBlockSTol BBLS = new DS_BuildingBlockSTol();
                                BBLS.BBLIdx = BBLIdx;
                                BBLS.STol = true;
                                Fragment.BBLList.add(BBLS);
                                Fragment.UpdateRRV();
                                
                                DS_NodeSolution NodeSol = new DS_NodeSolution();
                                NodeSol.NodeID = CurrentResID;
                                NodeSol.FragList.add(Fragment);
                                NodeSolList.add(NodeSol);
                                
                                //this.PrintNodeSolList(IsTestMode, NodeSolList, LibBBLList);
                            }
                            
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="The node has one child">
                        else if(CIDList.size() == 1){
                            int CID = CIDList.get(0);
                            if(NodeSolMap.get(CID) == null)
                                continue;
                            
                            //<editor-fold defaultstate="collapsed" desc="Perfect matched BBL">
                            if(IsPerfectMatchedBBL == true){
                                for(int SolIdx = 0; SolIdx < NodeSolMap.get(CID).size(); SolIdx++){
                                    //<editor-fold defaultstate="collapsed" desc="Fragment list of child node solution exists">
                                    if(NodeSolMap.get(CID).get(SolIdx).FragList.isEmpty())
                                        continue;
                                    
                                    List<DS_Fragment> ChildNodeFragList = this.GetChildNodeFragList(CID, SolIdx, CurrentResID);
                                    if(ChildNodeFragList.isEmpty())
                                        break;

                                    int DonorFragIdx = this.FindDonorFragIdx(CID, SolIdx, CurrentResID);
                                    if(ChildNodeFragList.get(DonorFragIdx).IsSTolEnd == false)
                                        continue;

                                    int FragBBLIndex = ChildNodeFragList.get(DonorFragIdx).BBLList.size() + 1;
                                    boolean Bool_CurrentBBLRRV_Less_PreBBLRRV = CheckCurrentRRV_Less_PreRRV(BBL.RRV, ChildNodeFragList.get(DonorFragIdx).BBLList, LibBBLList);
                                    boolean Bool_BBLContainPG = CheckBBLContainPG(BBL, ChildNodeFragList.get(DonorFragIdx).DePGMap.keySet());

                                    if(FragBBLIndex == 2){
                                        if(Bool_BBLContainPG == true)
                                           continue; 
                                        if(Bool_CurrentBBLRRV_Less_PreBBLRRV == true && BBL.RRV > this.RRV_THR_Medium && BBL.RRV <= this.RRV_THR_High){
                                            DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_OneChild(CID, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, true);
                                            if(NodeSol != null)
                                                NodeSolList.add(NodeSol);
                                        }
                                        if(ChildNodeFragList.get(DonorFragIdx).IsSTolEnd == true && this.ToConsiderNonSTol == true){
                                            DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_OneChild(CID, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, false);
                                            if(NodeSol != null)
                                                NodeSolList.add(NodeSol);
                                        }
                                    }
                                    else if(FragBBLIndex == 3){
                                        if(Bool_BBLContainPG == true)
                                            continue;
                                        //===============================
                                        if(Bool_CurrentBBLRRV_Less_PreBBLRRV == true && BBL.RRV <= this.RRV_THR_Medium){
                                            DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_OneChild(CID, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, true);
                                            if(NodeSol != null)
                                                NodeSolList.add(NodeSol);
                                        }
                                        //===============================
                                        if(ChildNodeFragList.get(DonorFragIdx).IsSTolEnd == true && this.ToConsiderNonSTol == true){
                                            DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_OneChild(CID, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, false);
                                            if(NodeSol != null)
                                                NodeSolList.add(NodeSol);
                                        }
                                    }
                                    //</editor-fold>                                
                                }
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Effective matched BBL (need to deprotect PG)">
                            else{
                                for(int SolIdx = 0; SolIdx < NodeSolMap.get(CID).size(); SolIdx++){
                                    //<editor-fold defaultstate="collapsed" desc="Fragment list of child node solution exists">
                                    if(NodeSolMap.get(CID).get(SolIdx).FragList.isEmpty())
                                        continue;
                                    
                                    List<DS_Fragment> ChildNodeFragList = this.GetChildNodeFragList(CID, SolIdx, CurrentResID);
                                    if(ChildNodeFragList.isEmpty())
                                        break;

                                    int DonorFragIdx = this.FindDonorFragIdx(CID, SolIdx, CurrentResID);
                                    //if(ChildNodeFragList.get(DonorFragIdx).IsSTolEnd == true)
                                    //    continue;
                                    if(ChildNodeFragList.get(DonorFragIdx).BBLList.size() < this.MinBBLNumOfEachFrag)
                                        continue;
                                    if(ChildNodeFragList.get(DonorFragIdx).BBLList.size() > this.MaxBBLNumOfEachFrag)
                                        continue;

                                    //FragBBLIndex == 1
                                    if(BBL.RRV > this.RRV_THR_High){
                                        DS_NodeSolution NodeSol = this.AddBBLToNewFrag_OneChild(CID, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, ToOpenPGMap, ResList);
                                        if(NodeSol != null)
                                            NodeSolList.add(NodeSol);
                                    }
                                    //</editor-fold>
                                }
                            }
                            //</editor-fold>
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="The node has multiple children">
                        else{
                            //<editor-fold defaultstate="collapsed" desc="Perfect matched BBL">
                            if(IsPerfectMatchedBBL == true){
                                if(CheckAllSameTree(SS, CIDList) == false)
                                    continue;
                                
                                int CID = CIDList.get(0);
                                for(int SolIdx = 0; SolIdx < NodeSolMap.get(CID).size(); SolIdx++){
                                    //<editor-fold defaultstate="collapsed" desc="Fragment list of child node solution exists">
                                    if(NodeSolMap.get(CID).get(SolIdx).FragList.isEmpty())
                                        continue;
                                    
                                    List<DS_Fragment> ChildNodeFragList = this.GetChildNodeFragList(CID, SolIdx, CurrentResID);
                                    
                                    int DonorFragIdx = this.FindDonorFragIdx(CID, SolIdx, CurrentResID);
                                    if(ChildNodeFragList.get(DonorFragIdx).IsSTolEnd == false)
                                        continue;
                                    
                                    int FragBBLIndex = ChildNodeFragList.get(DonorFragIdx).BBLList.size() + 1;
                                    boolean Bool_CurrentBBLRRV_Less_PreBBLRRV = CheckCurrentRRV_Less_PreRRV(BBL.RRV, ChildNodeFragList.get(DonorFragIdx).BBLList, LibBBLList);
                                    boolean Bool_BBLContainPG = CheckBBLContainPG(BBL, ChildNodeFragList.get(DonorFragIdx).DePGMap.keySet());
                                    
                                    if(FragBBLIndex == 2){
                                        if(Bool_BBLContainPG == true)
                                           continue; 
                                        if(Bool_CurrentBBLRRV_Less_PreBBLRRV == true && BBL.RRV > this.RRV_THR_Medium && BBL.RRV <= this.RRV_THR_High){
                                            DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_MultiChildren(CID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, true);
                                            if(NodeSol != null)
                                                NodeSolList.add(NodeSol);
                                        }
                                        if(ChildNodeFragList.get(DonorFragIdx).IsSTolEnd == true && this.ToConsiderNonSTol == true){
                                            DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_MultiChildren(CID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, false);
                                            if(NodeSol != null)
                                                NodeSolList.add(NodeSol);
                                        }
                                    }
                                    else if(FragBBLIndex == 3){
                                        if(Bool_BBLContainPG == true)
                                            continue;
                                        //===============================
                                        if(Bool_CurrentBBLRRV_Less_PreBBLRRV == true && BBL.RRV <= this.RRV_THR_Medium){
                                            DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_MultiChildren(CID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, true);
                                            if(NodeSol != null)
                                                NodeSolList.add(NodeSol);
                                        }
                                        //===============================
                                        if(ChildNodeFragList.get(DonorFragIdx).IsSTolEnd == true && this.ToConsiderNonSTol == true){
                                            DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_MultiChildren(CID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, false);
                                            if(NodeSol != null)
                                                NodeSolList.add(NodeSol);
                                        }
                                    }
                                    //</editor-fold>
                                }
                            }
                            //</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="Effective matched BBL (need to deprotect PG)">
                            else{
                                boolean IsSamePGSameTree = CheckPosWithSamePGHaveSameSubtree(ToOpenPGMap);
                                if(IsSamePGSameTree == false)
                                    continue;
                                
                                CIDList.clear();
                                for(String PG: ToOpenPGMap.keySet()){
                                    CIDList.add(ToOpenPGMap.get(PG).get(0).CID);
                                }
                                
                                //<editor-fold defaultstate="collapsed" desc="The BBL has free acceptor position(s)">
                                if(BBL.IsFullyProtected() == false){
                                    int DonorCID = ToOpenPGMap.get("OH").get(0).CID;
                                    for(int SolIdx = 0; SolIdx < NodeSolMap.get(DonorCID).size(); SolIdx++){
                                        if(NodeSolMap.get(DonorCID).get(SolIdx).FragList.isEmpty())
                                            continue;

                                        List<DS_Fragment> DonorChildNodeFragList = this.GetChildNodeFragList(DonorCID, SolIdx, CurrentResID);
                                        List<DS_Fragment> OtherChildNodeFragList = new ArrayList<>();
                                        for(int CID: CIDList)
                                            if(CID != DonorCID)
                                                for(int SolIdx2 = 0; SolIdx2 < NodeSolMap.get(CID).size(); SolIdx2++)
                                                    OtherChildNodeFragList.addAll(NodeSolMap.get(CID).get(SolIdx2).FragList);
                                        
                                        int DonorFragIdx = this.FindDonorFragIdx(DonorCID, SolIdx, CurrentResID);
                                        if(DonorChildNodeFragList.get(DonorFragIdx).BBLList.size() < this.MinBBLNumOfEachFrag)
                                            continue;
                                        if(DonorChildNodeFragList.get(DonorFragIdx).BBLList.size() > this.MaxBBLNumOfEachFrag)
                                            continue;

                                        int FragBBLIndex = DonorChildNodeFragList.get(DonorFragIdx).BBLList.size() + 1;
                                        boolean Bool_CurrentBBLRRV_Less_PreBBLRRV = CheckCurrentRRV_Less_PreRRV(BBL.RRV, DonorChildNodeFragList.get(DonorFragIdx).BBLList, LibBBLList);
                                        boolean Bool_BBLContainPG = CheckBBLContainPG(BBL, DonorChildNodeFragList.get(DonorFragIdx).DePGMap.keySet());
                                        
                                        //FragBBLIndex == 1
                                        if(FragBBLIndex == 1){
                                            if(BBL.RRV > this.RRV_THR_High){
                                                List<DS_NodeSolution> TempNodeSolList = this.AddFullProtectedBBLToNewFrag_MultiChildren(CIDList, BBL, BBLIdx, LibBBLList, CurrentResID, SubtreeIDList, ToOpenPGMap, ResList, true);
                                                if(TempNodeSolList != null)
                                                    NodeSolList.addAll(TempNodeSolList);
                                            }
                                            else if (this.ToConsiderNonSTol == true){
                                                List<DS_NodeSolution> TempNodeSolList = this.AddFullProtectedBBLToNewFrag_MultiChildren(CIDList, BBL, BBLIdx, LibBBLList, CurrentResID, SubtreeIDList, ToOpenPGMap, ResList, false);
                                                if(TempNodeSolList != null)
                                                    NodeSolList.addAll(TempNodeSolList);
                                            }
                                        }
                                        //FragBBLIndex == 2
                                        else if(FragBBLIndex == 2){
                                            if(Bool_BBLContainPG == true)
                                               continue;
                                            if(Bool_CurrentBBLRRV_Less_PreBBLRRV == true && BBL.RRV > this.RRV_THR_Medium && BBL.RRV <= this.RRV_THR_High){
                                                //DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_MultiChildren(DonorCID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, true);
                                                DS_NodeSolution NodeSol = this.AddPartialProtectedBBLToCurrentFrag_MultiChildren(DonorCID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, DonorChildNodeFragList, OtherChildNodeFragList, ToOpenPGMap, SolIdx, DonorFragIdx, true);
                                                if(NodeSol != null)
                                                    NodeSolList.add(NodeSol);
                                            }
                                            if(DonorChildNodeFragList.get(DonorFragIdx).IsSTolEnd == true && this.ToConsiderNonSTol == true){
                                                //DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_MultiChildren(DonorCID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, false);
                                                DS_NodeSolution NodeSol = this.AddPartialProtectedBBLToCurrentFrag_MultiChildren(DonorCID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, DonorChildNodeFragList, OtherChildNodeFragList, ToOpenPGMap, SolIdx, DonorFragIdx, false);
                                                if(NodeSol != null)
                                                    NodeSolList.add(NodeSol);
                                            }
                                        }
                                        //FragBBLIndex == 3
                                        else if(FragBBLIndex == 3){
                                            if(Bool_BBLContainPG == true)
                                                continue;
                                            if(Bool_CurrentBBLRRV_Less_PreBBLRRV == true && BBL.RRV <= this.RRV_THR_Medium){
                                                //DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_MultiChildren(DonorCID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, true);
                                                DS_NodeSolution NodeSol = this.AddPartialProtectedBBLToCurrentFrag_MultiChildren(DonorCID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, DonorChildNodeFragList, OtherChildNodeFragList, ToOpenPGMap, SolIdx, DonorFragIdx, true);
                                                if(NodeSol != null)
                                                    NodeSolList.add(NodeSol);
                                            }
                                            if(DonorChildNodeFragList.get(DonorFragIdx).IsSTolEnd == true && this.ToConsiderNonSTol == true){
                                                //DS_NodeSolution NodeSol = this.AddBBLToCurrentFrag_MultiChildren(DonorCID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, ChildNodeFragList, SolIdx, DonorFragIdx, false);
                                                DS_NodeSolution NodeSol = this.AddPartialProtectedBBLToCurrentFrag_MultiChildren(DonorCID, CIDList, BBLIdx, CurrentResID, SubtreeIDList, DonorChildNodeFragList, OtherChildNodeFragList, ToOpenPGMap, SolIdx, DonorFragIdx, false);
                                                if(NodeSol != null)
                                                    NodeSolList.add(NodeSol);
                                            }
                                        }
                                    }
                                }
                                //</editor-fold>
                                //<editor-fold defaultstate="collapsed" desc="The BBL is fully protected">
                                else{
                                    boolean CheckPoint = true;
                                    for(int CID: CIDList){
                                        for(int SolIdx = 0; SolIdx < NodeSolMap.get(CID).size(); SolIdx++){
                                            List<DS_Fragment> ChildNodeFragList = new ArrayList<>();
                                            ChildNodeFragList.addAll(NodeSolMap.get(CID).get(SolIdx).FragList);
                                            
                                            int FragIdx = this.FindDonorFragIdx(CID, SolIdx, CurrentResID);
                                            if(ChildNodeFragList.get(FragIdx).BBLList.size() < this.MinBBLNumOfEachFrag){
                                                CheckPoint = false;
                                                break;
                                            }
                                            if(ChildNodeFragList.get(FragIdx).BBLList.size() > this.MaxBBLNumOfEachFrag){
                                                CheckPoint = false;
                                                break;
                                            }
                                            if(CheckPoint == false)
                                                break;
                                        } 
                                    }
                                    if(CheckPoint == false)
                                        continue;
                                    
                                    //FragBBLIndex == 1
                                    if(BBL.RRV > this.RRV_THR_High){
                                        List<DS_NodeSolution> TempNodeSolList = this.AddFullProtectedBBLToNewFrag_MultiChildren(CIDList, BBL, BBLIdx, LibBBLList, CurrentResID, SubtreeIDList, ToOpenPGMap, ResList, true);
                                        if(TempNodeSolList != null)
                                            NodeSolList.addAll(TempNodeSolList);
                                    }
                                    else if (this.ToConsiderNonSTol == true){
                                        List<DS_NodeSolution> TempNodeSolList = this.AddFullProtectedBBLToNewFrag_MultiChildren(CIDList, BBL, BBLIdx, LibBBLList, CurrentResID, SubtreeIDList, ToOpenPGMap, ResList, false);
                                        if(TempNodeSolList != null)
                                            NodeSolList.addAll(TempNodeSolList);
                                    }
                                }
                                //</editor-fold>
                                
                            }
                            //</editor-fold>
                        }
                        //</editor-fold>
                    }
                    
                    if(DoesResHaveMatchedBBL == false)
                        NodeSolMap.put(CurrentResID, null);
                    else{
                        NodeSolMap.put(CurrentResID, NodeSolList);
                        if(IsTestMode)
                            System.out.println("NodeSolution#=" + NodeSolList.size());
                        this.PrintNodeSolList(IsTestMode, NodeSolList, LibBBLList);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            if(MainProcessor.GetInstance().IsTestMode == true){
                long EndTime = System.currentTimeMillis();
                long ProcessTime = EndTime - StartTime;
                System.out.println("Time(seconds)=" + (ProcessTime / 1000f));
                System.out.println("Error!!!");
            }
        }
        //</editor-fold>
        
        for(int CurrentNodeKey: NodeSolMap.keySet()){
            for(int SolIdx = 0; SolIdx < NodeSolMap.get(CurrentNodeKey).size(); SolIdx++){
                NodeSolMap.get(CurrentNodeKey).get(SolIdx).UpdateAvgFragYield();
            }
            NodeSolMap.get(CurrentNodeKey).sort(Comparator.comparing((DS_NodeSolution sol) -> sol.AvgFragYield).reversed());
            NodeSolMap.get(CurrentNodeKey).sort(Comparator.comparing((DS_NodeSolution sol) -> sol.FragList.size()));
        }
        
        //this.InOrderForFragmentConnection(LibBBLList);
        this.PrintNodeSolMapToResultText(true, LibBBLList);
        if(MainProcessor.GetInstance().IsTestMode == true){
            this.PrintNodeSolMapToFile(MainProcessor.GetInstance().PrintToFile, LibBBLList);
            long EndTime = System.currentTimeMillis();
            long ProcessTime = EndTime - StartTime;
            System.out.println("Time(seconds)=" + (ProcessTime / 1000f));
            System.out.println("Done!!!");
        }
        
        MainFormController.GetInstance().SetNodeSolMap(NodeSolMap);
        MainFormController.GetInstance().SetLibBBLList(LibBBLList);
        MainFormController.GetInstance().ShowSolResult();
    }
    
    private List<DS_OptResidue> FindLeafNodes(TreeMap<Integer, DS_OptResidue> TargetGlycanNode){
        List<DS_OptResidue> LeafResList = new ArrayList<>();
        
        for(int key: TargetGlycanNode.keySet()){
            DS_OptResidue r = TargetGlycanNode.get(key);
            boolean IsLeave = true;
            for(int CIDkey: r.CID.keySet()){
                if(r.CID.get(CIDkey) > 0){
                    IsLeave = false;
                    break;
                }
            }
            if(IsLeave == true){
                LeafResList.add(r);
            }
        }
        
        if(MainProcessor.GetInstance().IsTestMode == true)
            System.out.println("Leaf Node Number: " + LeafResList.size());
        
        return LeafResList;
    }
    
    private int FindDonorFragIdx(int CID, int SolIdx, int CurrentResID){
        for(int idx = 0; idx < NodeSolMap.get(CID).get(SolIdx).FragList.size(); idx++){
            if(NodeSolMap.get(CID).get(SolIdx).FragList.get(idx).ParentBBLID == 0)
                return idx;
            else if(NodeSolMap.get(CID).get(SolIdx).FragList.get(idx).ParentBBLID == CurrentResID)
                return idx;
        }
        return -1;
    }
    
    private List<DS_Fragment> GetChildNodeFragList(int CID, int SolIdx, int CurrentResID){
        List<DS_Fragment> ChildNodeFragList = new ArrayList<>();
        for(DS_Fragment Fragment: NodeSolMap.get(CID).get(SolIdx).FragList){
            DS_Fragment NewFrag = Fragment.Copy();
            if(NewFrag.ParentBBLID == 0){
                NewFrag.ParentBBLID = CurrentResID;
            }
            ChildNodeFragList.add(NewFrag);
        }
        return ChildNodeFragList;
    }
    
    private DS_ToOpenPGMapAndCIDList GetToOpenPGMapAndCIDList(Map<Integer, Integer> SubtreeIDMap, DS_OptGlycan CurrentGlycan, List<String> AllowedPGList, DS_BuildingBlock BBL){
        List<Integer>  CIDList = new ArrayList<>();                    //To record nodes that are out of subtree
        List<DS_PGRecord> PGRecordList = new ArrayList<>();            //To record protecting group information
        Map<String, List<DS_PGRecord>> ToOpenPGMap = new HashMap<>();  //To record which protecting groups should be opened? <PGType, PGRecordList>
        
        for(int TargetNodeID: SubtreeIDMap.keySet()){
            int BBLNodeID = SubtreeIDMap.get(TargetNodeID);
            for(int Pos: CurrentGlycan.node.get(TargetNodeID).CID.keySet()){
                int CID = CurrentGlycan.node.get(TargetNodeID).CID.get(Pos);
                if(!SubtreeIDMap.containsKey(CID)){
                    //String PG = BBL.Opt_Glycan.node.get(BBLNodeID).PG.get(Pos).Abbreviation;
                    String PG = BBL.Opt_Glycan.node.get(BBLNodeID).PG.get(Pos);
                    if(!AllowedPGList.contains(PG))
                        return null;
                    
                    for(int p: BBL.Opt_Glycan.node.get(BBLNodeID).PG.keySet()){
                        if(BBL.Opt_Glycan.node.get(BBLNodeID).PG.get(p).equals(PG) && !CurrentGlycan.node.get(TargetNodeID).CID.containsKey(p))
                            return null;
                    }
                    
                    DS_PGRecord record = new DS_PGRecord();
                    record.TargetNodeID = TargetNodeID;
                    record.CID = CID;
                    record.Position = Pos;
                    record.PG = PG;
                    PGRecordList.add(record);
                    CIDList.add(CID);
                    if(!ToOpenPGMap.containsKey(PG)){
                        List<DS_PGRecord> recordList = new ArrayList<>();
                        recordList.add(record);
                        ToOpenPGMap.put(PG, recordList);
                    }
                    else{
                        ToOpenPGMap.get(PG).add(record);
                    }
                }
            }
        }
        DS_ToOpenPGMapAndCIDList obj = new DS_ToOpenPGMapAndCIDList(CIDList, ToOpenPGMap);
        return obj;
    }
    
    private boolean CheckCurrentRRV_Less_PreRRV(double CurrentBBLRRV, List<DS_BuildingBlockSTol> BBLList, List<DS_BuildingBlock> LibraryBBLList){
        int LastBBLIdx = BBLList.size() - 1;
        int PreBBLIdx  = BBLList.get(LastBBLIdx).BBLIdx;
        double PreBBLRRV = LibraryBBLList.get(PreBBLIdx).RRV;
        if(CurrentBBLRRV < PreBBLRRV)
            return true;
        return false;
    }
    
    private boolean CheckBBLContainPG(DS_BuildingBlock BBL, Set<String> PGSet){
        return BBL.ContainPG(new ArrayList<>(PGSet));
    }
    
    private boolean CheckFragNumOfNodeSolLessTHR(List<DS_NodeSolution> NodeSolList){
        int FragNum = 0;
        if(NodeSolList.size() > 0){
            FragNum = NodeSolList.get(NodeSolList.size()- 1).FragList.size();
        }
        if(FragNum > this.MaxFragNum){
            if(IsTestMode)
                System.out.println("> Max Fragment Number!!!");
            return false;
        }
        return true;
    }
    
    private DS_NodeSolution AddBBLToCurrentFrag_OneChild(int CID, int BBLIdx, int CurrentResID, List<Integer> SubtreeIDList, List<DS_Fragment> ChildNodeFragList, int SolIdx, int DonorFragIdx, boolean IsSTolBBL){
        List<DS_BuildingBlockSTol> NodeBBLList = new ArrayList<>();
        NodeBBLList.addAll(NodeSolMap.get(CID).get(SolIdx).FragList.get(DonorFragIdx).BBLList);
        NodeBBLList.add(new DS_BuildingBlockSTol(BBLIdx));

        List<Integer> FragCID = new ArrayList<>();
        FragCID.addAll(ChildNodeFragList.get(DonorFragIdx).CIDList);
        
        List<Integer> FragResIDList = new ArrayList<>();
        FragResIDList.addAll(ChildNodeFragList.get(DonorFragIdx).ResIDList);
        FragResIDList.addAll(SubtreeIDList);

        List<Integer> NodeBBLIdxList = new ArrayList<>();
        for(DS_BuildingBlockSTol BBLS: NodeBBLList){
            NodeBBLIdxList.add(BBLS.BBLIdx);
        }
        
        double FragYield = Yield.CalcYield(NodeBBLIdxList);
        boolean CheckRRVRatio = Yield.CheckRRVRatio(NodeBBLIdxList);
        boolean CheckRRVDiff = Yield.CheckRRVDiff(NodeBBLIdxList);
        if(CheckRRVRatio && CheckRRVDiff && (FragYield >= MinFragYield)){
            DS_Fragment Fragment = new DS_Fragment();
            Fragment.RootID = CurrentResID;
            Fragment.ParentFragID = 0;
            Fragment.ParentBBLID = 0;
            Fragment.CIDList = FragCID;
            Fragment.DePGMap = new TreeMap<>(ChildNodeFragList.get(DonorFragIdx).DePGMap);
            Fragment.Yield = FragYield;
            Fragment.ResIDList = FragResIDList;
            Fragment.BBLList = NodeBBLList;
            Fragment.IsSTolEnd = IsSTolBBL;
            Fragment.UpdateRRV();

            DS_NodeSolution NodeSol = new DS_NodeSolution();
            NodeSol.NodeID = CurrentResID;
            for(int fragIdx = 0; fragIdx < ChildNodeFragList.size() - 1; fragIdx++){
                if(ChildNodeFragList.get(fragIdx).ParentFragID == 0)
                    ChildNodeFragList.get(fragIdx).ParentFragID = CurrentResID;
                NodeSol.FragList.add(ChildNodeFragList.get(fragIdx));    
            }
            NodeSol.FragList.add(Fragment);
            //NodeSolution.OverallYield = Yield.CalcOverallYield(NodeSol.FragList);
            //if(NodeSol.OverallYield >= MainProcessor.GetInstance().MinOverallYield)
            return NodeSol;
        }
        return null;
    }
    
    private DS_NodeSolution AddBBLToNewFrag_OneChild(int CID, int BBLIdx, int CurrentResID, List<Integer> SubtreeIDList, List<DS_Fragment> ChildNodeFragList, Map<String, List<DS_PGRecord>> ToOpenPGMap,  List<DS_OptResidue> ResList){
        List<DS_BuildingBlockSTol> NodeBBLList = new ArrayList();
        NodeBBLList.add(new DS_BuildingBlockSTol(BBLIdx));
        DS_Fragment Fragment = new DS_Fragment();
        Fragment.RootID = CurrentResID;
        //Fragment.ParentBBLID = ResList.get(CurrentResIdx).ParentBBLID;
        Fragment.CIDList.add(CID);
        Fragment.DePGMap = new HashMap<>(ToOpenPGMap);
        Fragment.Yield = 1.0;
        Fragment.ResIDList = SubtreeIDList;
        Fragment.BBLList = NodeBBLList;
        Fragment.UpdateRRV();
        Fragment.CheckStableProductAnomer(ResList);

        DS_NodeSolution NodeSol = new DS_NodeSolution();
        NodeSol.NodeID = CurrentResID;
        NodeSol.FragList.addAll(ChildNodeFragList);
        NodeSol.FragList.add(Fragment);
        //NodeSolution.OverallYield = Yield.CalcOverallYield(NodeSol.FragList);
        //if(NodeSol.OverallYield >= MainProcessor.GetInstance().MinOverallYield)
        return NodeSol;
    }
    
    //For perfect matched BBL
    private DS_NodeSolution AddBBLToCurrentFrag_MultiChildren(int DonorCID, List<Integer> CIDList, int BBLIdx, int CurrentResID, List<Integer> SubtreeIDList, List<DS_Fragment> ChildNodeFragList, int SolIdx, int DonorFragIdx, boolean IsSTolBBL){
        List<DS_BuildingBlockSTol> NodeBBLList = new ArrayList<>();
        NodeBBLList.addAll(NodeSolMap.get(DonorCID).get(SolIdx).FragList.get(DonorFragIdx).BBLList);
        NodeBBLList.add(new DS_BuildingBlockSTol(BBLIdx));
        
        List<Integer> FragCID = new ArrayList<>();
        FragCID.addAll(ChildNodeFragList.get(DonorFragIdx).CIDList);
        
        List<Integer> FragResIDList = new ArrayList<>();
        for(int ID: CIDList){
            int idx = NodeSolMap.get(ID).get(0).FragList.size() - 1;
            FragResIDList.addAll(NodeSolMap.get(ID).get(0).FragList.get(idx).ResIDList);
        }
        FragResIDList.addAll(SubtreeIDList);

        List<Integer> NodeBBLIdxList = new ArrayList<>();
        for(DS_BuildingBlockSTol BBLS: NodeBBLList){
            NodeBBLIdxList.add(BBLS.BBLIdx);
        }
        
        double FragYield = Yield.CalcYield(NodeBBLIdxList);
        boolean CheckRRVRatio = Yield.CheckRRVRatio(NodeBBLIdxList);
        boolean CheckRRVDiff = Yield.CheckRRVDiff(NodeBBLIdxList);
        if(CheckRRVRatio && CheckRRVDiff && (FragYield >= MinFragYield)){
            DS_Fragment Fragment = new DS_Fragment();
            Fragment.RootID = CurrentResID;
            //Fragment.ParentBBLID = ResList.get(CurrentResIdx).ParentBBLID;
            Fragment.CIDList = FragCID;
            Fragment.DePGMap = new TreeMap<>(ChildNodeFragList.get(DonorFragIdx).DePGMap);
            Fragment.Yield = FragYield;
            Fragment.ResIDList = FragResIDList;
            Fragment.BBLList = NodeBBLList;
            Fragment.IsSTolEnd = IsSTolBBL;
            Fragment.UpdateRRV();

            DS_NodeSolution NodeSol = new DS_NodeSolution();
            NodeSol.NodeID = CurrentResID;
            for(int fragIdx = 0; fragIdx < ChildNodeFragList.size() - 1; fragIdx++){
                NodeSol.FragList.add(ChildNodeFragList.get(fragIdx));    
            }
            NodeSol.FragList.add(Fragment);
            //NodeSolution.OverallYield = Yield.CalcOverallYield(NodeSol.FragList);
            //if(NodeSol.OverallYield >= MainProcessor.GetInstance().MinOverallYield){
            return NodeSol;
        }
        return null;
    }
    
    //For effectively matched BBL
    private DS_NodeSolution AddPartialProtectedBBLToCurrentFrag_MultiChildren(int DonorCID, List<Integer> CIDList, int BBLIdx, int CurrentResID, List<Integer> SubtreeIDList, List<DS_Fragment> DonorChildNodeFragList, List<DS_Fragment> OtherChildNodeFragList, Map<String, List<DS_PGRecord>> ToOpenPGMap, int SolIdx, int DonorFragIdx, boolean IsSTolBBL){
        List<DS_BuildingBlockSTol> NodeBBLList = new ArrayList<>();
        NodeBBLList.addAll(NodeSolMap.get(DonorCID).get(SolIdx).FragList.get(DonorFragIdx).BBLList);
        NodeBBLList.add(new DS_BuildingBlockSTol(BBLIdx));
        
        List<Integer> FragCID = new ArrayList<>();
        FragCID.addAll(DonorChildNodeFragList.get(DonorFragIdx).CIDList);
        
        List<Integer> FragResIDList = new ArrayList<>();
        for(int CID: CIDList)
            if(CID != DonorCID)
                FragCID.add(CID);
        
        int idx = NodeSolMap.get(DonorCID).get(0).FragList.size() - 1;
        FragResIDList.addAll(NodeSolMap.get(DonorCID).get(0).FragList.get(idx).ResIDList);
        FragResIDList.addAll(SubtreeIDList);

        List<Integer> NodeBBLIdxList = new ArrayList<>();
        for(DS_BuildingBlockSTol BBLS: NodeBBLList){
            NodeBBLIdxList.add(BBLS.BBLIdx);
        }
        
        double FragYield = Yield.CalcYield(NodeBBLIdxList);
        boolean CheckRRVRatio = Yield.CheckRRVRatio(NodeBBLIdxList);
        boolean CheckRRVDiff = Yield.CheckRRVDiff(NodeBBLIdxList);
        if(CheckRRVRatio && CheckRRVDiff && (FragYield >= MinFragYield)){
            DS_Fragment Fragment = new DS_Fragment();
            Fragment.RootID = CurrentResID;
            Fragment.ParentFragID = 0;
            Fragment.ParentBBLID = 0;
            Fragment.CIDList = FragCID;
            Fragment.DePGMap = new TreeMap<>(DonorChildNodeFragList.get(DonorFragIdx).DePGMap);
            
            for(String PG: ToOpenPGMap.keySet()){
                if(PG.equals("OH"))
                    continue;
                Fragment.DePGMap.put(PG, ToOpenPGMap.get(PG));
            }
            
            Fragment.Yield = FragYield;
            Fragment.ResIDList = FragResIDList;
            Fragment.BBLList = NodeBBLList;
            Fragment.IsSTolEnd = IsSTolBBL;
            Fragment.UpdateRRV();

            DS_NodeSolution NodeSol = new DS_NodeSolution();
            NodeSol.NodeID = CurrentResID;
            for(int FragIdx = 0; FragIdx < OtherChildNodeFragList.size(); FragIdx++){
                if(OtherChildNodeFragList.get(FragIdx).ParentFragID == 0){
                    OtherChildNodeFragList.get(FragIdx).ParentFragID = CurrentResID;
                    OtherChildNodeFragList.get(FragIdx).ParentBBLID = CurrentResID;
                }
                NodeSol.FragList.add(OtherChildNodeFragList.get(FragIdx));    
            }
            NodeSol.FragList.add(Fragment);
            return NodeSol;
        }
        return null;
    }
    
    //For effectively matched BBL
    private List<DS_NodeSolution> AddFullProtectedBBLToNewFrag_MultiChildren(List<Integer> CIDList, DS_BuildingBlock BBL, int BBLIdx, List<DS_BuildingBlock> LibBBLList, int CurrentResID, List<Integer> SubtreeIDList, Map<String, List<DS_PGRecord>> ToOpenPGMap,  List<DS_OptResidue> ResList, boolean IsSTolBBL){
        List<DS_NodeSolution> TempNodeSolList;
        TempNodeSolList = DoFullyProtectedBBLAddition(CIDList, BBLIdx, CurrentResID, SubtreeIDList, ToOpenPGMap, ResList, IsSTolBBL);
//        if(BBL.IsFullyProtected() == true)
//            TempNodeSolList = DoFullyProtectedBBLAddition(CIDList, BBLIdx, CurrentResID, SubtreeIDList, ToOpenPGMap, ResList, IsSTolBBL);
//        else
//            TempNodeSolList = DoPartialProtectedBBLAddition(CIDList, BBLIdx, CurrentResID, SubtreeIDList, ToOpenPGMap, ResList, LibBBLList, BBL, IsSTolBBL);
        return TempNodeSolList;
    }
    
    private List<DS_NodeSolution> DoFullyProtectedBBLAddition(List<Integer> CIDList, int BBLIdx, int CurrentResID, List<Integer> SubtreeIDList, Map<String, List<DS_PGRecord>> ToOpenPGMap, List<DS_OptResidue> ResList, boolean IsSTolBBL){
        List<DS_NodeSolution> NodeSolList = new ArrayList<>();
        for(int CIDIdx = 0; CIDIdx < CIDList.size(); CIDIdx++){
            int CID = CIDList.get(CIDIdx);
            List<DS_NodeSolution> TempNodeSolList = new ArrayList<>();

            for(int SolIdx = 0; SolIdx < NodeSolMap.get(CID).size(); SolIdx++){
                List<DS_Fragment> ChildNodeFragList = this.GetChildNodeFragList(CID, SolIdx, CurrentResID);
                
                if(CIDIdx == 0){
                    DS_NodeSolution NodeSol = new DS_NodeSolution();
                    NodeSol.NodeID = CurrentResID;
                    for(int FragIdx = 0; FragIdx < ChildNodeFragList.size(); FragIdx++){
                        if(ChildNodeFragList.get(FragIdx).ParentFragID == 0){
                            ChildNodeFragList.get(FragIdx).ParentFragID = CurrentResID;
                            ChildNodeFragList.get(FragIdx).ParentBBLID = CurrentResID;
                        }
                    }
                    NodeSol.FragList.addAll(ChildNodeFragList);
                    TempNodeSolList.add(NodeSol);
//                    NodeSol.OverallYield = Yield.CalcOverallYield(NodeSol.FragList);
//                    if(NodeSol.AvgFragYield >= MainProcessor.GetInstance().MinOverallYield)
//                        TempNodeSolList.add(NodeSol);
//                    else
//                        NodeSol = null;
                }
                else{
                    for(int index = 0; index < NodeSolList.size(); index++){
                        //TempNodeSolutionList.get(index).FragList.addAll(ChildNodeFragList);
                        DS_NodeSolution NodeSol = new DS_NodeSolution();
                        NodeSol.NodeID = CurrentResID;
                        NodeSol.FragList.addAll(NodeSolList.get(index).FragList);
                        for(int FragIdx = 0; FragIdx < ChildNodeFragList.size(); FragIdx++){
                            if(ChildNodeFragList.get(FragIdx).ParentFragID == 0){
                                ChildNodeFragList.get(FragIdx).ParentFragID = CurrentResID;
                                ChildNodeFragList.get(FragIdx).ParentBBLID = CurrentResID;
                            }
                        }
                        NodeSol.FragList.addAll(ChildNodeFragList);
                        TempNodeSolList.add(NodeSol);
//                        NodeSolution.OverallYield = Yield.CalcOverallYield(NodeSol.FragList);
//                        if(NodeSol.AvgFragYield >= MainProcessor.GetInstance().MinOverallYield)
//                            TempNodeSolList.add(NodeSol);
//                        else
//                            NodeSol = null;
                    }
                }
            }
            NodeSolList = TempNodeSolList;
        }

        for(int idx = 0; idx < NodeSolList.size(); idx++){
            DS_Fragment Fragment = new DS_Fragment();
            Fragment.RootID = CurrentResID;
            Fragment.ParentFragID = 0;
            Fragment.ParentBBLID = 0;
            Fragment.CIDList.addAll(CIDList);
            Fragment.DePGMap = new HashMap<>(ToOpenPGMap);
            Fragment.Yield = 1.0;
            Fragment.ResIDList = SubtreeIDList;
            Fragment.BBLList.add(new DS_BuildingBlockSTol(BBLIdx));
            Fragment.IsSTolEnd = IsSTolBBL;
            Fragment.UpdateRRV();
            Fragment.HasStableProductAnomer = this.CheckFragStableProductAnomer(Fragment, ResList);
            
            NodeSolList.get(idx).FragList.add(Fragment);
        }
        return NodeSolList;
    }
    
    private List<DS_NodeSolution> DoPartialProtectedBBLAddition(List<Integer> CIDList, int BBLIdx, int CurrentResID, List<Integer> SubtreeIDList, Map<String, List<DS_PGRecord>> ToOpenPGMap, List<DS_OptResidue> ResList, List<DS_BuildingBlock> LibBBLList, DS_BuildingBlock BBL, boolean IsSTolBBL){
        int DonorFragCID = ToOpenPGMap.get("OH").get(0).CID;
        List<Integer> FragCID = new ArrayList<>();
        List<DS_NodeSolution> NodeSolList = new ArrayList<>();
        for(int CIDIdx = 0; CIDIdx < CIDList.size(); CIDIdx++){
            int CID = CIDList.get(CIDIdx);
            List<DS_NodeSolution> TempNodeSolList = new ArrayList<>();

            for(int SolIdx = 0; SolIdx < NodeSolMap.get(CID).size(); SolIdx++){
                List<DS_Fragment> ChildNodeFragList = this.GetChildNodeFragList(CID, SolIdx, CurrentResID);
                
                //<editor-fold defaultstate="collapsed" desc="The first child">
                if(CIDIdx == 0){
                    DS_NodeSolution NodeSol = new DS_NodeSolution();
                    NodeSol.NodeID = CurrentResID;
                    //NodeSolution.Yield;

                    if(CID != DonorFragCID){
                        NodeSol.FragList.addAll(ChildNodeFragList);
                        //NodeSolution.OverallYield = Yield.CalcOverallYield(NodeSol.FragList);
                        if(NodeSol.AvgFragYield >= MainProcessor.GetInstance().MinOverallYield){
                            TempNodeSolList.add(NodeSol);
                            FragCID.add(CID);
                        }
                    }
                    else{
                        int DonorFragIdx = this.FindDonorFragIdx(CID, SolIdx, CurrentResID);
                        int LastBBIdx = ChildNodeFragList.get(DonorFragIdx).BBLList.size() - 1;
                        int PreBBIdx  = ChildNodeFragList.get(DonorFragIdx).BBLList.get(LastBBIdx).BBLIdx;
                        double PreBBRRV = LibBBLList.get(PreBBIdx).RRV;

                        if(BBL.RRV < PreBBRRV && !BBL.ContainPG(new ArrayList<>(ChildNodeFragList.get(DonorFragIdx).DePGMap.keySet()))){
                            List<DS_BuildingBlockSTol> NodeBBLList = new ArrayList<>();
                            NodeBBLList.addAll(NodeSolMap.get(DonorFragCID).get(SolIdx).FragList.get(DonorFragIdx).BBLList);
                            NodeBBLList.add(new DS_BuildingBlockSTol(BBLIdx));
                            
                            List<Integer> NodeBBLIdxList = new ArrayList<>();
                            for(DS_BuildingBlockSTol BBLS: NodeBBLList)
                                NodeBBLIdxList.add(BBLS.BBLIdx);

                            double FragYield = Yield.CalcYield(NodeBBLIdxList);
                            boolean CheckRRVRatio = Yield.CheckRRVRatio(NodeBBLIdxList);
                            boolean CheckRRVDiff = Yield.CheckRRVDiff(NodeBBLIdxList);
                            if(CheckRRVRatio && CheckRRVDiff && (FragYield >= MinFragYield)){
                                DS_Fragment Fragment = new DS_Fragment();
                                Fragment.RootID = CurrentResID;
                                Fragment.ParentFragID = 0;
                                Fragment.ParentBBLID = 0;
                                Fragment.CIDList = FragCID;
                                for(String PG: ToOpenPGMap.keySet()){
                                    if(!PG.equals("OH")){
                                        Fragment.DePGMap.put(PG, ToOpenPGMap.get(PG));
                                    }
                                }
                                Fragment.Yield = FragYield;
                                //Fragment.ResIDList = FragResIDList;
                                Fragment.BBLList = NodeBBLList;
                                Fragment.IsSTolEnd = IsSTolBBL;
                                Fragment.UpdateRRV();

                                //<editor-fold defaultstate="collapsed" desc="Chech the position 2 of reducing end residue of the fragment (position 2 rule)">
                                for(String PG: Fragment.DePGMap.keySet()){
                                        DS_BuildingBlock FragRootBBL = MainProcessor.GetInstance().Lib.BBLList.get(Fragment.GetBBLIDwithSmallestRRV());
                                        int RootGlycanID = FragRootBBL.Opt_Glycan.GetRootID();
                                        DS_OptResidue RootGlycan = FragRootBBL.Opt_Glycan.node.get(RootGlycanID);

                                        for(DS_OptResidue r: ResList){
                                                if(Fragment.RootID == r.ID){
                                                        if(r.CID.containsKey(2) && RootGlycan.PG.containsKey(2) && RootGlycan.PG.get(2).equals(PG)){
                                                                Fragment.HasStableProductAnomer = false;
                                                        }
                                                }
                                        }
                                }
                                //</editor-fold>

                                for(int fragIdx = 0; fragIdx < ChildNodeFragList.size() - 1; fragIdx++)
                                    NodeSol.FragList.add(ChildNodeFragList.get(fragIdx));
                                NodeSol.FragList.add(Fragment);
                                //NodeSol.OverallYield = Yield.CalcOverallYield(NodeSol.FragList);
                                if(NodeSol.AvgFragYield >= MainProcessor.GetInstance().MinOverallYield)
                                    TempNodeSolList.add(NodeSol);
                            }
                        }
                    }
                }
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Other children">
                else{
                    if(CID != DonorFragCID){
                        for(int index = 0; index < NodeSolList.size(); index++){
                            DS_NodeSolution NodeSol = new DS_NodeSolution();
                            NodeSol.NodeID = CurrentResID;
                            NodeSol.FragList.addAll(NodeSolList.get(index).FragList);
                            NodeSol.FragList.addAll(ChildNodeFragList);
                            //NodeSol.OverallYield = Yield.CalcOverallYield(NodeSol.FragList);
                            if(NodeSol.AvgFragYield >= MainProcessor.GetInstance().MinOverallYield){
                                TempNodeSolList.add(NodeSol);
                            }
                            else{
                                NodeSol = null;
                            }
                        }
                        FragCID.add(CID);
                    }
                    else{
                        for(int index = 0; index < NodeSolList.size(); index++){
                            int DonorFragIdx = this.FindDonorFragIdx(CID, SolIdx, CurrentResID);
                            int LastBBIdx = ChildNodeFragList.get(DonorFragIdx).BBLList.size() - 1;
                            int PreBBIdx  = ChildNodeFragList.get(DonorFragIdx).BBLList.get(LastBBIdx).BBLIdx;
                            double PreBBRRV = LibBBLList.get(PreBBIdx).RRV;

                            if(BBL.RRV < PreBBRRV && !BBL.ContainPG(new ArrayList<>(ChildNodeFragList.get(DonorFragIdx).DePGMap.keySet()))){
                                List<DS_BuildingBlockSTol> NodeBBLList = new ArrayList<>();
                                NodeBBLList.addAll(NodeSolMap.get(DonorFragCID).get(SolIdx).FragList.get(DonorFragIdx).BBLList);
                                NodeBBLList.add(new DS_BuildingBlockSTol(BBLIdx));

                                List<Integer> NodeBBLIdxList = new ArrayList<>();
                                for(DS_BuildingBlockSTol BBLS: NodeBBLList){
                                    NodeBBLIdxList.add(BBLS.BBLIdx);
                                }

                                double FragYield = Yield.CalcYield(NodeBBLIdxList);
                                boolean CheckRRVRatio = Yield.CheckRRVRatio(NodeBBLIdxList);
                                boolean CheckRRVDiff = Yield.CheckRRVDiff(NodeBBLIdxList);
                                if(CheckRRVRatio && CheckRRVDiff && (FragYield >= MinFragYield)){
                                    DS_Fragment Fragment = new DS_Fragment();
                                    Fragment.RootID = CurrentResID;
                                    Fragment.ParentFragID = 0;
                                    Fragment.ParentBBLID = 0;
                                    Fragment.CIDList = FragCID;
                                    for(String PG: ToOpenPGMap.keySet()){
                                        if(!PG.equals("OH")){
                                            Fragment.DePGMap.put(PG, ToOpenPGMap.get(PG));
                                        }
                                    }
                                    Fragment.Yield = FragYield;
                                    //Fragment.ResIDList = FragResIDList;
                                    Fragment.BBLList = NodeBBLList;
                                    Fragment.IsSTolEnd = IsSTolBBL;
                                    Fragment.UpdateRRV();

                                    //<editor-fold defaultstate="collapsed" desc="Chech the position 2 of reducing end residue of the fragment (position 2 rule)">
                                    for(String PG: Fragment.DePGMap.keySet()){
                                            DS_BuildingBlock FragRootBBL = MainProcessor.GetInstance().Lib.BBLList.get(Fragment.GetBBLIDwithSmallestRRV());
                                            int RootGlycanID = FragRootBBL.Opt_Glycan.GetRootID();
                                            DS_OptResidue RootGlycan = FragRootBBL.Opt_Glycan.node.get(RootGlycanID);

                                            for(DS_OptResidue r: ResList){
                                                    if(Fragment.RootID == r.ID){
                                                            if(r.CID.containsKey(2) && RootGlycan.PG.containsKey(2) && RootGlycan.PG.get(2).equals(PG)){
                                                                    Fragment.HasStableProductAnomer = false;
                                                            }
                                                    }
                                            }
                                    }
                                    //</editor-fold>

                                    DS_NodeSolution NodeSol = new DS_NodeSolution();
                                    NodeSol.NodeID = CurrentResID;
                                    NodeSol.FragList.addAll(NodeSolList.get(index).FragList);
                                    for(int fragIdx = 0; fragIdx < ChildNodeFragList.size() - 1; fragIdx++){
                                        NodeSol.FragList.add(ChildNodeFragList.get(fragIdx));
                                    }
                                    NodeSol.FragList.add(Fragment);
                                    //NodeSolution.OverallYield = Yield.CalcOverallYield(NodeSol.FragList);
                                    if(NodeSol.AvgFragYield >= MainProcessor.GetInstance().MinOverallYield){
                                        TempNodeSolList.add(NodeSol);
                                    }
                                    else{
                                        NodeSol = null;
                                    }
                                }
                            }
                        }
                    }
                }
                //</editor-fold>
            }
            NodeSolList = TempNodeSolList;
        }
        return NodeSolList;
    }
    
    private boolean CheckSubtreesHaveSol(List<DS_OptResidue> ResList, int CurrentResIdx){
        for(int CID: ResList.get(CurrentResIdx).CID.values())
            if(!NodeSolMap.containsKey(CID))
                return false;
        return true;
    }
    
    private boolean CheckPosWithSamePGHaveSameSubtree(Map<String, List<DS_PGRecord>> ToOpenPGMap){
        for(String PG: ToOpenPGMap.keySet()){
            List<DS_PGRecord> recordList = ToOpenPGMap.get(PG);
            List<Integer> PGCIDList = new ArrayList<>();
            for(DS_PGRecord record: recordList)
                PGCIDList.add(record.CID);
            
            if(CheckAllSameTree(SS, PGCIDList) == false)
                return false;
        }
        return true;
    }
    
    private boolean CheckAllChildNodesHaveSol(List<Integer> BranchIDList){
        for(int CID: BranchIDList)
            if(NodeSolMap.get(CID) == null)
                return false;
        return true;
    }
    
    private boolean CheckFragStableProductAnomer(DS_Fragment Fragment, List<DS_OptResidue> ResList){
        for(String PG: Fragment.DePGMap.keySet()){
            DS_BuildingBlock FragRootBBL = MainProcessor.GetInstance().Lib.BBLList.get(Fragment.GetBBLIDwithSmallestRRV());
            int RootGlycanID = FragRootBBL.Opt_Glycan.GetRootID();
            DS_OptResidue RootGlycan = FragRootBBL.Opt_Glycan.node.get(RootGlycanID);

            for(DS_OptResidue r: ResList)
                if(Fragment.RootID == r.ID && r.CID.containsKey(2) && RootGlycan.PG.containsKey(2) && RootGlycan.PG.get(2).equals(PG))
                    return false;
        }
        return true;
    }
    
    private void PrintNodeSolList(boolean IsTestMode, List<DS_NodeSolution> NodeSolList, List<DS_BuildingBlock> LibBBLList){
        if(IsTestMode == false)
            return;
        
        int SolCount = 1;
        for(DS_NodeSolution sns: NodeSolList){
            System.out.print("Solution " + SolCount + ":");
            System.out.println("\tOverallYield=" + sns.AvgFragYield);

            int FragCount = 1;
            for(DS_Fragment sf: sns.FragList){
                System.out.print("\t");
                System.out.print("Fragment " + FragCount + ":");
                System.out.print("\tFragmentYield=" + sf.Yield);
                System.out.println("\tIsSTolEnd=" + sf.IsSTolEnd);
                
                for(DS_BuildingBlockSTol BBLS: sf.BBLList){
                    int BBLIdx = BBLS.BBLIdx;
                    DS_BuildingBlock BBLtemp = LibBBLList.get(BBLIdx);
                    System.out.print("\t\t");
                    System.out.print("BBIdx=" + BBLIdx + "(Name=" + BBLtemp.Opt_Glycan.GetRootResName() + "|RRV=" + BBLtemp.RRV + "|Src=" + (BBLtemp.Source.equals("Experimental") ? "Exp" : "Vir") + ")");
                    Set<Integer> keySet = BBLtemp.Opt_Glycan.node.keySet();
                    for(int i: keySet){
                        System.out.print(BBLtemp.Opt_Glycan.node.get(i).PG.get(2) + "\t");
                        System.out.print(BBLtemp.Opt_Glycan.node.get(i).PG.get(3) + "\t");
                        System.out.print(BBLtemp.Opt_Glycan.node.get(i).PG.get(4) + "\t");
                        System.out.println(BBLtemp.Opt_Glycan.node.get(i).PG.get(6) + "\t");
                    }
                }
                ++FragCount;
            }
            ++SolCount;
        }
    }
    
    private void PrintNodeSolListToResultText(boolean IsTestMode, List<DS_NodeSolution> NodeSolList, List<DS_BuildingBlock> LibBBLList){
        if(IsTestMode == false)
            return;
        
        StringBuffer ResultText = new StringBuffer();
        int SolCount = 1;
        for(DS_NodeSolution sns: NodeSolList){
            ResultText.append("Solution " + SolCount + ":");
            ResultText.append("\tOverallYield=" + sns.AvgFragYield + "\n");
            
            int FragCount = 1;
            for(DS_Fragment sf: sns.FragList){
                ResultText.append("\t");
                ResultText.append("Fragment " + FragCount + ":");
                ResultText.append("\tFragmentYield=" + sf.Yield);
                ResultText.append("\tIsSTolEnd=" + sf.IsSTolEnd + "\n");
                
                for(DS_BuildingBlockSTol BBLS: sf.BBLList){
                    int BBLIdx = BBLS.BBLIdx;
                    DS_BuildingBlock BBLtemp = LibBBLList.get(BBLIdx);
                    ResultText.append("\t\t");
                    ResultText.append("BBIdx=" + BBLtemp.DBIdx + "(Name=" + BBLtemp.Opt_Glycan.GetRootResName() + "|RRV=" + BBLtemp.RRV + "|Src=" + (BBLtemp.Source.equals("Experimental") ? "Exp" : "Vir") + ")");
                    Set<Integer> keySet = BBLtemp.Opt_Glycan.node.keySet();
                    for(int i: keySet){
                        ResultText.append(BBLtemp.Opt_Glycan.node.get(i).PG.get(2) + "\t");
                        ResultText.append(BBLtemp.Opt_Glycan.node.get(i).PG.get(3) + "\t");
                        ResultText.append(BBLtemp.Opt_Glycan.node.get(i).PG.get(4) + "\t");
                        ResultText.append(BBLtemp.Opt_Glycan.node.get(i).PG.get(6) + "\n");
                    }
                }
                ++FragCount;
            }
            ++SolCount;
        }
    }
    
    private void InOrderForFragmentConnection(List<DS_BuildingBlock> LibBBLList){
        Object[] NodeSolKeys = NodeSolMap.keySet().toArray();
        Arrays.sort(NodeSolKeys);

        System.out.print("Experimental Building Blocks for Searching: ALL\n");
        System.out.print("Virtual Building Block(s) for Searching:");
        List<Integer> SelectedVBBLIdxList = MainFormController.GetInstance().GetSelectedVBBLIdx();

        if(SelectedVBBLIdxList.size() == 0)
            System.out.print(" None.");
        else
            System.out.print("\n");
        for(int i = 0; i < SelectedVBBLIdxList.size(); i++){
            int VBBLIdx = SelectedVBBLIdxList.get(i);
            DS_BuildingBlockTextVirtual VBBL = MainProcessor.GetInstance().Lib_VBBL.BBLTextList.get(VBBLIdx);
            System.out.print("\tIndex" + VBBLIdx + "\t" + VBBL.sugarType + "[" + VBBL.R2 + "," + VBBL.R3 + "," + VBBL.R4 + "," + VBBL.R6 + "]" + " PredictedRRV=" + VBBL.RRV + "\n");
        }
        System.out.print("\n");
        System.out.print("RESULT:\n");
        for(Object obj: NodeSolKeys){
            int ID = (int)obj;
            System.out.print("MonosaccharideID:[" + ID + "]" + "\n");
            if(NodeSolMap.get(ID) != null){
                for(int m = 0; m < NodeSolMap.get(ID).size(); m++){
                    System.out.print("Solution " + (m + 1) + ":" + "\n");
                    DS_Fragment.DFSOrder(NodeSolMap.get(ID).get(m).FragList);
                    
                    int FragListSize = NodeSolMap.get(ID).get(m).FragList.size();
                    
                    Map<Integer, DS_Fragment> FragmentMap = new HashMap<>();
                    for(int fragIdx = 0; fragIdx < FragListSize; fragIdx++){
                        DS_Fragment fragment = NodeSolMap.get(ID).get(m).FragList.get(fragIdx);
                        FragmentMap.put(fragment.RootID, fragment);
                    }
                    for(int fragIdx = 0; fragIdx < FragListSize; fragIdx++){
                        DS_Fragment fragment = NodeSolMap.get(ID).get(m).FragList.get(fragIdx);
                        System.out.println("FragRootID=" + fragment.RootID + "\tParentID=" + fragment.ParentFragID + "\tRRV=" + fragment.RRV);
                    }
//                    for(int fragIdx = 0; fragIdx < FragListSize; fragIdx++){
//                        DS_Fragment fragment = NodeSolMap.get(ID).get(m).FragList.get(fragIdx);
//                        
//                        System.out.print("\tFragment " + (fragIdx + 1) + "[Fragment Yield=");
//                        System.out.print(String.format("%.2f", fragment.Yield * 100));
//                        System.out.print("%]");
//                        /*
//                        System.out.print("[");
//                        System.out.print("RootID: " + fragment.RootID + " | ");
//                        System.out.print("ParentBBLID: " + fragment.ParentBBLID + " | ");
//                        System.out.print("ParentFragID: " + fragment.ParentFragID  + " | ");
//                        System.out.print("CID:");
//                        for(int CID: fragment.CIDList){
//                            System.out.print(" " + CID);
//                        }
//                        System.out.print("]");
//                        */
//                        System.out.print("[RRV=" + fragment.RRV +"]");
//                        System.out.print("[");
//                        System.out.print("Deprotect=");
//                        boolean NeedToDeprotect = false;
//                        for(String PGKey: NodeSolMap.get(ID).get(m).FragList.get(fragIdx).DePGMap.keySet()){
//                            for(DS_PGRecord record: NodeSolMap.get(ID).get(m).FragList.get(fragIdx).DePGMap.get(PGKey)){
//                                System.out.print(record.Position + "-");
//                            }
//                            System.out.print(PGKey + ",");
//                            NeedToDeprotect = true;
//                        }
//                        if(NeedToDeprotect == false)
//                            System.out.print("N/A");
//                        System.out.print("][");
//                        System.out.print("HasSatbleProductAnomer=" + (NodeSolMap.get(ID).get(m).FragList.get(fragIdx).HasStableProductAnomer ? "Yes" : "No"));
//                        System.out.print("][");
//                        System.out.print("LeavingGroup=" + (NodeSolMap.get(ID).get(m).FragList.get(fragIdx).IsSTolEnd ? "STol" : "Non-STol") + "]\n");
//                        for(int n = 0; n < NodeSolMap.get(ID).get(m).FragList.get(fragIdx).BBLList.size(); n++){
//                            int BBLIdx = NodeSolMap.get(ID).get(m).FragList.get(fragIdx).BBLList.get(n).BBLIdx;
//                            System.out.print("\t\t");
//                            System.out.print("BBL RRV=");
//                            System.out.print(String.format("%.2f",LibBBLList.get(BBLIdx).RRV));
//                            System.out.print("\tType=");
//                            System.out.print(LibBBLList.get(BBLIdx).Type);
//                            System.out.print("\tIndex=");
//                            System.out.print(LibBBLList.get(BBLIdx).DBIdx);
//                            System.out.print("\tProtectingGroup=[");
//                            int firstKey = LibBBLList.get(BBLIdx).Opt_Glycan.node.firstKey();
//                            Object[] PGKeys = LibBBLList.get(BBLIdx).Opt_Glycan.node.get(firstKey).PG.keySet().toArray();
//                            Arrays.sort(PGKeys);
//
//                            for(int i = 0; i < PGKeys.length; i++){
//                                int key = (int)PGKeys[i];
//                                String PG = LibBBLList.get(BBLIdx).Opt_Glycan.node.get(firstKey).PG.get(key);
//                                if(LibBBLList.get(BBLIdx).Name.equals("NeuAc") && key == 2)
//                                    continue;
//                                else if(key == 1)
//                                    continue;
//                                if(i != PGKeys.length - 1)
//                                    System.out.print(key + ":" + PG + ",");
//                                else
//                                    System.out.print(key + ":" + PG + "]");
//                            }
//                            if(n < NodeSolMap.get(ID).get(m).FragList.get(fragIdx).BBLList.size() - 1){
//                                System.out.print("\n");
//                            }
//                        }
//                        System.out.print("\n");
//                    }
                }
                if(NodeSolMap.get(ID).isEmpty()){
                    System.out.print("--\n");
                }
            }
            else{
                System.out.print("null\n");
            }
        }
    }
    
    private void PrintNodeSolMapToResultText(boolean ToPrint, List<DS_BuildingBlock> LibBBLList){
        if(ToPrint){
            StringBuffer text = new StringBuffer();
            Object[] NodeSolKeys = NodeSolMap.keySet().toArray();
            Arrays.sort(NodeSolKeys);

            text.append("Experimental Building Blocks for Searching: ALL\n");
            text.append("Virtual Building Block(s) for Searching:");
            List<Integer> SelectedVBBLIdxList = MainFormController.GetInstance().GetSelectedVBBLIdx();
            
            if(SelectedVBBLIdxList.size() == 0)
                text.append(" None.");
            else
                text.append("\n");
            for(int i = 0; i < SelectedVBBLIdxList.size(); i++){
                int VBBLIdx = SelectedVBBLIdxList.get(i);
                DS_BuildingBlockTextVirtual VBBL = MainProcessor.GetInstance().Lib_VBBL.BBLTextList.get(VBBLIdx);
                text.append("\tIndex" + VBBLIdx + "\t" + VBBL.sugarType + "[" + VBBL.R2 + "," + VBBL.R3 + "," + VBBL.R4 + "," + VBBL.R6 + "]" + " PredictedRRV=" + VBBL.RRV + "\n");
            }
            text.append("\n");
            text.append("RESULT:\n");
            for(Object obj: NodeSolKeys){
                int ID = (int)obj;
                text.append("MonosaccharideID:[" + ID + "]" + "\n");
                if(NodeSolMap.get(ID) != null){
                    for(int m = 0; m < NodeSolMap.get(ID).size(); m++){
                        text.append("Solution " + (m + 1) + ":" + "\n");
                        DS_Fragment.DFSOrder(NodeSolMap.get(ID).get(m).FragList);
                        
                        StringBuffer fragSynStepText = new StringBuffer();
                        int FragListSize = NodeSolMap.get(ID).get(m).FragList.size();
                        for(int fragIdx = 0; fragIdx < FragListSize; fragIdx++){
                            DS_Fragment fragment = NodeSolMap.get(ID).get(m).FragList.get(fragIdx);
                            text.append("\tFragment " + (fragIdx + 1) + "[Fragment Yield=");
                            text.append(String.format("%.2f", fragment.Yield * 100));
                            text.append("%]");
                            /*
                            text.append("[");
                            text.append("RootID: " + fragment.RootID + " | ");
                            text.append("ParentBBLID: " + fragment.ParentBBLID + " | ");
                            text.append("ParentFragID: " + fragment.ParentFragID  + " | ");
                            text.append("CID:");
                            for(int CID: fragment.CIDList){
                                text.append(" " + CID);
                            }
                            text.append("]");
                            */
                            text.append("[");
                            text.append("Deprotect=");
                            boolean NeedToDeprotect = false;
                            for(String PGKey: NodeSolMap.get(ID).get(m).FragList.get(fragIdx).DePGMap.keySet()){
                                for(DS_PGRecord record: NodeSolMap.get(ID).get(m).FragList.get(fragIdx).DePGMap.get(PGKey)){
                                    text.append(record.Position + "-");
                                }
                                text.append(PGKey + ",");
                                NeedToDeprotect = true;
                            }
                            if(NeedToDeprotect == false)
                                text.append("N/A");
                            text.append("][");
                            text.append("HasSatbleProductAnomer=" + (NodeSolMap.get(ID).get(m).FragList.get(fragIdx).HasStableProductAnomer ? "Yes" : "No"));
                            text.append("][");
                            text.append("LeavingGroup=" + (NodeSolMap.get(ID).get(m).FragList.get(fragIdx).IsSTolEnd ? "STol" : "Non-STol") + "]\n");
                            for(int n = 0; n < NodeSolMap.get(ID).get(m).FragList.get(fragIdx).BBLList.size(); n++){
                                int BBLIdx = NodeSolMap.get(ID).get(m).FragList.get(fragIdx).BBLList.get(n).BBLIdx;
                                text.append("\t\t");
                                text.append("BBL RRV=");
                                text.append(String.format("%.2f",LibBBLList.get(BBLIdx).RRV));
                                text.append("\tType=");
                                text.append(LibBBLList.get(BBLIdx).Type);
                                text.append("\tIndex=");
                                text.append(LibBBLList.get(BBLIdx).DBIdx);
                                text.append("\tProtectingGroup=[");
                                int firstKey = LibBBLList.get(BBLIdx).Opt_Glycan.node.firstKey();
                                Object[] PGKeys = LibBBLList.get(BBLIdx).Opt_Glycan.node.get(firstKey).PG.keySet().toArray();
                                Arrays.sort(PGKeys);
                                
                                for(int i = 0; i < PGKeys.length; i++){
                                    int key = (int)PGKeys[i];
                                    String PG = LibBBLList.get(BBLIdx).Opt_Glycan.node.get(firstKey).PG.get(key);
                                    if(LibBBLList.get(BBLIdx).Name.equals("NeuAc") && key == 2)
                                        continue;
                                    else if(key == 1)
                                        continue;
                                    if(i != PGKeys.length - 1)
                                        text.append(key + ":" + PG + ",");
                                    else
                                        text.append(key + ":" + PG + "]");
                                }
                                if(n < NodeSolMap.get(ID).get(m).FragList.get(fragIdx).BBLList.size() - 1){
                                    text.append("\n");
                                }
                            }
                            text.append("\n");
                        }
                    }
                    if(NodeSolMap.get(ID).isEmpty()){
                        text.append("--\n");
                    }
                }
                else{
                    text.append("null\n");
                }
            }
            Platform.runLater(()->MainFormController.GetInstance().ShowResultText(text.toString()));
        }
    }
    
    private void PrintNodeSolMapToFile(boolean ToPrint, List<DS_BuildingBlock> LibBBLList){
        if(ToPrint){
            try{
                PrintWriter writer = new PrintWriter("E:\\Auto-CHO_Result.txt", "UTF-8");
                Object[] NodeSolKeys = NodeSolMap.keySet().toArray();
                Arrays.sort(NodeSolKeys);
                
                for(Object obj: NodeSolKeys){
                    int ID = (int)obj;
                    writer.println("[" + ID + "]");
                    if(NodeSolMap.get(ID) != null){
                        for(int m = 0; m < NodeSolMap.get(ID).size(); m++){
                            writer.println("Solution " + (m + 1) + ":");
                            DS_Fragment.DFSOrder(NodeSolMap.get(ID).get(m).FragList);
                            
                            for(int fragIdx = 0; fragIdx < NodeSolMap.get(ID).get(m).FragList.size(); fragIdx++){
                                DS_Fragment fragment = NodeSolMap.get(ID).get(m).FragList.get(fragIdx);
                                writer.print("\tFragment " + (fragIdx + 1) + "[Fragment Yield=");
                                writer.printf("%.2f", fragment.Yield * 100);
                                writer.print("%][");
                                writer.print("RootID: " + fragment.RootID + " | ");
                                writer.print("ParentBBLID: " + fragment.ParentBBLID + " | ");
                                writer.print("ParentFragID: " + fragment.ParentFragID  + " | ");
                                writer.print("CID:");
                                for(int CID: fragment.CIDList){
                                    writer.print(" " + CID);
                                }
                                writer.print("][");
                                writer.print("Deprotect=");
                                for(String PGKey: NodeSolMap.get(ID).get(m).FragList.get(fragIdx).DePGMap.keySet()){
                                    for(DS_PGRecord record: NodeSolMap.get(ID).get(m).FragList.get(fragIdx).DePGMap.get(PGKey)){
                                        writer.print(record.Position + "-");
                                    }
                                    writer.print(PGKey + ",");
                                }
                                writer.print("][");
                                writer.print("HasSatbleProductAnomer=" + (NodeSolMap.get(ID).get(m).FragList.get(fragIdx).HasStableProductAnomer ? "Yes" : "No"));
                                writer.print("]");
                                writer.println();
                                for(int n = 0; n < NodeSolMap.get(ID).get(m).FragList.get(fragIdx).BBLList.size(); n++){
                                    int BBLIdx = NodeSolMap.get(ID).get(m).FragList.get(fragIdx).BBLList.get(n).BBLIdx;
                                    writer.print("\t\t");
                                    writer.print("BBL RRV=");
                                    writer.print(LibBBLList.get(BBLIdx).RRV + " | PG=");
                                    writer.print("[");
                                    int firstKey = LibBBLList.get(BBLIdx).Opt_Glycan.node.firstKey();
                                    writer.print(LibBBLList.get(BBLIdx).Opt_Glycan.node.get(firstKey).PG.get(2) + "\t");
                                    writer.print(LibBBLList.get(BBLIdx).Opt_Glycan.node.get(firstKey).PG.get(3) + "\t");
                                    writer.print(LibBBLList.get(BBLIdx).Opt_Glycan.node.get(firstKey).PG.get(4) + "\t");
                                    writer.print(LibBBLList.get(BBLIdx).Opt_Glycan.node.get(firstKey).PG.get(6) + "]");
                                    if(n < NodeSolMap.get(ID).get(m).FragList.get(fragIdx).BBLList.size() - 1){
                                        writer.println();
                                    }
                                }
                                writer.println();
                            }
                        }
                        if(NodeSolMap.get(ID).isEmpty()){
                            writer.println("--");
                        }
                    }
                    else{
                        writer.println("null");
                    }
                }
                writer.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    private List<Integer> GetReversedOrderRandemBBLIdx(int OrgLibSize, int NewLibSize){
        List<Integer> idxList = new ArrayList<>();
        List<Integer> subList = new ArrayList<>();
        for(int i= 0; i < OrgLibSize; i++){
            idxList.add(i);
        }
        Collections.shuffle(idxList);
        subList = idxList.subList(0, NewLibSize);
        Collections.sort(subList);
        Collections.reverse(subList);
        return subList;
    }
    
    private boolean CheckAllSameTree(DS_SugarStructure SS, List<Integer> CIDList){
        for(int CIDIdx = 1; CIDIdx < CIDList.size(); CIDIdx++){
            int CID1 = CIDList.get(CIDIdx - 1);
            int CID2 = CIDList.get(CIDIdx);

            DS_OptResidue residue1 = SS.TargetGlycan.node.get(CID1);
            DS_OptResidue residue2 = SS.TargetGlycan.node.get(CID2);

            DS_OptGlycan subtree1 = SS.TargetGlycan.GetSubtree(residue1);
            DS_OptGlycan subtree2 = SS.TargetGlycan.GetSubtree(residue2);

            if(!subtree1.IsSameTree(subtree2)){
                return false;
            }
        }
        return true;
    }
}
