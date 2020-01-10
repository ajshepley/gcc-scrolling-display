package com.github.ajshepley;

import processing.core.PApplet;

public class MainSketch extends PApplet {
  public void settings(){
    size(200, 200);
  }

  public void draw(){
    background(0);
    ellipse(mouseX, mouseY, 20, 20);
  }

  public static void main(String[] args){
//    PApplet.main("MainSketch");
    PApplet.main(new String[] { "--present", MainSketch.class.getCanonicalName() });
  }
}
