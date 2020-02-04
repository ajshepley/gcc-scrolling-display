package com.github.ajshepley;

import processing.core.PImage;

public class ArduinoButton {
  // The index of the parsed Arduino input that holds the button's current value.
  private final int arduinoInputIndex;
  private final float x;
  private final float y;

  // Pressed image, or stick base.
  private PImage frontImage;
  private PImage backImage;

  public ArduinoButton(
      final int arduinoInputIndex,
      final float xRenderPosition,
      final float yRenderPosition,
      final PImage frontImage,
      final PImage backImage
  ) {
    this.arduinoInputIndex = arduinoInputIndex;
    this.x = xRenderPosition;
    this.y = yRenderPosition;
    this.frontImage = frontImage;
    this.backImage = backImage;
  }

  public int getArduinoInputIndex() {
    return this.arduinoInputIndex;
  }

  public float getXRenderPosition() {
    return this.x;
  }

  public float getYRenderPosition() {
    return this.y;
  }

  public PImage getFrontImage() {
    return this.frontImage;
  }

  public PImage getBackImage() {
    return this.backImage;
  }
}
