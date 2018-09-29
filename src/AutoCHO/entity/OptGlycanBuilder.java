package AutoCHO.entity;
import AutoCHO.MainFormController;
import AutoCHO.MainProcessor;
import java.net.*;
import javafx.application.Platform;
import javax.swing.UIManager;
import org.eurocarbdb.application.glycanbuilder.*;

public class OptGlycanBuilder extends GlycanBuilder{
    public OptGlycanBuilder() throws MalformedURLException{
        super();
        try{
            String osName = System.getProperty("os.name");
            if(!osName.toLowerCase().contains("linux") && !osName.toLowerCase().contains("nix")){
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onExit(){
        this.exit(0);
    }
    @Override
    public void exit(int err_level){
        this.setVisible(false);
        try{
            Platform.runLater(new Runnable(){
                @Override
                public void run(){
                    MainFormController.GetInstance().DrawTargetGlycan(MainProcessor.GetInstance().DrawTargetGlycan());
                    MainFormController.GetInstance().EnableButtons();
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
