package pl.put.poznan.iwm.card;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TitleTopController {

    @FXML
    public Button minimize;
    @FXML
    public Button maximize;
    @FXML
    public Button exit;

    private double x, y;

    @FXML
    public void startDrag(MouseEvent mouseEvent) {
//        System.out.println("wciskam");
        x = mouseEvent.getScreenX();
        y = mouseEvent.getScreenY();
    }

    @FXML
    public void moveWindow(MouseEvent mouseEvent) {
//        System.out.println("porusza");
//        if(mouseEvent.getButton() == MouseButton.PRIMARY) {
//            System.out.println("przeciąga");
            App.changePosition(mouseEvent.getScreenX() - x, mouseEvent.getScreenY() - y);
//            x = mouseEvent.getScreenX();
//            y = mouseEvent.getScreenY();
//        }
    }

    @FXML
    public void stopDrag(MouseEvent mouseEvent) {
//        System.out.println("odpuszczam");
        App.setPosition();
    }

    @FXML
    public void minimizeClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
            App.minimize();
    }

    @FXML
    public void maximizeClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
            App.maximize();
    }

    @FXML
    public void exitClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
            App.exit();
    }

    @FXML
    public void minimizeFocus(MouseEvent mouseEvent) {
        minimize.setStyle("-fx-background-color: dodgerblue");
    }

    @FXML
    public void minimizeRefocus(MouseEvent mouseEvent) {
        minimize.setStyle("-fx-background-color: transparent");
    }

    @FXML
    public void maximizeFocus(MouseEvent mouseEvent) {
        maximize.setStyle("-fx-background-color: dodgerblue");
    }

    @FXML
    public void maximizeRefocus(MouseEvent mouseEvent) {
        maximize.setStyle("-fx-background-color: transparent");
    }

    @FXML
    public void exitFocus(MouseEvent mouseEvent) {
        exit.setStyle("-fx-background-color: red");
    }

    @FXML
    public void exitRefocus(MouseEvent mouseEvent) {
        exit.setStyle("-fx-background-color: brown");
    }
}
