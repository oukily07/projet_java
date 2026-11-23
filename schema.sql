CREATE DATABASE IF NOT EXISTS gestion_ventes
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gestion_ventes;

-- 1. Utilisateur
CREATE TABLE IF NOT EXISTS utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 2. Catégorie
CREATE TABLE IF NOT EXISTS categorie (
    id INT AUTO_INCREMENT PRIMARY KEY,
    designation VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- 3. Produit
CREATE TABLE IF NOT EXISTS produit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    designation VARCHAR(150) NOT NULL,
    prix_unitaire DECIMAL(10, 2) NOT NULL CHECK (prix_unitaire >= 0),
    quantite_stock INT NOT NULL DEFAULT 0 CHECK (quantite_stock >= 0),
    categorie_id INT NOT NULL,
    CONSTRAINT fk_produit_categorie
        FOREIGN KEY (categorie_id) REFERENCES categorie(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 4. Client
CREATE TABLE IF NOT EXISTS client (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    telephone VARCHAR(25),
    email VARCHAR(120) UNIQUE
);

-- 5. Vente
CREATE TABLE IF NOT EXISTS vente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date_vente DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_id INT NOT NULL,
    montant_total DECIMAL(12, 2) NOT NULL DEFAULT 0.00 CHECK (montant_total >= 0),
    CONSTRAINT fk_vente_client
        FOREIGN KEY (client_id) REFERENCES client(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 6. LigneVente
CREATE TABLE IF NOT EXISTS ligne_vente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vente_id INT NOT NULL,
    produit_id INT NOT NULL,
    quantite INT NOT NULL CHECK (quantite > 0),
    prix_unitaire DECIMAL(10, 2) NOT NULL CHECK (prix_unitaire >= 0),
    CONSTRAINT fk_lignevente_vente
        FOREIGN KEY (vente_id) REFERENCES vente(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_lignevente_produit
        FOREIGN KEY (produit_id) REFERENCES produit(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- Sample seed data for testing
INSERT IGNORE INTO utilisateur (login, password) VALUES ('admin', 'admin123');

INSERT IGNORE INTO categorie (designation, description) VALUES
    ('Informatique', 'Ordinateurs, périphériques et accessoires'),
    ('Électroménager', 'Appareils pour la maison');

INSERT IGNORE INTO client (nom, telephone, email) VALUES
    ('Karim Benali', '0612345678', 'karim.benali@example.com');