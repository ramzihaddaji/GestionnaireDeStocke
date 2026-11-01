
package controler;

import config.CRUD;
import model.Article;
import model.Category;
import model.Supplier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class ArticleSupplierDAO {

    private final CRUD crud = new CRUD();

    /**
     * Associe un article à un fournisseur.
     * articleId est l'identifiant numérique de la base de données.
     */
    public boolean assign(int articleId, int supplierId) {
        String sql = """
                INSERT INTO article_supplier (article_id, supplier_id)
                VALUES (?, ?)
                """;

        return crud.executeInsert(sql, articleId, supplierId);
    }

    /**
     * Supprime l'association entre un article et un fournisseur.
     */
    public boolean remove(int articleId, int supplierId) {
        String sql = """
                DELETE FROM article_supplier
                WHERE article_id = ? AND supplier_id = ?
                """;

        return crud.executeDelete(sql, articleId, supplierId);
    }

    /**
     * Recherche les fournisseurs associés à un article.
     */
    public ArrayList<Supplier> findSuppliersByArticleId(int articleId) {
        ArrayList<Supplier> list = new ArrayList<>();

        String sql = """
                SELECT s.*
                FROM supplier s
                INNER JOIN article_supplier x ON x.supplier_id = s.id
                WHERE x.article_id = ?
                ORDER BY s.fullname
                """;

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {

            ps.setInt(1, articleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier supplier = new Supplier();

                    supplier.setId(rs.getInt("id"));
                    supplier.setFullname(rs.getString("fullname"));
                    supplier.setEmail(rs.getString("email"));
                    supplier.setTel(rs.getString("tel"));
                    supplier.setVAT(rs.getString("VAT"));

                    list.add(supplier);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    /**
     * Recherche les articles associés à un fournisseur.
     */
    public ArrayList<Article> findArticlesBySupplierId(int supplierId) {
        ArrayList<Article> list = new ArrayList<>();

        String sql = """
                SELECT a.*
                FROM article a
                INNER JOIN article_supplier x ON x.article_id = a.id
                WHERE x.supplier_id = ?
                ORDER BY a.label
                """;

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {

            ps.setInt(1, supplierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article();

                    // uid est un UUID stocké sous forme de texte en SQL.
                    String uidValue = rs.getString("uid");
                    if (uidValue != null && !uidValue.isBlank()) {
                        article.setUid(UUID.fromString(uidValue));
                    }

                    article.setLabel(rs.getString("label"));
                    article.setDescription(rs.getString("description"));
                    article.setQty(rs.getDouble("qty"));
                    article.setMinLimite(rs.getDouble("minLimite"));
                    article.setMaxLimit(rs.getDouble("maxLimit"));
                    article.setPrixAchat(rs.getDouble("prixAchat"));
                    article.setPrixVente(rs.getDouble("prixVente"));

                    int categoryId = rs.getInt("category_id");

                    if (!rs.wasNull()) {
                        Category category = new Category();
                        category.setId(categoryId);
                        article.setCategory(category);
                    }

                    list.add(article);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }
}
