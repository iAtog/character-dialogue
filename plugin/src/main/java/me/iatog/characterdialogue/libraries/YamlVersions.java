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
            //Pattern pattern = new Pattern(Segment.range(1, Integer.MAX_VALUE), Segment.literal("."), Segment.range(0, 10));
            UpdaterSettings.Builder builder = UpdaterSettings.builder()
                  .setAutoSave(true)
                  .setVersioning(new BasicVersioning("file-version"))
                  /*
                  .addIgnoredRoute("1", "placeholders", '.')
                  .addIgnoredRoute("1", "npc", '.')*/
                  .setKeepAll(true);

            ignoreRoute("placeholders", builder, 10);
            ignoreRoute("npc", builder, 10);
            ignoreRoute("choice.number-heads", builder, 10);
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
