package com.github.ajshepley;

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
//    PApplet.main("MainSketch");
    PApplet.main(new String[] { "--present", MainSketch.class.getCanonicalName() });
  }
}
