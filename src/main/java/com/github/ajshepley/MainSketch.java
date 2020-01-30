package com.github.ajshepley;

import com.github.ajshepley.util.LoggingUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

  // TODO: rename serialPort
  private Serial serialPort;

  private final List<ArduinoButton> arduinoButtons = new ArrayList<ArduinoButton>();

  // TODO: Rename these arrays.
  private final PImage[] stick_bases = new PImage[2];
  private final PImage[] sticks = new PImage[2];

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

    this.setupButtons();

    this.loadImages(this.stick_bases, this.sticks);

    super.background(0,0,0);
  }

  // TODO: Extract to json or ini file, load dynamically.
  private void setupButtons() {
    final ArduinoButton aButton = this.arduinoButtonFactory.createButton(ButtonIndexes.A, 340f, 160f, "a_press.png", "a.png");
    final ArduinoButton bButton = this.arduinoButtonFactory.createButton(ButtonIndexes.B, 300f, 230f, "b_press.png", "b.png");
    final ArduinoButton xButton = this.arduinoButtonFactory.createButton(ButtonIndexes.X, 420f, 145f, "x_press.png", "x.png");
    final ArduinoButton yButton = this.arduinoButtonFactory.createButton(ButtonIndexes.Y, 323f, 112f, "y_press.png", "y.png");
    final ArduinoButton lButton = this.arduinoButtonFactory.createButton(ButtonIndexes.L, 55f, 90f, "l_press.png", "l.png");
    final ArduinoButton rButton = this.arduinoButtonFactory.createButton(ButtonIndexes.R, 190f, 90f, "r_press.png", "r.png");
    final ArduinoButton zButton = this.arduinoButtonFactory.createButton(ButtonIndexes.Z, 190f, 165f, "z_press.png", "z.png");

    // TODO: May be better to return a new collection rather than mutate local state.
    Collections.addAll(this.arduinoButtons, aButton, bButton, xButton, yButton, lButton, rButton, zButton);
  }

  // TODO: Extract/simplify method?
  // TODO: Throw error if an image can't be found. Maybe make error vs. log configurable
  // super.loadImage sucks visually but it's important to distinguish Processing calls, for now.
  private void loadImages(
      final PImage[] stick_bases,
      final PImage[] sticks
  ) {
    stick_bases[0] = super.loadImage("a_stick_base.png");
    sticks[0] = super.loadImage("a_stick.png");

    stick_bases[1] = super.loadImage("c_stick_base.png");
    sticks[1] = super.loadImage("c_stick.png");
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

        // FIXME: New input is 11 digits long for arduino 1.6 hex. No DPAD or Start buttons (hence, not 16).
        // [0, 0, 0, 0, 0, 0, 0, 126, 117, 126, 129]
        // [A, B, X, Y, Z, L, R, LS X, LS Y, CS X, CS Y]
        // Above ls/cs values are resting values, +/- 2

        if (this.logGCCInputs) {
          LoggingUtils.logMessage("Input string is: \n" + Arrays.toString(input), 0.2);
        }

        if (input.length != ButtonIndexes.MAX_INDEX + 1) {
          return;
        }

        draw_stick_base(0, 100, 220, this.stick_bases); // a stick
        draw_stick_base(1, 240, 260, this.stick_bases); // c stick

        this.arduinoButtons.forEach(button -> this.drawButton(button, input[button.getArduinoInputIndex()]));

        // TODO: Extract magic number indexes to static vars.
        draw_stick(input, ButtonIndexes.L_STICK_X, ButtonIndexes.L_STICK_Y, 0, 100, 220, 90, 2.844f, this.sticks); // a stick
        draw_stick(input, ButtonIndexes.C_STICK_X, ButtonIndexes.C_STICK_Y, 1, 240, 260, 80, 3.2f, this.sticks); // c stick
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

  // TODO: Rename this and similar methods to drawStickBase, etc.
  // TODO: Extract to separate class, provide self as a PApplet object and call back.
  public void draw_stick_base(
      final int stickBaseIndex,
      final int x,
      final int y,
      final PImage[] stick_bases
  ) {
    // TODO: Normalize around one imageMode?
    super.imageMode(CENTER);
    super.image(stick_bases[stickBaseIndex], x, y);
    super.imageMode(CORNERS);
  }

  public void draw_stick(
      final String[] input,
      final int xInputIndex,
      final int yInputIndex,
      final int stickImageIndex,
      final int x,
      final int y,
      final int distance,
      final float scale,
      final PImage[] sticks
  ) {
    super.imageMode(CENTER);
    final int ax = Integer.parseInt(input[xInputIndex]);
    final int ay = Integer.parseInt(input[yInputIndex]);

    final float stickXPosition = x + (ax / scale) - (distance / 2.0f);
    final float stickYPosition = y - (ay / scale) + (distance / 2.0f);

    super.image(sticks[stickImageIndex], stickXPosition, stickYPosition);
    super.imageMode(CORNERS);
  }

  public void drawButton(final ArduinoButton arduinoButton, final String currentButtonValue) {
    final PImage image = currentButtonValue.equals("0") ? arduinoButton.getBackImage() : arduinoButton.getFrontImage();
    super.image(image, arduinoButton.getX(), arduinoButton.getY());
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
