package org.example.logintestjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardController {

    @FXML
    private Label welcomeLabel; // Label pour afficher "Welcome, [username] (User ID: [userId])"

    @FXML
    private Label nombreProduitsCommandesLabel; // Label pour afficher le nombre de produits commandés

    @FXML
    private FlowPane produitsDisponiblesListView;

    @FXML
    private FlowPane produitsCommandesListView;

    @FXML
    private VBox produitsDisponiblesSection;

    @FXML
    private VBox produitsCommandesSection;

    @FXML
    private VBox homeSection;

    @FXML
    private TextField txtSearch;

    private int userId;
    private String userName;

    private List<VBox> allProducts = new ArrayList<>();

    // Méthode pour initialiser la session utilisateur
    public void setUserSession(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        welcomeLabel.setText("Welcome, " + userName + " (User ID: " + userId + ")");

        // Charger le nombre de produits commandés
        int nombreProduitsCommandes = getNombreProduitsCommandes();
        nombreProduitsCommandesLabel.setText("Vous avez commandé " +nombreProduitsCommandes + " produits");

        loadProduitsDisponibles(); // Charger les produits disponibles
    }

    // Méthode pour gérer le bouton Home
    @FXML
    private void handleHome() {
        produitsDisponiblesSection.setVisible(false);
        produitsCommandesSection.setVisible(false);
        homeSection.setVisible(true);

        // Mettre à jour le nombre de produits commandés
        int nombreProduitsCommandes = getNombreProduitsCommandes();
        nombreProduitsCommandesLabel.setText(nombreProduitsCommandes + " produits commandés");
    }

    // Méthode pour gérer le bouton Commander Produit
    @FXML
    private void handleCommander() {
        produitsDisponiblesSection.setVisible(true);
        produitsCommandesSection.setVisible(false);
        homeSection.setVisible(false);
        loadProduitsDisponibles(); // Recharger les produits disponibles
    }

    // Méthode pour gérer le bouton Consulter les produits commandés
    @FXML
    private void handleConsulterCommandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("commandes.fxml"));
            Parent root = loader.load();

            CommandesController commandesController = loader.getController();
            commandesController.setUserId(userId);

            Scene scene = new Scene(root, 800, 600);
            Stage stage = new Stage();
            stage.setTitle("Produits Commandés");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre des produits commandés.");
        }
    }

    // Méthode pour gérer le bouton Logout
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
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

    // Méthode pour gérer la recherche
    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        produitsDisponiblesListView.getChildren().clear();

        if (searchText.isEmpty()) {
            produitsDisponiblesListView.getChildren().addAll(allProducts);
        } else {
            for (VBox productCard : allProducts) {
                Label productNameLabel = (Label) productCard.getChildren().get(1);
                String productName = productNameLabel.getText().toLowerCase();
                if (productName.contains(searchText)) {
                    produitsDisponiblesListView.getChildren().add(productCard);
                }
            }
        }
    }

    // Méthode pour charger les produits disponibles
    private void loadProduitsDisponibles() {
        produitsDisponiblesListView.getChildren().clear();
        allProducts.clear();
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = """
            SELECT p.nom AS produitNom, p.description, p.prix, p.imageProduits, u.Nom AS userNom, u.UserID AS proprietaireID
            FROM Produits p
            JOIN Users u ON p.UserID = u.UserID
            WHERE p.UserID != ?
        """;

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

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
                    -fx-cursor: hand;
                """);
                carteProduit.setPrefSize(250, 300);

                ImageView imageView = new ImageView();
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                if (imagePath != null && !imagePath.isEmpty()) {
                    imageView.setImage(new Image("file:" + imagePath));
                } else {
                    imageView.setImage(new Image("file:placeholder.png"));
                }

                Label labelNom = new Label(produitNom);
                labelNom.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                Label labelPrix = new Label("Prix : " + prix + "€");
                labelPrix.setStyle("-fx-font-size: 12px;");

                Label labelUser = new Label("Vendu par : " + userNom);
                labelUser.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

                Button btnCommander = new Button("Commander");
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
                """);
                btnCommander.setOnAction(e -> commanderProduit(produitNom, proprietaireID, userId));

                carteProduit.getChildren().addAll(imageView, labelNom, labelPrix, labelUser, btnCommander);
                produitsDisponiblesListView.getChildren().add(carteProduit);
                allProducts.add(carteProduit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les produits disponibles : " + e.getMessage());
        }
    }

    // Méthode pour commander un produit
    private void commanderProduit(String produitNom, int proprietaireID, int acheteurID) {
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String queryProduit = "SELECT ProduitID, prix FROM Produits WHERE nom = ?";
        int produitID = -1;
        double prix = 0;

        try (PreparedStatement preparedStatement = conDb.prepareStatement(queryProduit)) {
            preparedStatement.setString(1, produitNom);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                produitID = resultSet.getInt("ProduitID");
                prix = resultSet.getDouble("prix");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de récupérer l'ID ou le prix du produit : " + e.getMessage());
            return;
        }

        int quantite = 1;
        double total = prix * quantite;

        String queryCommande = "INSERT INTO commandes (UserID, ProduitID, Total) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(queryCommande)) {
            preparedStatement.setInt(1, acheteurID);
            preparedStatement.setInt(2, produitID);
            preparedStatement.setDouble(3, total);
            preparedStatement.executeUpdate();
            showAlert("Succès", "Produit commandé avec succès !");

            // Mettre à jour le nombre de produits commandés
            int nombreProduitsCommandes = getNombreProduitsCommandes();
            nombreProduitsCommandesLabel.setText(nombreProduitsCommandes + " produits commandés");

            System.out.println("Commande insérée : UserID=" + acheteurID + ", ProduitID=" + produitID + ", Total=" + total);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'enregistrer la commande : " + e.getMessage());
        }
    }

    // Méthode pour récupérer le nombre de produits commandés
    private int getNombreProduitsCommandes() {
        int nombreProduitsCommandes = 0;
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = "SELECT COUNT(*) AS total FROM commandes WHERE UserID = ?";

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                nombreProduitsCommandes = resultSet.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de récupérer le nombre de produits commandés : " + e.getMessage());
        }

        return nombreProduitsCommandes;
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}