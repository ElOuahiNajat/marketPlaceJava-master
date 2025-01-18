package org.example.logintestjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class chat {

    @FXML
    private ListView<Message> messageList;

    @FXML
    private TextArea messageInput;

    @FXML
    private Label chatTitle;

    @FXML
    private Button btnSend;

    private int currentUserId;

    public chat() {
        // Assurez-vous que le FXML est chargé
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadNotifications();
        chatTitle.setText("Notifications");
        messageList.getItems().clear();
        messageInput.clear();
    }

    @FXML
    public void initialize() {
        // Chargement des notifications au démarrage
        loadNotifications();

        // Personnalisation de l'affichage des messages dans la ListView
        messageList.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox messageBox = new HBox(10);
                    Text senderText = new Text(message.getSender() + ": ");
                    senderText.setStyle("-fx-font-weight: bold;");

                    Text contentText = new Text(message.getContent());
                    TextFlow textFlow = new TextFlow(senderText, contentText);

                    messageBox.getChildren().add(textFlow);
                    setGraphic(messageBox);
                    setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0;");
                }
            }
        });

        // Écouteur du bouton envoyer
        btnSend.setOnAction(e -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageInput.getText();
        if (!messageText.isEmpty()) {
            // Obtenir l'horodatage actuel sous forme de Timestamp
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            // Enregistrer le message dans la table des notifications
            sendNotificationToDatabase(currentUserId, messageText, timestamp);
            messageInput.clear();
            loadNotifications();  // Recharger les notifications après l'envoi
        }
    }

    private void sendNotificationToDatabase(int userId, String messageText, Timestamp timestamp) {
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();
        String query = "INSERT INTO notifications (UserID, Message, Date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conDb.prepareStatement(query)) {
            stmt.setInt(1, userId);  // ID de l'utilisateur actuel
            stmt.setString(2, messageText);  // Le texte du message
            stmt.setTimestamp(3, timestamp);  // Le timestamp du message
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de l'envoi de la notification: " + e.getMessage());
        }
    }

    private void loadNotifications() {
        messageList.getItems().clear();
        List<Message> notifications = fetchNotificationsFromDatabase(currentUserId);
        messageList.getItems().addAll(notifications);
    }

    private List<Message> fetchNotificationsFromDatabase(int userId) {
        DatabaseConnection con = new DatabaseConnection();
        Connection conDb = con.getConnection();
        List<Message> notifications = new ArrayList<>();
        String query = "SELECT m.Message, m.Date " +
                "FROM notifications m " +
                "WHERE m.UserID = ? " +
                "ORDER BY m.Date DESC";
        try (PreparedStatement stmt = conDb.prepareStatement(query)) {
            stmt.setInt(1, userId);  // Récupérer les notifications pour cet utilisateur
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String content = rs.getString("Message");
                Timestamp timestamp = rs.getTimestamp("Date");
                notifications.add(new Message("Système", content, timestamp));  // Utilisez "Système" comme expéditeur générique
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec du chargement des notifications: " + e.getMessage());
        }
        return notifications;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
