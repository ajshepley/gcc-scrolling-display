package com.github.ajshepley;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import processing.core.PApplet;

public class MainSketch extends PApplet {
  public void settings(){
    this.size(200, 200);
  }

  public void draw(){
    this.background(0);
    this.ellipse(this.mouseX, this.mouseY, 20, 20);
  }

  public static void main(final String[] args){
    final Set<String> argSet = new HashSet<>(
        Arrays.asList("--present", MainSketch.class.getCanonicalName())
    );

    Optional.ofNullable(args).ifPresent(argArray -> argSet.addAll(Arrays.asList(args)));

    // If empty, toArray will use the type info to make an appropriately sized array.
    PApplet.main(argSet.toArray(new String[0]));
  }
}
