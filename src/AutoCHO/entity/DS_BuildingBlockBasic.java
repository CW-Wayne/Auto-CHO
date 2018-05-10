package AutoCHO.entity;

public class DS_BuildingBlockBasic {
    public double RRV;
    public DS_OptGlycan Opt_Glycan;
    
    public double getRRV(){
        return this.RRV;
    }
    public void setRRV(double RRV){
        this.RRV = RRV;
    }
    public DS_OptGlycan getST(){
        return this.Opt_Glycan;
    }
    public void setST(DS_OptGlycan ST){
        this.Opt_Glycan = ST;
    }
}
