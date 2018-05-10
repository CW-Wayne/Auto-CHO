package AutoCHO.entity;
import java.util.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;

public class DS_BuildingBlock extends DS_BuildingBlockBasic {
    public String FullName;
    public String Name;
    public String SMILES;
    public String Source;
    public String Type;
    public int Idx;
    public int DBIdx;
    public Hyperlink DocRef;
    
    // for testing
    public Image image;
    //========
    
    public String getFullName(){
        return this.FullName;
    }
    public String getName(){
        return this.Name;
    }
    public String getSMILES(){
        return this.SMILES;
    }
    public int getIdx(){
        return this.Idx;
    }
    public int getDBIdx(){
        return this.DBIdx;
    }
    public String getSource(){
        return this.Source;
    }
    public String getType(){
        return this.Type;
    }
    public Hyperlink getDocRef(){
        return this.DocRef;
    }
    public Image getImage(){
        return this.image;
    }
    
    public void setFullName(String fullName){
        this.FullName = fullName;
    }
    public void setName(String name){
        this.Name = name;
    }
    public void setSMILES(String SMILES){
        this.SMILES = SMILES;
    }
    public void setIdx(int idx){
        this.Idx = idx;
    }
    public void setDBIdx(int DBIdx){
        this.DBIdx = DBIdx;
    }
    public void setSource(String Source){
        this.Source = Source;
    }
    public void setType(String Type){
        this.Type = Type;
    }
    public void setDocRef(Hyperlink DocRef){
        this.DocRef = DocRef;
    }
    public void setImage(Image image){
        this.image = image;
    }
    
    public DS_BuildingBlock(){
        this.FullName = "";
        this.Name = "";
        this.SMILES = "";
        this.Source = "";
        this.Type = "";
        this.Idx = -1;
        this.DBIdx = -1;
        this.RRV = -1;
        this.Opt_Glycan = new DS_OptGlycan();
        this.DocRef = new Hyperlink();
    }
    
    public DS_BuildingBlock Clone(){
        DS_BuildingBlock BBL = new DS_BuildingBlock();
        BBL.FullName = this.FullName;
        BBL.Name = this.Name;
        BBL.SMILES = this.SMILES;
        BBL.Source = this.Source;
        BBL.Type = this.Type;
        BBL.Idx = this.Idx;
        BBL.DBIdx = this.DBIdx;
        BBL.RRV = this.RRV;
        BBL.Opt_Glycan = this.Opt_Glycan.Copy();
        BBL.DocRef = this.DocRef;
        
        return BBL;
    }
    
    public boolean IsFullyProtected(){
        for(int key: this.Opt_Glycan.node.keySet()){
            for(int pos: this.Opt_Glycan.node.get(key).CID.keySet()){
                if(this.Opt_Glycan.node.get(key).CID.get(pos) == -1){
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean ContainPG(List<String> ToOpenPGList){
        for(String ToOpenPG: ToOpenPGList){
            for(int key: this.Opt_Glycan.node.keySet()){
                for(int pos: this.Opt_Glycan.node.get(key).PG.keySet()){
                    //if(ToOpenPG.equalsIgnoreCase(this.Opt_Glycan.node.get(key).PG.get(pos).Abbreviation)){
                    if(ToOpenPG.equalsIgnoreCase(this.Opt_Glycan.node.get(key).PG.get(pos))){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
