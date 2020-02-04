package com.github.ajshepley.buttons;

import com.github.ajshepley.ImageLoader;
import com.github.ajshepley.buttons.config.ConfigButton;
import com.github.ajshepley.buttons.config.ConfigFile;
import com.github.ajshepley.buttons.config.ConfigLoader;
import com.github.ajshepley.buttons.config.ConfigStick;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import processing.core.PImage;

public class ArduinoButtonFactory {

  private final ImageLoader imageLoader;

  public ArduinoButtonFactory(final ImageLoader imageLoader) {
    this.imageLoader = imageLoader;
  }

  public List<ArduinoButton> createButtonsFromConfig(final ConfigFile userConfig) {
    return Arrays.stream(userConfig.getButtons())
        .map(this::createButtonFromConfig)
        .collect(Collectors.toList());
  }

  public List<ArduinoStick> createSticksFromConfig(final ConfigFile userConfig) {
    return Arrays.stream(userConfig.getSticks())
        .map(this::createStickFromConfig)
        .collect(Collectors.toList());
  }

  public ArduinoButton createButtonFromConfig(final ConfigButton configButton) {
    return this.createButton(
        configButton.getArduinoInputIndex(),
        configButton.getXPosition(),
        configButton.getYPosition(),
        configButton.getFrontImagePath(),
        configButton.getBackImagePath()
    );
  }

  public ArduinoStick createStickFromConfig(final ConfigStick configStick) {
    return this.createStick(
        configStick.getArduinoInputIndex(),
        configStick.getYInputIndex(),
        configStick.getXPosition(),
        configStick.getYPosition(),
        configStick.getDistance(),
        configStick.getScale(),
        configStick.getFrontImagePath(),
        configStick.getBackImagePath()
    );
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
