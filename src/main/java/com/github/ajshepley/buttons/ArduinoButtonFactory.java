package com.github.ajshepley.buttons;

import com.github.ajshepley.ImageLoader;
import com.github.ajshepley.buttons.config.ConfigLoader;
import processing.core.PImage;

public class ArduinoButtonFactory {

  private final ImageLoader imageLoader;
  private final ConfigLoader configLoader;

  public ArduinoButtonFactory(final ImageLoader imageLoader) {
    this.configLoader = new ConfigLoader();
    this.imageLoader = imageLoader;
    this.configLoader.loadConfig(null);
  }

  public ArduinoButton createButton(
      final int arduinoInputIndex,
      final float xRenderPosition,
      final float yRenderPosition,
      final String frontImagePath,
      final String backImagePath
  ) {
    final PImage frontImage = this.imageLoader.loadImage(frontImagePath);
    final PImage backImage = this.imageLoader.loadImage(backImagePath);
    return new ArduinoButton(arduinoInputIndex, xRenderPosition, yRenderPosition, frontImage, backImage);
  }

  public ArduinoStick createStick(
      final int arduinoXInputIndex,
      final int arduinoYInputIndex,
      final float xRenderPosition,
      final float yRenderPosition,
      final int distance,
      final float scale,
      final String stickImagePath,
      final String stickBaseImagePath
  ) {
    final PImage stickImage = this.imageLoader.loadImage(stickImagePath);
    final PImage stickBaseImage = this.imageLoader.loadImage(stickBaseImagePath);
    return new ArduinoStick(
        arduinoXInputIndex,
        arduinoYInputIndex,
        xRenderPosition,
        yRenderPosition,
        distance,
        scale,
        stickImage,
        stickBaseImage
    );
  }
}
