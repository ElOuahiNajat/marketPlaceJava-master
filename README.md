# 📌 Marketplace Project – Java Desktop Application

## 🖥️ Description
The **Marketplace Project** is a Java-based desktop application designed to simulate an online marketplace where buyers and sellers can interact to trade goods and services. This application provides an intuitive and user-friendly interface, enabling users to perform various e-commerce functions such as product listing, purchasing, and order management.  

This project is ideal for showcasing skills in **object-oriented programming, JavaFX, database integration, and desktop application design**.  

Additionally, the application allows managing users, products, and categories via a JavaFX interface connected to a MySQL database. It offers two roles: **Administrator** and **Client**, with functionalities tailored to each role.

---

## 🚀 Fonctionnalités principales

### 1. Authentification
- Login et Signup avec validation des champs.
- Rôles : Admin / Client.
- Upload d’image pour le profil utilisateur.

### 2. Partie Administrateur
- Dashboard avec statistiques.
- Gestion des utilisateurs : ajout, modification, suppression, recherche, tri.
- Gestion des produits : ajout, modification, suppression, tri, recherche.
- Gestion des catégories : ajout, modification, recherche, tri.
- Réception de notifications lorsqu’un client commande un produit.

### 3. Partie Client
- Dashboard dédié.
- Commander un produit.
- Rechercher un produit.
- Consulter l’historique de ses commandes.

---

## 🛠️ Outils utilisés

| Outil           | Rôle                                      |
|-----------------|------------------------------------------|
| JavaFX          | Interface graphique de l’application     |
| MySQL           | Base de données                           |
| PHPMyAdmin      | Gestion visuelle de la base MySQL        |
| XAMPP           | Serveur MySQL + Apache (PHPMyAdmin inclus) |
| IntelliJ IDEA   | IDE pour développer en Java               |

---

## 📂 Installation et configuration

### 1️⃣ Installer XAMPP
- Télécharger XAMPP : [https://www.apachefriends.org/index.html](https://www.apachefriends.org/index.html)
- Lancer XAMPP Control Panel et démarrer **Apache** et **MySQL**.

### 2️⃣ Configurer la base de données
- Ouvrir PHPMyAdmin via `http://localhost/phpmyadmin/`.
- Créer une base de données (ex. `gestion_app`).
- Importer le fichier `.sql` fourni dans le projet.

### 3️⃣ Configurer IntelliJ IDEA
- Installer IntelliJ IDEA : [https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)
- Importer le projet JavaFX.
- Vérifier que **JavaFX SDK** est bien configuré dans `Project Structure`.
- Ajouter le **JDBC MySQL Connector** au classpath.

### 4️⃣ Connexion à la base de données
Dans le fichier `DBConnection.java` (ou équivalent), modifier les informations :

```java
String url = "jdbc:mysql://localhost:3306/gestion_app";
String user = "root";
String password = "";
