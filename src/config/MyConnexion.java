
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyConnexion {

    private static final Logger LOGGER =
            Logger.getLogger(MyConnexion.class.getName());

    private final String url =
            "jdbc:mysql://localhost:3306/gds?useSSL=false&serverTimezone=UTC";
    private final String login = "root";
    private final String pwd = "";

    private static MyConnexion instance;
    private static Connection connection;

    private MyConnexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, login, pwd);
            LOGGER.info("Connexion à la base gds réussie !");
        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Impossible de se connecter à MySQL.", ex);
            throw new IllegalStateException(
                    "Échec de la connexion à la base gds.", ex);
        }
    }

    public static synchronized MyConnexion getInstance() {
        if (instance == null) {
            instance = new MyConnexion();
        }
        return instance;
    }

    public static Connection getConnection() {
        getInstance();
        return connection;
    }
}
