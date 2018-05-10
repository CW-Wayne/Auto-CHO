package AutoCHO.entity;
import javafx.scene.image.*;

public class FXBuildingBlock {
    public Image BuildingBlock;
    public Double RRV;
    public String Type;
    public String Deprotection;
    
    public FXBuildingBlock(){
        this.BuildingBlock = null;
        this.RRV = 0.0;
        this.Type = "";
        this.Deprotection = "";
    }
    
    public Image getBuildingBlock(){
        return this.BuildingBlock;
    }
    public void setBuildingBlock(Image BuildingBlock){
        this.BuildingBlock = BuildingBlock;
    }
    public Double getRRV(){
        return this.RRV;
    }
    public void setRRV(Double RRV){
        this.RRV = RRV;
    }
    public String getType(){
        return this.Type;
    }
    public void setType(String Type){
        this.Type = Type;
    }
    public String getDeprotection(){
        return this.Deprotection;
    }
    public void setDeprotection(String Deprotection){
        this.Deprotection = Deprotection;
    }
}
