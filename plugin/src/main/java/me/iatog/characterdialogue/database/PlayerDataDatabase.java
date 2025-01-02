package me.iatog.characterdialogue.database;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.player.PlayerData;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.*;


public class PlayerDataDatabase {

    private final String url;
    private final CharacterDialoguePlugin main;

    public PlayerDataDatabase(CharacterDialoguePlugin main) {
        this.main = main;
        File dbFile = new File(main.getDataFolder(), "database.db");
        this.url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        createTable();
    }

    private void createTable() { //finishedDialogs, firstInteractions
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                   "CREATE TABLE IF NOT EXISTS players ("+
                   "`uuid` TEXT NOT NULL PRIMARY KEY, " +
                   "`finishedDialogs` TEXT NOT NULL, " +
                   "`firstInteractions` TEXT NOT NULL, " +
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

    private String listToString(List<String> list1) {
        Set<String> set = new HashSet<>(list1);
        List<String> list = new ArrayList<>(set);
        return Strings.join(list, ',');
    }

    public void save(Player player, List<String> finishedDialogs, List<String> firstInteractions, boolean removeEffect, double lastSpeed) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                   "REPLACE INTO players " +
                         "(uuid, finishedDialogs, firstInteractions, removeEffect, lastSpeed) " +
                         "VALUES (?, ?, ?, ?, ?)")
        ) {
            String finished = listToString(finishedDialogs);
            String first = listToString(firstInteractions);

            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, finished);
            statement.setString(3, first);
            statement.setBoolean(4, removeEffect);
            statement.setDouble(5, lastSpeed);

            statement.executeUpdate();
        } catch (SQLException e) {
            main.getLogger().severe("Error while saving player data: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void save(PlayerData playerData) {
        save(playerData.getPlayer(), playerData.getFinishedDialogs(), playerData.getFirstInteractions(), playerData.getRemoveEffect(), playerData.getLastSpeed());
    }

    public PlayerData get(Player player) {
        UUID uuid = player.getUniqueId();

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                if(result.next()) {
                    String[] finished = result.getString("finishedDialogs").split(",");
                    String[] firstInteractions = result.getString("firstInteractions").split(",");
                    List<String> finishedList = new ArrayList<>(Arrays.asList(finished));
                    List<String> firstInteractionsList = new ArrayList<>(Arrays.asList(firstInteractions));
                    boolean removeEffect = result.getBoolean("removeEffect");
                    float lastSpeed = result.getFloat("lastSpeed");

                    return new PlayerData(uuid, finishedList, firstInteractionsList, removeEffect, lastSpeed);
                }
            }
        } catch (SQLException e) {
            main.getLogger().severe("Error while getting player data: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void saveAll() {
        Map<UUID, PlayerData> players = main.getCache().getPlayerData();

        for(PlayerData data : players.values()) {
            PlayerDataDatabase database = main.getServices().getPlayerDataDatabase();

            database.save(data);
        }
    }



}
