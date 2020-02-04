package com.github.ajshepley.buttons.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@SuppressWarnings("unused")
@JsonDeserialize
public class ConfigButton {

  @JsonProperty
  private String buttonName;

  @JsonProperty(required = true)
  private int arduinoInputIndex;

  @JsonProperty
  private float xPosition;

  @JsonProperty
  private float yPosition;

  @JsonProperty
  private String frontImagePath;

  @JsonProperty
  private String backImagePath;

  public String getButtonName() {
    return this.buttonName;
  }

  public int getArduinoInputIndex() {
    return this.arduinoInputIndex;
  }

  public float getXPosition() {
    return this.xPosition;
  }

  public float getYPosition() {
    return this.yPosition;
  }

  public String getFrontImagePath() {
    return this.frontImagePath;
  }

  public String getBackImagePath() {
    return this.backImagePath;
  }
}
