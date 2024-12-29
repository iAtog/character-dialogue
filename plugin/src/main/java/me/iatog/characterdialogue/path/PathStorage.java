package me.iatog.characterdialogue.path;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.iatog.characterdialogue.CharacterDialoguePlugin;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathStorage {
    private final String dbPath;
    private final Map<String, List<RecordLocation>> pathMap = new HashMap<>();
    private final Gson gson;

    public PathStorage(CharacterDialoguePlugin main) {
        this.gson = new Gson();
        File dbFile = new File(main.getDataFolder(), "database.db");
        this.dbPath = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        createTable();
        loadPathsFromDatabase();
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement stmt = conn.prepareStatement(
                   "CREATE TABLE IF NOT EXISTS paths (" +
                         "id TEXT PRIMARY KEY," +
                         "path TEXT NOT NULL)"
             )) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePath(String id, List<RecordLocation> path) {
        pathMap.put(id, path);
        String pathJson = gson.toJson(path);

        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement stmt = conn.prepareStatement(
                   "REPLACE INTO paths (id, path) VALUES (?, ?)"
             )) {
            stmt.setString(1, id);
            stmt.setString(2, pathJson);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RecordLocation> getPath(String id) {
        return pathMap.get(id);
    }

    public void removePath(String id) {
        pathMap.remove(id);

        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement stmt = conn.prepareStatement(
                   "DELETE FROM paths WHERE id = ?"
             )) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPathsFromDatabase() {
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement stmt = conn.prepareStatement(
                   "SELECT id, path FROM paths"
             );
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String pathJson = rs.getString("path");
                List<RecordLocation> path = gson.fromJson(pathJson, new TypeToken<List<RecordLocation>>(){}.getType());
                pathMap.put(id, path);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<RecordLocation>> getAllPaths() {
        return pathMap;
    }
}
