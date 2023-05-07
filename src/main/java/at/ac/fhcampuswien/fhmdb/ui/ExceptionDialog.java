package at.ac.fhcampuswien.fhmdb.ui;

import javafx.scene.control.Alert;

public class ExceptionDialog
{
    public static void show(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("An error occurred!");
        alert.setHeaderText(e.getMessage());
        alert.setContentText(String.valueOf(e.getCause()));
        alert.showAndWait();
    }
}
