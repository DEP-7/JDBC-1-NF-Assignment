/*
 * Copyright (c) Dhanushka Chandimal. All rights reserved.
 * Licensed under the MIT License. See License in the project root for license information.
 */

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

import java.sql.*;
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
    private Connection connection;

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

                            try {
                                Statement stmt = connection.createStatement();
                                String sql = "DELETE FROM student WHERE id = '" + param.getValue().getId() + "'";
                                int affectedRows = stmt.executeUpdate(sql);

                                if (affectedRows == 1) {
                                    tblStudents.getItems().remove(param.getValue());
                                    tblStudents.refresh();
                                    btnNew.fire();
                                } else {
                                    new Alert(Alert.AlertType.INFORMATION, "Failed to delete the student, retry").show();
                                    remove.requestFocus();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                new Alert(Alert.AlertType.INFORMATION, "Failed to delete the student, retry").show();
                            }
                        });
                        return remove;
                    }
                };
            }
        });

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dep7", "root", "mysql");
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to connect to the database server").showAndWait();
            e.printStackTrace();
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }));

        try {
            Statement stmt = connection.createStatement();
            ResultSet rst = stmt.executeQuery("SELECT * FROM student");

            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");

                tblStudents.getItems().add(new StudentTM(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
                txtContact.clear();
                txtId.setDisable(true);
                btnSave.setText("Update");

                try {
                    Statement stmt = connection.createStatement();
                    ResultSet rst = stmt.executeQuery("SELECT contact FROM contacts WHERE student_id = '" + newValue.getId() + "'");

                    while (rst.next()) {
                        lstContact.getItems().add(rst.getString("contact"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                btnSave.setText("Save");
            }
        });

        lstContact.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnEdit.setDisable(newValue == null);
            btnRemove.setDisable(newValue == null);
        });

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                String sql = "SELECT * FROM customer WHERE " +
                        "id LIKE '%"+newValue+
                        "%' OR nic LIKE '%"+newValue+
                        "%' OR name LIKE '%"+newValue+
                        "%' OR address LIKE '%"+newValue+"%'";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                tblStudents.getItems().clear();

                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    tblStudents.getItems().add(new StudentTM(id, name));
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,"Failed to fetch the data, try again").show();
                e.printStackTrace();
            }
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

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM student WHERE id = '" + id + "'");
                String sql;

                if (preparedStatement.executeQuery().next()) {
                    new Alert(Alert.AlertType.INFORMATION, "Student id already exist").show();
                    txtId.requestFocus();
                    return;
                }

                sql = "INSERT INTO student VALUES ('%s', '%s')";
                sql = String.format(sql, id, name);
                preparedStatement = connection.prepareStatement(sql);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows == 1) {
                    preparedStatement = connection.prepareStatement("INSERT INTO contacts VALUES ('" + id + "', ?)");

                    for (String contact : contacts) {
                        preparedStatement.setString(1, contact);
                        preparedStatement.executeUpdate();
                    }

                    tblStudents.getItems().add(new StudentTM(id, name));
                    btnNew.fire();
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Failed to save the student").show();
                    btnSave.requestFocus();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save the student, retry").show();
            }
        }else{

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE student SET name = ? WHERE id = ?");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, id);
                int affectedRows = preparedStatement.executeUpdate();
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("DELETE FROM contacts WHERE student_id = '" + id + "'");
                preparedStatement = connection.prepareStatement("INSERT INTO contacts VALUES ('" + id + "', ?)");

                for (String contact : contacts) {
                    preparedStatement.setString(1, contact);
                    affectedRows += preparedStatement.executeUpdate();
                }

                if (affectedRows > 0) {
                    StudentTM selectedStudent = tblStudents.getSelectionModel().getSelectedItem();

                    selectedStudent.setName(name);
                    tblStudents.refresh();
                    btnNew.fire();
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Failed to update the student, retry").show();
                    btnSave.requestFocus();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.INFORMATION, "Failed to update the student, retry").show();
            }
        }
    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
        if (txtContact.getText().matches("0\\d{2}-\\d{7}")) {

            if (lstContact.getItems().contains(txtContact.getText())) {
                new Alert(Alert.AlertType.ERROR, "Contact number exist").show();
                return;
            }
            lstContact.getItems().add(txtContact.getText());
            txtContact.clear();
            btnAdd.setText("Add");
            txtContact.requestFocus();
        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid contact number").show();
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
