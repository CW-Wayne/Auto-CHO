package AutoCHO.entity;

public class FXSolution {
    public String Solution;
    public String NumOfFrag;
    public String AvgFragYield;
    
    public FXSolution(){
        this.Solution = "";
        this.NumOfFrag = "";
        this.AvgFragYield = "";
    }
    
    public String getSolution(){
        return this.Solution;
    }
    public void setSolution(String Solution){
        this.Solution = Solution;
    } 
    public String getNumOfFrag(){
        return this.NumOfFrag;
    }
    public void setNumOfFrag(String NumOfFrag){
        this.NumOfFrag = NumOfFrag;
    }
    public String getAvgFragYield(){
        return this.AvgFragYield;
    }
    public void setAvgFragYield(String AvgFragYield){
        this.AvgFragYield = AvgFragYield;
    }
}
