# gcc-scrolling-display

A scrolling input display for GameCube controllers, similar to other fighting games.

Works with Arduino mod (tested with hex version 1.6).

Based on Captain L's controller display project: https://github.com/captainl/gcc-display

## Building and Running

The intellij project should be able to build itself, when imported as a gradle project.

The gradle project will build and create an executable in build/executable, as well as a jar in build/libs that can be executed with `java -jar`.

The `data` folder must be present in the same directory as the jarfile or EXE execution.

Only tested on Windows, currently.