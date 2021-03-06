/*
 * Copyright (c) Dhanushka Chandimal. All rights reserved.
 * Licensed under the MIT License. See License in the project root for license information.
 */

package lk.ijse.dep7.rdbms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("view/MainForm.fxml"));
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Student Management System");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
