package com.company;

import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.swing.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yuliia on 02-May-17.
 */
public class Star extends Circle{
    public Star(){
        super( 3);
        create();
    }

    private void create() {
        Random rand = new Random ();
        int x = rand.nextInt((int)Main.width), y = rand.nextInt((int)Main.height);
        setCenterX(x);
        setCenterY(y);
        setStroke(Color.WHITE);
        setFill(Color.ANTIQUEWHITE);
        yellow = false;
        animate();
    }

    public void animate(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater((() ->{
                        if (!yellow) {
                            setFill(Color.YELLOW);
                            yellow = true;
                        } else {
                            setFill(Color.WHITESMOKE);
                            yellow = false;
                        }

                }));
            }


        }, 0, 1000);
    }

    boolean yellow;

}
