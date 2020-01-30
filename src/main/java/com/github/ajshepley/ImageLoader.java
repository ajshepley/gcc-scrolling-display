package com.github.ajshepley;

import processing.core.PImage;

public interface ImageLoader {
  // Loads an image at `./data/imageName`
  PImage loadImage(final String imageName);
}
