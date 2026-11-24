package com.centrecommerce.dao;

import com.centrecommerce.config.DatabaseConnection;
import com.centrecommerce.model.BilanMensuel;
import com.centrecommerce.model.Client;
import com.centrecommerce.model.LigneVente;
import com.centrecommerce.model.Vente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteDAO {

    public void enregistrerVente(Vente vente) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean previousAutoCommit = conn.getAutoCommit();

        String sqlCheckStock = "SELECT designation, quantite_stock FROM produit WHERE id = ? FOR UPDATE";
        String sqlInsertVente = "INSERT INTO vente (date_vente, client_id, montant_total) VALUES (?, ?, ?)";
        String sqlInsertLigne = "INSERT INTO ligne_vente (vente_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE produit SET quantite_stock = quantite_stock - ? WHERE id = ?";

        try {
            conn.setAutoCommit(false);

            // 1. Verify stock for every item before inserting anything
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckStock)) {
                for (LigneVente ligne : vente.getLignes()) {
                    psCheck.setInt(1, ligne.getProduit().getId());
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Produit introuvable (ID: " + ligne.getProduit().getId() + ").");
                        }
                        int stockDispo = rs.getInt("quantite_stock");
                        String nomProduit = rs.getString("designation");
                        if (stockDispo < ligne.getQuantite()) {
                            throw new SQLException(
                                "Stock insuffisant pour \"" + nomProduit + "\" (disponible : "
                                + stockDispo + ", demandé : " + ligne.getQuantite() + ")."
                            );
                        }
                    }
                }
            }

            // 2. Insert the Vente header and retrieve its generated ID
            try (PreparedStatement psVente = conn.prepareStatement(sqlInsertVente, Statement.RETURN_GENERATED_KEYS)) {
                psVente.setTimestamp(1, Timestamp.valueOf(vente.getDateVente()));
                psVente.setInt(2, vente.getClient().getId());
                psVente.setDouble(3, vente.getMontantTotal());
                psVente.executeUpdate();

                try (ResultSet keys = psVente.getGeneratedKeys()) {
                    if (keys.next()) {
                        vente.setId(keys.getInt(1));
                    }
                }
            }

            // 3. Insert each LigneVente and deduct stock
            try (PreparedStatement psLigne = conn.prepareStatement(sqlInsertLigne, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psStock = conn.prepareStatement(sqlUpdateStock)) {

                for (LigneVente ligne : vente.getLignes()) {
                    ligne.setVente(vente);

                    psLigne.setInt(1, vente.getId());
                    psLigne.setInt(2, ligne.getProduit().getId());
                    psLigne.setInt(3, ligne.getQuantite());
                    psLigne.setDouble(4, ligne.getPrixUnitaire());
                    psLigne.executeUpdate();

                    try (ResultSet keys = psLigne.getGeneratedKeys()) {
                        if (keys.next()) {
                            ligne.setId(keys.getInt(1));
                        }
                    }

                    psStock.setInt(1, ligne.getQuantite());
                    psStock.setInt(2, ligne.getProduit().getId());
                    psStock.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
        }
    }

    public List<Vente> listerToutes() throws SQLException {
        List<Vente> ventes = new ArrayList<>();
        String sql = """
            SELECT v.id, v.date_vente, v.montant_total,
                   c.id AS client_id, c.nom, c.telephone, c.email
            FROM vente v
            JOIN client c ON v.client_id = c.id
            ORDER BY v.date_vente DESC
        """;
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("client_id"),
                    rs.getString("nom"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                ventes.add(new Vente(
                    rs.getInt("id"),
                    rs.getTimestamp("date_vente").toLocalDateTime(),
                    client,
                    rs.getDouble("montant_total")
                ));
            }
        }
        return ventes;
    }

    public BilanMensuel genererBilanMensuel(int mois, int annee) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        int nombreVentes = 0;
        double chiffreAffaires = 0.0;
        String sqlResume = """
            SELECT COUNT(*) AS nb_ventes, COALESCE(SUM(montant_total), 0) AS ca
            FROM vente
            WHERE MONTH(date_vente) = ? AND YEAR(date_vente) = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sqlResume)) {
            ps.setInt(1, mois);
            ps.setInt(2, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombreVentes = rs.getInt("nb_ventes");
                    chiffreAffaires = rs.getDouble("ca");
                }
            }
        }

        List<BilanMensuel.ProduitVendu> topProduits = new ArrayList<>();
        String sqlTop = """
            SELECT p.id, p.designation,
                   SUM(lv.quantite) AS total_qte,
                   SUM(lv.quantite * lv.prix_unitaire) AS total_revenu
            FROM ligne_vente lv
            JOIN vente v ON lv.vente_id = v.id
            JOIN produit p ON lv.produit_id = p.id
            WHERE MONTH(v.date_vente) = ? AND YEAR(v.date_vente) = ?
            GROUP BY p.id, p.designation
            ORDER BY total_qte DESC
            LIMIT 10
        """;
        try (PreparedStatement ps = conn.prepareStatement(sqlTop)) {
            ps.setInt(1, mois);
            ps.setInt(2, annee);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    topProduits.add(new BilanMensuel.ProduitVendu(
                        rs.getInt("id"),
                        rs.getString("designation"),
                        rs.getInt("total_qte"),
                        rs.getDouble("total_revenu")
                    ));
                }
            }
        }

        return new BilanMensuel(mois, annee, nombreVentes, chiffreAffaires, topProduits);
    }
}