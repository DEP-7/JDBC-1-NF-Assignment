package lk.ijse.dep7.rdbms.tm;

import javafx.collections.ObservableList;

import java.io.Serializable;

public class StudentTM implements Serializable {
    private String id;
    private String name;
    private ObservableList<String> contact;

    public StudentTM() {
    }

    public StudentTM(String id, String name, ObservableList<String> contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
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

    public ObservableList<String> getContact() {
        return contact;
    }

    public void setContact(ObservableList<String> contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "StudentTM{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", contact=" + contact +
                '}';
    }
}
