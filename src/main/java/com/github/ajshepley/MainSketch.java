package com.github.ajshepley;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;
import processing.core.PApplet;
import processing.core.PImage;
import processing.serial.Serial;

public class MainSketch extends PApplet {

  // must match that of Arduino
  private static final int ARDUINO_BAUD_RATE = 115200;
  private static final int WINDOW_WIDTH = 500;
  private static final int WINDOW_HEIGHT = 500;

  // the number 10 is ASCII for linefeed (end of serial.println),
  // later we will look for this to break up individual messages
  // Adam: Not sure if this is utilized by PApplet in some way.
  public final int end = 10;

  // TODO: rename serialPort
  // TODO: See if it works if we inline the initialization/assignment here, make final.
  private Serial port;

  // TODO: Rename these arrays.
  private final PImage[] buttons = new PImage[12];
  private final PImage[] pressed_buttons = new PImage[12];
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
      this.errorMessageWindow(
          "Cannot start GCC Display.",
          "Couldn't detect the serial port. Make sure your Arduino is connected and powered.\n" +
          "The program will now quit.",
          true,
          true
      );
    }

    // initializing the object by assigning a port and baud rate (must match that of Arduino)
    this.port = new Serial(this, Serial.list()[1], ARDUINO_BAUD_RATE);
    // function from serial library that throws out the first reading,
    // in case we started reading in the middle of a string from Arduino
    this.port.clear();

    this.loadImages(this.buttons, this.pressed_buttons, this.stick_bases, this.sticks);

    super.background(0,0,0);
  }

  // TODO: Extract/simplify method?
  // TODO: Throw error if an image can't be found. Maybe make error vs. log configurable
  // super.loadImage sucks visually but it's important to distinguish Processing calls, for now.
  private void loadImages(
    final PImage[] buttons,
    final PImage[] pressed_buttons,
    final PImage[] stick_bases,
    final PImage[] sticks
  ) {
    // TODO: Remove unpressed buttons. Maybe useful for a negative edge display feature, though?
    buttons[0] = super.loadImage("a.png");
    pressed_buttons[0] = super.loadImage("a_press.png");

    buttons[1] = super.loadImage("b.png");
    pressed_buttons[1] = super.loadImage("b_press.png");

    buttons[2] = super.loadImage("x.png");
    pressed_buttons[2] = super.loadImage("x_press.png");

    buttons[3] = super.loadImage("y.png");
    pressed_buttons[3] = super.loadImage("y_press.png");

    buttons[4] = super.loadImage("z.png");
    pressed_buttons[4] = super.loadImage("z_press.png");

    buttons[5] = super.loadImage("l.png");
    pressed_buttons[5] = super.loadImage("l_press.png");

    buttons[6] = super.loadImage("r.png");
    pressed_buttons[6] = super.loadImage("r_press.png");

    stick_bases[0] = super.loadImage("a_stick_base.png");
    sticks[0] = super.loadImage("a_stick.png");

    stick_bases[1] = super.loadImage("c_stick_base.png");
    sticks[1] = super.loadImage("c_stick.png");
  }

  @Override
  public void draw() {
    final String raw = this.port.readString();

//    if (new java.util.Random().nextInt(10) / 2 > 3 && raw != null) {
//      System.out.println("Raw string is: \n[" + raw.replace('\n', ' ') + "]");
//    }

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

      // TODO: Extract to logInput(input[]) method, make randomness clearer.
      if (new java.util.Random().nextInt(10) / 2 > 3) {
        System.out.println("Input string is: \n" + Arrays.toString(input) + "");
      }

      // This won't work. The input for the 1.6 hex is now, inexplicably, only 11 digits long.
      // Perhaps this is due to differences with String.split vs.
      if (input.length != ButtonIndexes.MAX_INDEX + 1) {
        return;
      }

      draw_stick_base(0, 100, 220, this.stick_bases); // a stick
      draw_stick_base(1, 240, 260, this.stick_bases); // c stick

      draw_button(input, ButtonIndexes.A, 340, 160, this.buttons, this.pressed_buttons); // a
      draw_button(input, ButtonIndexes.B, 300, 230, this.buttons, this.pressed_buttons); // b
      draw_button(input, ButtonIndexes.X, 420, 145, this.buttons, this.pressed_buttons); // x
      draw_button(input, ButtonIndexes.Y, 323, 112, this.buttons, this.pressed_buttons); // y
      draw_button(input, ButtonIndexes.L, 55, 90, this.buttons, this.pressed_buttons); // l
      draw_button(input, ButtonIndexes.R, 190, 90, this.buttons, this.pressed_buttons); // r
      draw_button(input, ButtonIndexes.Z, 190, 165, this.buttons, this.pressed_buttons); // z

      // TODO: Extract magic number indexes to static vars.
      draw_stick(input, ButtonIndexes.L_STICK_X, ButtonIndexes.L_STICK_Y, 0, 100, 220, 90, 2.844f, this.sticks); // a stick
      draw_stick(input, ButtonIndexes.C_STICK_X, ButtonIndexes.C_STICK_Y, 1, 240, 260, 80, 3.2f, this.sticks); // c stick
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

    // TODO: Split into methods, fix integer division warnings.
    super.image(sticks[stickImageIndex], x + (ax / scale) - (distance / 2), y - (ay / scale) + (distance / 2));
    super.imageMode(CORNERS);
  }

  public void draw_button(
      final String[] input,
      final int arduinoInputIndex,
      final int x,
      final int y,
      final PImage[] buttons,
      final PImage[] pressed_buttons
  ) {
    if (input[arduinoInputIndex].equals("0")) {
      super.image(buttons[arduinoInputIndex], x, y);
    } else {
      super.image(pressed_buttons[arduinoInputIndex], x, y);
    }
  }

  private void errorMessageWindow(
      final String title,
      final String message,
      final boolean dumpStack,
      final boolean exit
  ) {
    // TODO: Swing sucks but it gets the job done for now.
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    if (dumpStack) {
      Thread.dumpStack();
    }

    if (exit) {
      System.exit(-1);
    }
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
