package com.github.ajshepley;

import com.github.ajshepley.buttons.ArduinoButton;
import com.github.ajshepley.buttons.ArduinoButtonFactory;
import com.github.ajshepley.buttons.ArduinoStick;
import com.github.ajshepley.buttons.ButtonUtils;
import com.github.ajshepley.buttons.config.ConfigFile;
import com.github.ajshepley.buttons.config.ConfigLoader;
import com.github.ajshepley.util.LoggingUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import processing.core.PApplet;
import processing.core.PImage;
import processing.serial.Serial;

public class MainSketch extends PApplet implements ImageLoader {

  // must match that of Arduino
  private static final int ARDUINO_BAUD_RATE = 115200;
  private static final int WINDOW_WIDTH = 500;
  private static final int WINDOW_HEIGHT = 500;

  private final ArduinoButtonFactory arduinoButtonFactory = new ArduinoButtonFactory(this);

  // TODO: Make configurable.
  private final boolean logRaw = false;
  private final boolean logGCCInputs = true;

  private final List<ArduinoButton> arduinoButtons = new ArrayList<>();
  private final List<ArduinoStick> arduinoSticks = new ArrayList<>();

  private int maxButtonIndex;

  // TODO: rename serialPort
  private Serial serialPort;


  @Override
  public void settings() {
    super.size(WINDOW_WIDTH, WINDOW_HEIGHT);
  }

  @Override
  public void setup() {
    final String[] serials = Serial.list();
    // First entry is COM1, second entry should be the arduino port.
    if (serials.length < 2) {
      LoggingUtils.errorMessageWindow(
          "Cannot start GCC Display.",
          "Couldn't detect the serial port. Make sure your Arduino is connected and powered.\n" +
          "The program will now quit.",
          true,
          true
      );
    }

    // initializing the object by assigning a port and baud rate (must match that of Arduino)
    this.serialPort = new Serial(this, Serial.list()[1], ARDUINO_BAUD_RATE);

    // function from serial library that throws out the first reading,
    // in case we started reading in the middle of a string from Arduino
    this.serialPort.clear();

    this.setupButtonsAndSticks();

    super.background(0,0,0);
  }

  private void setupButtonsAndSticks() {
    final ConfigFile userConfig = new ConfigLoader().loadConfig();
    final List<ArduinoButton> buttons =  this.arduinoButtonFactory.createButtonsFromConfig(userConfig);
    final List<ArduinoStick> sticks = this.arduinoButtonFactory.createSticksFromConfig(userConfig);

    // TODO: It may be better to return a new collection rather than mutate local state.
    this.arduinoButtons.addAll(buttons);
    this.arduinoSticks.addAll(sticks);
    this.maxButtonIndex = ButtonUtils.calculateLargestIndex(this.arduinoButtons, this.arduinoSticks);
  }

  @Override
  public PImage loadImage(final String filename) {
    // TODO: Detect error and log or open window.
    return super.loadImage(filename);
  }

  @Override
  public void draw() {
    try {
      final String raw = this.serialPort.readString();

      if (this.logRaw) {
        LoggingUtils.logMessage("Raw string is: \n[" + raw.replace('\n', ' ') + "]", 0.1);
      }

      if (StringUtils.isNotBlank(raw)) {
        final String[] inputGlob = raw.split("\r");

        if (inputGlob.length < 2) {
          return;
        }

        final String serial = inputGlob[inputGlob.length - 2];
        if (serial.length() == 0) {
          return;
        }

        final String parsedSerial = serial.substring(1);

        // (separated by commas specified in your Arduino program)
        // PApplet.split(serial, ',');
        final String[] input = parsedSerial.split(",");

        // input is 11 digits long for arduino 1.6 hex. No DPAD or Start buttons (hence, not 16).
        // [0, 0, 0, 0, 0, 0, 0, 126, 117, 126, 129]
        // [A, B, X, Y, Z, L, R, LS X, LS Y, CS X, CS Y]
        // Above ls/cs values are resting values, +/- 2

        if (this.logGCCInputs) {
          LoggingUtils.logMessage("Input string is: \n" + Arrays.toString(input), 0.2);
        }

        if (input.length != this.maxButtonIndex + 1) {
          return;
        }

        // TODO: Find a way to enqueue the super.image calls and then render all of them at the end, so that we can let the user specify the render order.
        this.arduinoSticks.forEach(stick ->
            this.drawStick(stick, input[stick.getXInputIndex()], input[stick.getYInputIndex()])
        );

        this.arduinoButtons.forEach(button -> this.drawButton(button, input[button.getArduinoInputIndex()]));
      }
    } catch (final Exception exc) {
      LoggingUtils.errorMessageWindow(
          "Unknown fatal error.",
          "Program failed due to unknown error: " + exc.getMessage() +
            "\nStacktrace:\n" + ExceptionUtils.getStackTrace(exc),
          true,
          true
      );
    }
  }

  public void drawStick(final ArduinoStick stick, final String currentXValue, final String currentYValue) {
    super.imageMode(CENTER);
    super.image(stick.getStickBaseImage(), stick.getXRenderPosition(), stick.getYRenderPosition());

    final int ax = Integer.parseInt(currentXValue);
    final int ay = Integer.parseInt(currentYValue);
    final float stickXPosition = stick.getXRenderPosition() + (ax / stick.getScale()) - (stick.getDistance() / 2.0f);
    final float stickYPosition = stick.getYRenderPosition() - (ay / stick.getScale()) + (stick.getDistance() / 2.0f);

    super.image(stick.getStickImage(), stickXPosition, stickYPosition);
    super.imageMode(CORNERS);
  }

  public void drawButton(final ArduinoButton arduinoButton, final String currentButtonValue) {
    final PImage image = currentButtonValue.equals("0") ? arduinoButton.getBackImage() : arduinoButton.getFrontImage();
    super.image(image, arduinoButton.getXRenderPosition(), arduinoButton.getYRenderPosition());
  }

  public static void main(final String[] args){
    // I don't recall why `--present` is needed. Comments are important, kids.
    final Set<String> argSet = new HashSet<>(
        Arrays.asList("--present", MainSketch.class.getCanonicalName())
    );

    Optional.ofNullable(args).ifPresent(argArray -> argSet.addAll(Arrays.asList(args)));

    // If empty, toArray will use the type info to make an appropriately sized array.
    PApplet.main(argSet.toArray(new String[0]));
  }
}
