package AutoCHO.entity;
import javafx.beans.property.*;

public class DS_BuildingBlockTextVirtual {
    
    public BooleanProperty selected;
    public String sugarType;
    public String productAnomer;
    public String R2;
    public String R3;
    public String R4;
    public String R6;
    public double RRV;
    
    
    public DS_BuildingBlockTextVirtual(){
        this.selected = new SimpleBooleanProperty(false);
        this.sugarType = "";
        this.productAnomer = "";
        this.R2 = "";
        this.R3 = "";
        this.R4 = "";
        this.R6 = "";
        this.RRV = -1;
    }
    public BooleanProperty selectedProperty(){
        return this.selected;
    }
    public String getSugarType(){
        return this.sugarType;
    }
    public String getProductAnomer(){
        return this.productAnomer;
    }
    public String getR2(){
        return this.R2;
    }
    public String getR3(){
        return this.R3;
    }
    public String getR4(){
        return this.R4;
    }
    public String getR6(){
        return this.R6;
    }
    public double getRRV(){
        return this.RRV;
    }
    
//    public void setSelected(BooleanProperty value){
//       this.selected = value;
//    }
    public void setSugarType(String value){
        this.sugarType = value;
    }
    public void setProductAnomer(String value){
        this.productAnomer = value;
    }
    public void setR2(String value){
        this.R2 = value;
    }
    public void setR3(String value){
        this.R3 = value;
    }
    public void setR4(String value){
        this.R4 = value;
    }
    public void setR6(String value){
        this.R6 = value;
    }
    public void setRRV(double value){
        this.RRV = value;
    }
    
}
