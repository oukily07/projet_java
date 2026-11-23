package com.centrecommerce.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Vente {
    private int id;
    private LocalDateTime dateVente;
    private Client client;
    private double montantTotal;
    private List<LigneVente> lignes = new ArrayList<>();

    public Vente() {
        this.dateVente = LocalDateTime.now();
    }

    public Vente(int id, LocalDateTime dateVente, Client client, double montantTotal) {
        this.id = id;
        this.dateVente = dateVente;
        this.client = client;
        this.montantTotal = montantTotal;
    }

    public Vente(Client client, List<LigneVente> lignes) {
        this.dateVente = LocalDateTime.now();
        this.client = client;
        this.lignes = lignes;
        this.montantTotal = calculerMontantTotal();
    }

    public double calculerMontantTotal() {
        return lignes.stream()
                .mapToDouble(l -> l.getQuantite() * l.getPrixUnitaire())
                .sum();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDateVente() { return dateVente; }
    public void setDateVente(LocalDateTime dateVente) { this.dateVente = dateVente; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

    public List<LigneVente> getLignes() { return lignes; }
    public void setLignes(List<LigneVente> lignes) {
        this.lignes = lignes;
        this.montantTotal = calculerMontantTotal();
    }
}