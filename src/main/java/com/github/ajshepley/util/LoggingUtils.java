package com.github.ajshepley.util;

import javax.swing.JOptionPane;

public class LoggingUtils {

  private void LoggingUtils() {
    // no-op
  }

  // Random number between 0 and 1 will log if less than chance.
  public static void logMessage(final String message, final double chance) {
    if (Math.random() < chance) {
      System.out.println(message);
    }
  }

  public static void errorMessageWindow(
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
}
