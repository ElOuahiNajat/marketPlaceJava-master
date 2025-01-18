package org.example.logintestjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandesController {

    @FXML
    private FlowPane produitsCommandesListView; // FlowPane pour afficher les produits

    private int userId;

    // Méthode pour définir l'ID de l'utilisateur
    public void setUserId(int userId) {
        this.userId = userId;
        loadProduitsCommandes(); // Charger les produits commandés
    }

    // Charger les produits commandés par l'utilisateur
    private void loadProduitsCommandes() {
        produitsCommandesListView.getChildren().clear();
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();

        String query = """
        SELECT p.nom AS produitNom, p.description, p.prix, p.imageProduits, u.Nom AS userNom, c.Total
        FROM Produits p
        JOIN Users u ON p.UserID = u.UserID
        JOIN commandes c ON p.ProduitID = c.ProduitID
        WHERE c.UserID = ?
    """;

        try (PreparedStatement preparedStatement = conDb.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Chargement des produits commandés pour UserID=" + userId); // Log pour débogage

            while (resultSet.next()) {
                String produitNom = resultSet.getString("produitNom");
                String description = resultSet.getString("description");
                double prix = resultSet.getDouble("prix");
                String imagePath = resultSet.getString("imageProduits");
                String userNom = resultSet.getString("userNom");
                double total = resultSet.getDouble("Total"); // Récupérer le total

                System.out.println("Produit trouvé : " + produitNom + ", Total : " + total); // Log pour débogage

                // Créer une carte de produit
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
                carteProduit.setPrefSize(250, 300); // Largeur de 250 pour afficher 3 cartes par ligne

                // Image du produit
                ImageView imageView = new ImageView();
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                if (imagePath != null && !imagePath.isEmpty()) {
                    imageView.setImage(new Image("file:" + imagePath));
                } else {
                    imageView.setImage(new Image("file:placeholder.png")); // Image par défaut
                }

                // Nom du produit
                Label labelNom = new Label(produitNom);
                labelNom.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                // Prix du produit
                Label labelPrix = new Label("Prix : " + prix + "€");
                labelPrix.setStyle("-fx-font-size: 12px;");

                // Total de la commande
                Label labelTotal = new Label("Total : " + total + "€");
                labelTotal.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

                // Nom du vendeur
                Label labelUser = new Label("Vendu par : " + userNom);
                labelUser.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

                // Ajouter les éléments à la carte
                carteProduit.getChildren().addAll(imageView, labelNom, labelPrix, labelTotal, labelUser);
                produitsCommandesListView.getChildren().add(carteProduit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les produits commandés : " + e.getMessage());
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}