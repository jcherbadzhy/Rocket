package com.company;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

/**
 * Created by Yuliia on 25-Apr-17.
 */
public class Asteroid extends ImageView implements Movable{
    private int dx, dy = 0;
    private double widthCorner, heightCorner;

    public Asteroid(Image img, double width, double height){
        super(img);
        widthCorner = width;
        heightCorner = height;
        create();
    }

    private void create(){
        Random random = new Random();
        dx = -3;
        setX(widthCorner);
        setY(random.nextInt((int)heightCorner));

        while (dy == 0) {
            dy = random.nextInt(7) - 3;
        }
    }

    public void move(){
        double cx = getBoundsInLocal().getWidth()  / 2;
        double cy = getBoundsInLocal().getHeight() / 2;

        double x = cx + getX() + dx;
        double y = cy + getY() + dy;

        setX(x - cx);
        setY(y - cy);

        if((getX() < 0) || (getX() > widthCorner) || (getY() < 0) || (getY() > heightCorner))
            create();
    }

    public void ifDestroyed(){
        create();
    }

}
