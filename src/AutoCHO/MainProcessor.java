package AutoCHO;
import AutoCHO.entity.DS_SyntheticTarget;
import AutoCHO.entity.DS_BuildingBlock;
import AutoCHO.entity.OptGlycanBuilder;
import AutoCHO.entity.DS_Library_VBBL;
import AutoCHO.entity.DS_Library;
import com.symyx.draw.ImageGenerator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.eurocarbdb.application.glycanbuilder.*;

public class MainProcessor {
    private static MainProcessor mainProcessor;
    public OptGlycanBuilder GBF;
    
    public int LibMode;
    
    public DS_Library Lib;
    public DS_Library_VBBL Lib_VBBL;
    public List<DS_BuildingBlock> CombinedBBLList;
    public DS_SyntheticTarget TargetGlycan;
    
    public boolean HasMainFormBeenRun;
    public List<Integer> CurrentIDList;
    public boolean IsFragSearchMode;
    public double MinFragYield;
    public double MinOverallYield;
    public int MaxFragNum;
    public int MinBBLNumOfEachFrag;
    public int MaxBBLNumOfEachFrag;
    public List<Integer> MinBBLNumInFrag;
    public List<Integer> MaxBBLNumInFrag;
    
    public double MinDonorAcceptorRRVRatio;
    public double MaxDonorAcceptorRRVRatio;
    public double MinDonorAcceptorRRVDiff;
    public int SugarNumberOfReducingEndBB;
    public boolean ToConsiderNonSTol;
    
    public double RRV_THR_High;
    public double RRV_THR_Medium;
    
    public int RandomBBLNum;
    
    public boolean IsTestMode;
    public boolean PrintToFile;
    
