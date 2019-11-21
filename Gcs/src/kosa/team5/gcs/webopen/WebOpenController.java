package kosa.team5.gcs.webopen;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class WebOpenController implements Initializable {
    @FXML private WebView webList;
    private WebEngine webEngine;

    public void initialize(URL location, ResourceBundle resources) {
        WebViewOpen();
    }



    public void WebViewOpen() {
        webEngine = webList.getEngine();
        webEngine.load("http://localhost:8085/FinalWebProject/admin/main");

    }
}
