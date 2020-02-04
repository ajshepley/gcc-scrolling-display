package com.github.ajshepley.buttons.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@SuppressWarnings("unused")
@JsonDeserialize
public class ConfigFile {

  // TODO: Add other settings here.

  @JsonProperty
  private ConfigButton[] buttons;

  @JsonProperty
  private ConfigStick[] sticks;

  public ConfigButton[] getButtons() {
    return this.buttons;
  }

  public ConfigStick[] getSticks() {
    return this.sticks;
  }
}
