package me.iatog.characterdialogue.database;

import me.iatog.characterdialogue.CharacterDialoguePlugin;
import me.iatog.characterdialogue.api.dialog.Dialogue;
import me.iatog.characterdialogue.session.DialogSession;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DialogPersistence {

    private final CharacterDialoguePlugin main;
    private final String url;

    public DialogPersistence(CharacterDialoguePlugin main) {
        this.main = main;
        File dbFile = new File(main.getDataFolder(), "sessions.db");
        this.url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        createTable();
    }

    public DialogSession createSession(Player player) {
        String sql = "SELECT * FROM sessions WHERE uuid = ?";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());

            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    String dialogueName = rs.getString("dialogue");
                    int index = rs.getInt("currentIndex");
                    Dialogue dialogue = main.getCache().getDialogues().get(dialogueName);
                    DialogSession session = new DialogSession(main, player, dialogue);

                    session.setCurrentIndex(index);
                    return session;
                }
            }
        } catch(Exception ex) {
            main.getLogger().severe("Error while creating session: " + ex.getMessage());
        }

        return null;
    }

    public void saveSession(DialogSession session) {
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement(
                  "REPLACE INTO sessions (uuid, dialogue, currentIndex) VALUES (?, ?, ?)"
            )) {
            statement.setString(1, session.getPlayer().getUniqueId().toString());
            statement.setString(2, session.getDialogue().getName());
            statement.setInt(3, session.getCurrentIndex());

            statement.executeUpdate();
        } catch(Exception ex) {
            main.getLogger().severe("Failed saving dialogue session: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean hasStoredSession(Player player) {
        String query = "SELECT * FROM sessions WHERE uuid = ?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, player.getUniqueId().toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch(Exception ex) {
            main.getLogger().severe("Error while getting stored session: " + ex.getMessage());
        }

        return false;
    }

    public void deleteSession(Player player) {
        String query = "DELETE FROM sessions WHERE uuid = ?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (Exception ex) {
             main.getLogger().severe("Failed to delete session: " + ex.getMessage());
        }
    }

    private void createTable() {
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement(
                  "CREATE TABLE IF NOT EXISTS sessions (" +
                        "uuid TEXT PRIMARY KEY," +
                        "dialogue TEXT NOT NULL," +
                        "currentIndex SMALLINT)"
            )) {
            statement.executeUpdate();
        } catch(Exception ex) {
            main.getLogger().severe("Failed while creating persistence database: " + ex.getMessage());
        }
    }

}
