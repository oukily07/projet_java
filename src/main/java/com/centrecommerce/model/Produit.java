package com.centrecommerce.model;

public class Produit {
    private int id;
    private String designation;
    private double prixUnitaire;
    private int quantiteStock;
    private Categorie categorie;

    public Produit() {}

    public Produit(int id, String designation, double prixUnitaire, int quantiteStock, Categorie categorie) {
        this.id = id;
        this.designation = designation;
        this.prixUnitaire = prixUnitaire;
        this.quantiteStock = quantiteStock;
        this.categorie = categorie;
    }

    public Produit(String designation, double prixUnitaire, int quantiteStock, Categorie categorie) {
        this.designation = designation;
        this.prixUnitaire = prixUnitaire;
        this.quantiteStock = quantiteStock;
        this.categorie = categorie;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public int getQuantiteStock() { return quantiteStock; }
    public void setQuantiteStock(int quantiteStock) { this.quantiteStock = quantiteStock; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    @Override
    public String toString() {
        return designation + " (" + prixUnitaire + " DH | Stock: " + quantiteStock + ")";
    }
}