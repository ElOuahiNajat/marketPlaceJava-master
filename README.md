#  Marketplace Project – Java Desktop Application

## 1. Introduction
The **Marketplace Project** is a Java-based desktop application designed to simulate an online marketplace where buyers and sellers can interact to trade goods and services.  
The application provides an intuitive and user-friendly interface, enabling users to perform various e-commerce functions such as product listing, purchasing, and order management.  
It demonstrates skills in **object-oriented programming, JavaFX, database integration, and desktop application design**.

---

##  2. Objectifs du projet
- Créer une application desktop fonctionnelle de type e-commerce.
- Permettre la gestion des utilisateurs, produits et catégories.
- Mettre en œuvre une architecture basée sur les rôles : **Admin** et **Client**.
- Illustrer l’intégration JavaFX avec une base de données MySQL.

---

##  3. Architecture du projet
L’application suit une architecture simple **MVC (Model-View-Controller)** :
- **Model** : classes représentant les entités (User, Product, Category, Order).
- **View** : interfaces JavaFX (Login, Dashboard Admin, Dashboard Client, etc.).
- **Controller** : gestion des événements, interaction avec la base de données via JDBC.

Diagramme de l’architecture (à ajouter) :  
*![Architecture Diagram](path/to/architecture.png)*

---

##  4. Fonctionnalités principales

### Authentification
- Login et Signup avec validation des champs.
- Upload d’image pour le profil.
- Gestion des rôles : Admin / Client.

### Partie Administrateur
- Dashboard avec statistiques.
- Gestion des utilisateurs, produits et catégories.
- Notifications sur les commandes clients.

### Partie Client
- Dashboard dédié.
- Passer et suivre des commandes.
- Rechercher et filtrer les produits.

---

## 🛠️ 5. Technologies utilisées

| Outil           | Rôle                                      |
|-----------------|------------------------------------------|
| JavaFX          | Interface graphique                       |
| MySQL           | Base de données                           |
| PHPMyAdmin      | Gestion visuelle de la base MySQL        |
| XAMPP           | Serveur MySQL + Apache                    |
| IntelliJ IDEA   | IDE de développement Java                 |

---

## 📂 6. Installation et configuration
1. Installer XAMPP et démarrer Apache + MySQL.
2. Créer la base de données via PHPMyAdmin et importer le fichier `.sql`.
3. Configurer IntelliJ IDEA avec JavaFX SDK et JDBC MySQL Connector.
4. Modifier le fichier `DBConnection.java` avec vos informations MySQL.

---

##  7. Exécution
- Lancer la classe principale `Main.java` dans IntelliJ IDEA.  
- L’interface de connexion apparaît et l’application est prête à être utilisée.

---

##  8. Captures d’écran
*(Ajouter des images : Login, Dashboard Admin, Gestion Produits, Commande Client, etc.)*

---

##  9. Résultats
- Gestion complète des utilisateurs, produits et catégories.
- Interaction fluide entre le client et l’administrateur.
- Fonctionnalités e-commerce simulées avec succès.

---

##  10. Conclusion
Le projet **Marketplace** illustre la conception et le développement d’une application desktop Java complète, intégrant **JavaFX**, **JDBC**, et une base de données **MySQL**.  
Il montre la maîtrise des concepts OOP, de l’architecture MVC et des interactions utilisateur en e-commerce.


