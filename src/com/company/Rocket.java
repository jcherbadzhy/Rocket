package com.company;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;

/**
 * Created by Yuliia on 24-Apr-17.
 */
public class Rocket extends ImageView implements Movable{

    public Rocket(Image img){
        super(img);
    }

    private void moveTo(double x, double y) {
        final double cx = getBoundsInLocal().getWidth()  / 2;
        final double cy = getBoundsInLocal().getHeight() / 2;

        if (x - cx >= 0 &&
                x + cx <= Main.width &&
                y - cy >= 0 &&
                y + cy <= Main.height) {
            setX(x - cx);
            setY(y - cy);
        }
    }

    public void move() {
        int velocity = 5;

        double cx = getBoundsInLocal().getWidth()  / 2;
        double cy = getBoundsInLocal().getHeight() / 2;

        double x = cx + getX() + velocity * Math.cos(Math.toRadians(getRotate()));
        double y = cy + getY() + velocity * Math.sin(Math.toRadians(getRotate()));

        moveTo(x, y);
    }
}
