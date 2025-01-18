package org.example.logintestjavafx;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty userID;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty role;

    public User(int userID, String name, String email, String role) {
        this.userID = new SimpleIntegerProperty(userID);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.role = new SimpleStringProperty(role);
    }

    // Getters et Setters
    public int getUserID() { return userID.get(); }
    public void setUserID(int userID) { this.userID.set(userID); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }

    // Propriétés JavaFX
    public IntegerProperty userIDProperty() { return userID; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty roleProperty() { return role; }


    @Override
    public String toString() {
        return String.format("🆔 ID: %d | 👤 Nom: %s | 📧 Email: %s | 🛠️ Rôle: %s",
                userID.get(), name.get(), email.get(), role.get());
    }
}