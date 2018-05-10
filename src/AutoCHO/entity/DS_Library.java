package AutoCHO.entity;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eurocarbdb.application.glycanbuilder.*;
import org.w3c.dom.*;

public class DS_Library {
    public int MaxSaccharideNumInBB;
    public List<DS_BuildingBlock> BBLList;
    public List<DS_BuildingBlockText> BBTList;
    
    public DS_Library() throws Exception{
        this.MaxSaccharideNumInBB = 0;
        this.BBLList = new ArrayList<>();
        this.BBTList = new ArrayList<>();
        this.ReadDataFromXML();
    }
    
    private void ReadDataFromXML(){
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("AutoCHO/library/ExpLib.xml");
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("BuildingBlock");
            int DBIdx = 1;
            
            for (int BBIdx = 0; BBIdx < nodeList.getLength(); BBIdx++) {
                org.w3c.dom.Node node = nodeList.item(BBIdx);
                
                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    DS_BuildingBlock BBL = new DS_BuildingBlock();
                    DS_BuildingBlockText BBT = new DS_BuildingBlockText();
                    
                    NodeList IUPACNmElmntLst = element.getElementsByTagName("IUPAC");
                    Element IUPACNmElmnt = (Element) IUPACNmElmntLst.item(0);
                    NodeList IUPACNm = IUPACNmElmnt.getChildNodes();
                    String IUPAC = ((org.w3c.dom.Node) IUPACNm.item(0)).getNodeValue();
                    
                    NodeList IndexNmElmntLst = element.getElementsByTagName("Index");
                    Element IndexNmElmnt = (Element) IndexNmElmntLst.item(0);
                    NodeList IndexNm = IndexNmElmnt.getChildNodes();
                    String Index = ((org.w3c.dom.Node) IndexNm.item(0)).getNodeValue();
                    
                    NodeList SourceNmElmntLst = element.getElementsByTagName("Source");
                    Element SourceNmElmnt = (Element) SourceNmElmntLst.item(0);
                    NodeList SourceNm = SourceNmElmnt.getChildNodes();
                    String Source = ((org.w3c.dom.Node) SourceNm.item(0)).getNodeValue();
                    
                    NodeList RRVNmElmntLst = element.getElementsByTagName("RRV");
                    Element RRVNmElmnt = (Element) RRVNmElmntLst.item(0);
                    NodeList RRVNm = RRVNmElmnt.getChildNodes();
                    String RRV = ((org.w3c.dom.Node) RRVNm.item(0)).getNodeValue();
                    
                    NodeList AnomerNmElmntLst = element.getElementsByTagName("ProductAnomer");
                    Element AnomerNmElmnt = (Element) AnomerNmElmntLst.item(0);
                    NodeList AnomerNm = AnomerNmElmnt.getChildNodes();
                    String ProductAnomer = ((org.w3c.dom.Node) AnomerNm.item(0)).getNodeValue();
                    
                    NodeList SaccharideNmElmntLst = element.getElementsByTagName("Saccharide");
                    String tForm = "";
                    String tType = "";
                    String tAcceptorPosition = "";
                    Map<Integer, Integer> IDMap = new TreeMap<>();   //<Saccharide ID in XML, Saccharide ID in Data Structure>
                    Map<Integer, Integer> CIDMap = new TreeMap<>();  //<Saccharide ID in XML, Parent Saccharide Linkage Position>
                    
                    for(int SaccharideIdx = 0; SaccharideIdx < SaccharideNmElmntLst.getLength(); SaccharideIdx++){
                        Element SaccharideNmElmnt = (Element) SaccharideNmElmntLst.item(SaccharideIdx);
                        String Form = SaccharideNmElmnt.getAttribute("Form");
                        String ID = SaccharideNmElmnt.getAttribute("ID");
                        String PID = SaccharideNmElmnt.getAttribute("PID");
                        String Anomer = SaccharideNmElmnt.getAttribute("Anomer");
                        String Type = SaccharideNmElmnt.getAttribute("Type");
                        if(Type.equals("Neu5Ac")){
                            Type = "NeuAc";
                        }
                        
                        tForm += Form;
                        tType += Type;
                        
                        DS_OptResidue or = new DS_OptResidue();
                        Residue r = new Residue();
                        r.setType(ResidueDictionary.getResidueType(Type));
                        r.setAnomericCarbon(r.getType().getAnomericCarbon());
                        //System.out.println(Anomer.charAt(0));
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
                        NodeList SaccharideNmList = SaccharideNmElmnt.getElementsByTagName("HydroxylGroup");
                        int OH_count = 0;
                        for(int GroupIdx = 0; GroupIdx < SaccharideNmList.getLength(); GroupIdx++){
                            Element HPelement = (Element) SaccharideNmList.item(GroupIdx);
                            String Value = HPelement.getAttribute("Value");
                            if(Value.equals("OH")){
                                ++OH_count;
                            }
                        }
                        int counter = 0;
                        boolean HasAcceptorPosition = false;
                        for(int GroupIdx = 0; GroupIdx < SaccharideNmList.getLength(); GroupIdx++){
                            Element HPelement = (Element) SaccharideNmList.item(GroupIdx);
                            String Position = HPelement.getAttribute("Position");
                            String Value = HPelement.getAttribute("Value");
                            if(Value.equals("OH")){
                                if(SaccharideIdx > 0 && counter == 0){
                                    tAcceptorPosition += "_";
                                }
                                tAcceptorPosition += Position;
                                if(counter < OH_count && counter != OH_count - 1){
                                    tAcceptorPosition += ",";
                                    ++counter;
                                }
                                or.CID.put(Integer.parseInt(Position), -1);
                                HasAcceptorPosition = true;
                            }
                            else if(Value.matches("[0-9]+")){
                                CIDMap.put(Integer.parseInt(Value), Integer.parseInt(Position));
                            }
                            or.PG.put(Integer.parseInt(Position), Value);
                        }
                        if(SaccharideIdx != SaccharideNmElmntLst.getLength() - 1){
                            tForm += "_";
                            tType += "_";
                        }
                        if(HasAcceptorPosition == false){
                            if(SaccharideIdx == 0)
                                tAcceptorPosition += "None";
                            else
                                tAcceptorPosition += "_None";
                        } 
                    }
                    
                    BBL.DBIdx = DBIdx;
                    BBL.Idx = Integer.parseInt(Index);
                    BBL.RRV = Double.parseDouble(RRV);
                    BBL.FullName = IUPAC;
                    BBL.Source = Source;
                    BBL.Type = "Exp.";
                    BBL.Name = this.ReverseString(tType);
                    this.BBLList.add(BBL);
                    
                    BBT.DBIdx = DBIdx;
                    BBT.idx = Integer.parseInt(Index);
                    BBT.RRV = Double.parseDouble(RRV);
                    BBT.IUPAC = IUPAC;
                    BBT.Source = Source;
                    BBT.acceptorPosition = this.CheckAcceptorPosition(this.ReverseString(tAcceptorPosition));
                    BBT.productAnomer = ProductAnomer;
                    BBT.form = this.ReverseString(tForm);
                    BBT.sugarType = this.ReverseString(tType);
                    //BBT.SMILES = str.split("\t")[8];
                    this.BBTList.add(BBT);
                    
                    ++DBIdx;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String ReverseString(String s){
        if(s.equals(""))
            s = "None";
        String[] array = s.split("_");
        if(array.length > 1){
            s = "";
            for(int i = array.length - 1; i >= 0; i--){
                s += array[i];
                if(i != 0)
                    s += "_";
            }
        }
        return s;
    }
    
    private String CheckAcceptorPosition(String s){
        String[] array = s.split("_");
        boolean HasAcceptorPosition = false;
        for(int i = 0; i < array.length; i++){
            if(!array[i].equals("None")){
                HasAcceptorPosition = true;
                break;
            }
        }
        if(HasAcceptorPosition == true)
            return s;
        else
            return "Full Protection";
    }
}

