package com.github.ajshepley;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import processing.core.PApplet;
import processing.core.PImage;
import processing.serial.Serial;

public class MainSketch extends PApplet {

  private static final int WINDOW_WIDTH = 500;
  private static final int WINDOW_HEIGHT = 500;

  // the number 10 is ASCII for linefeed (end of serial.println),
  // later we will look for this to break up individual messages
  // Adam: Not sure if this is utilized by PApplet in some way.
  protected final int end = 10;

  // TODO: Can this be inlined in draw()?
  protected String serial;

  // TODO: rename serialPort
  // TODO: See if it works if we inline the initialization/assignment here, make final.
  protected Serial port;

  // TODO: Rename these arrays.
  private final PImage[] buttons = new PImage[12];
  private final PImage[] pressed_buttons = new PImage[12];
  private final PImage[] stick_bases = new PImage[2];
  private final PImage[] sticks = new PImage[2];

  // TODO: Unneeded?
  private PImage gcc;

  @Override
  public void settings() {
    super.size(WINDOW_WIDTH, WINDOW_HEIGHT);
  }

  @Override
  public void setup() {
    // initializing the object by assigning a port and baud rate (must match that of Arduino)
    this.port = new Serial(this, Serial.list()[1], 115200);
    // function from serial library that throws out the first reading,
    // in case we started reading in the middle of a string from Arduino
    this.port.clear();

    this.loadImages(this.buttons, this.pressed_buttons, this.stick_bases, this.sticks);

    super.background(0,0,0);
  }

  // TODO: Extract/simplify method?
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

    buttons[7] = super.loadImage("d_left.png");
    pressed_buttons[7] = super.loadImage("d_left_press.png");

    buttons[8] = super.loadImage("d_up.png");
    pressed_buttons[8] = super.loadImage("d_up_press.png");

    buttons[9] = super.loadImage("d_down.png");
    pressed_buttons[9] = super.loadImage("d_down_press.png");

    buttons[10] = super.loadImage("d_right.png");
    pressed_buttons[10] = super.loadImage("d_right_press.png");

    buttons[11] = super.loadImage("start.png");
    pressed_buttons[11] = super.loadImage("start_press.png");

    stick_bases[0] = super.loadImage("a_stick_base.png");
    sticks[0] = super.loadImage("a_stick.png");

    stick_bases[1] = super.loadImage("c_stick_base.png");
    sticks[1] = super.loadImage("c_stick.png");
  }

  @Override
  public void draw() {
    final String raw = this.port.readString();

    if (StringUtils.isNotBlank(raw)) {
      final String[] inputGlob = PApplet.split(raw, '\r');

      if (inputGlob.length < 2) {
        return;
      }

      serial = inputGlob[inputGlob.length - 2];
      if (serial.length() == 0) {
        return;
      }

      serial = serial.substring(1);

      // a new array (called 'a') that stores values into separate cells
      // (separated by commas specified in your Arduino program)
      final String[] input = PApplet.split(serial, ',');

      if (input.length != 16) {
        return;
      }

      draw_stick_base(0, 100, 220, this.stick_bases); // a stick
      draw_stick_base(1, 240, 260, this.stick_bases); // c stick

      draw_button(input, 0, 340, 160, this.buttons, this.pressed_buttons); // a
      draw_button(input, 1, 300, 230, this.buttons, this.pressed_buttons); // b
      draw_button(input, 2, 420, 145, this.buttons, this.pressed_buttons); // x
      draw_button(input, 3, 323, 112, this.buttons, this.pressed_buttons); // y

      draw_button(input, 5, 55, 90, this.buttons, this.pressed_buttons); // l
      draw_button(input, 6, 190, 90, this.buttons, this.pressed_buttons); // r

      draw_button(input, 4, 190, 165, this.buttons, this.pressed_buttons); // z
      draw_button(input, 11, 300, 185, this.buttons, this.pressed_buttons); // start

      draw_button(input, 7, 20, 290, this.buttons, this.pressed_buttons); // d_left
      draw_button(input, 8, 60, 290, this.buttons, this.pressed_buttons); // d_up
      draw_button(input, 9, 100, 290, this.buttons, this.pressed_buttons); // d_down
      draw_button(input, 10, 140, 290, this.buttons, this.pressed_buttons); // d_right

      // TODO: Extract magic number indexes to static vars.
      draw_stick(input, 12, 0, 100, 220, 90, 2.844f, this.sticks); // a stick
      draw_stick(input, 14, 1, 240, 260, 80, 3.2f, this.sticks); // c stick
    }
  }

  // TODO: Rename this and similar methods to drawStickBase, etc.
  // TODO: Extract to separate class, provide self as a PApplet object and call back.
  public void draw_stick_base(final int index, final int x, final int y, final PImage[] stick_bases) {
    // TODO: Normalize around one imageMode?
    super.imageMode(CENTER);
    super.image(stick_bases[index], x, y);
    super.imageMode(CORNERS);
  }

  public void draw_stick(
      final String[] input,
      final int input_index,
      final int index,
      final int x,
      final int y,
      final int distance,
      final float scale,
      final PImage[] sticks
  ) {
    super.imageMode(CENTER);
    final int ax = Integer.parseInt(input[input_index]);
    final int ay = Integer.parseInt(input[input_index + 1]);

    // TODO: Split into methods, fix integer division warnings.
    super.image(sticks[index], x + (ax / scale) - (distance / 2), y - (ay / scale) + (distance / 2));
    super.imageMode(CORNERS);
  }

  public void draw_button(
      final String[] input,
      final int index,
      final int x,
      final int y,
      final PImage[] buttons,
      final PImage[] pressed_buttons
  ) {
    if (input[index].equals("0")) {
      super.image(buttons[index], x, y);
    } else {
      super.image(pressed_buttons[index], x, y);
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
