
package controler;

import config.CRUD;
import model.Category;
import model.Article;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class CategoryDAO implements DAO<Category> {

    private final CRUD crud = new CRUD();

    private Category mapCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));

        int parentId = rs.getInt("parent_id");
        if (!rs.wasNull()) {
            Category parent = new Category();
            parent.setId(parentId);
            c.setParent(parent);
        }

        return c;
    }

    @Override
    public boolean insert(Category c) {
        String sql = "INSERT INTO category(name, description, parent_id) "
                + "VALUES (?, ?, ?)";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getDescription());

            if (c.getParent() != null && c.getParent().getId() > 0) {
                ps.setInt(3, c.getParent().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            boolean ok = ps.executeUpdate() > 0;

            if (ok) {
                try (PreparedStatement idPs =
                             crud.getConnection().prepareStatement(
                                     "SELECT LAST_INSERT_ID()");
                     ResultSet rs = idPs.executeQuery()) {
                    if (rs.next()) {
                        c.setId(rs.getInt(1));
                    }
                }
            }

            return ok;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Category c, int id) {
        if (c.getParent() != null && c.getParent().getId() == id) {
            return false;
        }

        String sql = "UPDATE category SET name=?, description=?, "
                + "parent_id=? WHERE id=?";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getDescription());

            if (c.getParent() != null && c.getParent().getId() > 0) {
                ps.setInt(3, c.getParent().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        return crud.executeDelete(
                "DELETE FROM category WHERE id=?", id);
    }

    @Override
    public ArrayList<Category> select() {
        ArrayList<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY id";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCategory(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public ArrayList<Category> findChildren(int parentId) {
        ArrayList<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE parent_id=?";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {
            ps.setInt(1, parentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCategory(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public ArrayList<Article> findArticles(int categoryId) {
        return new ArticleDAO().findByCategoryId(categoryId);
    }
}
