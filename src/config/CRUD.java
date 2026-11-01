
package config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CRUD {

    private final MyConnexion mc = MyConnexion.getInstance();

    public Connection getConnection() {
        return mc.getConnection();
    }

    public boolean executeInsert(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logError("Insertion", ex);
            return false;
        }
    }

    public int executeInsertAndGetId(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(ps, params);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            logError("Insertion", ex);
        }

        return -1;
    }

    public boolean executeUpdate(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logError("Modification", ex);
            return false;
        }
    }

    public boolean executeDelete(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logError("Suppression", ex);
            return false;
        }
    }

    public ResultSet executeSelect(String sql, Object... params)
            throws SQLException {

        PreparedStatement ps = getConnection().prepareStatement(sql);
        setParameters(ps, params);
        return ps.executeQuery();
    }

    private void setParameters(PreparedStatement ps, Object... params)
            throws SQLException {

        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    private void logError(String operation, SQLException ex) {
        System.err.println("Erreur : " + operation);
        Logger.getLogger(CRUD.class.getName())
                .log(Level.SEVERE, null, ex);
    }
}
