package com.github.ajshepley.buttons;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ButtonUtils {

  private ButtonUtils() {
    // hidden
  }

  public static int calculateLargestIndex(final List<ArduinoButton> buttons, final List<ArduinoStick> sticks) {
    final int largestButtonIndex = Collections.max(
        buttons,
        Comparator.comparingInt(ArduinoButton::getArduinoInputIndex)
    ).getArduinoInputIndex();

    final int largestStickXIndex = Collections.max(
        sticks,
        Comparator.comparing(ArduinoStick::getXInputIndex)
    ).getArduinoInputIndex();

    final int largestStickYIndex = Collections.max(
        sticks,
        Comparator.comparingInt(ArduinoStick::getYInputIndex)
    ).getYInputIndex();

    return Math.max(largestButtonIndex, Math.max(largestStickXIndex, largestStickYIndex));
  }
}
