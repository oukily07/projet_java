package com.centrecommerce.service;

import java.sql.SQLException;
import java.util.List;

import com.centrecommerce.dao.VenteDAO;
import com.centrecommerce.model.BilanMensuel;
import com.centrecommerce.model.Client;
import com.centrecommerce.model.LigneVente;
import com.centrecommerce.model.Vente;

public class VenteService {

    private final VenteDAO venteDAO = new VenteDAO();

    public Vente validerVente(Client client, List<LigneVente> lignes) throws SQLException {
        if (client == null) {
            throw new IllegalArgumentException("Veuillez sélectionner un client.");
        }
        if (lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("Le panier doit contenir au moins un produit.");
        }
        for (LigneVente ligne : lignes) {
            if (ligne.getQuantite() <= 0) {
                throw new IllegalArgumentException("La quantité doit être supérieure à 0.");
            }
        }

        Vente vente = new Vente(client, lignes);
        venteDAO.enregistrerVente(vente);
        return vente;
    }

    public List<Vente> listerHistoriqueVentes() throws SQLException {
        return venteDAO.listerToutes();
    }

    public BilanMensuel getBilanMensuel(int mois, int annee) throws SQLException {
        if (mois < 1 || mois > 12) {
            throw new IllegalArgumentException("Le mois doit être compris entre 1 et 12.");
        }
        return venteDAO.genererBilanMensuel(mois, annee);
    }
}