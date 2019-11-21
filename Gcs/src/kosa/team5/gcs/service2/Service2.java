package kosa.team5.gcs.service2;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kosa.team5.gcs.main.GcsMain;

public class Service2 {
    //Field
    private Stage stage;
    private boolean aBoolean;
    //Constructor
    public Service2() {
        try{
            stage = new Stage(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(GcsMain.instance.primaryStage);
            BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("Service2.fxml"));
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(GcsMain.class.getResource("style_dark.css").toExternalForm());

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
