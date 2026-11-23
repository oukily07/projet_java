package com.centrecommerce.model;

public class Categorie {
    private int id;
    private String designation;
    private String description;

    public Categorie() {}

    public Categorie(int id, String designation, String description) {
        this.id = id;
        this.designation = designation;
        this.description = description;
    }

    public Categorie(String designation, String description) {
        this.designation = designation;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return designation;
    }
}