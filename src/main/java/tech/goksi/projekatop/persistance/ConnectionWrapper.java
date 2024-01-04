package tech.goksi.projekatop.persistance;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionWrapper {
    private static final Logger LOGGER = Logger.getLogger(ConnectionWrapper.class.getName());
    private static final Map<String, String> drivers;

    static {
        drivers = new HashMap<>();
        drivers.put("SQLite", "jdbc:sqlite");
    }

    private final Connection connection;

    private ConnectionWrapper(Connection connection) {
        this.connection = connection;
    }

    public static ConnectionWrapper getConnection(String driver, String url) throws SQLException {
        return new ConnectionWrapper(DriverManager.getConnection(String.format("%s:%s", drivers.get(driver), url)));
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public <T> T withConnection(String query, Function<PreparedStatement, T> body, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 1; i <= params.length; i++) {
                statement.setObject(i, params[i - 1]);
            }
            return body.apply(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Greska u komunikaciji sa bazom podataka !", e);
            return null;
        }
    }

    public void withConnection(String query, Consumer<PreparedStatement> body, Object... params) {
        withConnection(query, preparedStatement -> {
            body.accept(preparedStatement);
            return null;
        }, params);
    }
}