    public static MainProcessor GetInstance(){
        if(mainProcessor == null){
            mainProcessor = new MainProcessor();
        }
        return mainProcessor;
    }
    public MainProcessor(){
        try{
            this.GBF = new OptGlycanBuilder();
            this.InitializeSearchParameters();
        }
        catch(Exception e){
            Logger.getLogger(MainProcessor.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    private void InitializeSearchParameters(){
        this.LibMode = 0; //0: ExpLib, 1: ExpLib+VirLib
        
        this.HasMainFormBeenRun = false;
        this.CurrentIDList = new ArrayList<>();
        this.IsFragSearchMode = true;
        this.MinFragYield = 0.30;
        this.MinOverallYield = 0.30;
        this.MaxFragNum = 10;
        this.MinBBLNumOfEachFrag = 1;
        this.MaxBBLNumOfEachFrag = 3;
        
        this.MinBBLNumInFrag = new ArrayList<>();
        this.MaxBBLNumInFrag = new ArrayList<>();
        
        this.MinDonorAcceptorRRVRatio = 1.0;
        this.MaxDonorAcceptorRRVRatio = 200.0;
        this.MinDonorAcceptorRRVDiff = 100.0;
        
        this.SugarNumberOfReducingEndBB = 1;
        this.ToConsiderNonSTol = false;
        
        this.RRV_THR_High = 4000;
        this.RRV_THR_Medium = 600;
        
        this.RandomBBLNum = 1500;
        
        this.IsTestMode = false;
        this.PrintToFile = true;
        
        //Initialize Fragment Size
        //MinBBLNumInFrag.add(1);
        //MinBBLNumInFrag.add(1);
        //MinBBLNumInFrag.add(1);
        //MaxBBLNumInFrag.add(3);
        //MaxBBLNumInFrag.add(3);
        //MaxBBLNumInFrag.add(3);
    }
    
    //<editor-fold defaultstate="collapsed" desc="GlycanBuilder Editor">
    public Image StartGlycanBuilder() throws Exception{
        GBF = new OptGlycanBuilder();
        GBF.setTitle("GlycanBuilder");
        GBF.setAlwaysOnTop(true);
        
        return InitializeTargetStructure();
    }
    private Image InitializeTargetStructure() throws Exception{
        //Globo-H
        String synthesisTarget = "RES\n1b:b-dglc-HEX-1:5\n2b:b-dgal-HEX-1:5\n3b:a-dgal-HEX-1:5\n4b:b-dgal-HEX-1:5\n5s:n-acetyl\n6b:b-dgal-HEX-1:5\n7b:a-lgal-HEX-1:5|6:d\nLIN\n1:1o(4+1)2d\n2:2o(4+1)3d\n3:3o(3+1)4d\n4:4d(2+1)5n\n5:4o(3+1)6d\n6:6o(2+1)7d\n";
        
        GlycoCTCondensedParser parser = new GlycoCTCondensedParser(false);
        MassOptions options = MassOptions.empty();
        Glycan glycan = parser.readGlycan(synthesisTarget, options);
        List<Glycan> gList = new ArrayList<>();
        gList.add(glycan);
        
        BuilderWorkspace ws = new BuilderWorkspace(new GlycanRendererAWT());
        GlycanDocument gd = new GlycanDocument(ws);
        gd.addStructures(gList);
        
        GBF.getCanvas().getViewMenu().getItem(2).setSelected(true);
        GBF.getCanvas().setNotation("cfg"); //See GraphicOptions.java
        //GBF.getCanvas().getNotation("text");
        GBF.getCanvas().setDocument(gd);
        
        return DrawTargetGlycan();
    }
    
    public Image DrawGloboH() throws Exception{
        //Globo-H
        String synthesisTarget = "RES\n1b:b-dglc-HEX-1:5\n2b:b-dgal-HEX-1:5\n3b:a-dgal-HEX-1:5\n4b:b-dgal-HEX-1:5\n5s:n-acetyl\n6b:b-dgal-HEX-1:5\n7b:a-lgal-HEX-1:5|6:d\nLIN\n1:1o(4+1)2d\n2:2o(4+1)3d\n3:3o(3+1)4d\n4:4d(2+1)5n\n5:4o(3+1)6d\n6:6o(2+1)7d\n";
        return DrawTargetGlycan(synthesisTarget);
    }
    
    public Image DrawGloboB() throws Exception{
        //Globo-B
        String synthesisTarget = "RES\n1b:b-dglc-HEX-1:5\n2b:b-dgal-HEX-1:5\n3b:a-dgal-HEX-1:5\n4b:b-dgal-HEX-1:5\n5s:n-acetyl\n6b:b-dgal-HEX-1:5\n7b:a-lgal-HEX-1:5|6:d\n8b:a-dgal-HEX-1:5\nLIN\n1:1o(4+1)2d\n2:2o(4+1)3d\n3:3o(3+1)4d\n4:4d(2+1)5n\n5:4o(3+1)6d\n6:6o(2+1)7d\n7:6o(3+1)8d\n";
        return DrawTargetGlycan(synthesisTarget);
    }
    
    public Image DrawSSEA4() throws Exception{
        //SSEA-4
        String synthesisTarget = "RES\n1b:b-dglc-HEX-1:5\n2b:b-dgal-HEX-1:5\n3b:a-dgal-HEX-1:5\n4b:b-dgal-HEX-1:5\n5s:n-acetyl\n6b:b-dgal-HEX-1:5\n7b:a-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n8s:n-acetyl\nLIN\n1:1o(4+1)2d\n2:2o(4+1)3d\n3:3o(3+1)4d\n4:4d(2+1)5n\n5:4o(3+1)6d\n6:6o(3+2)7d\n7:7d(5+1)8n\n";
        return DrawTargetGlycan(synthesisTarget);
    }
    
    public Image DrawOligoLacNAc() throws Exception{
        //OligoLacNAc
        String synthesisTarget = "RES\n1b:a-dman-HEX-1:5\n2b:b-dglc-HEX-1:5\n3s:n-acetyl\n4b:b-dgal-HEX-1:5\n5b:b-dglc-HEX-1:5\n6s:n-acetyl\n7b:b-dgal-HEX-1:5\n8b:b-dglc-HEX-1:5\n9s:n-acetyl\nLIN\n1:1o(2+1)2d\n2:2d(2+1)3n\n3:2o(4+1)4d\n4:4o(3+1)5d\n5:5d(2+1)6n\n6:5o(4+1)7d\n7:7o(3+1)8d\n8:8d(2+1)9n\n";
        return DrawTargetGlycan(synthesisTarget);
    }
    
    public Image DrawHeparinPetaseccharide() throws Exception{
        //Heparin Petaseccharide
        String synthesisTarget = "RES\n1b:a-dglc-HEX-1:5\n2s:amino\n3b:a-lido-HEX-1:5|6:a\n4b:a-dglc-HEX-1:5\n5s:amino\n6b:b-dglc-HEX-1:5|6:a\n7b:a-dglc-HEX-1:5\n8s:amino\nLIN\n1:1d(2+1)2n\n2:1o(4+1)3d\n3:3o(4+1)4d\n4:4d(2+1)5n\n5:4o(4+1)6d\n6:6o(4+1)7d\n7:7d(2+1)8n\n";
        return DrawTargetGlycan(synthesisTarget);
    }
    
    public Image DrawTargetGlycan(String synthesisTarget) throws Exception{
        GlycoCTCondensedParser parser = new GlycoCTCondensedParser(false);
        MassOptions options = MassOptions.empty();
        Glycan glycan = parser.readGlycan(synthesisTarget, options);
        List<Glycan> gList = new ArrayList<>();
        gList.add(glycan);
        
        BuilderWorkspace ws = new BuilderWorkspace(new GlycanRendererAWT());
        GlycanDocument gd = new GlycanDocument(ws);
        gd.addStructures(gList);
        
        //GBF.getCanvas().getViewMenu().getItem(2).setSelected(true);
        //GBF.getCanvas().setNotation("cfg"); //See GraphicOptions.java
        //GBF.getCanvas().getNotation("text");
        GBF.getCanvas().setDocument(gd);
        
        return DrawTargetGlycan();
    }
    
    public Image DrawTargetGlycan(){
        List<Glycan> GlycanList = GBF.getCanvas().getDocument().getStructures();
        BufferedImage bi = GBF.getCanvas().getGlycanRenderer().getImage(GlycanList, false, false, true);
        Image image = SwingFXUtils.toFXImage(bi, null);
        //ImageIcon icon = new ImageIcon();
        //icon.setImage(bi);
        //JOptionPane.showMessageDialog(null, icon);
        return image;
    }
    
    public Image DrawFragmentStructure(Glycan glycan){
        List<Glycan> gList = new ArrayList<>();
        Fragmenter frag = new Fragmenter();
        Glycan newGlycan = glycan.clone();
        newGlycan.getRoot().addChild(ResidueDictionary.createZCleavage(), '3');
        Glycan g = new Glycan();
        //g.setRoot(ResidueDictionary.createReducingEnd());
        g.setRoot(ResidueDictionary.createBCleavage());
        g.getRoot().addChild(newGlycan.getRoot());
        
        //Glycan structure=GBF.getCanvas().getDocument().getFirstStructure();
        //Glycan B_ion=frag.getBFragment(structure, structure.getRoot().firstSaccharideChild().firstSaccharideChild());
        //Glycan Z_ion=frag.getZFragment(B_ion, B_ion.getRoot().firstSaccharideChild().firstSaccharideChild().firstSaccharideChild().firstSaccharideChild());
        
        gList.add(g);
        BufferedImage bi = GBF.getCanvas().getGlycanRenderer().getImage(gList, false, false, true);
        Image image = SwingFXUtils.toFXImage(bi,null);
        
        return image;
    }
    public void EnableGlycanBuilder(){
        GBF.setVisible(true);
    }
    //</editor-fold>
    public Image DrawChemicalStructure(int index) throws MalformedURLException{
        ImageGenerator imageGenerator = new ImageGenerator();
        //Experimental BBL Library
        String path = "mol/" + index + ".mol";
        URL url = this.getClass().getResource(path);
        
        imageGenerator.setMolecule(url);
        imageGenerator.preferences().setAbsStereoLabelText("");
        imageGenerator.preferences().setAndStereoLabelText("");
        imageGenerator.preferences().setOrStereoLabelText("");
        imageGenerator.preferences().setStereoGroupColorDisplay(3);
        
        BufferedImage bufferedImage = new BufferedImage(450, 225, BufferedImage.TYPE_INT_RGB);
        imageGenerator.paint(bufferedImage);
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        
        return image;
    }
    public Image DrawVirtualBBLChemicalStructure(String name) throws MalformedURLException{
        ImageGenerator imageGenerator = new ImageGenerator();
        //Virtual BBL Library
        String path = "" + name + ".mol";
        
        URL url = new File(path).toURI().toURL();
        imageGenerator.setMolecule(url);
        imageGenerator.preferences().setAbsStereoLabelText("");
        imageGenerator.preferences().setAndStereoLabelText("");
        imageGenerator.preferences().setOrStereoLabelText("");
        imageGenerator.preferences().setStereoGroupColorDisplay(3);
        
        BufferedImage bufferedImage = new BufferedImage(450, 225, BufferedImage.TYPE_INT_RGB);
        imageGenerator.paint(bufferedImage);
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        
        return image;
    }
    //<editor-fold defaultstate="collapsed" desc="DS_Library Functions">
    public DS_Library LoadLibrary() throws Exception{
        Lib = new DS_Library();
        return Lib;
    }
    public DS_Library GetLibrary(){
        return Lib;
    }
    public DS_Library_VBBL LoadLibraryVBBL() throws Exception{
        Lib_VBBL = new DS_Library_VBBL();
        return Lib_VBBL;
    }
    public DS_Library_VBBL GetLibraryVBBL(){
        return Lib_VBBL;
    }
    //</editor-fold>
}