
package controler;

import config.CRUD;
import model.Supplier;
import model.Article;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SupplierDAO implements DAO<Supplier> {

    private final CRUD crud = new CRUD();

    private Supplier mapSupplier(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setId(rs.getInt("id"));
        s.setFullname(rs.getString("fullname"));
        s.setEmail(rs.getString("email"));
        s.setTel(rs.getString("tel"));
        s.setVAT(rs.getString("VAT"));
        return s;
    }

    @Override
    public boolean insert(Supplier s) {
        String sql = """
                INSERT INTO supplier (fullname, email, tel, VAT)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(
                             sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getFullname());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getTel());
            ps.setString(4, s.getVAT());

            if (ps.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    s.setId(keys.getInt(1));
                }
            }

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Supplier s, int id) {
        String sql = """
                UPDATE supplier
                SET fullname=?, email=?, tel=?, VAT=?
                WHERE id=?
                """;

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getFullname());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getTel());
            ps.setString(4, s.getVAT());
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        return crud.executeDelete(
                "DELETE FROM supplier WHERE id=?", id);
    }

    @Override
    public ArrayList<Supplier> select() {
        ArrayList<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier ORDER BY id";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapSupplier(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public Supplier findById(int id) {
        String sql = "SELECT * FROM supplier WHERE id=?";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSupplier(rs) : null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<Article> findArticlesBySupplierId(int supplierId) {
        return new ArticleSupplierDAO().findArticlesBySupplierId(supplierId);
    }
}
