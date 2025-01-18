package org.example.logintestjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class GererCategoriesController {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private void initialize() {
        // Charger l'onglet "Gérer les catégories" dynamiquement
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gerer_categories.fxml"));
            AnchorPane gererCategoriesPane = loader.load();
            Tab gererCategoriesTab = new Tab("Gérer les catégories", gererCategoriesPane);
            mainTabPane.getTabs().add(gererCategoriesTab);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de gerer_categories.fxml : " + e.getMessage());
        }
    }
}