
package config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTables() throws SQLException {

        String[] queries = {
                """
            CREATE TABLE IF NOT EXISTS category (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                description TEXT,
                parent_id INT NULL,
                CONSTRAINT fk_category_parent
                    FOREIGN KEY (parent_id) REFERENCES category(id)
                    ON DELETE SET NULL
            ) ENGINE=InnoDB
            """,

                """
            CREATE TABLE IF NOT EXISTS supplier (
                id INT AUTO_INCREMENT PRIMARY KEY,
                fullname VARCHAR(150) NOT NULL,
                email VARCHAR(150),
                tel VARCHAR(30),
                VAT VARCHAR(50)
            ) ENGINE=InnoDB
            """,

                """
            CREATE TABLE IF NOT EXISTS article (
                id INT AUTO_INCREMENT PRIMARY KEY,
                uid VARCHAR(36) NOT NULL UNIQUE,
                label VARCHAR(150) NOT NULL,
                description TEXT,
                qty DOUBLE NOT NULL DEFAULT 0,
                minLimite DOUBLE NOT NULL DEFAULT 0,
                maxLimit DOUBLE NOT NULL DEFAULT 0,
                prixAchat DOUBLE NOT NULL DEFAULT 0,
                prixVente DOUBLE NOT NULL DEFAULT 0,
                category_id INT NULL,
                CONSTRAINT fk_article_category
                    FOREIGN KEY (category_id) REFERENCES category(id)
                    ON DELETE SET NULL
            ) ENGINE=InnoDB
            """,

                """
            CREATE TABLE IF NOT EXISTS mvt (
                id INT AUTO_INCREMENT PRIMARY KEY,
                dateTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                note TEXT,
                type ENUM('INPUT', 'OUTPUT') NOT NULL,
                qty DOUBLE NOT NULL,
                article_id INT NOT NULL,
                CONSTRAINT fk_mvt_article
                    FOREIGN KEY (article_id) REFERENCES article(id)
                    ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,

                """
            CREATE TABLE IF NOT EXISTS article_supplier (
                article_id INT NOT NULL,
                supplier_id INT NOT NULL,
                PRIMARY KEY (article_id, supplier_id),
                CONSTRAINT fk_as_article
                    FOREIGN KEY (article_id) REFERENCES article(id)
                    ON DELETE CASCADE,
                CONSTRAINT fk_as_supplier
                    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
                    ON DELETE CASCADE
            ) ENGINE=InnoDB
            """
        };

        try (Connection connection = MyConnexion.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            for (String query : queries) {
                statement.executeUpdate(query);
            }

            System.out.println("Toutes les tables sont créées ou existent déjà.");
        }
    }
}
