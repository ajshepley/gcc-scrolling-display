package com.github.ajshepley.buttons.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@SuppressWarnings("unused")
@JsonDeserialize
public class ConfigStick extends ConfigButton {

  @JsonProperty
  private int yInputIndex;

  @JsonProperty
  private int distance;

  @JsonProperty
  private float scale;

  public int getYInputIndex() {
    return this.yInputIndex;
  }

  public int getDistance() {
    return this.distance;
  }

  public float getScale() {
    return this.scale;
  }
}
