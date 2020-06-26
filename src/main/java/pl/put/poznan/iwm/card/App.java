package pl.put.poznan.iwm.card;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;


/**
 * JavaFX App
 */
public class App extends Application {

    public static final String PRIMARY = "primary";
    public static FHIRDatabase db;

    private static Scene scene;
    private static Stage stage;
    private static boolean maximized = false;
    private static Rectangle2D bounds;
    private static Rectangle2D minimizedBorder;

    @Override
    public void start(Stage stage) {
        db = new FHIRDatabase("http://localhost:8888/baseR4");
//        db = new FHIRDatabase("http://hapi.fhir.org/baseR4");


        scene = new Scene(Objects.requireNonNull(loadFXML(PRIMARY)));
        App.stage = stage;

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        positionX = stage.getX();
        positionY = stage.getY();
        bounds = Screen.getPrimary().getBounds();
    }

    static void setRoot(String fxml) {
        scene.setRoot(loadFXML(fxml));
    }

    static void setRoot(String fxml, PatientData patient) {
        var loader = loadFXML(fxml, patient);
        Parent loaded = null;
        try {
            loaded = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        SecondaryController controller = loader.getController();
        controller.setPatient(patient);
        scene.setRoot(loaded);
    }

    private static Parent loadFXML(String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static FXMLLoader loadFXML(String fxml, PatientData patient) {
        return new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    }

    public static void minimize() {
        stage.setIconified(true);
    }

    public static void maximize() {
        if (maximized) { //TODO: zmniejszanie
            System.out.printf("%f %f\n", minimizedBorder.getMinX(), minimizedBorder.getMinY());
            stage.setWidth(minimizedBorder.getWidth());
            stage.setHeight(minimizedBorder.getHeight());
//            stage.show();
            stage.setX(minimizedBorder.getMinX());
            stage.setY(minimizedBorder.getMinY());
            maximized = false;
        } else {
            System.out.printf("%f %f\n", stage.getX(), stage.getY());
            minimizedBorder = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            maximized = true;
        }
    }

    public static void exit() {
        stage.close();
    }

    private static double positionX, positionY;

    public static void changePosition(double x, double y) {
        stage.setX(positionX + x);
        stage.setY(positionY + y);
    }

    public static void setPosition() {
        positionX = stage.getX();
        positionY = stage.getY();
    }

    public static double getWindowWidth() {
        if (scene != null)
            return scene.getWidth();
        return 100.0;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }

}