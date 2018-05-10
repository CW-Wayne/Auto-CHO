package AutoCHO.entity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.*;
import javafx.scene.control.Hyperlink;

public class DS_BuildingBlockText {
    public int DBIdx;
    public int idx;
    public double RRV;
    public String acceptorPosition;
    public String productAnomer;
    public String form;
    public String sugarType;
    public String IUPAC;
    public String SMILES;
    public String Source;
    public Hyperlink DocRef;
    public String Provider;
    
    public DS_BuildingBlockText(){
        this.DBIdx = -1;
        this.idx = -1;
        this.RRV = -1;
        this.acceptorPosition = "";
        this.productAnomer = "";
        this.form = "";
        this.sugarType = "";
        this.IUPAC = "";
        this.SMILES = "";
        this.Source = "";
        this.DocRef = new Hyperlink("PubMed");
        this.DocRef.setStyle("-fx-text-fill: red");
        this.DocRef.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    java.awt.Desktop.getDesktop().browse(new URI("http://www.ncbi.nlm.nih.gov/pubmed/"));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(DS_BuildingBlockText.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(DS_BuildingBlockText.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        this.Provider = "Academia Sinica";
    }
    
    public int getDatabaseIndex(){
        return this.DBIdx;
    }
    public int getIndex(){
        return this.idx;
    }
    public double getRRV(){
        return this.RRV;
    }
    public String getAcceptorPosition(){
        return this.acceptorPosition;
    }
    public String getProductAnomer(){
        return this.productAnomer;
    }
    public String getForm(){
        return this.form;
    }
    public String getSugarType(){
        return this.sugarType;
    }
    public String getIUPAC(){
        return this.IUPAC;
    }
    public String getSMILES(){
        return this.SMILES;
    }
    public String getSource(){
        return this.Source;
    }
    public Hyperlink getDocRef(){
        return this.DocRef;
    }
    public String getProvider(){
        return this.Provider;
    }
    
    public void setDatabaseIndex(int value){
        this.DBIdx = value;
    }
    public void setIndex(int value){
        this.idx = value;
    }
    public void setRRV(double value){
        this.RRV = value;
    }
    public void setAcceptorPosition(String value){
        this.acceptorPosition = value;
    }
    public void setProductAnomer(String value){
        this.productAnomer = value;
    }
    public void setForm(String value){
        this.form = value;
    }
    public void setSugarType(String value){
        this.sugarType = value;
    }
    public void setIUPAC(String value){
        this.IUPAC = value;
    }
    public void setSMILES(String value){
        this.SMILES = value;
    }
    public void setSource(String value){
        this.Source = value;
    }
    public void setDocRef(Hyperlink value){
        this.DocRef = value;
    }
    public void setProvider(String value){
        this.Provider = value;
    }
}
