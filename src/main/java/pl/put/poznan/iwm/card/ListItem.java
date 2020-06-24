package pl.put.poznan.iwm.card;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.Period;

public class ListItem {

//    private HBox item;
//
//    public ListItem(String name, LocalDateTime recent, String target) {
//        item = new HBox();
//        item.setOnMouseClicked(e->App.setRoot(target));
//
//    }

    static public void generateItem(VBox list, String name, LocalDate recent, String target) {
        GridPane item = new GridPane();
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.SOMETIMES);
        columnConstraints.setPercentWidth(30.0);
        item.getColumnConstraints().add(columnConstraints);
//        ColumnConstraints columnConstraints0 = new ColumnConstraints();
//        columnConstraints0.setHgrow(Priority.SOMETIMES);
//        columnConstraints0.setPercentWidth(1.0);
//        item.getColumnConstraints().add(columnConstraints0);
        ColumnConstraints columnConstraints1 = new ColumnConstraints();
        columnConstraints1.setHgrow(Priority.SOMETIMES);
        columnConstraints1.setPercentWidth(70.0);
        item.getColumnConstraints().add(columnConstraints1);
        item.setHgap(10.0);
        item.getStyleClass().clear();
        item.getStyleClass().add("item");
        item.setOnMouseClicked(e -> App.setRoot(target));
        item.setAlignment(Pos.CENTER);
//        item.getChildren().add(new Text(name));
        item.addColumn(0, new Text(name));

//        Separator sep = new Separator();
//        sep.setOrientation(Orientation.VERTICAL);
//        item.addColumn(1, sep);

        Period period = Period.between(LocalDate.from(recent), LocalDate.now());
        if (period.getYears() >= 1) {// TODO: polskie odmiany
            item.addColumn(1, new Text(String.format("%d lat", period.getYears())));
        } else if (period.getMonths() >= 1) {
            item.addColumn(1, new Text(String.format("%d miesięcy", period.getMonths())));
        } else {
            item.addColumn(1, new Text(String.format("%d dni", period.getDays())));
        }

        list.getChildren().add(item);
        Separator sepa = new Separator();
        sepa.setHalignment(HPos.CENTER);
        sepa.setValignment(VPos.CENTER);
        list.getChildren().add(sepa);
    }


}
