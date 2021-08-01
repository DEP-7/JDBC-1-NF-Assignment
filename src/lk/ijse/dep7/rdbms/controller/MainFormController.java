package lk.ijse.dep7.rdbms.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import lk.ijse.dep7.rdbms.tm.StudentTM;

import java.util.Optional;

public class MainFormController {
    public TableView<StudentTM> tblStudents;
    public ListView<String> lstContact;
    public TextField txtContact;
    public TextField txtSearch;
    public TextField txtName;
    public TextField txtId;
    public Button btnRemove;
    public Button btnSave;
    public Button btnNew;

    public void initialize(){
    }

    public void btnNew_OnAction(ActionEvent actionEvent) {
        txtId.clear();
        txtName.clear();
        txtContact.clear();
        txtSearch.clear();
        tblStudents.getSelectionModel().clearSelection();
        txtId.setDisable(false);
        txtId.requestFocus();
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        String id = txtId.getText();
        String name = txtName.getText();
        ObservableList<String> contacts = lstContact.getItems();

        if (btnSave.getText().equals("Save")) {

            //TODO: save in db

            if (true) {
                tblStudents.getItems().add(new StudentTM(id, name, contacts));
                btnNew.fire();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Failed to save the student").show();
                btnSave.requestFocus();
            }
        }else{

            //TODO: update db

            if (true) {
                StudentTM selectedStudent = tblStudents.getSelectionModel().getSelectedItem();

                selectedStudent.setName(name);
                selectedStudent.setContact(contacts);
                tblStudents.refresh();
                btnNew.fire();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Failed to update the student, retry").show();
                btnSave.requestFocus();
            }
        }
    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
        if (txtContact.getText().matches("0\\d{2}-\\d{7}")) {
            lstContact.getItems().add(txtContact.getText());
            txtContact.clear();
        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid contact number");
        }
    }

    public void btnRemove_OnAction(ActionEvent actionEvent) {
        String selectedItem = lstContact.getSelectionModel().getSelectedItem();
        lstContact.getItems().remove(selectedItem);
    }

    public void btnClear_OnAction(ActionEvent actionEvent) {
        int numberOfContacts = lstContact.getItems().size();

        if (numberOfContacts > 0) {
            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.INFORMATION, "Are you sure you want to remove " + numberOfContacts + " contact" + (numberOfContacts > 1 ? "s" : ""), ButtonType.YES, ButtonType.NO).showAndWait();

            if (buttonType.get() == ButtonType.YES) {
                lstContact.getItems().clear();
            }
        }
    }
}
