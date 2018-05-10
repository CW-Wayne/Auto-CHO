package AutoCHO.entity;
import java.util.*;
import org.eurocarbdb.application.glycanbuilder.Residue;

public class DS_OptResidue{
    //private static long CurrentID = 1;
    public int ID;
    public int PID;
    
    //     -1: OH
    // 0 ~  N: Link to other or
    //-2 ~ -N: Different protected groups (has not been used)
    
    //For new algorithm======================
    public TreeMap<Integer, Integer> CID;           //<Position, Child's ID>
    //public TreeMap<Integer, ProtectingGroup> PG;  //<Position, Protecting group>
    public TreeMap<Integer, String> PG;             //<Position, Protecting group>
    //=======================================
    
    //For old algorithm======================
    public List<Integer> TempCIDLink;
    public List<Integer> DeprotectingCIDLink;
    //=======================================
    
    public Residue GBResidue;
    
    public DS_OptResidue(){
        this(0, new TreeMap<Integer, Integer>(), new Residue());
    }
    public DS_OptResidue(TreeMap<Integer, Integer> CID, Residue r){
        this(0, CID, r);
    }
    public DS_OptResidue(int PID, TreeMap<Integer, Integer> CID, Residue r){
        this.ID = 0;
        this.PID = PID;
        this.CID = CID;
        this.PG = new TreeMap<>();
        this.TempCIDLink = new ArrayList<>();
        this.DeprotectingCIDLink = new ArrayList<>();
        this.GBResidue = r;
    }
    
    public boolean EqualTo(DS_OptResidue or){
        boolean check = false;
        boolean checkName = this.GBResidue.getTypeName().equalsIgnoreCase(or.GBResidue.getTypeName());
        boolean checkForm = (this.GBResidue.getChirality() == or.GBResidue.getChirality());
        boolean checkAnomer = (this.GBResidue.getAnomericState() == or.GBResidue.getAnomericState());
        
        if(checkName && checkForm && checkAnomer){
            //Here is different from EqualToFrag function
            if (this.CID.isEmpty() && or.CID.isEmpty()){
                return true;
            }
            else{
                for (int key: this.CID.keySet()){
                    if (this.CID.get(key) > 0 || this.CID.get(key) == -1){
                        if (!or.CID.containsKey(key)){
                            return false;
                        }
                        else{
                            if (or.CID.get(key) > 0 || or.CID.get(key) == -1){
                                check = true;
                            }
                            else{
                                return false;
                            }
                        }
                    }
                }
                for (int key: or.CID.keySet()){
                    if (or.CID.get(key) > 0 || or.CID.get(key) == -1){
                        if (!this.CID.containsKey(key)){
                            return false;
                        }
                    }
                }
            }
            return check;
        }
        else{
            return false;
        }
    }
    public boolean EqualToFrag(DS_OptResidue or){
        boolean check = false;
        boolean checkName = this.GBResidue.getTypeName().equalsIgnoreCase(or.GBResidue.getTypeName());
        boolean checkForm = (this.GBResidue.getChirality() == or.GBResidue.getChirality());
        boolean checkAnomer = (this.GBResidue.getAnomericState() == or.GBResidue.getAnomericState());
        
        if(checkName && checkForm && checkAnomer){
            //Here is different from EqualTo function
            if (this.CID.isEmpty() || or.CID.isEmpty()){
                return true;
            }
            else{
                for (int key: this.CID.keySet()){
                    if (this.CID.get(key) > 0 || this.CID.get(key) == -1){
                        if (!or.CID.containsKey(key)){
                            return false;
                        }
                        else{
                            if (or.CID.get(key) > 0 || or.CID.get(key) == -1){
                                check = true;
                            }
                            else{
                                return false;
                            }
                        }
                    }
                }
                for (int key: or.CID.keySet()){
                    if (or.CID.get(key) > 0 || or.CID.get(key) == -1){
                        if (!this.CID.containsKey(key)){
                            return false;
                        }
                    }
                }
            }
            return check;
        }
        else{
            return false;
        }
    }
    public boolean EqualToBB(DS_OptResidue bbor){
        boolean check = false;
        boolean checkName = this.GBResidue.getTypeName().equalsIgnoreCase(bbor.GBResidue.getTypeName());
        boolean checkForm = (this.GBResidue.getChirality() == bbor.GBResidue.getChirality());
        boolean checkAnomer = (this.GBResidue.getAnomericState() == bbor.GBResidue.getAnomericState());
        
        if(checkName && checkForm && checkAnomer){
            if (bbor.CID.isEmpty()){
                return true;
            }
            else if(this.CID.isEmpty()){
                return false;
            }
            else{
                for (int key: bbor.CID.keySet()){
                    if (bbor.CID.get(key) > 0 || bbor.CID.get(key) == -1){
                        if(!this.CID.containsKey(key)){
                            return false;
                        }
                        else{
                            if(this.CID.get(key) > 0 || this.CID.get(key) == -1){
                                check = true;
                            }
                            else{
                                return false;
                            }
                        }
                    }
                }
            }
            return check;
        }
        else{
            return false;
        }
    }

    public DS_OptResidue Copy(){
        DS_OptResidue or = new DS_OptResidue();
        or.GBResidue = this.GBResidue.cloneResidue();
        or.ID = or.GBResidue.id;
        or.PID = 0;
        or.PG = new TreeMap<>(this.PG);
        
        return or;
    }
    
    public String GetName(){
        return this.GBResidue.getTypeName();
    }
}
