package me.iatog.characterdialogue.database;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.player.PlayerData;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class PlayerDataDatabase {

    private final String url;
    private final CharacterDialoguePlugin main;

    public PlayerDataDatabase(CharacterDialoguePlugin main) {
        this.main = main;
        File dbFile = new File(main.getDataFolder(), "database.db");
        this.url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        createTable();
    }

    private void createTable() {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                   "CREATE TABLE IF NOT EXISTS players ("+
                   "`uuid` TEXT NOT NULL PRIMARY KEY, " +
                   "`readedDialogs` TEXT NOT NULL, " +
                   "`removeEffect` BOOLEAN, " +
                   "`lastSpeed` FLOAT(8,2))")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            main.getLogger().severe("Error while creating players database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // GET AND SET METHODS
    // DATA: uuid(text), readedDialogs(text), removeEffect(boolean), lastSpeed(double)

    private String listToString(List<String> list) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i));
            if (i < list.size() - 1) {
                builder.append(',');
            }
        }

        return builder.toString();
    }

    public void save(Player player, List<String> readedDialogs, boolean removeEffect, double lastSpeed) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                   "REPLACE INTO players (uuid, readedDialogs, removeEffect, lastSpeed) VALUES (?, ?, ?, ?)")
        ) {
            String parsedList = listToString(readedDialogs);

            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, parsedList);
            statement.setBoolean(3, removeEffect);
            statement.setDouble(4, lastSpeed);

            statement.executeUpdate();
        } catch (SQLException e) {
            main.getLogger().severe("Error while saving player data: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void save(PlayerData playerData) {
        save(playerData.getPlayer(), playerData.getReadedDialogs(), playerData.getRemoveEffect(), playerData.getLastSpeed());
    }

    public PlayerData get(Player player) {
        UUID uuid = player.getUniqueId();

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                if(result.next()) {
                    String[] readedDialogsString = result.getString("readedDialogs").split(",");
                    List<String> readed = Arrays.asList(readedDialogsString);
                    boolean removeEffect = result.getBoolean("removeEffect");
                    float lastSpeed = result.getFloat("lastSpeed");

                    return new PlayerData(uuid, readed, removeEffect, lastSpeed);
                }
            }
        } catch (SQLException e) {
            main.getLogger().severe("Error while getting player data: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


}
