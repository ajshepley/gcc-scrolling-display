package com.github.ajshepley;

import processing.core.PImage;

public class ArduinoButtonFactory {

  private final ImageLoader imageLoader;

  public ArduinoButtonFactory(final ImageLoader imageLoader) {
    this.imageLoader = imageLoader;
  }

  public ArduinoButton createButton(
      final int arduinoInputIndex,
      final float x,
      final float y,
      final String frontImagePath,
      final String backImagePath
  ) {
    final PImage frontImage = this.imageLoader.loadImage(frontImagePath);
    final PImage backImage = this.imageLoader.loadImage(backImagePath);
    return new ArduinoButton(arduinoInputIndex, x, y, frontImage, backImage);
  }
}
