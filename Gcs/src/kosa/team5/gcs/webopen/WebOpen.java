package kosa.team5.gcs.webopen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kosa.team5.gcs.main.GcsMain;

public class WebOpen {
    //Field
    private Stage stage;
    private boolean aBoolean;
    //Constructor
    public WebOpen() {
        try{
            stage = new Stage(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(GcsMain.instance.primaryStage);
            AnchorPane pane = (AnchorPane) FXMLLoader.load(getClass().getResource("WebOpen.fxml"));
            Scene scene = new Scene(pane);


            stage.setScene(scene);
            stage.setResizable(false);
        } catch (Exception e){}
    }
    //Method
    public void show(){
        stage.show();
    }
    public void close(){
        stage.close();
    }

}
