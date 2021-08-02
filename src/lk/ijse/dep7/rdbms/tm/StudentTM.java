/*
 * Copyright (c) Dhanushka Chandimal. All rights reserved.
 * Licensed under the MIT License. See License in the project root for license information.
 */

package lk.ijse.dep7.rdbms.tm;

import javafx.collections.ObservableList;

import java.io.Serializable;

public class StudentTM implements Serializable {
    private String id;
    private String name;

    public StudentTM() {
    }

    public StudentTM(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StudentTM{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
