package me.iatog.characterdialogue.libraries;

import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

// this file may become absurdly large

public class YamlVersions {

    public interface FileVersion {
        LoaderSettings getLoaderSettings();

        UpdaterSettings getUpdaterSettings();
    }

    public static class ConfigVersion implements FileVersion {

        @Override
        public LoaderSettings getLoaderSettings() {
            return LoaderSettings.builder()
                  .setAutoUpdate(true)
                  .setCreateFileIfAbsent(true)
                  .build();
        }

        @Override
        public UpdaterSettings getUpdaterSettings() {
            int ignoredTimes = 15;

            UpdaterSettings.Builder builder = UpdaterSettings.builder()
                  .setAutoSave(true)
                  .setVersioning(new BasicVersioning("file-version"))
                  .setKeepAll(true);

            ignoreRoute("placeholders", builder, ignoredTimes);
            ignoreRoute("npc", builder, ignoredTimes);
            ignoreRoute("choice.number-heads", builder, ignoredTimes);
            ignoreRoute("hidden-npcs", builder, ignoredTimes);
            ignoreRoute("regional-dialogues", builder, ignoredTimes);

            return builder.build();
        }

        private void ignoreRoute(String route, UpdaterSettings.Builder builder, int times) {
            for(int i = 1; i < (times + 1); i++) {
                builder.addIgnoredRoute(i+"", route, '.')
                      .addIgnoredRoute(i+"", route, '.');
            }
        }
    }

    public static class LanguageVersion implements FileVersion {

        @Override
        public LoaderSettings getLoaderSettings() {
            return LoaderSettings.builder()
                  .setAutoUpdate(true)
                  .setCreateFileIfAbsent(true)
                  .build();
        }

        @Override
        public UpdaterSettings getUpdaterSettings() {
            return UpdaterSettings.builder()
                  .setAutoSave(true)
                  .setVersioning(new BasicVersioning("file-version"))

                  .build();
        }
    }

}
