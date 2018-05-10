package AutoCHO.entity;
import java.io.*;
import java.util.*;

public class DS_ProtectingGroup {
    public String Name;
    public String FullName;
    public double MolecularWeight;
    public double Volume;
    public double Ovality;
    //C,H,O,N,S,Cl,Si,F
    public List<Integer> ElementCount;
    
    public DS_ProtectingGroup(){
        this.Name = "";
        this.FullName = "";
        this.MolecularWeight = 0.0;
        this.Volume = 0.0;
        this.Ovality = 0.0;
        this.ElementCount = new ArrayList<>();
    }
    
    public DS_ProtectingGroup(String name, double volume){
        this.Name = name;
        this.FullName = "";
        this.MolecularWeight = 0.0;
        this.Volume = 0.0;
        this.Ovality = 0.0;
        this.ElementCount = new ArrayList<>();
    }
    
    private static Map<String, DS_ProtectingGroup> PGMap;
    private static List<String> NameList;
    private static List<String> AllowedPGList;
    
    public static Map<String, DS_ProtectingGroup> GetPGMap() throws IOException{
        PGMap = new HashMap<>();
        final String FilePath = "ProtectingGroup_20161110.csv";
        FileReader fr;
        try{
            fr = new FileReader(FilePath);
            String str = "";
            BufferedReader br = new BufferedReader(fr);
            while(true){
                str = br.readLine();
                if(str == null){
                    break;
                }
                String[] strArray = str.split(",");
                DS_ProtectingGroup PG = new DS_ProtectingGroup();
                PG.Name = strArray[0];
                PG.MolecularWeight = Double.parseDouble(strArray[9]);
                PG.Volume = Double.parseDouble(strArray[10]);
                PG.Ovality = Double.parseDouble(strArray[11]);
                
                PG.ElementCount.add(Integer.parseInt(strArray[1]));
                PG.ElementCount.add(Integer.parseInt(strArray[2]));
                PG.ElementCount.add(Integer.parseInt(strArray[3]));
                PG.ElementCount.add(Integer.parseInt(strArray[4]));
                PG.ElementCount.add(Integer.parseInt(strArray[5]));
                PG.ElementCount.add(Integer.parseInt(strArray[6]));
                PG.ElementCount.add(Integer.parseInt(strArray[7]));
                PG.ElementCount.add(Integer.parseInt(strArray[8]));
                
                PGMap.put(PG.Name, PG);
            }
            fr.close();
            return PGMap;
        }
        catch(FileNotFoundException e){
            return null;
        }
    }
    public static List<String> GetAllowedPGList(){
        if(AllowedPGList == null)
        {
            AllowedPGList = new ArrayList<>();
            AllowedPGList.add("OH");
            AllowedPGList.add("OAc");
            AllowedPGList.add("OPh");
            //AllowedPGList.add("OBn");
            AllowedPGList.add("OBz");
            AllowedPGList.add("OClAc");
            //AllowedPGList.add("ClBn");
            AllowedPGList.add("OLev");
            AllowedPGList.add("NBz");
            AllowedPGList.add("NO2Bz");
            AllowedPGList.add("OPMB");
            AllowedPGList.add("OTBDPS");
            AllowedPGList.add("OTBS");
            AllowedPGList.add("OTIPS");
            AllowedPGList.add("NPhth");
        }
        return AllowedPGList;
    }
}
