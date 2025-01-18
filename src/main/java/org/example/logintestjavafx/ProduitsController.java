package org.example.logintestjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProduitsController {
    @FXML
    private ComboBox<String> comboCategories; // Assurez-vous que le fx:id correspond à celui dans le FXML

    @FXML
    private ListView<Produits> produitsListView;

    @FXML
    private ListView<String> notificationsListView;

    @FXML
    private ListView<String> usersListView;

    @FXML
    private ListView<String> categoriesListView;

    @FXML
    private TextField txtNom, txtDescription, txtPrix, txtQuantite, txtImageProduits, txtSearch, txtSearchUsers, txtNomCategorie, txtSearchCategorie;

    @FXML
    private Button btnAjouter, btnModifier, btnSupprimer, btnEffacer, btnImage, btnQuitter, btnOpenChat, btnLogout, btnSearch, btnModifierUser, btnSearchUsers, btnAjouterCategorie, btnSupprimerCategorie, btnSearchCategorie, btnTrierCategories;

    @FXML
    private Label welcomeLabel;

    @FXML
    private FlowPane produitsDisponiblesListView;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab tabHome, tabCommander, tabGestion, tabNotifications, tabDashboard, tabGererUsers;

    @FXML
    private Label lblNombreUsers, lblNombreAdmins, lblNombreProduits;

    @FXML
    private ComboBox<String> comboTri, comboTriUsers, comboTriCategories;

    @FXML
    private Button btnTrier, btnTrierUsers;

    @FXML
    private Tab tabGererCategories; // Assurez-vous que le nom correspond à l'ID dans le fichier FXML
    @FXML
    private void handleGererCategories() {
        mainTabPane.getSelectionModel().select(tabGererCategories);
    }

    private int userId;
    private String userName;

    public void setUserId(int userId) {
        this.userId = userId;
        loadUserData();
    }

    private void loadUserData() {
        welcomeLabel.setText("Welcome, User ID: " + userId);
    }

    public void setUserSession(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        welcomeLabel.setText("Welcome, " + userName);
        loadProduits();
        loadNotifications();
    }

    @FXML
    public void initialize() {
        comboTri.setItems(FXCollections.observableArrayList(
                "Nom (A-Z)", "Nom (Z-A)", "Prix (Croissant)", "Prix (Décroissant)"
        ));

        comboTriUsers.setItems(FXCollections.observableArrayList(
                "Nom (A-Z)", "Nom (Z-A)", "Rôle (A-Z)", "Rôle (Z-A)"
        ));

        comboTriCategories.setItems(FXCollections.observableArrayList(
                "Nom (A-Z)", "Nom (Z-A)"
        ));

        loadProduits();
        loadProduitsDisponibles();
        loadNotifications();
        loadDashboardData();
        loadCategories();

        btnAjouter.setOnAction(e -> ajouterProduit());
        btnModifier.setOnAction(e -> modifierProduit());
        btnSupprimer.setOnAction(e -> supprimerProduit());
        btnEffacer.setOnAction(e -> effacerChamps());
        btnImage.setOnAction(e -> handleImageSelection());
        btnQuitter.setOnAction(e -> handleQuitter());
        btnOpenChat.setOnAction(e -> handleOpenChat());
        btnLogout.setOnAction(e -> handleLogout());
        btnTrier.setOnAction(e -> trierProduits());
        btnSearch.setOnAction(e -> handleSearch());
        btnTrierUsers.setOnAction(e -> trierUsers());
        btnSearchUsers.setOnAction(e -> handleSearchUsers());
        btnAjouterCategorie.setOnAction(e -> ajouterCategorie());
        btnSupprimerCategorie.setOnAction(e -> supprimerCategorie());
        btnSearchCategorie.setOnAction(e -> handleSearchCategorie());
        btnTrierCategories.setOnAction(e -> trierCategories());

        produitsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtNom.setText(newValue.getNom());
                txtDescription.setText(newValue.getDescription());
                txtPrix.setText(String.valueOf(newValue.getPrix()));
                txtQuantite.setText(String.valueOf(newValue.getQuantiteStock()));
                txtImageProduits.setText(newValue.getImageProduits());
            }
        });

        tabGererUsers.setOnSelectionChanged(event -> {
            if (tabGererUsers.isSelected()) {
                loadUsers();
            }
        });

        btnModifierUser.setOnAction(e -> handleModifierUser());
    }

    private void loadCategoriesIntoComboBox() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = "SELECT NomCategorie FROM categorie";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String nomCategorie = resultSet.getString("NomCategorie");
                categories.add(nomCategorie);
            }

            // Ajouter les catégories au ComboBox
            comboCategories.setItems(categories);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les catégories : " + e.getMessage());
        }
    }

    private void loadCategories() {
        categoriesListView.getItems().clear();
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = "SELECT CategorieID, NomCategorie FROM categorie";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int categorieID = resultSet.getInt("CategorieID");
                String nomCategorie = resultSet.getString("NomCategorie");

                String item = "ID: " + categorieID + "    Nom: " + nomCategorie;
                categoriesListView.getItems().add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les catégories : " + e.getMessage());
        }
    }

    private void ajouterCategorie() {
        String nomCategorie = txtNomCategorie.getText().trim();

        if (nomCategorie.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un nom de catégorie !");
            return;
        }

        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();
        String query = "INSERT INTO categorie (NomCategorie) VALUES (?)";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setString(1, nomCategorie);
            preparedStatement.executeUpdate();

            loadCategories();
            txtNomCategorie.clear();
            showAlert("Succès", "Catégorie ajoutée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter la catégorie : " + e.getMessage());
        }
    }

    @FXML
    private void handleModifierCategorie() {
        String selectedCategorie = categoriesListView.getSelectionModel().getSelectedItem();

        if (selectedCategorie == null) {
            showAlert("Erreur", "Veuillez sélectionner une catégorie à modifier !");
            return;
        }

        int categorieID = Integer.parseInt(selectedCategorie.split("ID:")[1].trim().split(" ")[0]);
        String nomCategorie = selectedCategorie.split("Nom:")[1].trim();

        // Ouvrir une boîte de dialogue pour modifier la catégorie
        TextInputDialog dialog = new TextInputDialog(nomCategorie);
        dialog.setTitle("Modifier la catégorie");
        dialog.setHeaderText("Modifier le nom de la catégorie");
        dialog.setContentText("Nouveau nom :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newNomCategorie -> {
            if (newNomCategorie.isEmpty()) {
                showAlert("Erreur", "Le nom de la catégorie ne peut pas être vide !");
                return;
            }

            DatabaseConnection con = new DatabaseConnection();
            Connection conDb = con.getConnection();
            String query = "UPDATE categorie SET NomCategorie = ? WHERE CategorieID = ?";

            try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
                preparedStatement.setString(1, newNomCategorie);
                preparedStatement.setInt(2, categorieID);
                preparedStatement.executeUpdate();

                loadCategories();
                showAlert("Succès", "Catégorie modifiée avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de modifier la catégorie : " + e.getMessage());
            }
        });
    }

    private void supprimerCategorie() {
        String selectedCategorie = categoriesListView.getSelectionModel().getSelectedItem();

        if (selectedCategorie == null) {
            showAlert("Erreur", "Veuillez sélectionner une catégorie à supprimer !");
            return;
        }

        int categorieID = Integer.parseInt(selectedCategorie.split("ID:")[1].trim().split(" ")[0]);

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette catégorie ?");
        confirmationAlert.setContentText("Catégorie : " + selectedCategorie);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DatabaseConnection con = new DatabaseConnection();
                Connection conDb = con.getConnection();
                String query = "DELETE FROM categorie WHERE CategorieID = ?";

                try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
                    preparedStatement.setInt(1, categorieID);
                    preparedStatement.executeUpdate();

                    loadCategories();
                    showAlert("Succès", "Catégorie supprimée avec succès !");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer la catégorie : " + e.getMessage());
                }
            }
        });
    }

    private void handleSearchCategorie() {
        String searchText = txtSearchCategorie.getText().toLowerCase();
        ObservableList<String> filteredCategories = FXCollections.observableArrayList();

        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = "SELECT CategorieID, NomCategorie FROM categorie WHERE LOWER(NomCategorie) LIKE ?";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchText + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int categorieID = resultSet.getInt("CategorieID");
                String nomCategorie = resultSet.getString("NomCategorie");

                String item = "ID: " + categorieID + "    Nom: " + nomCategorie;
                filteredCategories.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les catégories : " + e.getMessage());
        }

        categoriesListView.setItems(filteredCategories);
    }

    private void trierCategories() {
        String critereTri = comboTriCategories.getValue();
        if (critereTri == null) {
            showAlert("Erreur", "Veuillez sélectionner un critère de tri.");
            return;
        }

        ObservableList<String> categories = categoriesListView.getItems();

        switch (critereTri) {
            case "Nom (A-Z)":
                categories.sort(Comparator.comparing(categorie -> categorie.split("Nom:")[1].trim(), String.CASE_INSENSITIVE_ORDER));
                break;
            case "Nom (Z-A)":
                categories.sort(Comparator.<String, String>comparing(categorie -> categorie.split("Nom:")[1].trim(), String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            default:
                showAlert("Erreur", "Critère de tri non reconnu.");
                return;
        }

        categoriesListView.setItems(categories);
        showAlert("Succès", "Catégories triées avec succès !");
    }

    private void supprimerProduit() {
        Produits produit = produitsListView.getSelectionModel().getSelectedItem();
        if (produit == null) {
            showAlert("Erreur", "Veuillez sélectionner un produit à supprimer !");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce produit ?");
        confirmationAlert.setContentText("ID : " + produit.getProduitID() + "\nNom : " + produit.getNom());

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DatabaseConnection con = new DatabaseConnection();
                Connection conDb = con.getConnection();
                String query = "DELETE FROM Produits WHERE produitID = ?";

                try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
                    preparedStatement.setInt(1, produit.getProduitID());
                    preparedStatement.executeUpdate();

                    loadProduits();
                    loadProduitsDisponibles();
                    showAlert("Succès", "Produit supprimé avec succès !");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer le produit : " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleImageSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Stage stage = (Stage) btnImage.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            txtImageProduits.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleSearchUsers() {
        String searchText = txtSearchUsers.getText().toLowerCase();
        ObservableList<String> filteredUsers = FXCollections.observableArrayList();

        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = "SELECT UserID, Nom, Role FROM Users WHERE LOWER(Nom) LIKE ?";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchText + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("UserID");
                String nom = resultSet.getString("Nom");
                String role = resultSet.getString("Role");

                String userInfo = String.format("ID: %d | Nom: %s | Rôle: %s", userId, nom, role);
                filteredUsers.add(userInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les utilisateurs : " + e.getMessage());
        }

        usersListView.setItems(filteredUsers);
    }

    private void trierUsers() {
        String critereTri = comboTriUsers.getValue();
        if (critereTri == null) {
            showAlert("Erreur", "Veuillez sélectionner un critère de tri.");
            return;
        }

        ObservableList<String> users = usersListView.getItems();

        switch (critereTri) {
            case "Nom (A-Z)":
                users.sort(Comparator.comparing(user -> user.split("\\|")[1].split(":")[1].trim(), String.CASE_INSENSITIVE_ORDER));
                break;
            case "Nom (Z-A)":
                users.sort(Comparator.<String, String>comparing(user -> user.split("\\|")[1].split(":")[1].trim(), String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Rôle (A-Z)":
                users.sort(Comparator.comparing(user -> user.split("\\|")[2].split(":")[1].trim(), String.CASE_INSENSITIVE_ORDER));
                break;
            case "Rôle (Z-A)":
                users.sort(Comparator.<String, String>comparing(user -> user.split("\\|")[2].split(":")[1].trim(), String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            default:
                showAlert("Erreur", "Critère de tri non reconnu.");
                return;
        }

        usersListView.setItems(users);
        showAlert("Succès", "Utilisateurs triés avec succès !");
    }

    @FXML
    private void handleModifierUser() {
        String selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur à modifier !");
            return;
        }

        int userId = Integer.parseInt(selectedUser.split("\\|")[0].replace("ID:", "").trim());
        openModifierUserDialog(userId);
    }

    private void openModifierUserDialog(int userId) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'utilisateur");
        dialog.setHeaderText("Modifier les informations de l'utilisateur");

        ButtonType modifierButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(modifierButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        TextField roleField = new TextField();

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Rôle:"), 0, 1);
        grid.add(roleField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();
        String query = "SELECT Nom, Role FROM Users WHERE UserID = ?";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                nomField.setText(resultSet.getString("Nom"));
                roleField.setText(resultSet.getString("Role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les informations de l'utilisateur : " + e.getMessage());
            return;
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == modifierButtonType) {
                return new Pair<>(nomField.getText(), roleField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(nomRole -> {
            String nom = nomRole.getKey();
            String role = nomRole.getValue();

            String updateQuery = "UPDATE Users SET Nom = ?, Role = ? WHERE UserID = ?";
            try (PreparedStatement preparedStatement = conDb.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, nom);
                preparedStatement.setString(2, role);
                preparedStatement.setInt(3, userId);
                preparedStatement.executeUpdate();
                showAlert("Succès", "Utilisateur modifié avec succès !");
                loadUsers();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de modifier l'utilisateur : " + e.getMessage());
            }
        });
    }

    private void loadUsers() {
        usersListView.getItems().clear();
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = "SELECT UserID, Nom, Role FROM Users";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("UserID");
                String nom = resultSet.getString("Nom");
                String role = resultSet.getString("Role");

                String userInfo = String.format("ID: %d | Nom: %s | Rôle: %s", userId, nom, role);
                usersListView.getItems().add(userInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les utilisateurs : " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimerUser() {
        String selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur à supprimer !");
            return;
        }

        int userId = Integer.parseInt(selectedUser.split("\\|")[0].replace("ID:", "").trim());

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DatabaseConnection con = new DatabaseConnection();
                Connection conDb = con.getConnection();
                String query = "DELETE FROM Users WHERE UserID = ?";

                try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
                    preparedStatement.setInt(1, userId);
                    preparedStatement.executeUpdate();
                    showAlert("Succès", "Utilisateur supprimé avec succès !");
                    loadUsers();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer l'utilisateur : " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().toLowerCase();
        produitsDisponiblesListView.getChildren().clear();

        loadProduitsDisponibles();

        List<VBox> filteredProducts = produitsDisponiblesListView.getChildren().stream()
                .filter(node -> node instanceof VBox)
                .map(node -> (VBox) node)
                .filter(vbox -> {
                    Label productNameLabel = (Label) vbox.getChildren().get(1);
                    String productName = productNameLabel.getText().toLowerCase();
                    return productName.contains(searchText);
                })
                .collect(Collectors.toList());

        produitsDisponiblesListView.getChildren().clear();
        produitsDisponiblesListView.getChildren().addAll(filteredProducts);
    }

    private void trierProduits() {
        String critereTri = comboTri.getValue();
        if (critereTri == null) {
            showAlert("Erreur", "Veuillez sélectionner un critère de tri.");
            return;
        }

        ObservableList<Produits> produits = produitsListView.getItems();

        switch (critereTri) {
            case "Nom (A-Z)":
                produits.sort(Comparator.comparing(Produits::getNom, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Nom (Z-A)":
                produits.sort(Comparator.comparing(Produits::getNom, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Prix (Croissant)":
                produits.sort(Comparator.comparingDouble(Produits::getPrix));
                break;
            case "Prix (Décroissant)":
                produits.sort(Comparator.comparingDouble(Produits::getPrix).reversed());
                break;
            default:
                showAlert("Erreur", "Critère de tri non reconnu.");
                return;
        }

        produitsListView.setItems(produits);
        showAlert("Succès", "Produits triés avec succès !");
    }

    @FXML
    private void handleHome() {
        mainTabPane.getSelectionModel().select(tabHome);
    }

    @FXML
    private void handleCommander() {
        mainTabPane.getSelectionModel().select(tabCommander);
    }

    @FXML
    private void handleGestion() {
        mainTabPane.getSelectionModel().select(tabGestion);
    }

    @FXML
    private void handleNotifications() {
        mainTabPane.getSelectionModel().select(tabNotifications);
    }

    @FXML
    private void handleDashboard() {
        mainTabPane.getSelectionModel().select(tabDashboard);
        loadDashboardData();
    }

    @FXML
    private void handleGererUsers() {
        mainTabPane.getSelectionModel().select(tabGererUsers);
    }

    private void loadDashboardData() {
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String queryUsers = "SELECT COUNT(*) AS totalUsers FROM Users";
        String queryProduits = "SELECT COUNT(*) AS totalProduits FROM Produits";
        String queryAdmins = "SELECT COUNT(*) AS totalAdmins FROM Users WHERE Role = 'Admin'";

        try {
            PreparedStatement stmtUsers = conDb.prepareStatement(queryUsers);
            ResultSet rsUsers = stmtUsers.executeQuery();
            if (rsUsers.next()) {
                int totalUsers = rsUsers.getInt("totalUsers");
                lblNombreUsers.setText(String.valueOf(totalUsers));
            }

            PreparedStatement stmtProduits = conDb.prepareStatement(queryProduits);
            ResultSet rsProduits = stmtProduits.executeQuery();
            if (rsProduits.next()) {
                int totalProduits = rsProduits.getInt("totalProduits");
                lblNombreProduits.setText(String.valueOf(totalProduits));
            }

            PreparedStatement stmtAdmins = conDb.prepareStatement(queryAdmins);
            ResultSet rsAdmins = stmtAdmins.executeQuery();
            if (rsAdmins.next()) {
                int totalAdmins = rsAdmins.getInt("totalAdmins");
                lblNombreAdmins.setText(String.valueOf(totalAdmins));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les données du dashboard : " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Parent root = loader.load();

            chat chatController = loader.getController();
            chatController.setCurrentUserId(userId);

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat");
            chatStage.setScene(new Scene(root));
            chatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the chat window: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) btnLogout.getScene().getWindow();

            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Login");

            userId = 0;
            userName = null;

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de connexion.");
        }
    }

    private void loadProduits() {
        produitsListView.getItems().clear();
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = "SELECT * FROM Produits";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int produitID = resultSet.getInt("ProduitID");
                String nom = resultSet.getString("Nom");
                String description = resultSet.getString("Description");
                double prix = resultSet.getDouble("Prix");
                int quantiteStock = resultSet.getInt("QuantiteStock");
                String imageProduits = resultSet.getString("ImageProduits");

                Produits produit = new Produits(produitID, nom, description, prix, quantiteStock, imageProduits);
                produitsListView.getItems().add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les produits : " + e.getMessage());
        }
    }

    private void loadProduitsDisponibles() {
        produitsDisponiblesListView.getChildren().clear();
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = """
    SELECT p.nom AS produitNom, p.description, p.prix, p.imageProduits, u.Nom AS userNom, u.UserID AS proprietaireID
    FROM Produits p
    JOIN Users u ON p.UserID = u.UserID
    """;

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String produitNom = resultSet.getString("produitNom");
                String description = resultSet.getString("description");
                double prix = resultSet.getDouble("prix");
                String imagePath = resultSet.getString("imageProduits");
                String userNom = resultSet.getString("userNom");
                int proprietaireID = resultSet.getInt("proprietaireID");

                VBox carteProduit = new VBox(10);
                carteProduit.setAlignment(Pos.CENTER);
                carteProduit.setStyle("""
    -fx-background-color: #ffffff;
    -fx-border-color: #2C3E50;
    -fx-border-width: 2px;
    -fx-border-radius: 10;
    -fx-background-radius: 10;
    -fx-padding: 15;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
    -fx-background-insets: 0;
    -fx-cursor: hand;
""");
                carteProduit.setPrefSize(200, 250);

                ImageView imageView = new ImageView();
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        imageView.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        imageView.setImage(new Image("file:placeholder.png"));
                    }
                }

                Label labelNom = new Label(produitNom);
                labelNom.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                Label labelPrix = new Label("Prix : " + prix + "€");
                labelPrix.setStyle("-fx-font-size: 12px;");

                Label labelUser = new Label("Ajouté par : " + userNom);
                labelUser.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

                // Commenting out the "Demander" button and its action
                /*
                Button btnCommander = new Button("Demander");
                btnCommander.setStyle("""
    -fx-background-color: #2C3E50;
    -fx-text-fill: white;
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
    -fx-padding: 10 20;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);
    -fx-cursor: hand;
    -fx-background-insets: 0;
    -fx-border-color: transparent;
""");
                btnCommander.setOnAction(e -> commanderProduit(produitNom, proprietaireID, userId));

                carteProduit.getChildren().addAll(imageView, labelNom, labelPrix, labelUser, btnCommander);
                */
                carteProduit.getChildren().addAll(imageView, labelNom, labelPrix, labelUser);
                produitsDisponiblesListView.getChildren().add(carteProduit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les produits disponibles : " + e.getMessage());
        }
    }

    // Commenting out the commanderProduit method
    /*
    private void commanderProduit(String produitNom, int proprietaireID, int acheteurID) {
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();
        String acheteurNom = "";
        String acheteurTelephone = "";

        String queryAcheteur = "SELECT Nom, Telephone FROM Users WHERE UserID = ?";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(queryAcheteur)) {
            preparedStatement.setInt(1, acheteurID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                acheteurNom = resultSet.getString("Nom");
                acheteurTelephone = resultSet.getString("Telephone");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de récupérer les informations de l'acheteur : " + e.getMessage());
            return;
        }

        String message = "Votre produit \"" + produitNom + "\" a été commandé par : " + acheteurNom +
                " (Tel : " + acheteurTelephone + ")";

        String queryNotification = "INSERT INTO Notifications (UserID, Message) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(queryNotification)) {
            preparedStatement.setInt(1, proprietaireID);
            preparedStatement.setString(2, message);
            preparedStatement.executeUpdate();
            showAlert("Succès", "Produit commandé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'envoyer la notification : " + e.getMessage());
        }
    }
    */

    private void loadNotifications() {
        notificationsListView.getItems().clear(); // Efface les notifications existantes dans la ListView

        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        // Requête SQL pour récupérer toutes les notifications
        String query = "SELECT Message, Date FROM notifications ORDER BY Date DESC";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            // Parcourir les résultats de la requête
            while (resultSet.next()) {
                String message = resultSet.getString("Message"); // Récupérer le message
                String date = resultSet.getTimestamp("Date").toString(); // Récupérer la date

                // Ajouter la notification formatée à la ListView
                notificationsListView.getItems().add(date + " - " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les notifications : " + e.getMessage());
        }
    }
    private void ajouterProduit() {
        String nom = txtNom.getText();
        String description = txtDescription.getText();
        String imageProduits = txtImageProduits.getText();
        double prix;
        int quantite;

        try {
            prix = Double.parseDouble(txtPrix.getText());
            quantite = Integer.parseInt(txtQuantite.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix et Quantité doivent être des valeurs numériques !");
            return;
        }

        if (nom.isEmpty() || description.isEmpty() || imageProduits.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis !");
            return;
        }

        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();
        String query = "INSERT INTO Produits (Nom, Description, Prix, QuantiteStock, ImageProduits, UserID) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, prix);
            preparedStatement.setInt(4, quantite);
            preparedStatement.setString(5, imageProduits);
            preparedStatement.setInt(6, userId);
            preparedStatement.executeUpdate();

            loadProduits();
            effacerChamps();
            showAlert("Succès", "Produit ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter le produit : " + e.getMessage());
        }
    }

    private void modifierProduit() {
        Produits produit = produitsListView.getSelectionModel().getSelectedItem();
        if (produit == null) {
            showAlert("Erreur", "Veuillez sélectionner un produit à modifier !");
            return;
        }

        String nom = txtNom.getText();
        String description = txtDescription.getText();
        String imageProduits = txtImageProduits.getText();
        double prix;
        int quantite;

        try {
            prix = Double.parseDouble(txtPrix.getText());
            quantite = Integer.parseInt(txtQuantite.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix et Quantité doivent être des valeurs numériques !");
            return;
        }

        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();
        String query = "UPDATE Produits SET nom = ?, description = ?, prix = ?, quantiteStock = ?, imageProduits = ? WHERE produitID = ?";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, prix);
            preparedStatement.setInt(4, quantite);
            preparedStatement.setString(5, imageProduits);
            preparedStatement.setInt(6, produit.getProduitID());
            preparedStatement.executeUpdate();

            loadProduits();
            loadProduitsDisponibles();
            effacerChamps();
            showAlert("Succès", "Produit modifié avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier le produit : " + e.getMessage());
        }
    }

    private void effacerChamps() {
        txtNom.clear();
        txtDescription.clear();
        txtPrix.clear();
        txtQuantite.clear();
        txtImageProduits.clear();
    }

    @FXML
    private void handleQuitter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) btnQuitter.getScene().getWindow();

            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Login");

            userId = 0;
            userName = null;

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de connexion.");
        }
    }
}