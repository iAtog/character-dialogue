package me.iatog.characterdialogue.database;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.misc.PlayerData;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * This is a test class, basically extracted from someone else, so as not to make a mess of something so simple
 */
public class PlayerDataDatabase {

    //private Connection connection;
    public final String tableName;
    private final CharacterDialoguePlugin main;

    public PlayerDataDatabase(CharacterDialoguePlugin main) {
        this.main = main;
        this.tableName = "players";
    }

    public void initialize() {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid = ?");
            ResultSet resultSet = statement.executeQuery();

            close(statement, resultSet);
        } catch (SQLException e) {
            main.getLogger().severe("Unable to get connection");
        }
    }

    public Connection getConnection() {
        File dbFile = new File(main.getDataFolder(), "players.db");

        if (! dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                main.getLogger().severe("Failed creating players database");
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");

            return DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException | ClassNotFoundException e) {
            main.getLogger().severe("SQLite not found, using default yaml.");
            return null;
        }
    }

    public void load() {
        try (Connection connection = getConnection()) {
            Statement s = connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + "(`uuid` text, " +
                  "`readedDialogs` text NOT NULL, " +
                  "`removeEffect` boolean, " +
                  "`lastSpeed` double, " +
                  "PRIMARY KEY (`uuid`));");
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }

    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            main.getLogger().severe("Error closing SQL statement.");
            ex.printStackTrace();
        }
    }

    // GET AND SET METHODS
    // DATA: uuid(text), readedDialogs(text), removeEffect(boolean), lastSpeed(double)

    private String listToString(List<?> list, char separator) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i));
            if (i < list.size() - 1) {
                builder.append(separator);
            }
        }

        return builder.toString();
    }

    public void save(Player player, List<String> readedDialogs, boolean removeEffect, double lastSpeed) {
        PreparedStatement statement = null;

        try (Connection connection = getConnection()) {
            statement = connection.prepareStatement("REPLACE INTO " + tableName + " (uuid, readedDialogs, removeEffect, lastSpeed) VALUES (?,?,?)");
            String parsedList = listToString(readedDialogs, ',');

            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, parsedList);
            statement.setBoolean(3, removeEffect);
            statement.setDouble(4, lastSpeed);

            statement.executeUpdate();
        } catch (SQLException e) {
            main.getLogger().severe("Error while saving player data");
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                main.getLogger().severe("Error while saving player data");
                e.printStackTrace();
            }
        }

    }

    public void save(PlayerData playerData) {
        save(playerData.getPlayer(), playerData.getReadedDialogs(), playerData.getRemoveEffect(), playerData.getLastSpeed());
    }

    public PlayerData get(Player player) {
        UUID uuid = player.getUniqueId();
        PreparedStatement statement = null;
        ResultSet result = null;

        try (Connection connection = getConnection()) {
            statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid = '" + uuid + "'");
            result = statement.executeQuery();

            while (result.next()) {
                if (result.getString("uuid").equalsIgnoreCase(uuid.toString())) {
                    String[] readedDialogsString = result.getString("readedDialogs").split(",");
                    List<String> readed = new ArrayList<>(Arrays.asList(readedDialogsString));
                    boolean removeEffect = result.getBoolean("removeEffect");
                    double lastSpeed = result.getDouble("lastSpeed");

                    return new PlayerData(uuid, readed, removeEffect, lastSpeed);
                }
            }

        } catch (SQLException e) {
            main.getLogger().severe("Error while getting player data");
            e.printStackTrace();
        } finally {
            close(statement, result);
        }

        return null;
    }


}
