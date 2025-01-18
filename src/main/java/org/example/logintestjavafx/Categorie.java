package org.example.logintestjavafx;

public class Categorie {
    private int categorieID;
    private String nomCategorie;
    private String descriptionCategorie;

    public Categorie(int categorieID, String nomCategorie, String descriptionCategorie) {
        this.categorieID = categorieID;
        this.nomCategorie = nomCategorie;
        this.descriptionCategorie = descriptionCategorie;
    }

    // Getters et Setters
    public int getCategorieID() { return categorieID; }
    public void setCategorieID(int categorieID) { this.categorieID = categorieID; }

    public String getNomCategorie() { return nomCategorie; }
    public void setNomCategorie(String nomCategorie) { this.nomCategorie = nomCategorie; }

    public String getDescriptionCategorie() { return descriptionCategorie; }
    public void setDescriptionCategorie(String descriptionCategorie) { this.descriptionCategorie = descriptionCategorie; }

    @Override
    public String toString() {
        return nomCategorie;
    }
}