package AutoCHO;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import javafx.application.*;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.stage.*;
import org.eurocarbdb.application.glycanbuilder.*;

public class AutoCHO extends Application {
    private static boolean IsOSSupported = false;
    public static Stage mainStage;
    public static HostServices hostServices;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        hostServices = getHostServices();
        if(IsOSSupported == false){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Auto-CHO supports Windows (64bit) or macOS (64bit) currently.");
            alert.showAndWait();
        }
        else{
            NativeInterface.open();
            Parent root = FXMLLoader.load(getClass().getResource("MainForm.fxml"));
            setUserAgentStylesheet(STYLESHEET_CASPIAN);
            //setUserAgentStylesheet(STYLESHEET_MODENA);
            Scene scene = new Scene(root, 1024, 768);
            primaryStage.setTitle("Auto-CHO");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });
        }
    }
    
    public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        if(osName.toLowerCase().contains("win") && osArch.contains("64")){
            //File swtJAR = new File("lib/swt-win64.jar");
            //addJarToClasspath(swtJAR);
            //addJARToClasspath(swtJAR);
            IsOSSupported = true;
            System.setErr(new PrintStream("nul"));
        }
        else if(osName.toLowerCase().contains("mac") && osArch.contains("64")){
            //File swtJAR = new File("lib/swt-mac.jar");
            //addJarToClasspath(swtJAR);
            //addJARToClasspath(swtJAR);
            IsOSSupported = true;
            System.setErr(new PrintStream("/dev/null"));
        }
        else if((osName.toLowerCase().contains("linux") || osName.toLowerCase().contains("nix")) && osArch.contains("64")){
            //File swtJAR = new File("lib/swt-linux64.jar");
            //addJarToClasspath(swtJAR);
            //addJARToClasspath(swtJAR);
            IsOSSupported = true;
            System.setErr(new PrintStream("/dev/null"));
        }
        else{
            IsOSSupported = false;
        }
        launch(args);
    }
    
    public static void addJarToClasspath(File jarFile) { 
       try 
       { 
           URL url = jarFile.toURI().toURL(); 
           URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader(); 
           Class<?> urlClass = URLClassLoader.class; 
           Method method = urlClass.getDeclaredMethod("addURL", new Class<?>[] { URL.class }); 
           method.setAccessible(true);         
           method.invoke(urlClassLoader, new Object[] { url });             
       } 
       catch (Throwable t) 
       { 
           t.printStackTrace(); 
       } 
    }
    
    public static void addJARToClasspath(File jarFile){
        try{
            URL url = jarFile.toURI().toURL(); 
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url});
            Class.forName("AutoCHO.AutoCHO", true, urlClassLoader);
            System.out.println();
            GlycanBuilder gb = new GlycanBuilder();
            gb.setVisible(true);
            //Method method = urlClass.getDeclaredMethod("addURL", new Class<?>[] { URL.class }); 
            //method.setAccessible(true);         
            //method.invoke(urlClassLoader, new Object[] { url });   
            
        } catch(Exception e){
            e.printStackTrace(); 
        }
    }
}
