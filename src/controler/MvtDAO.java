
package controler;

import config.CRUD;
import model.Article;
import model.Mvt;
import model.TypeMvt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDateTime;

public class MvtDAO implements DAO<Mvt> {

    private final CRUD crud = new CRUD();

    private Mvt mapMvt(ResultSet rs) throws SQLException {
        Mvt m = new Mvt();

        m.setId(rs.getInt("id"));

        Timestamp timestamp = rs.getTimestamp("dateTime");
        if (timestamp != null) {
            m.setDateTime(timestamp.toLocalDateTime());
        }

        m.setNote(rs.getString("note"));
        m.setType(TypeMvt.valueOf(rs.getString("type")));
        m.setQty(rs.getDouble("qty"));

        int articleId = rs.getInt("article_id");

        if (!rs.wasNull()) {
            String sql = "SELECT uid FROM article WHERE id = ?";

            try (PreparedStatement ps =
                         crud.getConnection().prepareStatement(sql)) {
                ps.setInt(1, articleId);

                try (ResultSet articleRs = ps.executeQuery()) {
                    if (articleRs.next()) {
                        Article article = new Article();
                        article.setUid(
                                UUID.fromString(articleRs.getString("uid")));
                        m.setArticle(article);
                    }
                }
            }
        }

        return m;
    }

    private int findArticleId(UUID uid) throws SQLException {
        if (uid == null) {
            return -1;
        }

        String sql = "SELECT id FROM article WHERE uid = ?";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {
            ps.setString(1, uid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return -1;
    }

    private boolean validArticle(Mvt m) {
        if (m == null || m.getArticle() == null
                || m.getArticle().getUid() == null
                || m.getType() == null || m.getQty() <= 0) {
            return false;
        }

        try {
            return findArticleId(m.getArticle().getUid()) > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean insert(Mvt m) {
        if (!validArticle(m)) {
            System.err.println("Mouvement invalide ou article introuvable.");
            return false;
        }

        String sql = """
                INSERT INTO mvt (dateTime, note, type, qty, article_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(
                             sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime dateTime = m.getDateTime() != null
                    ? m.getDateTime()
                    : LocalDateTime.now();

            ps.setTimestamp(1, Timestamp.valueOf(dateTime));
            ps.setString(2, m.getNote());
            ps.setString(3, m.getType().name());
            ps.setDouble(4, m.getQty());

            int articleId = findArticleId(m.getArticle().getUid());
            if (articleId <= 0) {
                return false;
            }
            ps.setInt(5, articleId);

            if (ps.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    m.setId(keys.getInt(1));
                }
            }

            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Mvt m, int id) {
        if (!validArticle(m)) {
            System.err.println("Mouvement invalide ou article introuvable.");
            return false;
        }

        String sql = """
                UPDATE mvt
                SET dateTime=?, note=?, type=?, qty=?, article_id=?
                WHERE id=?
                """;

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {

            LocalDateTime dateTime = m.getDateTime() != null
                    ? m.getDateTime()
                    : LocalDateTime.now();

            int articleId = findArticleId(m.getArticle().getUid());
            if (articleId <= 0) {
                return false;
            }

            ps.setTimestamp(1, Timestamp.valueOf(dateTime));
            ps.setString(2, m.getNote());
            ps.setString(3, m.getType().name());
            ps.setDouble(4, m.getQty());
            ps.setInt(5, articleId);
            ps.setInt(6, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        return crud.executeDelete("DELETE FROM mvt WHERE id=?", id);
    }

    @Override
    public ArrayList<Mvt> select() {
        ArrayList<Mvt> list = new ArrayList<>();
        String sql = "SELECT * FROM mvt ORDER BY dateTime DESC";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapMvt(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public ArrayList<Mvt> findByArticleId(int articleId) {
        ArrayList<Mvt> list = new ArrayList<>();
        String sql = """
                SELECT * FROM mvt
                WHERE article_id=?
                ORDER BY dateTime DESC
                """;

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {
            ps.setInt(1, articleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMvt(rs));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public Mvt findById(int id) {
        String sql = "SELECT * FROM mvt WHERE id=?";

        try (PreparedStatement ps =
                     crud.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapMvt(rs) : null;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
