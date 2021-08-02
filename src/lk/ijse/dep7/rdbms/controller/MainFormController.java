package lk.ijse.dep7.rdbms.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
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
    public Button btnEdit;
    public Button btnNew;
    public Button btnAdd;

    public void initialize(){
        btnSave.setDefaultButton(true);
        btnSave.setDisable(true);
        btnRemove.setDisable(true);
        btnEdit.setDisable(true);

        tblStudents.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblStudents.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<StudentTM, Button> lastColumn = (TableColumn<StudentTM, Button>) tblStudents.getColumns().get(2);

        lastColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StudentTM, Button>, ObservableValue<Button>>() {
            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<StudentTM, Button> param) {
                return new ObservableValueBase<Button>() {
                    @Override
                    public Button getValue() {
                        Button remove = new Button("Remove");
                        remove.setOnAction(event -> {
                            // TODO: Remove from db

                            if (true) {
                                tblStudents.getItems().remove(param.getValue());
                            }
                        });

                        return remove;
                    }
                };
            }
        });

        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            String id = txtId.getText();
            String name = txtName.getText();

            btnSave.setDisable(!(id.matches("[Ss]\\d{3}") &&
                    name.matches("[A-Za-z ]{3,}")));
        };

        txtId.textProperty().addListener(listener);
        txtName.textProperty().addListener(listener);

        tblStudents.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                txtId.setText(newValue.getId());
                txtName.setText(newValue.getName());
                lstContact.getItems().clear();
                txtId.setDisable(true);
                btnSave.setText("Update");

                //TODO: update contact list using db
            } else {
                btnSave.setText("Save");
            }
        });

        lstContact.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnEdit.setDisable(newValue == null);
            btnRemove.setDisable(newValue == null);
        });
    }

    public void btnNew_OnAction(ActionEvent actionEvent) {
        txtId.clear();
        txtName.clear();
        txtContact.clear();
        txtSearch.clear();
        tblStudents.getSelectionModel().clearSelection();
        lstContact.getItems().clear();
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
                tblStudents.getItems().add(new StudentTM(id, name));
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
            btnAdd.setText("Add");
            txtContact.requestFocus();
        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid contact number");
        }
    }

    public void btnRemove_OnAction(ActionEvent actionEvent) {
        String selectedItem = lstContact.getSelectionModel().getSelectedItem();
        lstContact.getItems().remove(selectedItem);
        btnAdd.setText("Add");
        txtContact.requestFocus();
    }

    public void btnClear_OnAction(ActionEvent actionEvent) {
        int numberOfContacts = lstContact.getItems().size();

        if (numberOfContacts > 0) {
            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.INFORMATION, "Are you sure you want to remove " + numberOfContacts + " contact" + (numberOfContacts > 1 ? "s" : ""), ButtonType.YES, ButtonType.NO).showAndWait();

            if (buttonType.get() == ButtonType.YES) {
                lstContact.getItems().clear();
                btnAdd.setText("Add");
            }
        }
        txtContact.requestFocus();
    }

    public void btnEdit_OnAction(ActionEvent actionEvent) {
        String selectedContact = lstContact.getSelectionModel().getSelectedItem();
        txtContact.setText(selectedContact);
        lstContact.getItems().remove(selectedContact);
        btnAdd.setText("Update");
        txtContact.requestFocus();
    }
}
