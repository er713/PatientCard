package pl.put.poznan.iwm.card;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.put.poznan.iwm.fhir.PatientData;
import pl.put.poznan.iwm.fhir.PatientMedicationElement;

import java.time.LocalDate;
import java.time.Period;

public abstract class ListItem {

//    private HBox item;
//
//    public ListItem(String name, LocalDateTime recent, String target) {
//        item = new HBox();
//        item.setOnMouseClicked(e->App.setRoot(target));
//
//    }

    static public void generateItem(VBox list, String name, LocalDate recent, String target, PatientData patientData) {
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
        item.setOnMouseClicked(e -> App.setRoot(target, patientData));
        item.setAlignment(Pos.CENTER);
//        item.getChildren().add(new Text(name));
        item.addColumn(0, new Text(name));

//        Separator sep = new Separator();
//        sep.setOrientation(Orientation.VERTICAL);
//        item.addColumn(1, sep);

        if (patientData.birthDate() != null) {
            if (patientData.deathDate() != null) {
                Period period = Period.between(patientData.birthDate(), patientData.deathDate());
                item.addColumn(1, new Text(String.format("death at age of %d years", period.getYears())));
            } else {
                Period period = Period.between(LocalDate.from(recent), LocalDate.now());
                if (period.getYears() >= 1) {
                    item.addColumn(1, new Text(String.format("%d years", period.getYears())));
                } else if (period.getMonths() >= 1) {
                    item.addColumn(1, new Text(String.format("%d months", period.getMonths())));
                } else {
                    item.addColumn(1, new Text(String.format("%d days", period.getDays())));
                }
            }
        } else item.addColumn(1, new Text("not known"));


        list.getChildren().add(item);
        Separator sepa = new Separator();
        sepa.setHalignment(HPos.CENTER);
        sepa.setValignment(VPos.CENTER);
        list.getChildren().add(sepa);
    }

    static public void generateDate(@NotNull VBox list, String date) {
        var hbox = new HBox();
        var text = new Text(date);
        hbox.setAlignment(Pos.CENTER);
        hbox.getStyleClass().add("date");
        hbox.setPrefWidth(list.getWidth());
        hbox.setPrefHeight(25.0);
        hbox.getChildren().add(text);
        list.getChildren().add(hbox);
    }

    static public void generateHistory(@NotNull VBox list, String hist, @Nullable String detail) {
        var hbox = new HBox();
        var text = new Text(hist);
        hbox.setSpacing(20.0);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().add(text);
        hbox.getStyleClass().add("bad");
        hbox.setPrefWidth(list.getWidth());

        if (detail != null) hbox.getChildren().add(new Text(detail));
        list.getChildren().add(hbox);
    }

    static public void generateHistory(@NotNull VBox list, @NotNull PatientMedicationElement med){
        var hbox = new HBox();
        var text = new Text(med.getTitle());
        var tooltip = new Tooltip(
                String.format("%s\nDosage: %s\nPriority: %s\nStatus: %s\nNotes: %s\n",
                        notNull(med.getDetail()), notNull(med.getDosage()), notNull(med.getPriority()),
                        notNull(med.getStatus()), notNull(med.getNote()))
        );
        Tooltip.install(hbox, tooltip);

        hbox.setSpacing(20.0);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().add(text);
        hbox.getStyleClass().add("bad");
        hbox.setPrefWidth(list.getWidth());

//        if (detail != null) hbox.getChildren().add(new Text(detail));
        list.getChildren().add(hbox);
    }

    static private String notNull(@Nullable String info){
        if(info==null) return "No information";
        return info;
    }
}
