package AutoCHO;
import AutoCHO.entity.FXBuildingBlock;
import AutoCHO.entity.DS_SyntheticTarget;
import AutoCHO.entity.DS_BuildingBlock;
import AutoCHO.entity.DS_Fragment;
import AutoCHO.entity.FXFragmentConnection;
import AutoCHO.entity.DS_OptResidue;
import AutoCHO.entity.DS_BuildingBlockText;
import AutoCHO.entity.DS_Library_VBBL;
import AutoCHO.entity.FXFragment;
import AutoCHO.entity.DS_Library;
import AutoCHO.entity.FXNode;
import AutoCHO.entity.DS_BuildingBlockTextVirtual;
import AutoCHO.entity.FXSolution;
import AutoCHO.entity.DS_NodeSolution;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.*;
import javafx.embed.swing.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.util.Callback;
import javax.swing.*;
import AutoCHO.algorithm.*;
import AutoCHO.entity.DS_FragmentPair;
import java.awt.Desktop;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.FileChooser;
import org.eurocarbdb.application.glycanbuilder.*;

public class MainFormController implements Initializable {
    private static MainFormController instance = new MainFormController();
    public DS_Library BBLLib;
    public DS_Library_VBBL VBBLLib;
    public DS_SyntheticTarget TargetGlycan;
    public Map<Integer, List<DS_NodeSolution>> NodeSolMap; //<Node Key, Node Solution>
    public List<DS_BuildingBlock> LibBBLList;
    
    private int CurrentNodeKey;
    private int CurrentSolIdx;
    private int CurrentFragIdx;
    private int SelectedBBLIndex;
    private int CurrentBBLIndex;
    private String DefaultExample;
    
    private boolean ToShowIUPACName;
    
    //<editor-fold defaultstate="collapsed" desc="FXML Variable Declaration">
    @FXML private ImageView ImageTargetStructure;
    @FXML private Button FXButton_Edit;
    @FXML private Button FXButton_Search;
    @FXML private Button FXButton_GloboH;
    @FXML private Button FXButton_SSEA4;
    @FXML private Button FXButton_OligoLacNAc;
    
    @FXML private TabPane FXTabPane_Lib;
    @FXML private Tab FXTab_ExpLib;
    @FXML private Tab FXTab_VirLib;
    @FXML private RadioButton FXRButton_ExpLibOnly;
    @FXML private RadioButton FXRButton_ExpAndVirLib;
    @FXML private CheckBox FXCB_Gal;
    @FXML private CheckBox FXCB_Glc;
    @FXML private CheckBox FXCB_Man;
    @FXML private CheckBox FXCB_GalNAc;
    @FXML private CheckBox FXCB_GlcNAc;
    @FXML private CheckBox FXCB_Alpha;
    @FXML private CheckBox FXCB_Beta;
    
    @FXML private CheckBox FXCB_R2_ALL;
    @FXML private CheckBox FXCB_R2_OH;
    @FXML private CheckBox FXCB_R2_OAc;
    @FXML private CheckBox FXCB_R2_OBn;
    @FXML private CheckBox FXCB_R2_OBz;
    @FXML private CheckBox FXCB_R2_OClAc;
    @FXML private CheckBox FXCB_R2_OLev;
    @FXML private CheckBox FXCB_R2_NO2Bz;
    @FXML private CheckBox FXCB_R2_OPMB;
    @FXML private CheckBox FXCB_R2_OTBDPS;
    @FXML private CheckBox FXCB_R2_OTBS;
    @FXML private CheckBox FXCB_R2_OTIPS;
    @FXML private CheckBox FXCB_R2_NHTroc;
    @FXML private CheckBox FXCB_R2_NPhth;
    @FXML private CheckBox FXCB_R2_N3;
    
    @FXML private CheckBox FXCB_R3_ALL;
    @FXML private CheckBox FXCB_R3_OH;
    @FXML private CheckBox FXCB_R3_OAc;
    @FXML private CheckBox FXCB_R3_OBn;
    @FXML private CheckBox FXCB_R3_OBz;
    @FXML private CheckBox FXCB_R3_OClAc;
    @FXML private CheckBox FXCB_R3_OLev;
    @FXML private CheckBox FXCB_R3_NO2Bz;
    @FXML private CheckBox FXCB_R3_OPMB;
    @FXML private CheckBox FXCB_R3_OTBDPS;
    @FXML private CheckBox FXCB_R3_OTBS;
    @FXML private CheckBox FXCB_R3_OTIPS;
    
    @FXML private CheckBox FXCB_R4_ALL;
    @FXML private CheckBox FXCB_R4_OH;
    @FXML private CheckBox FXCB_R4_OAc;
    @FXML private CheckBox FXCB_R4_OBn;
    @FXML private CheckBox FXCB_R4_OBz;
    @FXML private CheckBox FXCB_R4_OClAc;
    @FXML private CheckBox FXCB_R4_OLev;
    @FXML private CheckBox FXCB_R4_NO2Bz;
    @FXML private CheckBox FXCB_R4_OPMB;
    @FXML private CheckBox FXCB_R4_OTBDPS;
    @FXML private CheckBox FXCB_R4_OTBS;
    @FXML private CheckBox FXCB_R4_OTIPS;
    
    @FXML private CheckBox FXCB_R6_ALL;
    @FXML private CheckBox FXCB_R6_OH;
    @FXML private CheckBox FXCB_R6_OAc;
    @FXML private CheckBox FXCB_R6_OBn;
    @FXML private CheckBox FXCB_R6_OBz;
    @FXML private CheckBox FXCB_R6_OClAc;
    @FXML private CheckBox FXCB_R6_OLev;
    @FXML private CheckBox FXCB_R6_NO2Bz;
    @FXML private CheckBox FXCB_R6_OPMB;
    @FXML private CheckBox FXCB_R6_OTBDPS;
    @FXML private CheckBox FXCB_R6_OTBS;
    @FXML private CheckBox FXCB_R6_OTIPS;
    
    @FXML private Tab FXTab_ResultVisualization;
    @FXML private Tab FXTab_ResultDialog;
    
    @FXML private TableView<FXNode> FXTable_ReducingEndNode;
    @FXML private TableView<FXSolution> FXTable_Solution;
    @FXML private TableView<FXFragment> FXTable_Fragment;
    @FXML private TableView<FXBuildingBlock> FXTable_BBL;
    @FXML private TableView<FXFragmentConnection> FXTable_FragmentConnection;
    
    @FXML private TableColumn<FXNode, Image> TC_ReducingEndNode;
    @FXML private TableColumn<FXSolution, String> TC_Sol_ID;
    @FXML private TableColumn<FXSolution, String> TC_Sol_NumOfFrag;
    @FXML private TableColumn<FXSolution, String> TC_Sol_AvgFragYield;
    @FXML private TableColumn<FXFragment, Image> TC_Fragment_Image;
    @FXML private TableColumn<FXFragment, String> TC_Fragment_RRV;
    @FXML private TableColumn<FXFragment, String> TC_Fragment_Yield;
    @FXML private TableColumn<FXFragment, String> TC_Fragment_Deprotection;
    @FXML private TableColumn<FXBuildingBlock, Double> TC_BBL_RRV;
    @FXML private TableColumn<FXBuildingBlock, String> TC_BBL_Type;
    @FXML private TableColumn<FXFragmentConnection, String> TC_FragmentConnection;
    
    @FXML private ImageView ImageBuildingBlockBrowser;
    @FXML private TableView<DS_BuildingBlockText> LibraryTable;
    @FXML private TableColumn<DS_BuildingBlockText, Integer> TC_DBIdx;
    @FXML private TableColumn<DS_BuildingBlockText, Double> TC_RRV;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_AcceptorPosition;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_ProductAnomer;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_SugarType;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_DocRef;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_Provider;
    
