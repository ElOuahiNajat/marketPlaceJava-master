package org.example.logintestjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;

public class GererUsersController {

    // Références aux éléments FXML
    @FXML private ListView<User> usersListView;
    @FXML private TextField txtName, txtEmail, txtRole, txtSearch;
    @FXML private Button btnEditUser, btnDeleteUser, btnSortByName, btnSortByRole;

    // Listes pour gérer les utilisateurs
    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private FilteredList<User> filteredUsers;
    private SortedList<User> sortedUsers;

    // Méthode d'initialisation
    @FXML
    public void initialize() {
        loadUsers(); // Charger les utilisateurs au démarrage

        // Initialisation de la liste filtrée et triée
        filteredUsers = new FilteredList<>(usersList, p -> true);
        sortedUsers = new SortedList<>(filteredUsers);

        // Liaison de la ListView avec la liste triée
        usersListView.setItems(sortedUsers);

        // Listener pour la recherche
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUsers.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return user.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Listener pour la sélection d'un utilisateur
        usersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtName.setText(newValue.getName());
                txtEmail.setText(newValue.getEmail());
                txtRole.setText(newValue.getRole());
            }
        });

        // Listener pour le tri par nom
        btnSortByName.setOnAction(event -> sortUsersByName());

        // Listener pour le tri par rôle
        btnSortByRole.setOnAction(event -> sortUsersByRole());
    }

    // Charger les utilisateurs depuis la base de données
    private void loadUsers() {
        usersList.clear();
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = "SELECT UserID, Nom, Email, Role FROM Users";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userID = resultSet.getInt("UserID");
                String name = resultSet.getString("Nom");
                String email = resultSet.getString("Email");
                String role = resultSet.getString("Role");

                User user = new User(userID, name, email, role);
                usersList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les utilisateurs : " + e.getMessage());
        }
    }

    // Trier les utilisateurs par nom
    private void sortUsersByName() {
        sortedUsers.setComparator(Comparator.comparing(User::getName));
    }

    // Trier les utilisateurs par rôle
    private void sortUsersByRole() {
        sortedUsers.setComparator(Comparator.comparing(User::getRole));
    }

    // Modifier un utilisateur
    @FXML
    private void handleEditUser() {
        User selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur à modifier !");
            return;
        }

        String name = txtName.getText();
        String email = txtEmail.getText();
        String role = txtRole.getText();

        if (name.isEmpty() || email.isEmpty() || role.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis !");
            return;
        }

        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();
        String query = "UPDATE Users SET Nom = ?, Email = ?, Role = ? WHERE UserID = ?";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, role);
            preparedStatement.setInt(4, selectedUser.getUserID());
            preparedStatement.executeUpdate();

            loadUsers(); // Recharger la liste des utilisateurs
            clearFields();
            showAlert("Succès", "Utilisateur modifié avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier l'utilisateur : " + e.getMessage());
        }
    }

    // Supprimer un utilisateur
    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur à supprimer !");
            return;
        }

        // Afficher une boîte de dialogue de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        // Attendre la réponse de l'utilisateur
        ButtonType result = confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);

        // Si l'utilisateur confirme la suppression
        if (result == ButtonType.OK) {
            DatabaseConnection con = new DatabaseConnection();
            Connection conDb = con.getConnection();

            // Supprimer les notifications associées à l'utilisateur
            String deleteNotificationsQuery = "DELETE FROM notifications WHERE UserID = ?";
            try (PreparedStatement deleteNotificationsStmt = conDb.prepareStatement(deleteNotificationsQuery)) {
                deleteNotificationsStmt.setInt(1, selectedUser.getUserID());
                deleteNotificationsStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de supprimer les notifications associées : " + e.getMessage());
                return;
            }

            // Supprimer l'utilisateur
            String deleteUserQuery = "DELETE FROM users WHERE UserID = ?";
            try (PreparedStatement deleteUserStmt = conDb.prepareStatement(deleteUserQuery)) {
                deleteUserStmt.setInt(1, selectedUser.getUserID());
                deleteUserStmt.executeUpdate();

                loadUsers(); // Recharger la liste des utilisateurs
                showAlert("Succès", "Utilisateur supprimé avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de supprimer l'utilisateur : " + e.getMessage());
            }
        } else {
            // Si l'utilisateur annule, ne rien faire
            showAlert("Information", "Suppression annulée.");
        }
    }

    // Effacer les champs du formulaire
    private void clearFields() {
        txtName.clear();
        txtEmail.clear();
        txtRole.clear();
    }

    // Afficher une alerte
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}