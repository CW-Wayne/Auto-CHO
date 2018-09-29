package AutoCHO.entity;
import java.io.*;
import java.util.*;
import org.eurocarbdb.application.glycanbuilder.*;

public class DS_Library_VBBL {
    public List<DS_BuildingBlock> BBLList;
    public List<DS_BuildingBlockTextVirtual> BBLTextList;
    
    public DS_Library_VBBL() throws Exception{
        this.BBLList = new ArrayList<>();
        this.BBLTextList = new ArrayList<>();
        this.ReadVBBLFromCSV();
    }
    
    //Read virtual building blocks from the text file1 in CSV format
    private void ReadVBBLFromCSV(){
        InputStreamReader fr;
        try{
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("AutoCHO/library/VirLib.csv");
            fr = new InputStreamReader(stream);
            this.BBLList.clear();
            String str = "";
            BufferedReader br = new BufferedReader(fr);
            int DBIdx = 1001;
            while(true){
                str = br.readLine();
                if(str == null){
                    break;
                }
                DS_BuildingBlock BBL = new DS_BuildingBlock();
                DS_BuildingBlockTextVirtual BBLText = new DS_BuildingBlockTextVirtual();

                String[] strarr = str.split(",");
                String chirality = strarr[0];
                String sugar_type = strarr[1];
                String anomeric_state = strarr[2];
                double predicted_RRV = Double.parseDouble(strarr[3]);
                String P2 = strarr[4];
                String P3 = strarr[5];
                String P4 = strarr[6];
                String P6 = strarr[7];
                
                String IUPAC = "";
                Map<Integer, Integer> IDMap = new TreeMap<>();   //<Saccharide ID in XML, Saccharide ID in Data Structure>
                Map<Integer, Integer> CIDMap = new TreeMap<>();  //<Saccharide ID in XML, Parent Saccharide Linkage Position>
                
                String Form = chirality;
                String ID = "1";
                String PID = "0";
                String Anomer = anomeric_state;
                String Type = sugar_type;
                if(Type.equals("Neu5Ac")){
                    Type = "NeuAc";
                }

                DS_OptResidue or = new DS_OptResidue();
                Residue r = new Residue();
                r.setType(ResidueDictionary.getResidueType(Type));
                r.setAnomericCarbon(r.getType().getAnomericCarbon());
                r.setAnomericState(Anomer.charAt(0));
                r.setChirality(r.getType().getChirality());
                r.setRingSize(r.getType().getRingSize());
                or.GBResidue = r;

                if (BBL.Opt_Glycan.node.isEmpty()){
                    BBL.Opt_Glycan.AddBBSugar(or, 0, 0);
                    IDMap.put(Integer.parseInt(ID), BBL.Opt_Glycan.node.lastKey());
                }
                else{
                    or.PID = IDMap.get(Integer.parseInt(PID));
                    BBL.Opt_Glycan.AddBBSugar(or, or.PID, CIDMap.get(Integer.parseInt(ID)));
                    IDMap.put(Integer.parseInt(ID), BBL.Opt_Glycan.node.lastKey());
                }

                if(P2.equals("OH")){
                    or.CID.put(2, -1);
                }
                if(P3.equals("OH")){
                    or.CID.put(3, -1);
                }
                if(P4.equals("OH")){
                    or.CID.put(4, -1);
                }
                if(P6.equals("OH")){
                    or.CID.put(6, -1);
                }
                or.PG.put(2, P2);
                or.PG.put(3, P3);
                or.PG.put(4, P4);
                or.PG.put(6, P6);

                BBL.DBIdx = DBIdx;
                BBL.Idx = DBIdx;
                BBL.RRV = Math.round(predicted_RRV * 1000) / 1000d;
                BBL.FullName = IUPAC;
                BBL.Name = "";
                BBL.Type = "Vir.";
                this.BBLList.add(BBL);

                BBLText.selected.setValue(false);
                BBLText.sugarType = Type;
                if(Anomer.equals("a")){
                    BBLText.productAnomer = "Alpha";
                }
                else{
                    BBLText.productAnomer = "Beta";
                }
                BBLText.R2 = P2;
                BBLText.R3 = P3;
                BBLText.R4 = P4;
                BBLText.R6 = P6;
                BBLText.RRV = Math.round(predicted_RRV * 1000) / 1000d;
                
                this.BBLTextList.add(BBLText);

                ++DBIdx; 
            }
            fr.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

