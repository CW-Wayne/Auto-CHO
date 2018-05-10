package AutoCHO.entity;
import javafx.scene.image.Image;

public class FXNode {
    public String ResidueID;
    public Image ReducingEndImage;
    
    public FXNode(){
        this.ResidueID = "";
        this.ReducingEndImage = null;
    }
    
    public String getResidueID(){
        return this.ResidueID;
    }
    public void setResidueID(String ResidueID){
        this.ResidueID = ResidueID;
    }
    public Image getReducingEndImage(){
        return this.ReducingEndImage;
    }
    public void setReducingEndImage(Image image){
        this.ReducingEndImage = image;
    }
}
