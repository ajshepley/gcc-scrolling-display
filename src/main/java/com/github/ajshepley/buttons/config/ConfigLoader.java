package com.github.ajshepley.buttons.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ajshepley.util.LoggingUtils;
import java.io.FileInputStream;
import java.io.InputStream;

public class ConfigLoader {

  private static final String DEFAULT_CONFIG_PATH = "config.json";

  private final ObjectMapper objectMapper = new ObjectMapper();

  public ConfigFile loadConfig() {
    return loadConfig(DEFAULT_CONFIG_PATH);
  }

  public ConfigFile loadConfig(final String configPath) {
    try {
      try (final InputStream inputStream = new FileInputStream(configPath)) {
        return this.objectMapper.readValue(inputStream, ConfigFile.class);
      }
    } catch (final Exception exc) {
      exc.printStackTrace();
      LoggingUtils.errorMessageWindow(
          "Fatal error loading config file.",
          "Failed to load config file at: [" + configPath + "].\nException: " + exc.getMessage(),
          true,
          true
      );

      // Unreachable.
      return null;
    }
  }
}
