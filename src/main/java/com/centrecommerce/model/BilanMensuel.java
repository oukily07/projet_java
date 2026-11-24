package com.centrecommerce.model;

import java.util.List;

public record BilanMensuel(
    int mois,
    int annee,
    int nombreVentes,
    double chiffreAffaires,
    List<ProduitVendu> topProduits
) {
    public record ProduitVendu(
        int produitId,
        String designation,
        int quantiteVendue,
        double revenuGenere
    ) {}
}