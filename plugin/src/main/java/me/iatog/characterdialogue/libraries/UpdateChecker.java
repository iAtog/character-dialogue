package me.iatog.characterdialogue.libraries;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class UpdateChecker {

    private final JavaPlugin main;
    private final String versionUrl;

    public UpdateChecker(JavaPlugin main, String versionUrl) {
        this.main = main;
        this.versionUrl = versionUrl;
    }

    public Optional<String> checkForUpdates(String currentVersion) {
        try {
            URL url = URI.create(versionUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String latestVersion = reader.readLine().trim().split("=")[1];
                if (!currentVersion.equals(latestVersion)) {
                    return Optional.of(latestVersion);
                }
            }
        } catch(Exception ex) {
            main.getLogger().warning("Unable to check for updates.");
        }

        return Optional.empty();
    }

}