    @FXML private TableView<DS_BuildingBlockTextVirtual> LibraryTableVBBL;
    @FXML private TableColumn<DS_BuildingBlockText, Boolean> TC_VBBL_Selected;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_VBBL_SugarType;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_VBBL_ProductAnomer;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_VBBL_R2;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_VBBL_R3;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_VBBL_R4;
    @FXML private TableColumn<DS_BuildingBlockText, String> TC_VBBL_R6;
    @FXML private TableColumn<DS_BuildingBlockText, Double> TC_VBBL_RRV;
    
    @FXML private TableColumn<DS_BuildingBlock, Image> TC_Image;
    @FXML private TableColumn<DS_BuildingBlock, Double> TC_Value;
    @FXML private TextArea ResultText;
    
    @FXML private TextArea IUPACText;
    @FXML private CheckMenuItem IsFragmentSearchItem;
    
    @FXML private Button ShowAllNodeButton;
    @FXML private Button ShowAllSolutionButton;
    @FXML private Button ShowIUPACNameButton;
    
    @FXML private TextField FX_RRV_THR_High;
    @FXML private TextField FX_RRV_THR_Medium;
    @FXML private TextField FX_MaxFragNum;
    @FXML private TextField FX_MinFragYield;

    @FXML private TextField FX_MinBBLNumOfEachFrag;
    @FXML private TextField FX_MaxBBLNumOfEachFrag;

    @FXML private TextField FX_MinDonorAcceptorRRVDiff;
    @FXML private TextField FX_MinDonorAcceptorRRVRatio;
    @FXML private TextField FX_MaxDonorAcceptorRRVRatio;
    
    @FXML private ToggleButton FX_ToggleButton_ConsiderNonSTol;
    //</editor-fold>
    
