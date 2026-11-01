
package controler;

import config.CRUD;
import model.Article;
import model.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class ArticleDAO implements DAO<Article> {

    private final CRUD crud = new CRUD();

    private Article mapArticle(ResultSet rs) throws SQLException {
        Article a = new Article();

        String uidValue = rs.getString("uid");
        if (uidValue != null && !uidValue.isBlank()) {
            a.setUid(UUID.fromString(uidValue));
        }

        a.setLabel(rs.getString("label"));
        a.setDescription(rs.getString("description"));
        a.setQty(rs.getDouble("qty"));
        a.setMinLimite(rs.getDouble("minLimite"));
        a.setMaxLimit(rs.getDouble("maxLimit"));
        a.setPrixAchat(rs.getDouble("prixAchat"));
        a.setPrixVente(rs.getDouble("prixVente"));

        int categoryId = rs.getInt("category_id");

        if (!rs.wasNull()) {
            Category category = new Category();
            category.setId(categoryId);
            a.setCategory(category);
        }

        return a;
    }

    @Override
    public boolean insert(Article a) {
        if (a.getCategory() == null || a.getCategory().getId() <= 0) {
            System.err.println("Une catégorie valide est obligatoire.");
            return false;
        }

        if (a.getUid() == null) {
            a.setUid(UUID.randomUUID());
        }

        String sql = """
                INSERT INTO article
                (uid, label, description, qty, minLimite, maxLimit,
                 prixAchat, prixVente, category_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {

            ps.setString(1, a.getUid().toString());
            ps.setString(2, a.getLabel());
            ps.setString(3, a.getDescription());
            ps.setDouble(4, a.getQty());
            ps.setDouble(5, a.getMinLimite());
            ps.setDouble(6, a.getMaxLimit());
            ps.setDouble(7, a.getPrixAchat());
            ps.setDouble(8, a.getPrixVente());
            ps.setInt(9, a.getCategory().getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Article a, int id) {
        if (a.getCategory() == null || a.getCategory().getId() <= 0) {
            System.err.println("Une catégorie valide est obligatoire.");
            return false;
        }

        String sql = """
                UPDATE article
                SET label=?, description=?, qty=?, minLimite=?,
                    maxLimit=?, prixAchat=?, prixVente=?, category_id=?
                WHERE id=?
                """;

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {

            ps.setString(1, a.getLabel());
            ps.setString(2, a.getDescription());
            ps.setDouble(3, a.getQty());
            ps.setDouble(4, a.getMinLimite());
            ps.setDouble(5, a.getMaxLimit());
            ps.setDouble(6, a.getPrixAchat());
            ps.setDouble(7, a.getPrixVente());
            ps.setInt(8, a.getCategory().getId());
            ps.setInt(9, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        return crud.executeDelete(
                "DELETE FROM article WHERE id=?", id);
    }

    @Override
    public ArrayList<Article> select() {
        ArrayList<Article> list = new ArrayList<>();

        String sql = "SELECT * FROM article ORDER BY id";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapArticle(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public Article findById(int id) {
        String sql = "SELECT * FROM article WHERE id=?";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapArticle(rs) : null;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Article findByUid(UUID uid) {
        String sql = "SELECT * FROM article WHERE uid=?";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {

            ps.setString(1, uid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapArticle(rs) : null;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<Article> findByCategoryId(int categoryId) {
        ArrayList<Article> list = new ArrayList<>();

        String sql = "SELECT * FROM article WHERE category_id=?";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {

            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapArticle(rs));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }
}
