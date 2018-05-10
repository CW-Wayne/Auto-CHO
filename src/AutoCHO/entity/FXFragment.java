package AutoCHO.entity;
import javafx.scene.image.*;

public class FXFragment {
    public Image FragmentImage;
    public String RRV;
    public String Yield;
    public String Deprotection;
    public String StableProductAnomer;
    
    public FXFragment(){
        this.FragmentImage = null;
        this.RRV = "0.00";
        this.Yield = "0.00";
        this.Deprotection = "";
        this.StableProductAnomer = "";
    }
    
    public Image getFragmentImage(){
        return this.FragmentImage;
    }
    public void setFragmentImage(Image FragmentImage){
        this.FragmentImage = FragmentImage;
    }
    public String getRRV(){
        return this.RRV;
    }
    public void setRRV(String RRV){
        this.RRV = RRV;
    }
    public String getYield(){
        return this.Yield;
    }
    public void setYield(String Yield){
        this.Yield = Yield;
    }
    public String getDeprotection(){
        return this.Deprotection;
    }
    public void setDeprotection(String Deprotection){
        this.Deprotection = Deprotection;
    }
    public String getStableProductAnomer(){
        return this.StableProductAnomer;
    }
    public void setStableProductAnomer(String StableProductAnomer){
        this.StableProductAnomer = StableProductAnomer;
    }
    
}