    public static MainFormController GetInstance(){
        return instance;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        try {
            StartProgram();
        } catch (Exception ex) {
            Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void StartProgram(){
        if(MainProcessor.GetInstance().HasMainFormBeenRun == false){
            try {
                instance = this;
                StartGlycanBuilder();
                this.LoadLibrary();
                this.LoadLibraryVBBL();
                CurrentNodeKey = 0;
                CurrentSolIdx = -1;
                CurrentFragIdx = -1;
                SelectedBBLIndex = BBLLib.BBLList.get(0).Idx;
                CurrentBBLIndex = -1;
                
                ToShowIUPACName = true;
                DrawChemicalStructure(SelectedBBLIndex);
                ShowIUPAC(BBLLib.BBTList.get(0));
                SetSearchParametersByProgram();
                this.DefaultExample = "GloboH";
            }
            catch(Exception e){
                e.printStackTrace();
            }

            LibraryTable.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>(){
                @Override
                public void onChanged(ListChangeListener.Change<? extends Integer> change){
                    if(change.getList().size() > 0){
                        int index = change.getList().get(0);
                        try{
                            DrawChemicalStructure(BBLLib.BBLList.get(index).Idx);
                            ShowIUPAC(BBLLib.BBTList.get(index));
                        }
                        catch(IOException e){
                            e.printStackTrace();  
                        }
                    }
                }
            });
            FXTable_ReducingEndNode.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>(){
                @Override
                public void onChanged(ListChangeListener.Change<? extends Integer> change){
                    if(change.getList().size() > 0){
                        int index = change.getList().get(0);
                        try{
                            CurrentNodeKey = Integer.parseInt(FXTable_ReducingEndNode.getItems().get(index).getResidueID());
                            RefreshResultTable_Solution();
                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    FXTable_Solution.getSelectionModel().selectFirst();
                                }
                            });
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            FXTable_Solution.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>(){
                @Override
                public void onChanged(ListChangeListener.Change<? extends Integer> change){
                    if(change.getList().size() > 0){
                        CurrentSolIdx = change.getList().get(0);
                        try{
                            if(CurrentNodeKey < 0){
                                CurrentNodeKey = Integer.parseInt(FXTable_ReducingEndNode.getSelectionModel().getSelectedItem().ResidueID);
                            }
                            RefreshResultTable_Fragment();
                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    FXTable_Fragment.getSelectionModel().selectFirst();
                                }
                            });
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            FXTable_Fragment.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>(){
                @Override
                public void onChanged(ListChangeListener.Change<? extends Integer> change){
                    if(change.getList().size() > 0){
                        CurrentFragIdx = change.getList().get(0);
                        try{
                            if(CurrentNodeKey < 0){
                                CurrentNodeKey = Integer.parseInt(FXTable_ReducingEndNode.getSelectionModel().getSelectedItem().ResidueID);
                            }
                            if(CurrentSolIdx < 0){
                                FXTable_Solution.getSelectionModel().selectFirst();
                                CurrentSolIdx = FXTable_Solution.getSelectionModel().getSelectedIndex();
                            }
                            RefreshResultTable_BBL();
                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    FXTable_BBL.getSelectionModel().selectFirst();
                                    MoveToCurrentBBL(CurrentBBLIndex);
                                }
                            });
                            RefreshResultTable_FragmentConnection();
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            FXTable_BBL.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>(){
                @Override
                public void onChanged(ListChangeListener.Change<? extends Integer> change){
                    if(change.getList().size() > 0){
                        CurrentBBLIndex = change.getList().get(0);
                        try{
                            if(CurrentNodeKey < 0){
                                CurrentNodeKey = Integer.parseInt(FXTable_ReducingEndNode.getSelectionModel().getSelectedItem().ResidueID);
                            }
                            if(CurrentSolIdx < 0){
                                CurrentSolIdx = FXTable_Solution.getSelectionModel().getSelectedIndex();
                            }
                            if(CurrentFragIdx < 0){
                                CurrentFragIdx = FXTable_Fragment.getSelectionModel().getSelectedIndex();
                            }
                            if(FXTable_BBL.getSelectionModel().getSelectedItem() == null)
                                return;
                            MoveToCurrentBBL(CurrentBBLIndex);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
    public void OutputPGFromLib(){
        for(DS_BuildingBlock BB: BBLLib.BBLList){
            if(BB.Opt_Glycan.node.size() == 1){
                System.out.print(BB.Idx);
                System.out.print("\t" + BB.RRV);
                DS_OptResidue or = BB.Opt_Glycan.node.get(BB.Opt_Glycan.GetRootID());
                String sugarName = or.GBResidue.getTypeName();
                System.out.print("\t" + sugarName);
                String anomericState = String.valueOf(or.GBResidue.getAnomericState());
                System.out.print("\t" + anomericState);
                String chirality = String.valueOf(or.GBResidue.getChirality());
                System.out.print("\t" + chirality);
                
                if(sugarName.equals("Neu5Ac") || sugarName.equals("NeuAc")){
                    System.out.print("\t" + or.PG.get(1));
                    System.out.print("\t" + or.PG.get(4));
                    System.out.print("\t" + or.PG.get(5));
                    System.out.print("\t" + or.PG.get(7));
                    System.out.print("\t" + or.PG.get(8));
                    System.out.print("\t" + or.PG.get(9));
                }
                else{
                    System.out.print("\t" + or.PG.get(2));
                    System.out.print("\t" + or.PG.get(3));
                    System.out.print("\t" + or.PG.get(4));
                    System.out.print("\t" + or.PG.get(6));
                }
                System.out.println();
            }
        }
    }
    public void StartGlycanBuilder() throws Exception{
        MainProcessor.GetInstance().HasMainFormBeenRun = true;
        Image image = MainProcessor.GetInstance().StartGlycanBuilder();
        //DrawTargetGlycan(image);
    }
    public void DrawTargetGlycan(Image image){
        ImageTargetStructure.setFitWidth(image.getWidth());
        ImageTargetStructure.setFitHeight(image.getHeight());
        ImageTargetStructure.setImage(image);
    }
    public void EnableGlycanBuilder(){
        this.DisableButtons();
        MainProcessor.GetInstance().EnableGlycanBuilder();
        this.DefaultExample = "";
    }
    public void DisableButtons(){
        this.FXButton_Edit.setDisable(true);
        this.FXButton_Search.setDisable(true);
        this.FXButton_GloboH.setDisable(true);
        this.FXButton_SSEA4.setDisable(true);
        this.FXButton_OligoLacNAc.setDisable(true);
    }
    public void EnableButtons(){
        this.FXButton_Edit.setDisable(false);
        this.FXButton_Search.setDisable(false);
        this.FXButton_GloboH.setDisable(false);
        this.FXButton_SSEA4.setDisable(false);
        this.FXButton_OligoLacNAc.setDisable(false);
    }
    public void DrawGloboH() throws Exception{
        Image image = MainProcessor.GetInstance().DrawGloboH();
        //Image image = MainProcessor.GetInstance().DrawGloboB();
        DrawTargetGlycan(image);
        MainProcessor.GetInstance().MinDonorAcceptorRRVRatio = 1.0;
        MainProcessor.GetInstance().MaxDonorAcceptorRRVRatio = 200.0;
        MainProcessor.GetInstance().MinDonorAcceptorRRVDiff = 100.0;
        MainProcessor.GetInstance().RRV_THR_High = 4000;
        MainProcessor.GetInstance().RRV_THR_Medium = 600;
        MainProcessor.GetInstance().MinBBLNumOfEachFrag = 1;
        MainProcessor.GetInstance().MaxBBLNumOfEachFrag = 3;
        
        this.DisableVBBL();
        this.VBBLLib.BBLTextList.get(18335).selected.set(false);
        
        SetSearchParametersByProgram();
        this.FXButton_Search.setDisable(false);
        this.DefaultExample = "GloboH";
    }
    public void DrawSSEA4() throws Exception{
        Image image = MainProcessor.GetInstance().DrawSSEA4();
        DrawTargetGlycan(image);
        MainProcessor.GetInstance().MinDonorAcceptorRRVRatio = 1.0;
        MainProcessor.GetInstance().MaxDonorAcceptorRRVRatio = 50.0;
        MainProcessor.GetInstance().MinDonorAcceptorRRVDiff = 100.0;
        MainProcessor.GetInstance().RRV_THR_High = 1000;
        MainProcessor.GetInstance().RRV_THR_Medium = 30;
        MainProcessor.GetInstance().MinBBLNumOfEachFrag = 1;
        MainProcessor.GetInstance().MaxBBLNumOfEachFrag = 3;
        
        this.DisableVBBL();
        this.VBBLLib.BBLTextList.get(18335).selected.set(false);
        
        SetSearchParametersByProgram();
        this.FXButton_Search.setDisable(false);
        this.DefaultExample = "SSEA4";
    }
    public void DrawOligoLacNAc() throws Exception{
        Image image = MainProcessor.GetInstance().DrawOligoLacNAc();
        DrawTargetGlycan(image);
        MainProcessor.GetInstance().MinDonorAcceptorRRVRatio = 5.0;
        MainProcessor.GetInstance().MaxDonorAcceptorRRVRatio = 15.0;
        MainProcessor.GetInstance().MinDonorAcceptorRRVDiff = 100.0;
        MainProcessor.GetInstance().RRV_THR_High = 200;
        MainProcessor.GetInstance().RRV_THR_Medium = 30;
        MainProcessor.GetInstance().MinBBLNumOfEachFrag = 2;
        MainProcessor.GetInstance().MaxBBLNumOfEachFrag = 3;
        
        this.EnableVBBL();
        this.VBBLLib.BBLTextList.get(18335).selected.set(true);
        
        SetSearchParametersByProgram();
        this.FXButton_Search.setDisable(false);
        this.DefaultExample = "OlogoLacNAc";
    }
    public void OpenAutoCHOWebsite(){
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                URI uri = new URI("https://sites.google.com/view/auto-cho/home");
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="DS_Library Function">
    public void DrawChemicalStructure(int index) throws MalformedURLException{
        Image image = MainProcessor.GetInstance().DrawChemicalStructure(index);
        ImageBuildingBlockBrowser.setFitWidth(image.getWidth());
        ImageBuildingBlockBrowser.setFitHeight(image.getHeight());
        ImageBuildingBlockBrowser.setImage(image);
    }
    public void DrawVirtualBBLChemicalStructure(String name) throws MalformedURLException{
        //Image image = MainProcessor.GetInstance().DrawVirtualBBLChemicalStructure(name);
        //ImageVirtualBBLBrowser.setFitWidth(image.getWidth());
        //ImageVirtualBBLBrowser.setFitHeight(image.getHeight());
        //ImageVirtualBBLBrowser.setImage(image);
    }
    public void LoadLibrary() throws Exception{
        BBLLib = MainProcessor.GetInstance().LoadLibrary();
        TC_DBIdx.setCellValueFactory(new PropertyValueFactory<>("databaseIndex"));
        TC_RRV.setCellValueFactory(new PropertyValueFactory<>("RRV"));
        TC_AcceptorPosition.setCellValueFactory(new PropertyValueFactory<>("acceptorPosition"));
        TC_ProductAnomer.setCellValueFactory(new PropertyValueFactory<>("productAnomer"));
        TC_SugarType.setCellValueFactory(new PropertyValueFactory<>("sugarType"));
        TC_DocRef.setCellValueFactory(new PropertyValueFactory<>("DocRef"));
        TC_Provider.setCellValueFactory(new PropertyValueFactory<>("Provider"));
        
        ObservableList<DS_BuildingBlockText> oBBTList = FXCollections.observableArrayList(BBLLib.BBTList);
        LibraryTable.setItems(oBBTList);
    }
    public void LoadLibraryVBBL() throws Exception{
        VBBLLib = MainProcessor.GetInstance().LoadLibraryVBBL();
        TC_VBBL_Selected.setCellFactory(CheckBoxTableCell.forTableColumn(TC_VBBL_Selected));
        TC_VBBL_Selected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        TC_VBBL_SugarType.setCellValueFactory(new PropertyValueFactory<>("sugarType"));
        TC_VBBL_ProductAnomer.setCellValueFactory(new PropertyValueFactory<>("productAnomer"));
        TC_VBBL_R2.setCellValueFactory(new PropertyValueFactory<>("R2"));
        TC_VBBL_R3.setCellValueFactory(new PropertyValueFactory<>("R3"));
        TC_VBBL_R4.setCellValueFactory(new PropertyValueFactory<>("R4"));
        TC_VBBL_R6.setCellValueFactory(new PropertyValueFactory<>("R6"));
        TC_VBBL_RRV.setCellValueFactory(new PropertyValueFactory<>("RRV"));
        
        ObservableList<DS_BuildingBlockTextVirtual> oBBTList = FXCollections.observableArrayList(VBBLLib.BBLTextList);
        LibraryTableVBBL.setItems(oBBTList);
        LibraryTableVBBL.setEditable(true);
    }
    public List<Integer> GetSelectedVBBLIdx(){
        int idx = 0;
        List<Integer> SelectedIdxList = new ArrayList<>();
        for(DS_BuildingBlockTextVirtual VBBL: VBBLLib.BBLTextList){
            if(VBBL.selected.getValue()){
                SelectedIdxList.add(idx);
            }
            ++idx;
        }
        return SelectedIdxList;
    }
    public void EnableVBBL(){
        this.FXRButton_ExpAndVirLib.selectedProperty().set(true);
        this.FXRButton_ExpLibOnly.selectedProperty().set(false);
        MainProcessor.GetInstance().LibMode = 1;
        EnableVBBLOptions();
    }
    public void DisableVBBL(){
        this.FXRButton_ExpLibOnly.selectedProperty().set(true);
        this.FXRButton_ExpAndVirLib.selectedProperty().set(false);
        MainProcessor.GetInstance().LibMode = 0;
        DisableVBBLOptions();
    }
    public void EnableVBBLOptions(){
        this.TC_VBBL_Selected.setEditable(true);
    }
    public void DisableVBBLOptions(){
        this.TC_VBBL_Selected.setEditable(false);
    }
    public void SelectAllR2(){
        if(this.FXCB_R2_ALL.selectedProperty().get() == true){
            this.FXCB_R2_NO2Bz.selectedProperty().set(true);
            this.FXCB_R2_OAc.selectedProperty().set(true);
            this.FXCB_R2_OBn.selectedProperty().set(true);
            this.FXCB_R2_OBz.selectedProperty().set(true);
            this.FXCB_R2_OClAc.selectedProperty().set(true);
            this.FXCB_R2_OH.selectedProperty().set(true);
            this.FXCB_R2_OLev.selectedProperty().set(true);
            this.FXCB_R2_OPMB.selectedProperty().set(true);
            this.FXCB_R2_OTBDPS.selectedProperty().set(true);
            this.FXCB_R2_OTBS.selectedProperty().set(true);
            this.FXCB_R2_OTIPS.selectedProperty().set(true);
            this.FXCB_R2_NHTroc.selectedProperty().set(true);
            this.FXCB_R2_NPhth.selectedProperty().set(true);
            this.FXCB_R2_N3.selectedProperty().set(true);
        }
        else{
            this.FXCB_R2_NO2Bz.selectedProperty().set(false);
            this.FXCB_R2_OAc.selectedProperty().set(false);
            this.FXCB_R2_OBn.selectedProperty().set(false);
            this.FXCB_R2_OBz.selectedProperty().set(false);
            this.FXCB_R2_OClAc.selectedProperty().set(false);
            this.FXCB_R2_OH.selectedProperty().set(false);
            this.FXCB_R2_OLev.selectedProperty().set(false);
            this.FXCB_R2_OPMB.selectedProperty().set(false);
            this.FXCB_R2_OTBDPS.selectedProperty().set(false);
            this.FXCB_R2_OTBS.selectedProperty().set(false);
            this.FXCB_R2_OTIPS.selectedProperty().set(false);
            this.FXCB_R2_NHTroc.selectedProperty().set(false);
            this.FXCB_R2_NPhth.selectedProperty().set(false);
            this.FXCB_R2_N3.selectedProperty().set(false);
        }
        this.RefreshLibraryVBBL();
    }
    public void SelectAllR3(){
        if(this.FXCB_R3_ALL.selectedProperty().get() == true){
            this.FXCB_R3_NO2Bz.selectedProperty().set(true);
            this.FXCB_R3_OAc.selectedProperty().set(true);
            this.FXCB_R3_OBn.selectedProperty().set(true);
            this.FXCB_R3_OBz.selectedProperty().set(true);
            this.FXCB_R3_OClAc.selectedProperty().set(true);
            this.FXCB_R3_OH.selectedProperty().set(true);
            this.FXCB_R3_OLev.selectedProperty().set(true);
            this.FXCB_R3_OPMB.selectedProperty().set(true);
            this.FXCB_R3_OTBDPS.selectedProperty().set(true);
            this.FXCB_R3_OTBS.selectedProperty().set(true);
            this.FXCB_R3_OTIPS.selectedProperty().set(true);
        }
        else{
            this.FXCB_R3_NO2Bz.selectedProperty().set(false);
            this.FXCB_R3_OAc.selectedProperty().set(false);
            this.FXCB_R3_OBn.selectedProperty().set(false);
            this.FXCB_R3_OBz.selectedProperty().set(false);
            this.FXCB_R3_OClAc.selectedProperty().set(false);
            this.FXCB_R3_OH.selectedProperty().set(false);
            this.FXCB_R3_OLev.selectedProperty().set(false);
            this.FXCB_R3_OPMB.selectedProperty().set(false);
            this.FXCB_R3_OTBDPS.selectedProperty().set(false);
            this.FXCB_R3_OTBS.selectedProperty().set(false);
            this.FXCB_R3_OTIPS.selectedProperty().set(false);
        }
        this.RefreshLibraryVBBL();
    }
    public void SelectAllR4(){
        if(this.FXCB_R4_ALL.selectedProperty().get() == true){
            this.FXCB_R4_NO2Bz.selectedProperty().set(true);
            this.FXCB_R4_OAc.selectedProperty().set(true);
            this.FXCB_R4_OBn.selectedProperty().set(true);
            this.FXCB_R4_OBz.selectedProperty().set(true);
            this.FXCB_R4_OClAc.selectedProperty().set(true);
            this.FXCB_R4_OH.selectedProperty().set(true);
            this.FXCB_R4_OLev.selectedProperty().set(true);
            this.FXCB_R4_OPMB.selectedProperty().set(true);
            this.FXCB_R4_OTBDPS.selectedProperty().set(true);
            this.FXCB_R4_OTBS.selectedProperty().set(true);
            this.FXCB_R4_OTIPS.selectedProperty().set(true);
        }
        else{
            this.FXCB_R4_NO2Bz.selectedProperty().set(false);
            this.FXCB_R4_OAc.selectedProperty().set(false);
            this.FXCB_R4_OBn.selectedProperty().set(false);
            this.FXCB_R4_OBz.selectedProperty().set(false);
            this.FXCB_R4_OClAc.selectedProperty().set(false);
            this.FXCB_R4_OH.selectedProperty().set(false);
            this.FXCB_R4_OLev.selectedProperty().set(false);
            this.FXCB_R4_OPMB.selectedProperty().set(false);
            this.FXCB_R4_OTBDPS.selectedProperty().set(false);
            this.FXCB_R4_OTBS.selectedProperty().set(false);
            this.FXCB_R4_OTIPS.selectedProperty().set(false);
        }
        this.RefreshLibraryVBBL();
    }
    public void SelectAllR6(){
        if(this.FXCB_R6_ALL.selectedProperty().get() == true){
            this.FXCB_R6_NO2Bz.selectedProperty().set(true);
            this.FXCB_R6_OAc.selectedProperty().set(true);
            this.FXCB_R6_OBn.selectedProperty().set(true);
            this.FXCB_R6_OBz.selectedProperty().set(true);
            this.FXCB_R6_OClAc.selectedProperty().set(true);
            this.FXCB_R6_OH.selectedProperty().set(true);
            this.FXCB_R6_OLev.selectedProperty().set(true);
            this.FXCB_R6_OPMB.selectedProperty().set(true);
            this.FXCB_R6_OTBDPS.selectedProperty().set(true);
            this.FXCB_R6_OTBS.selectedProperty().set(true);
            this.FXCB_R6_OTIPS.selectedProperty().set(true);
        }
        else{
            this.FXCB_R6_NO2Bz.selectedProperty().set(false);
            this.FXCB_R6_OAc.selectedProperty().set(false);
            this.FXCB_R6_OBn.selectedProperty().set(false);
            this.FXCB_R6_OBz.selectedProperty().set(false);
            this.FXCB_R6_OClAc.selectedProperty().set(false);
            this.FXCB_R6_OH.selectedProperty().set(false);
            this.FXCB_R6_OLev.selectedProperty().set(false);
            this.FXCB_R6_OPMB.selectedProperty().set(false);
            this.FXCB_R6_OTBDPS.selectedProperty().set(false);
            this.FXCB_R6_OTBS.selectedProperty().set(false);
            this.FXCB_R6_OTIPS.selectedProperty().set(false);
        }
        this.RefreshLibraryVBBL();
    }
    public void RefreshLibraryVBBL(){
        CheckProperty();
        List<DS_BuildingBlockTextVirtual> VBBLList = VBBLLib.BBLTextList.stream()
                .filter(x -> ("Gal".equals(x.sugarType) && FXCB_Gal.selectedProperty().get() == true) || 
                        ("Glc".equals(x.sugarType) && FXCB_Glc.selectedProperty().get() == true) || 
                        ("Man".equals(x.sugarType) && FXCB_Man.selectedProperty().get() == true) || 
                        ("GalNAc".equals(x.sugarType) && FXCB_GalNAc.selectedProperty().get() == true) ||
                        ("GlcNAc".equals(x.sugarType) && FXCB_GlcNAc.selectedProperty().get() == true))
                .filter(x -> ("Alpha".equals(x.productAnomer) && FXCB_Alpha.selectedProperty().get() == true) || 
                        ("Beta".equals(x.productAnomer) && FXCB_Beta.selectedProperty().get() == true))
                .filter(x -> ("OH".equals(x.R2) && FXCB_R2_OH.selectedProperty().get() == true) || 
                        ("OAc".equals(x.R2) && FXCB_R2_OAc.selectedProperty().get() == true) || 
                        ("OBn".equals(x.R2) && FXCB_R2_OBn.selectedProperty().get() == true) || 
                        ("OBz".equals(x.R2) && FXCB_R2_OBz.selectedProperty().get() == true) || 
                        ("OClAc".equals(x.R2) && FXCB_R2_OClAc.selectedProperty().get() == true) || 
                        ("OLev".equals(x.R2) && FXCB_R2_OLev.selectedProperty().get() == true) || 
                        ("NO2Bz".equals(x.R2) && FXCB_R2_NO2Bz.selectedProperty().get() == true) || 
                        ("OPMB".equals(x.R2) && FXCB_R2_OPMB.selectedProperty().get() == true) || 
                        ("OTBDPS".equals(x.R2) && FXCB_R2_OTBDPS.selectedProperty().get() == true) || 
                        ("OTBS".equals(x.R2) && FXCB_R2_OTBS.selectedProperty().get() == true) || 
                        ("OTIPS".equals(x.R2) && FXCB_R2_OTIPS.selectedProperty().get() == true) ||
                        ("NHTroc".equals(x.R2) && FXCB_R2_NHTroc.selectedProperty().get() == true) ||
                        ("NPhth".equals(x.R2) && FXCB_R2_NPhth.selectedProperty().get() == true) ||
                        ("N3".equals(x.R2) && FXCB_R2_N3.selectedProperty().get() == true))
                .filter(x -> ("OH".equals(x.R3) && FXCB_R3_OH.selectedProperty().get() == true) || 
                        ("OAc".equals(x.R3) && FXCB_R3_OAc.selectedProperty().get() == true) || 
                        ("OBn".equals(x.R3) && FXCB_R3_OBn.selectedProperty().get() == true) || 
                        ("OBz".equals(x.R3) && FXCB_R3_OBz.selectedProperty().get() == true) || 
                        ("OClAc".equals(x.R3) && FXCB_R3_OClAc.selectedProperty().get() == true) || 
                        ("OLev".equals(x.R3) && FXCB_R3_OLev.selectedProperty().get() == true) || 
                        ("NO2Bz".equals(x.R3) && FXCB_R3_NO2Bz.selectedProperty().get() == true) || 
                        ("OPMB".equals(x.R3) && FXCB_R3_OPMB.selectedProperty().get() == true) || 
                        ("OTBDPS".equals(x.R3) && FXCB_R3_OTBDPS.selectedProperty().get() == true) || 
                        ("OTBS".equals(x.R3) && FXCB_R3_OTBS.selectedProperty().get() == true) || 
                        ("OTIPS".equals(x.R3) && FXCB_R3_OTIPS.selectedProperty().get() == true))
                .filter(x -> ("OH".equals(x.R4) && FXCB_R4_OH.selectedProperty().get() == true) || 
                        ("OAc".equals(x.R4) && FXCB_R4_OAc.selectedProperty().get() == true) || 
                        ("OBn".equals(x.R4) && FXCB_R4_OBn.selectedProperty().get() == true) || 
                        ("OBz".equals(x.R4) && FXCB_R4_OBz.selectedProperty().get() == true) || 
                        ("OClAc".equals(x.R4) && FXCB_R4_OClAc.selectedProperty().get() == true) || 
                        ("OLev".equals(x.R4) && FXCB_R4_OLev.selectedProperty().get() == true) || 
                        ("NO2Bz".equals(x.R4) && FXCB_R4_NO2Bz.selectedProperty().get() == true) || 
                        ("OPMB".equals(x.R4) && FXCB_R4_OPMB.selectedProperty().get() == true) || 
                        ("OTBDPS".equals(x.R4) && FXCB_R4_OTBDPS.selectedProperty().get() == true) || 
                        ("OTBS".equals(x.R4) && FXCB_R4_OTBS.selectedProperty().get() == true) || 
                        ("OTIPS".equals(x.R4) && FXCB_R4_OTIPS.selectedProperty().get() == true))
                .filter(x -> ("OH".equals(x.R6) && FXCB_R6_OH.selectedProperty().get() == true) || 
                        ("OAc".equals(x.R6) && FXCB_R6_OAc.selectedProperty().get() == true) || 
                        ("OBn".equals(x.R6) && FXCB_R6_OBn.selectedProperty().get() == true) || 
                        ("OBz".equals(x.R6) && FXCB_R6_OBz.selectedProperty().get() == true) || 
                        ("OClAc".equals(x.R6) && FXCB_R6_OClAc.selectedProperty().get() == true) || 
                        ("OLev".equals(x.R6) && FXCB_R6_OLev.selectedProperty().get() == true) || 
                        ("NO2Bz".equals(x.R6) && FXCB_R6_NO2Bz.selectedProperty().get() == true) || 
                        ("OPMB".equals(x.R6) && FXCB_R6_OPMB.selectedProperty().get() == true) || 
                        ("OTBDPS".equals(x.R6) && FXCB_R6_OTBDPS.selectedProperty().get() == true) || 
                        ("OTBS".equals(x.R6) && FXCB_R6_OTBS.selectedProperty().get() == true) || 
                        ("OTIPS".equals(x.R6) && FXCB_R6_OTIPS.selectedProperty().get() == true))
                .collect(Collectors.toList());
        ObservableList<DS_BuildingBlockTextVirtual> oBBTList = FXCollections.observableArrayList(VBBLList);
        LibraryTableVBBL.setItems(oBBTList);
    }
    public void CheckProperty(){
        if(FXCB_R2_NO2Bz.selectedProperty().get() && 
                FXCB_R2_OAc.selectedProperty().get() && 
                FXCB_R2_OBn.selectedProperty().get() &&
                FXCB_R2_OBz.selectedProperty().get() &&
                FXCB_R2_OClAc.selectedProperty().get() &&
                FXCB_R2_OH.selectedProperty().get() &&
                FXCB_R2_OLev.selectedProperty().get() &&
                FXCB_R2_OPMB.selectedProperty().get() &&
                FXCB_R2_OTBDPS.selectedProperty().get() &&
                FXCB_R2_OTBS.selectedProperty().get() &&
                FXCB_R2_OTIPS.selectedProperty().get() &&
                FXCB_R2_NHTroc.selectedProperty().get() &&
                FXCB_R2_NPhth.selectedProperty().get() &&
                FXCB_R2_N3.selectedProperty().get())
            this.FXCB_R2_ALL.selectedProperty().set(true);
        else
            this.FXCB_R2_ALL.selectedProperty().set(false);
        if(FXCB_R3_NO2Bz.selectedProperty().get() && 
                FXCB_R3_OAc.selectedProperty().get() && 
                FXCB_R3_OBn.selectedProperty().get() &&
                FXCB_R3_OBz.selectedProperty().get() &&
                FXCB_R3_OClAc.selectedProperty().get() &&
                FXCB_R3_OH.selectedProperty().get() &&
                FXCB_R3_OLev.selectedProperty().get() &&
                FXCB_R3_OPMB.selectedProperty().get() &&
                FXCB_R3_OTBDPS.selectedProperty().get() &&
                FXCB_R3_OTBS.selectedProperty().get() &&
                FXCB_R3_OTIPS.selectedProperty().get())
            this.FXCB_R3_ALL.selectedProperty().set(true);
        else
            this.FXCB_R3_ALL.selectedProperty().set(false);
        if(FXCB_R4_NO2Bz.selectedProperty().get() && 
                FXCB_R4_OAc.selectedProperty().get() && 
                FXCB_R4_OBn.selectedProperty().get() &&
                FXCB_R4_OBz.selectedProperty().get() &&
                FXCB_R4_OClAc.selectedProperty().get() &&
                FXCB_R4_OH.selectedProperty().get() &&
                FXCB_R4_OLev.selectedProperty().get() &&
                FXCB_R4_OPMB.selectedProperty().get() &&
                FXCB_R4_OTBDPS.selectedProperty().get() &&
                FXCB_R4_OTBS.selectedProperty().get() &&
                FXCB_R4_OTIPS.selectedProperty().get())
            this.FXCB_R4_ALL.selectedProperty().set(true);
        else
            this.FXCB_R4_ALL.selectedProperty().set(false);
        if(FXCB_R6_NO2Bz.selectedProperty().get() && 
                FXCB_R6_OAc.selectedProperty().get() && 
                FXCB_R6_OBn.selectedProperty().get() &&
                FXCB_R6_OBz.selectedProperty().get() &&
                FXCB_R6_OClAc.selectedProperty().get() &&
                FXCB_R6_OH.selectedProperty().get() &&
                FXCB_R6_OLev.selectedProperty().get() &&
                FXCB_R6_OPMB.selectedProperty().get() &&
                FXCB_R6_OTBDPS.selectedProperty().get() &&
                FXCB_R6_OTBS.selectedProperty().get() &&
                FXCB_R6_OTIPS.selectedProperty().get())
            this.FXCB_R6_ALL.selectedProperty().set(true);
        else
            this.FXCB_R6_ALL.selectedProperty().set(false);
    }
    public void ShowIUPAC(DS_BuildingBlockText BBT){
        if(ToShowIUPACName == true){
            IUPACText.textProperty().setValue(BBT.IUPAC);
        }
    }
    public void ShowResultText(String text){
        ResultText.textProperty().setValue(text);
    }
    public void SaveResultText(){
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save as");
        fileChooser.setInitialFileName("Auto-CHO_ResultText.txt");
        //Show save file dialog
        File file = fileChooser.showSaveDialog(AutoCHO.mainStage);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(ResultText.textProperty().getValue());
            fileWriter.close();
        } catch (IOException ex) {
            
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Search Function">
    public void StartSearch(){
        try{
            this.Initialize(MainProcessor.GetInstance().GBF.getCanvas().getDocument().getFirstStructure());
            boolean IsTargetStructureOK = this.CheckTargetStructure();
            int NumOfSelectedVBBL = this.CheckNumOfSelectedVBBL();
            if(IsTargetStructureOK){
                if(NumOfSelectedVBBL <= 500){
                    this.DisableButtons();
                    this.Run();
                    this.EnableButtons();
                }
                else{
                    this.DisplayTooManyVBBLMsg();
                }
            }
            else{
                this.DisplayStructureErrMsg();
            }
        }
        catch(Exception ex){
            this.DisplayStructureErrMsg();
            System.out.println(ex.toString());
        }
    }
    public void Initialize(Glycan glycan) throws Exception{
        TargetGlycan = new DS_SyntheticTarget(glycan);
    }
    public boolean CheckTargetStructure(){
        return TargetGlycan.sugarStuctList.get(0).TargetGlycan.DFS_CheckStructure();
    }
    public int CheckNumOfSelectedVBBL(){
        return MainFormController.GetInstance().GetSelectedVBBLIdx().size();
    }
    public void DisplayStructureErrMsg(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("The structure information is not complete. Please check it.");
        alert.showAndWait();
        this.FXTable_ReducingEndNode.getItems().clear();
        this.FXTable_Solution.getItems().clear();
        this.FXTable_Fragment.getItems().clear();
        this.FXTable_BBL.getItems().clear();
        this.FXTable_FragmentConnection.getItems().clear();
        this.FXTable_ReducingEndNode.setDisable(true);
        this.FXTable_Solution.setDisable(true);
        this.FXTable_Fragment.setDisable(true);
        this.FXTable_BBL.setDisable(true);
        this.FXTable_FragmentConnection.setDisable(true);
        this.ResultText.clear();
    }
    public void DisplayTooManyVBBLMsg(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Number of selected virtual building blocks should <= 500.");
        alert.showAndWait();
        this.FXTable_ReducingEndNode.getItems().clear();
        this.FXTable_Solution.getItems().clear();
        this.FXTable_Fragment.getItems().clear();
        this.FXTable_BBL.getItems().clear();
        this.FXTable_FragmentConnection.getItems().clear();
        this.FXTable_ReducingEndNode.setDisable(true);
        this.FXTable_Solution.setDisable(true);
        this.FXTable_Fragment.setDisable(true);
        this.FXTable_BBL.setDisable(true);
        this.FXTable_FragmentConnection.setDisable(true);
        this.ResultText.clear();
    }
    public void Run(){
        MainProcessor.GetInstance().TargetGlycan = TargetGlycan;
        try{
            Thread search = new Search();
            search.start();
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Display Function">
    public void ShowSolResult(){
        int counter = 0;
        for(int MapKey: NodeSolMap.keySet()){
            if(counter == 1){
                CurrentNodeKey = MapKey;
                break;
            }
            ++counter;
        }
        this.FXTab_ResultVisualization.setDisable(false);
        this.FXTab_ResultDialog.setDisable(false);
        
        RefreshResultTable_ReducingEndNode();
        try{
            if(this.DefaultExample.equals("SSEA4"))
                FXTable_ReducingEndNode.getSelectionModel().select(2);
            else
                FXTable_ReducingEndNode.getSelectionModel().select(1);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void RefreshResultTable_ReducingEndNode(){
        try{
            Fragmenter fragmenter = new Fragmenter();
            Glycan TargetStructure = TargetGlycan.sugarStuctList.get(0).TargetGlycan.glycan;
            List<FXNode> NodeList = new ArrayList<>();
            Glycan NewTargetStructure = TargetStructure.clone();
            int OrgRootID = TargetStructure.getRoot().id;
            int NewRootID = NewTargetStructure.getRoot().id;
            int diff = NewRootID - OrgRootID;

            Map<Integer, Residue> ID_Residue_Map = new HashMap<>();
            for(Residue r: NewTargetStructure.getAllResidues()){
                ID_Residue_Map.put(r.id, r);
            }
            
            for(int MapKey: NodeSolMap.keySet()){
                Glycan ZFragment = fragmenter.getZFragment(NewTargetStructure, ID_Residue_Map.get(MapKey + diff));
                List<Glycan> GlycanList = new ArrayList<>();
                GlycanList.add(ZFragment);
                BufferedImage bi = MainProcessor.GetInstance().GBF.getCanvas().getGlycanRenderer().getImage(GlycanList, false, false, true);
                Image image = SwingFXUtils.toFXImage(bi, null);
                FXNode node = new FXNode();
                node.setResidueID(String.valueOf(MapKey));
                node.setReducingEndImage(image);
                if(node != null)
                    NodeList.add(node);
            }
            Collections.sort(NodeList, Comparator.comparing(FXNode::getResidueID));
            NodeList.remove(0);
            
            TC_ReducingEndNode.setCellValueFactory(new PropertyValueFactory<>("ReducingEndImage"));
            TC_ReducingEndNode.setCellFactory(new Callback<TableColumn<FXNode, Image>, TableCell<FXNode, Image>>(){
                 @Override
                 public TableCell<FXNode, Image> call(TableColumn<FXNode, Image> param) {
                     TableCell<FXNode, Image> cell = new TableCell<FXNode, Image>(){
                         @Override
                         public void updateItem(Image image, boolean empty){
                             if(image != null){
                                 ImageView imgVw = new ImageView(image);
                                 setGraphic(imgVw);
                             }
                         }
                     };
                     return cell;
                 }
            });
            ObservableList<FXNode> ONodeList = FXCollections.observableArrayList(NodeList);
            FXTable_ReducingEndNode.setDisable(false);
            FXTable_ReducingEndNode.setItems(ONodeList);
            CurrentSolIdx = 0;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void RefreshResultTable_Solution(){
        try{
            if(NodeSolMap.get(CurrentNodeKey).isEmpty()){
                this.FXTable_Solution.getItems().clear();
                this.FXTable_Fragment.getItems().clear();
                this.FXTable_BBL.getItems().clear();
                this.FXTable_FragmentConnection.getItems().clear();
                this.FXTable_Solution.setDisable(true);
                this.FXTable_Fragment.setDisable(true);
                this.FXTable_BBL.setDisable(true);
                this.FXTable_FragmentConnection.setDisable(true);
                return;
            }
            this.FXTable_Solution.setDisable(false);
            
            List<FXSolution> SolutionList = new ArrayList<>();
            for(int idx = 0; idx < NodeSolMap.get(CurrentNodeKey).size(); idx++){
                int NumOfFrag = NodeSolMap.get(CurrentNodeKey).get(idx).FragList.size();
                double AvgFragYield = NodeSolMap.get(CurrentNodeKey).get(idx).AvgFragYield;
                
                FXSolution solution = new FXSolution();
                solution.setSolution(String.valueOf(idx + 1));
                solution.setNumOfFrag(String.valueOf(NumOfFrag));
                solution.setAvgFragYield(String.valueOf(String.format(("%.2f"), AvgFragYield * 100) + "%"));
                SolutionList.add(solution);
            }
            
            TC_Sol_ID.setCellValueFactory(new PropertyValueFactory<>("Solution"));
            TC_Sol_NumOfFrag.setCellValueFactory(new PropertyValueFactory<>("NumOfFrag"));
            TC_Sol_AvgFragYield.setCellValueFactory(new PropertyValueFactory<>("AvgFragYield"));
            ObservableList<FXSolution> OSolutionList = FXCollections.observableArrayList(SolutionList);
            FXTable_Solution.setItems(OSolutionList);
            CurrentFragIdx = 0;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void RefreshResultTable_Fragment(){
        try{
            if(NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList.isEmpty()){
                this.FXTable_Fragment.getItems().clear();
                this.FXTable_BBL.getItems().clear();
                this.FXTable_FragmentConnection.getItems().clear();
                this.FXTable_Fragment.setDisable(true);
                this.FXTable_BBL.setDisable(true);
                this.FXTable_FragmentConnection.setDisable(true);
                return;
            }
            this.FXTable_Fragment.setDisable(false);
            
            Fragmenter fragmenter = new Fragmenter();
            Glycan TargetStructure = TargetGlycan.sugarStuctList.get(0).TargetGlycan.glycan;
            Object[] ResidueArray = TargetStructure.getAllResidues().toArray();
            List<FXFragment> FragmentList = new ArrayList<>();
            for(int idx = 0; idx < NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList.size(); idx++){
                DS_Fragment fragment = NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList.get(idx);
                FXFragment FXFragment = new FXFragment();
                
                int FragmentRootIdx = 0;
                List<Integer> FragmentCIDIdxList = new ArrayList();
                for(int i = 0; i < ResidueArray.length; i++){
                    int ResidueID = ((Residue)ResidueArray[i]).id;
                    if(ResidueID == fragment.RootID){
                        FragmentRootIdx = i;
                    }
                    else if(fragment.CIDList.contains(ResidueID)){
                        FragmentCIDIdxList.add(i);
                    }
                }
                
                Glycan NewTargetStructure = TargetStructure.clone();
                int OrgRootID = TargetStructure.getRoot().id;
                int NewRootID = NewTargetStructure.getRoot().id;
                int diff = NewRootID - OrgRootID;
                
                Map<Integer, Residue> ID_Residue_Map = new HashMap<>();
                for(Residue r: NewTargetStructure.getAllResidues()){
                    ID_Residue_Map.put(r.id, r);
                }
                
                Glycan BFragment = fragmenter.getBFragment(NewTargetStructure, ID_Residue_Map.get(fragment.RootID + diff));
                diff = BFragment.getRoot().firstSaccharideChild().id - fragment.RootID;
                ID_Residue_Map.clear();
                for(Residue r: BFragment.getAllResidues()){
                    ID_Residue_Map.put(r.id, r);
                }
                
                for(int CID: fragment.CIDList){
                    Residue r = ID_Residue_Map.get(CID + diff);
                    r.setType(ResidueType.createZCleavage());
                    while(r.firstSaccharideChild() != null){
                        BFragment.removeResidue(r.firstSaccharideChild());
                    }
                }
                Glycan NewBZFragment = BFragment;
                if(NewBZFragment == null)
                    NewBZFragment = BFragment;
                
                List<Glycan> GlycanList = new ArrayList<>();
                GlycanList.add(NewBZFragment);
                BufferedImage bi = MainProcessor.GetInstance().GBF.getCanvas().getGlycanRenderer().getImage(GlycanList, false, false, true);             
                Image image = SwingFXUtils.toFXImage(bi, null);
//                ImageIcon icon = new ImageIcon();
//                icon.setImage(bi);
//                JOptionPane.showMessageDialog(null, icon);
                
//                final SwingNode swingNode = new SwingNode();
//                this.createAndSetSwingContent(swingNode, icon);
//                StackPane pane = new StackPane();
//                pane.getChildren().add(swingNode);
//                Stage stage = new Stage();
//                stage.setScene(new Scene(pane, 400, 200));
//                stage.show();
                FXFragment.FragmentImage = image;
                FXFragment.RRV = String.format(("%.2f"), fragment.RRV);
                FXFragment.Yield = String.format(("%.2f"), fragment.Yield * 100) + "%";
                int counter = 0;
                for(String PG: fragment.DePGMap.keySet()){
                    FXFragment.Deprotection += PG;
                    if(counter < fragment.DePGMap.size() - 1){
                        FXFragment.Deprotection += ",";
                    }
                }
                if(FXFragment.Deprotection.equals("")){
                    FXFragment.Deprotection = "N/A";
                }
                else if(fragment.HasStableProductAnomer == false){
                    FXFragment.Deprotection += " (Uncertain Product Anomer)";
                }
                FragmentList.add(FXFragment);
            }
            TC_Fragment_Image.setCellValueFactory(new PropertyValueFactory<FXFragment, Image>("FragmentImage"));
            TC_Fragment_Image.setCellFactory(new Callback<TableColumn<FXFragment, Image>, TableCell<FXFragment, Image>>(){
                @Override
                public TableCell<FXFragment, Image> call(TableColumn<FXFragment, Image> param) {
                    TableCell<FXFragment, Image> cell = new TableCell<FXFragment, Image>(){
                        @Override
                        public void updateItem(Image image, boolean empty){
                            if(image != null){
                                ImageView imgVw = new ImageView(image);
                                setGraphic(imgVw);
                            }
                        }
                    };
                    return cell;
                }
            });
            TC_Fragment_RRV.setCellValueFactory(new PropertyValueFactory<FXFragment, String>("RRV"));
            TC_Fragment_Yield.setCellValueFactory(new PropertyValueFactory<FXFragment, String>("Yield"));
            TC_Fragment_Deprotection.setCellValueFactory(new PropertyValueFactory<FXFragment, String>("Deprotection"));
            ObservableList<FXFragment> OFragmentList = FXCollections.observableArrayList(FragmentList);
            FXTable_Fragment.setItems(OFragmentList);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void RefreshResultTable_BBL(){
        try{
            this.FXTable_BBL.setDisable(false);
            List<FXBuildingBlock> BuildingBlockList = new ArrayList<>();
            for(int idx = 0; idx < NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList.get(CurrentFragIdx).BBLList.size(); idx++){
                int BBLIdx = NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList.get(CurrentFragIdx).BBLList.get(idx).BBLIdx;
                FXBuildingBlock FXBBL = new FXBuildingBlock();
                FXBBL.BuildingBlock = null;
                FXBBL.RRV = LibBBLList.get(BBLIdx).RRV;
                FXBBL.Type = LibBBLList.get(BBLIdx).Type;
                BuildingBlockList.add(FXBBL);
            }
            TC_BBL_RRV.setCellValueFactory(new PropertyValueFactory<FXBuildingBlock, Double>("RRV"));
            TC_BBL_Type.setCellValueFactory(new PropertyValueFactory<FXBuildingBlock, String>("Type"));
            ObservableList<FXBuildingBlock> OBuildingBlockList = FXCollections.observableArrayList(BuildingBlockList);
            FXTable_BBL.setItems(OBuildingBlockList);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void RefreshResultTable_FragmentConnection(){
        try{
            this.FXTable_FragmentConnection.setDisable(false);
            
            List<FXFragmentConnection> FragmentConnectionList = new ArrayList<>();
            int FragSize = NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList.size();
            
            List<Integer> ParentFragIdxList = new ArrayList<>();
            Map<Integer, Integer> FragMap = new HashMap<>();
            List<DS_FragmentPair> FragmentDonorAcceptorIdxPairList = DS_Fragment.DFSOrder2(NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList);
            
            for(int idx1 = 0; idx1 < FragSize; idx1++){
                DS_Fragment fragment1 = NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList.get(idx1);
                FragMap.put(fragment1.RootID, idx1);
                for(int idx2 = 0; idx2 < FragSize; idx2++){
                    DS_Fragment fragment2 = NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList.get(idx2);
                    if(fragment1.ParentFragID == fragment2.RootID){
                        ParentFragIdxList.add(fragment1.ParentFragID);
                        break;
                    }    
                }
            }
            
            List<Integer> UsedIdxList = new ArrayList<>();
            for(int idx = 0; idx < FragmentDonorAcceptorIdxPairList.size(); idx++){
                DS_FragmentPair pair = FragmentDonorAcceptorIdxPairList.get(idx);
                int DonorFragIdx = pair.Donor;
                int AcceptorFragIdx = pair.Acceptor;
                FXFragmentConnection conn = new FXFragmentConnection();
                if(FragmentDonorAcceptorIdxPairList.size() == 1){
                    conn.Step = "X1: Fragment1 + Reducing End Acceptor";
                }
                else if(idx == 0){
                    conn.Step = "X1: Fragment" + (DonorFragIdx + 1) + " + Fragment" + (AcceptorFragIdx + 1);
                    UsedIdxList.add(DonorFragIdx);
                    UsedIdxList.add(AcceptorFragIdx);
                }
                else if(idx == FragSize - 1){
                    conn.Step = "X" + (idx + 1) + ": X" + idx + " + Reducing End Acceptor";
                }
                else{
                    if(UsedIdxList.contains(DonorFragIdx)){
                        if(AcceptorFragIdx != -1)
                            conn.Step = "X" + (idx + 1) + ": X" + idx + " + Fragment" + (AcceptorFragIdx + 1);
                        else
                            conn.Step = "X" + (idx + 1) + ": X" + idx + " + Reducing End Acceptor";
                        UsedIdxList.add(AcceptorFragIdx);
                    }
                    else if(UsedIdxList.contains(AcceptorFragIdx)){
                        conn.Step = "X" + (idx + 1) + ": Fragment" + (DonorFragIdx + 1) + " + X" + idx;
                        UsedIdxList.add(DonorFragIdx);
                    }
                }
                FragmentConnectionList.add(conn);
            }

            TC_FragmentConnection.setCellValueFactory(new PropertyValueFactory<FXFragmentConnection, String>("Step"));
            ObservableList<FXFragmentConnection> OFragmentConnectionList = FXCollections.observableArrayList(FragmentConnectionList);
            FXTable_FragmentConnection.setItems(OFragmentConnectionList);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void MoveToCurrentBBL(int index){
        int BBLIdx = NodeSolMap.get(CurrentNodeKey).get(CurrentSolIdx).FragList.get(CurrentFragIdx).BBLList.get(index).BBLIdx;
        int selectedBBLIdx = LibBBLList.get(BBLIdx).DBIdx - 1;
        if(FXTable_BBL.getSelectionModel().getSelectedItem().Type.equals("Exp.")){
            FXTabPane_Lib.getSelectionModel().select(FXTab_ExpLib);
            LibraryTable.getSelectionModel().select(selectedBBLIdx);
            LibraryTable.scrollTo(selectedBBLIdx);
        }
        else
            FXTabPane_Lib.getSelectionModel().select(FXTab_VirLib);
    }
    public void createAndSetSwingContent(final SwingNode swingNode, ImageIcon icon){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                swingNode.setContent(new JLabel(icon));
            }
        });
    }
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Basic Function">
    public void ShowBBLIUPAC(){
        if(ToShowIUPACName == true){
            ShowIUPACNameButton.setText("Show IUPAC Name");
            IUPACText.visibleProperty().setValue(false);
            ToShowIUPACName = false;
        }
        else{
            ShowIUPACNameButton.setText("Hide IUPAC Name");
            IUPACText.visibleProperty().setValue(true);
            ToShowIUPACName = true;
        }
    }
    public void Exit(){
        System.exit(0);
    }
    //</editor-fold>
    
    public void SetNodeSolMap(Map<Integer, List<DS_NodeSolution>> NodeSolMap){
        this.NodeSolMap = NodeSolMap;
    }
    public void SetLibBBLList(List<DS_BuildingBlock> LibBBLList){
        this.LibBBLList = LibBBLList;
    }
    public void SetSearchParametersByProgram(){
        this.FX_RRV_THR_High.setText(String.valueOf(MainProcessor.GetInstance().RRV_THR_High));
        this.FX_RRV_THR_Medium.setText(String.valueOf(MainProcessor.GetInstance().RRV_THR_Medium));
        this.FX_MaxFragNum.setText(String.valueOf(MainProcessor.GetInstance().MaxFragNum));
        this.FX_MinFragYield.setText(String.valueOf(MainProcessor.GetInstance().MinFragYield));
        this.FX_MinBBLNumOfEachFrag.setText(String.valueOf(MainProcessor.GetInstance().MinBBLNumOfEachFrag));
        this.FX_MaxBBLNumOfEachFrag.setText(String.valueOf(MainProcessor.GetInstance().MaxBBLNumOfEachFrag));
        this.FX_MinDonorAcceptorRRVDiff.setText(String.valueOf(MainProcessor.GetInstance().MinDonorAcceptorRRVDiff));
        this.FX_MinDonorAcceptorRRVRatio.setText(String.valueOf(MainProcessor.GetInstance().MinDonorAcceptorRRVRatio));
        this.FX_MaxDonorAcceptorRRVRatio.setText(String.valueOf(MainProcessor.GetInstance().MaxDonorAcceptorRRVRatio));
    }
    public void SetSearchParametersByUser(){
        MainProcessor.GetInstance().RRV_THR_High = Double.parseDouble(this.FX_RRV_THR_High.getText());
        MainProcessor.GetInstance().RRV_THR_Medium = Double.parseDouble(this.FX_RRV_THR_Medium.getText());
        MainProcessor.GetInstance().MaxFragNum = Integer.parseInt(this.FX_MaxFragNum.getText());
        MainProcessor.GetInstance().MinFragYield = Double.parseDouble(this.FX_MinFragYield.getText());
        MainProcessor.GetInstance().MinBBLNumOfEachFrag = Integer.parseInt(this.FX_MinBBLNumOfEachFrag.getText());
        MainProcessor.GetInstance().MaxBBLNumOfEachFrag = Integer.parseInt(this.FX_MaxBBLNumOfEachFrag.getText());
        MainProcessor.GetInstance().MinDonorAcceptorRRVDiff = Double.parseDouble(this.FX_MinDonorAcceptorRRVDiff.getText());
        MainProcessor.GetInstance().MinDonorAcceptorRRVRatio = Double.parseDouble(this.FX_MinDonorAcceptorRRVRatio.getText());
        MainProcessor.GetInstance().MaxDonorAcceptorRRVRatio = Double.parseDouble(this.FX_MaxDonorAcceptorRRVRatio.getText());
        if(this.FX_ToggleButton_ConsiderNonSTol.textProperty().get().equals("No"))
            MainProcessor.GetInstance().ToConsiderNonSTol = false;
        else
            MainProcessor.GetInstance().ToConsiderNonSTol = true;
    }
    public void SetSTolSelection(){
        if(this.FX_ToggleButton_ConsiderNonSTol.textProperty().get().equals("No")){
            this.FX_ToggleButton_ConsiderNonSTol.textProperty().set("Yes");
        }
        else{
            this.FX_ToggleButton_ConsiderNonSTol.textProperty().set("No");
        }
    }
}
