package se.miun.dt176g.ebni2100.reactive.Client;

import java.awt.*;

public class Clear extends Shape {

    private final boolean clearShapes;

    public Clear(Color color, int thickness) {
        super(color, thickness);
        this.clearShapes = true;
    }

    public boolean isClearShapes(){
        return clearShapes;
    }

    @Override
    public void draw(Graphics g) {

    }
}
