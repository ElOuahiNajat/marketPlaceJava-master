# Marketplace Project – Java Desktop Application

## 1. Introduction
The **Marketplace Project** is a Java-based desktop application designed to simulate an online marketplace where buyers and sellers can interact to trade goods and services.  
The application provides an intuitive and user-friendly interface, enabling users to perform various e-commerce functions such as product listing, purchasing, and order management.  
It demonstrates skills in **object-oriented programming, JavaFX, database integration, and desktop application design**.

---

## 2. Project Objectives
- Develop a functional desktop e-commerce application.
- Enable management of users, products, and categories.
- Implement a role-based architecture: **Admin** and **Client**.
- Demonstrate JavaFX integration with a MySQL database.

---

## 3. Project Architecture
The application follows a simple **MVC (Model-View-Controller)** architecture:  
- **Model**: classes representing entities (User, Product, Category, Order).  
- **View**: JavaFX interfaces (Login, Admin Dashboard, Client Dashboard, etc.).  
- **Controller**: handles events and interacts with the database via JDBC.  

Architecture diagram (to add):  
<img width="553" height="375" alt="archMarket" src="https://github.com/user-attachments/assets/6d074804-d944-49bd-ac17-a4d90e543db0" />


---

## 4. Main Features

### Authentication
- Login and Signup with field validation.
- Profile image upload.
- Role management: Admin / Client.

### Admin Section
- Dashboard with statistics.
- Management of users, products, and categories.
- Notifications on client orders.

### Client Section
- Dedicated dashboard.
- Place and track orders.
- Search and filter products.

---

## 🛠️ 5. Technologies Used

| Tool           | Purpose                                    |
|----------------|--------------------------------------------|
| JavaFX          | Graphical user interface                   |
| MySQL           | Database                                   |
| PHPMyAdmin      | Visual MySQL database management           |
| XAMPP           | MySQL + Apache server                       |
| IntelliJ IDEA   | Java development IDE                        |

---

## 📂 6. Installation and Configuration
1. Install XAMPP and start Apache + MySQL.
2. Create the database via PHPMyAdmin and import the `.sql` file.
3. Configure IntelliJ IDEA with JavaFX SDK and JDBC MySQL Connector.
4. Update `DBConnection.java` with your MySQL credentials.

---

## 7. Execution
- Run the main class `Main.java` in IntelliJ IDEA.  
- The login interface will appear, and the application is ready to use.

---


## 8. Results
- Complete management of users, products, and categories.
- Smooth interaction between client and administrator.
- E-commerce functionalities successfully simulated.

---

## 9. Conclusion
The **Marketplace** project demonstrates the design and development of a complete Java desktop application integrating **JavaFX**, **JDBC**, and a **MySQL** database.  
It showcases mastery of OOP concepts, MVC architecture, and user interactions in an e-commerce environment.
