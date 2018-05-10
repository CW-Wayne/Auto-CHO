package AutoCHO.entity;
import java.util.*;
import org.eurocarbdb.application.glycanbuilder.*;

public class DS_OptGlycan {
    public Glycan glycan;
    public TreeMap<Integer, DS_OptResidue> node; //<ID, DS_OptResidue>
    
    public DS_OptGlycan(){
        this.glycan = null;
        this.node = new TreeMap<>();
    }
    
    public DS_OptGlycan(Glycan newGlycan){
        this.glycan = newGlycan;
        this.node = new TreeMap<>();
        this.BuildNode();
    }
    
    public DS_OptGlycan(Glycan newGlycan, DS_OptGlycan oldOptGlycan){
        this.glycan = newGlycan;
        this.node = new TreeMap<>();
        this.BuildNode();
        this.BuildNode(this.BuildMap(oldOptGlycan), oldOptGlycan);
    }
    
    private TreeMap<Integer, Integer> BuildMap(DS_OptGlycan oldOptGlycan){
        TreeMap<Integer, Integer> IDMap=new TreeMap<>(); // <New ID, Old ID>
        Collection<Residue> newResidueCollection = this.glycan.getAllResidues();
        Collection<Residue> oldResidueCollection = oldOptGlycan.glycan.getAllResidues();
        Iterator<Residue> newResIterator=newResidueCollection.iterator();
        Iterator<Residue> oldResIterator=oldResidueCollection.iterator();
        
        while(oldResIterator.hasNext()){
            Residue newResidue=newResIterator.next();
            Residue oldResidue=oldResIterator.next();
            IDMap.put(newResidue.id, oldResidue.id);
        }
        
        return IDMap;
    }
    private void BuildNode(){
        for(Residue r: this.glycan.getAllResidues()){
            if(r.isSaccharide()){
                DS_OptResidue or = new DS_OptResidue();
                or.GBResidue = r;
                or.ID = r.id;
                if(r.getParent() == null){
                    or.PID = 0;
                }
                else if(r.getParent()!=null && r.getParent().getParent() == null){
                    or.PID = 0;
                }
                else{
                    or.PID=r.getParent().id;
                }
                for(Linkage l:r.getChildrenLinkages()){
                    if(l.getChildResidue().isSaccharide()){
                        for(char cCIDPos: l.glycosidicBond().getParentPositions()){
                            int CIDPos = Integer.parseInt(Character.toString(cCIDPos));
                            int CID = l.getChildResidue().id;
                            or.CID.put(CIDPos, CID);
                        }
                    }
                }
                this.node.put(or.ID, or);
            }
        }
    }
    private void BuildNode(TreeMap<Integer, Integer> IDMap, DS_OptGlycan oldOptGlycan){
        for(int newID: this.node.keySet()){
            int oldID=IDMap.get(newID);
            for(int pos: oldOptGlycan.node.get(oldID).CID.keySet()){
                if(!this.node.get(newID).CID.containsKey(pos)){
                    this.node.get(newID).CID.put(pos, oldOptGlycan.node.get(oldID).CID.get(pos));
                }
            }
            
            this.node.get(newID).PG = new TreeMap<>(oldOptGlycan.node.get(oldID).PG);
            this.node.get(newID).DeprotectingCIDLink = new ArrayList<>(oldOptGlycan.node.get(oldID).DeprotectingCIDLink);
            this.node.get(newID).TempCIDLink = new ArrayList<>(oldOptGlycan.node.get(oldID).TempCIDLink);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Add subtree or or or BB">
    //When link == 0 means this DS_OptResidue is a root node
    public void AddSugar(DS_OptResidue or, int PID, int link){
        if (this.node.isEmpty()){
            or.PID = PID;
            or.ID=or.GBResidue.id;
            this.node.put(or.ID, or);
            ControlAddGlycanStructure(or, PID, link);
        }
        else{
            or.PID = PID;
            or.ID=or.GBResidue.id;
            if (this.node.get(or.PID).CID.containsKey(link) && this.node.get(or.PID).CID.get(link) == -1){
                this.node.get(or.PID).CID.put(link, or.PID);
                //this.node.get(or.PID).CID = this.node.get(or.PID).CID.OrderBy(v => v.Key).ToDictionary(e => e.Key, e => e.Value);
                this.node.put(or.ID, or);
                ControlAddGlycanStructure(or, PID, link);
            }
            else{
                or.PID = 0;
                System.out.println("Cannot add the sugar " + or.ID + " into this tree since the parent link is not available.");
            }
        }
    }
    public void AddSugarFrag(DS_OptResidue or, int PID, int link){
        if (node.isEmpty()){
            or.PID = PID;
            or.ID=or.GBResidue.id;
            this.node.put(or.ID, or);
            ControlAddGlycanStructure(or, PID, link);
        }
        else{
            or.PID = PID;
            or.ID=or.GBResidue.id;
            if (this.node.get(or.PID).CID.containsKey(link) && this.node.get(or.PID).CID.get(link) == -1){
                this.node.get(or.PID).CID.put(link, or.ID);
                //this.node.get(or.PID).CID = this.node[or.PID].CID.OrderBy(v => v.Key).ToDictionary(e => e.Key, e => e.Value);
                this.node.put(or.ID, or);
                ControlAddGlycanStructure(or, PID, link);
            }
            else if (!this.node.get(or.PID).CID.containsKey(link)){
                this.node.get(or.PID).CID.put(link, or.ID);
                //this.node[or.PID].CID = this.node[or.PID].CID.OrderBy(v => v.Key).ToDictionary(e => e.Key, e => e.Value);
                this.node.get(or.PID).TempCIDLink.add(link);
                this.node.put(or.ID, or);
                ControlAddGlycanStructure(or, PID, link);
            }
            else{
                or.PID = 0;
                System.out.println("Cannot add the sugar " + or.ID + " into this tree since the parent link is not available.");
            }
        }
    }
    //This is for library parsing
    public void AddBBSugar(DS_OptResidue or, int PID, int link){
        if (node.isEmpty()){
            or.PID = PID;
            or.ID = or.GBResidue.id;
            this.node.put(or.ID, or);
            ControlAddGlycanStructure(or, PID, link);
        }
        else{
            or.PID = PID;
            or.ID=or.GBResidue.id;
            if (this.node.get(or.PID).CID.containsKey(link) && this.node.get(or.PID).CID.get(link) == -1){
                this.node.get(or.PID).CID.put(link, or.ID);
                //this.node[or.PID].CID = this.node[or.PID].CID.OrderBy(v => v.Key).ToDictionary(e => e.Key, e => e.Value);
                this.node.put(or.ID, or);
                ControlAddGlycanStructure(or, PID, link);
            }
            else if (!this.node.get(or.PID).CID.containsKey(link)){
                this.node.get(or.PID).CID.put(link, or.ID);
                //this.node[or.PID].CID = this.node[or.PID].CID.OrderBy(v => v.Key).ToDictionary(e => e.Key, e => e.Value);
                this.node.put(or.ID, or);
                ControlAddGlycanStructure(or, PID, link);
            }
            else{
                or.PID = 0;
                System.out.println("Cannot add the sugar " + or.ID + " into this tree since the parent link is not available.");
            }
        }
    }
    public void AddSubtree(DS_OptGlycan og, int PID, int link){
        if (node.isEmpty()){
            og.node.get(og.GetRootID()).PID = PID;
            this.node = og.node;
            ControlAddGlycanStructure(og, PID, link);
        }
        else{
            if (this.node.get(PID).CID.containsKey(link) && this.node.get(PID).CID.get(link) == -1){
                og.node.get(og.GetRootID()).PID = PID;
                this.node.get(PID).CID.put(link,og.GetRootID());
                //this.node[PID].CID = this.node[PID].CID.OrderBy(v => v.Key).ToDictionary(e => e.Key, e => e.Value);
                for (int key:og.node.keySet()){
                    this.node.put(key, og.node.get(key));
                }
                ControlAddGlycanStructure(og, PID, link);
            }
            else{
                System.out.println("Cannot combine the subtree [Root=" + og.node.firstEntry().getValue().GBResidue.getTypeName() + "(" + og.GetRootID() + ")] with this tree since the the parent link is not available");
            }
        }
    }
    public void AddSubtreeFrag(DS_OptGlycan og, int PID, int link){
        if (node.isEmpty()){
            og.node.get(og.GetRootID()).PID = PID;
            this.node = og.node;
            ControlAddGlycanStructure(og, PID, link);
        }
        else{
            if (this.node.get(PID).CID.containsKey(link) && this.node.get(PID).CID.get(link) == -1){
                og.node.get(og.GetRootID()).PID = PID;
                this.node.get(PID).CID.put(link,og.GetRootID());
                //this.node[PID].CID = this.node[PID].CID.OrderBy(v => v.Key).ToDictionary(e => e.Key, e => e.Value);
                for (int key:og.node.keySet()){
                    this.node.put(key, og.node.get(key));
                }
                ControlAddGlycanStructure(og, PID, link);
            }
            else if (!this.node.get(PID).CID.containsKey(link)){
                og.node.get(og.GetRootID()).PID = PID;
                this.node.get(PID).CID.put(link, og.GetRootID());
                this.node.get(PID).TempCIDLink.add(link);
                //this.node[PID].CID = this.node[PID].CID.OrderBy(v => v.Key).ToDictionary(e => e.Key, e => e.Value);
                for (int key:og.node.keySet()){
                    this.node.put(key, og.node.get(key));
                }
                ControlAddGlycanStructure(og, PID, link);
            }
            else{
                System.out.println("Cannot combine the subtree [Root=" + og.node.firstEntry().getValue().GBResidue.getTypeName() + "(" + og.GetRootID() + ")] with this tree since the the parent link is not available");
            }
        }
    }
    private void ControlAddGlycanStructure(DS_OptResidue or, int PID, int link){
        if(glycan == null){
            glycan = new Glycan(or.GBResidue, false, MassOptions.empty());
        }
        else{
            Collection<Residue> residueList = glycan.getAllResidues();
            for(Residue r:residueList){
                if(r.id == PID){
                    r.addChild(or.GBResidue,Integer.toString(link).charAt(0));
                    break;
                }
            }
        }
    }
    private void ControlAddGlycanStructure(DS_OptGlycan og, int PID, int link){
        ControlAddGlycanStructure(og.node.get(og.GetRootID()), PID, link);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Remove subtree">
    private List<Integer> rmList = new ArrayList<>();
    private TreeMap<Integer, DS_OptResidue> newNode = new TreeMap<>();
    public void Remove(int ID){
        rm(ID);
        //rmList = rmList.Distinct().ToList();
        rmList = new ArrayList<>(new HashSet<>(rmList));

        for (int idx:this.node.keySet()){
            if (!rmList.contains(idx)){
                newNode.put(idx, this.node.get(idx));
            }
        }
        for (int idx:newNode.keySet()){
            TreeMap<Integer, Integer> newCID = new TreeMap<>();
            for (int link:newNode.get(idx).CID.keySet()){
                if (!rmList.contains(newNode.get(idx).CID.get(link))){
                    newCID.put(link, newNode.get(idx).CID.get(link));
                }
                else if (!newNode.get(idx).TempCIDLink.contains(link)){
                    newCID.put(link, -1);
                }
            }

            List<Integer> newTempCIDLink = new ArrayList<>();
            for (int link:newNode.get(idx).TempCIDLink)
            {
                if (!rmList.contains(newNode.get(idx).CID.get(link))){
                    newTempCIDLink.add(link);
                }
            }

            newNode.get(idx).CID.clear();
            newNode.get(idx).TempCIDLink.clear();

            newNode.get(idx).CID = newCID;
            //newNode[idx].CID = newNode[idx].CID.OrderBy(v => v.Key).ToDictionary(e => e.Key, e => e.Value);
            newNode.get(idx).TempCIDLink = newTempCIDLink;
        }

        //this.node = newNode.ToDictionary(entry => entry.Key, entry => entry.Value);
        this.node = (TreeMap<Integer, DS_OptResidue>)newNode.clone();
        newNode.clear();
        rmList.clear();
        //=========
        ControlRemoveGlycanStructure(ID);
        //=========
    }
    private List<Integer> rm(int ID){
        if(this.node.containsKey(ID)){
            rmList.add(ID);
            for (int link:this.node.get(ID).CID.keySet()){
                if(this.node.get(ID).CID.get(link) > 0){
                    rmList.addAll(rm(this.node.get(ID).CID.get(link)));
                }
            }
        }
        return rmList;
    }
    private void ControlRemoveGlycanStructure(int ID){
        Collection<Residue> residueList=glycan.getAllResidues();
        List<Residue> removeList=new ArrayList<>();
        for(Residue r:residueList){
            if(r.id == ID){
                removeList.add(r);
                
                int idx=0;
                while(true){
                    Residue res=removeList.get(idx);
                    for(Linkage l: res.getChildrenLinkages()){
                        removeList.add(l.getChildResidue());
                    }
                    ++idx;
                    if(idx == removeList.size()){
                        break;
                    }
                }

                for(Residue res:removeList){
                    glycan.removeResidue(res);
                }
                break;
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Get root ID">
    public int GetRootID(){
        if(!this.node.isEmpty()){
            //return this.node.firstKey();
            for(int key: this.node.keySet()){
                if(this.node.get(key).PID == 0){
                    return key;
                }
            }
            return -1;
        }
        else{
            return -1;
        }
    }
    
    public String GetRootResName(){
        if(!this.node.isEmpty()){
            //return this.node.firstKey();
            for(int key: this.node.keySet()){
                if(this.node.get(key).PID == 0){
                    return this.node.get(key).GetName();
                }
            }
            return null;
        }
        else{
            return null;
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Get Subtree">
    public DS_OptGlycan GetSubtree(DS_OptResidue or)
    {
        DS_OptGlycan newOptGlycan=new DS_OptGlycan();
        List<Integer> IDList = DFS_ID(or.ID);
        for(int key: this.node.keySet())
        {
            if(IDList.contains(key))
            {
                newOptGlycan.node.put(key, this.node.get(key));
            }
        }
        newOptGlycan.node.get(or.ID).PID = 0;
        return newOptGlycan;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Copy">
    //Copy a new og (ID numbers in the new og are total different with the original og)
    public DS_OptGlycan Copy(){
        Glycan newGlycan = this.glycan.clone();
        DS_OptGlycan og = new DS_OptGlycan(newGlycan, this);
        return og;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Clear the DS_OptGlycan">
    public void Clear(){
        glycan = null;
        node.clear();
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Depth-first search (return string)">
    private String DFSStr = "";
    public String DFS_Str(){
        return DFS_Str(this.GetRootID());
    }
    public String DFS_Str(int ID){
        if (ID != -1){
            DFS_Str_Recursive(ID);
        }
        String returnStr = DFSStr;
        DFSStr = "";
        return returnStr;
    }
    private void DFS_Str_Recursive(int ID){
        DFSStr += this.node.get(ID).GBResidue.getChirality() + this.node.get(ID).GBResidue.getTypeName() + this.node.get(ID).GBResidue.getAnomericState() + "(" + ID + ")-";
        if (!this.node.get(ID).CID.isEmpty() && Collections.max(this.node.get(ID).CID.values()) > 0){
            for (int link:this.node.get(ID).CID.keySet()){
                if (this.node.get(ID).CID.get(link) > 0){
                    DFS_Str_Recursive(this.node.get(ID).CID.get(link));
                }
            }
        }
        else{
            //return;
        }
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Depth-first search (return node ID)">
    private List<Integer> DFSID = new ArrayList<>();
    public List<Integer> DFS_ID(){
        return DFS_ID(this.GetRootID());
    }
    public List<Integer> DFS_ID(int ID){
        if (ID != -1){
            DFS_ID_Recursive(ID);
        }
        List<Integer> returnList = new ArrayList<>(DFSID);
        DFSID.clear();
        return returnList;
    }
    private void DFS_ID_Recursive(int ID){
        DFSID.add(ID);
        if (!this.node.get(ID).CID.isEmpty() && Collections.max(this.node.get(ID).CID.values()) > 0){
            for (int link:this.node.get(ID).CID.keySet()){
                if (this.node.get(ID).CID.get(link) > 0){
                    DFS_ID_Recursive(this.node.get(ID).CID.get(link));
                }
            }
        }
        else{
            //return;
        }
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Depth-first search (return IsStructureOK)">
    boolean IsStructureOK;
    public boolean DFS_CheckStructure(){
        IsStructureOK = true;
        DFS_CheckStructure(this.GetRootID());
        return IsStructureOK;
    }
    public void DFS_CheckStructure(int ID){
        if(ID != -1){
            DFS_CheckStructure_Recursive(ID);
        }
        else{
            IsStructureOK = false;
        }
    }
    private void DFS_CheckStructure_Recursive(int ID){
        if(IsStructureOK == false)
            return;
        char chirality = this.node.get(ID).GBResidue.getChirality();
        char anomer = this.node.get(ID).GBResidue.getAnomericState();
        if(chirality != 'D' && chirality != 'L'){
            IsStructureOK = false;
            return;
        }
        if(anomer != 'a' && anomer != 'b'){
            IsStructureOK = false;
            return;
        }
        if (!this.node.get(ID).CID.isEmpty() && Collections.max(this.node.get(ID).CID.values()) > 0){
            for (int link:this.node.get(ID).CID.keySet()){
                if (this.node.get(ID).CID.get(link) > 0){
                    DFS_CheckStructure_Recursive(this.node.get(ID).CID.get(link));
                }
            }
	}
        else{
            //return;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Is subtree or the same og">
    int RecursiveCheck = 0;
    List<Integer> SubtreeIDList;
    Map<Integer, Integer> SubtreeIDMap;
    public boolean IsSubtree(DS_OptGlycan subtree){
        if (subtree.node.isEmpty()){
            return true;
        }

        int check = -1;
        SubtreeIDList = new ArrayList<>();
        SubtreeIDMap = new HashMap<>();
        for (int ID: this.node.keySet()){
            if (this.node.get(ID).EqualTo(subtree.node.get(subtree.GetRootID()))){
                check = IsSubtreeRecursive(ID, subtree.GetRootID(), subtree);
            }
        }
        RecursiveCheck = 0;
        if (check == subtree.node.size()){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean IsSubtreeFromCurrentID(int currentID, DS_OptGlycan subtree){
        if(subtree.node.isEmpty()){
            return true;
        }
        
        int check = -1;
        SubtreeIDList = new ArrayList<>();
        SubtreeIDMap = new HashMap<>();
        if (this.node.get(currentID).EqualTo(subtree.node.get(subtree.GetRootID()))){
            check = IsSubtreeRecursive(currentID, subtree.GetRootID(), subtree);
        }
        RecursiveCheck = 0;
        if (check >= subtree.node.size()){
            return true;
        }
        else{
            return false;
        }
    }
    public Map<Integer, Integer> FindSubtreeFromCurrentID(int currentID, DS_OptGlycan subtree){
        if(subtree.node.isEmpty()){
            return null;
        }
        
        int check = -1;
        SubtreeIDList = new ArrayList<>();
        SubtreeIDMap = new HashMap<>();
        
        if (this.node.get(currentID).EqualTo(subtree.node.get(subtree.GetRootID()))){
            check = IsSubtreeRecursive(currentID, subtree.GetRootID(), subtree);
        }
        RecursiveCheck = 0;
        if (check >= subtree.node.size()){
            //return SubtreeIDList;
            return SubtreeIDMap;
        }
        else{
            return null;
        }
    }
    private int IsSubtreeRecursive(int ID, int subtreeID, DS_OptGlycan subtree){
        if (this.node.get(ID).EqualTo(subtree.node.get(subtreeID))){
            SubtreeIDList.add(ID);
            SubtreeIDMap.put(ID, subtreeID);
            for (int link:subtree.node.get(subtreeID).CID.keySet()){
                if (subtree.node.get(subtreeID).CID.get(link) > 0 && this.node.get(ID).CID.containsKey(link) && this.node.get(ID).CID.get(link) > 0){
                    IsSubtreeRecursive(this.node.get(ID).CID.get(link), subtree.node.get(subtreeID).CID.get(link), subtree);
                }
            }
            ++RecursiveCheck;
        }
        return RecursiveCheck;
    }
    public boolean IsSameTree(DS_OptGlycan og){
        if (this.node.size() != og.node.size()){
            return false;
        }
        else{
            if (this.IsSubtree(og) && og.IsSubtree(this)){
                return true;
            }
            else{
                return false;
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Is subtree or the same DS_OptGlycan (consider or without free OH position)">
    int RecursiveCheckFrag = 0;
    List<Integer> SubtreeIDListFrag;
    Map<Integer, Integer> SubtreeIDMapFrag;
    public boolean IsSubtreeFrag(DS_OptGlycan subtree){
        if (subtree.node.isEmpty()){
            return true;
        }

        int check = -1;
        SubtreeIDListFrag = new ArrayList<>();
        SubtreeIDMapFrag = new HashMap<>();
        for (int ID: this.node.keySet()){
            if (this.node.get(ID).EqualToBB(subtree.node.get(subtree.GetRootID()))){
                check = IsSubtreeRecursiveFrag(ID, subtree.GetRootID(), subtree);
            }
        }
        RecursiveCheckFrag = 0;
        if (check >= subtree.node.size()){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean IsSubtreeFragFromCurrentID(int currentID, DS_OptGlycan subtree){
        if(subtree.node.isEmpty()){
            return true;
        }
        
        int check = -1;
        SubtreeIDListFrag = new ArrayList<>();
        SubtreeIDMapFrag = new HashMap<>();
        if (this.node.get(currentID).EqualToBB(subtree.node.get(subtree.GetRootID()))){
            check = IsSubtreeRecursiveFrag(currentID, subtree.GetRootID(), subtree);
        }
        RecursiveCheckFrag = 0;
        if (check >= subtree.node.size()){
            return true;
        }
        else{
            return false;
        }
    }
    public Map<Integer, Integer> FindSubtreeFragFromCurrentID(int currentID, DS_OptGlycan subtree){
        if(subtree.node.isEmpty()){
            return null;
        }
        
        int check = -1;
        SubtreeIDListFrag = new ArrayList<>();
        SubtreeIDMapFrag = new HashMap<>();
        if (this.node.get(currentID).EqualToBB(subtree.node.get(subtree.GetRootID()))){
            check = IsSubtreeRecursiveFrag(currentID, subtree.GetRootID(), subtree);
        }
        RecursiveCheckFrag = 0;
        if (check >= subtree.node.size()){
            //return SubtreeIDListFrag;
            return SubtreeIDMapFrag;
        }
        else{
            return null;
        }
    }
    private int IsSubtreeRecursiveFrag(int ID, int subtreeID, DS_OptGlycan subtree){
        if (this.node.get(ID).EqualToBB(subtree.node.get(subtreeID))){
            SubtreeIDListFrag.add(ID);
            SubtreeIDMapFrag.put(ID, subtreeID);
            for (int link: subtree.node.get(subtreeID).CID.keySet()){
                if (subtree.node.get(subtreeID).CID.get(link) > 0 && this.node.get(ID).CID.containsKey(link) && this.node.get(ID).CID.get(link) > 0){
                    IsSubtreeRecursiveFrag(this.node.get(ID).CID.get(link), subtree.node.get(subtreeID).CID.get(link), subtree);
                }
            }
            ++RecursiveCheckFrag;
        }
        return RecursiveCheckFrag;
    }
    public boolean IsSameTreeFrag(DS_OptGlycan tree){
        if (this.node.size() != tree.node.size()){
            return false;
        }
        else{
            if (this.IsSubtreeFrag(tree) && tree.IsSubtreeFrag(this)){
                return true;
            }
            else{
                return false;
            }
        }
    }
    //</editor-fold>
}
