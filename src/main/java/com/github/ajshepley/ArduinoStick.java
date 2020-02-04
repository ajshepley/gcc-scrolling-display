package com.github.ajshepley;

import processing.core.PImage;

public class ArduinoStick extends ArduinoButton {

  private final int yInputIndex;
  private final int distance;
  private final float scale;

  public ArduinoStick(
      final int arduinoXInputIndex,
      final int arduinoYInputIndex,
      final float xRenderPosition,
      final float yRenderPosition,
      final int distance,
      final float scale,
      final PImage stickImage,
      final PImage stickBaseImage
  ) {
    super(arduinoXInputIndex, xRenderPosition, yRenderPosition, stickImage, stickBaseImage);
    this.yInputIndex = arduinoYInputIndex;
    this.distance = distance;
    this.scale = scale;
  }

  public int getXInputIndex() {
    return this.getArduinoInputIndex();
  }

  public int getYInputIndex() {
    return this.yInputIndex;
  }

  public int getDistance() {
    return this.distance;
  }

  public float getScale() {
    return this.scale;
  }

  public PImage getStickImage() {
    return this.getFrontImage();
  }

  public PImage getStickBaseImage() {
    return this.getBackImage();
  }
}
