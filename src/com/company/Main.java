package com.company;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.swing.*;


public class Main extends Application{

    static double width = 700, height = 800;
    Image imgRocket = new Image("red-rocket.png");
    Image imgAsteroid = new Image("asteroid.png");
    public static int score = 0;
    Asteroid[] asteroid;
    Rocket rocket;
    Star[] star = new Star[30];
    Label scoreLabel, loose;
    Label[] menu;

    Scene scene;

    private boolean goRight, goLeft, move, pause, pauseBullet, endGame = true;
    static int isPausePressed = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        rocket = new Rocket(imgRocket);
        asteroid = new Asteroid[5];

        for(int i = 0; i < asteroid.length; i++)
            asteroid[i] = new Asteroid(imgAsteroid, width, height);

        for(int i = 0; i < star.length; i++)
            star[i] = new Star();

        Group root = new Group(rocket);
        scene = new Scene(root, width, height, Color.GREY);
        rocket.setVisible(false);

        root.getChildren().addAll(star);
        root.getChildren().addAll(asteroid);

        setArrayOfNodesInvisible(asteroid);

        primaryStage.setTitle("Rocket");

        menu = printMenu();
        root.getChildren().addAll(menu);

        scoreLabel = createLabel("Score: " + score, 540, 0);
        root.getChildren().add(scoreLabel);
        loose = createLabel("You lose", 300, 300);
        root.getChildren().add(loose);
        loose.setVisible(false);

        scene.setOnKeyPressed((event ->  {
            switch(event.getCode()) {
                case A: goLeft = true; break;
                case D: goRight = true; break;
                case W: move = true; break;
                case SPACE: if(!endGame) {
                    reactOnPause();
                    isPausePressed++;
                }
                    break;
                case ENTER:
                    if(endGame)
                        startGame();
                    break;
            }
        }));

        scene.setOnKeyReleased((event -> {
                    switch (event.getCode()) {
                        case A:  goLeft = false; break;
                        case D:  goRight = false; break;
                        case W: move = false; break;
                    }
         }));

        scene.setOnMouseClicked((event -> {
            int bulletLength = 5;
            double centerRocketX = rocket.getX() + 52;
            double centerRocketY = rocket.getY() + 26;
            double angle = fromGradToRadian(rocket.getRotate());

            int standardWidth = 52, standardHeight = -1; //width and height till weapon of the rocket

            double x = centerRocketX + standardWidth * Math.cos(angle)
                    - standardHeight * Math.sin(angle);
            double y = centerRocketY + standardHeight * Math.cos(angle)
                    + standardWidth * Math.sin(angle);

            Bullet bullet = new Bullet(x, y, x + bulletLength, y, angle);
            root.getChildren().add(bullet);
        }));

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void reactOnPause(){
        Timer timer = new Timer(100, (event) -> {
            if(isPausePressed == 1){
                pause = true;
                scene.setFill(Color.DARKGREY);
                setArrayOfNodesVisible(menu);
            }
            else if (isPausePressed == 2){
                pause = false;
                createTimer();
                pauseBullet = true;
                scene.setFill(Color.BLACK);
                setArrayOfNodesInvisible(menu);
                isPausePressed = 0;
            }
        });
        timer.start();
    }

    private void startGame(){
        rocket.setVisible(true);
        rocket.setX(0);
        rocket.setY(0);
        rocket.setRotate(0);
        scene.setFill(Color.BLACK);

        endGame = false;

        setArrayOfNodesVisible(asteroid);
        setArrayOfNodesInvisible(menu);

        for (Asteroid asteroid1 : asteroid) {
            asteroid1.ifDestroyed();
        }

        loose.setVisible(false);

        createTimer();
        score = 0;
    }

    private void endGame(){
        loose.setVisible(true);
        scene.setFill(Color.GRAY);
        setArrayOfNodesInvisible(asteroid);
        endGame = true;
    }

    private Label[] printMenu(){
        Label labelEnter = createLabel("Press ENTER to start game", 20);
        Label labelSpace = createLabel("Press SPACE to pause game", 60);
        Label labelMove = createLabel("Press W to move rocket", 100);
        Label labelLeft = createLabel("Press A to rotate rocket left", 140);
        Label labelRight = createLabel("Press D to rotate rocket right", 180);

        return new Label[] {labelEnter, labelSpace, labelMove, labelLeft, labelRight};
    }

    private void setArrayOfNodesInvisible(Node[] array){
        for (Node node1 : array) {
            node1.setVisible(false);
        }
    }

    private void setArrayOfNodesVisible(Node[] array){
        for (Node node1 : array) {
            node1.setVisible(true);
        }
    }

    private Label createLabel(String text, int y){
        return createLabel(text, 0, y);
    }

    private Label createLabel(String text, int x, int y){
        Label label = new Label(text);
        label.setTextFill(Color.GREEN);
        label.setLayoutY(y);
        label.setLayoutX(x);
        label.setFont(new Font(22));
        return label;
    }

    private void createTimer(){
        AnimationTimer timer = new AnimationTimer()  {
            @Override
            public void handle(long now) {
                repaint();

                if (collision()) {
                    endGame();
                    stop();
                }

                if (pause)
                    stop();
            }
        };
        timer.start();
    }

    private void repaint(){
        if (goLeft) rocket.setRotate(rocket.getRotate() - 5);
        if (goRight) rocket.setRotate(rocket.getRotate() + 5);
        if (move) rocket.move();

        scoreLabel.setText("Score: " + score);

        for (Asteroid asteroid : asteroid)
            asteroid.move();
    }

    //using some magical numbers to calculate collision, which help to create sprite for rectangle and circle
    private boolean collision(){
        double rectX1 = rocket.getX() + 19, rectX2 = rocket.getX() + 91; // X-values for Rocket sprite (rectangle)
        double rectY1 = rocket.getY() + 9, rectY2 = rocket.getY() + 43; // Y-values for Rocket sprite (rectangle)

        double rectCenterX = rocket.getX() + (91 - 19) / 2;
        double rectCenterY = rocket.getY() + (43 - 9) / 2;

        double angle = fromGradToRadian(rocket.getRotate());

        double closestX, closestY;
        int radius = 20;

        for (Asteroid asteroid : asteroid) {
            double circleCenterX = asteroid.getX() + 20, circleCenterY = asteroid.getY() + 22; //Circle sprite for each asteroid in array

            double unrotatedCircleX = Math.cos(angle) * (circleCenterX - rectCenterX) -
                    Math.sin(angle) * (circleCenterY - rectCenterY) + rectCenterX;
            double unrotatedCircleY  = Math.sin(angle) * (circleCenterX - rectCenterX) +
                    Math.cos(angle) * (circleCenterY - rectCenterY) + rectCenterY;

            closestX = findClosestPoint(unrotatedCircleX, rectX1, rectX2);
            closestY = findClosestPoint(unrotatedCircleY, rectY1, rectY2);

            double distance = findDistance(unrotatedCircleX, unrotatedCircleY, closestX, closestY);
            if (distance < radius)
                return true;
        }
        return false;
    }

    public double fromGradToRadian(double grad){
        return grad * Math.PI / 180;
    }

    public double findClosestPoint(double unrotatedCircle, double rect1, double rect2){
        if(unrotatedCircle < rect1)
            return rect1;
        else if(unrotatedCircle > rect2)
            return rect2;
        else return unrotatedCircle;
    }

    public double findDistance(double fromX, double fromY, double toX, double toY){
        double a = Math.abs(fromX - toX);
        double b = Math.abs(fromY - toY);

        return Math.sqrt((a * a) + (b * b));
    }

    private class Bullet extends Line implements Movable{
        boolean enabled = true;
        double rotation;

        public Bullet(double startX, double startY, double endX, double endY, double rotation){
            super(startX, startY, endX, endY);
            this.rotation = rotation;
            startTimer();
            setStroke(Color.WHITE);
        }

        private void startTimer(){
            AnimationTimer timerForBullets = new AnimationTimer()  {
                @Override
                public void handle(long now) {
                    move();
                    kill();

                    if(pause) {
                        stop();
                        createPause();
                    }

                    if(!enabled || collision()) {
                        stop();
                        setVisible(false);
                    }
                }
            };
            timerForBullets.start();
        }

        public void createPause(){
            Timer timer = new Timer(100, (event) -> {
                if (pauseBullet){
                    startTimer();
                    pauseBullet = false;

                    try {
                        stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            timer.start();
        }

        public void move(){
            int velocity = 6;

            double dx, dy;
            dx = velocity * Math.cos(rotation);
            dy = velocity * Math.sin(rotation);

            setStartX(getStartX() + dx);
            setEndX(getEndX() + dx);
            setStartY(getStartY() + dy);
            setEndY(getEndY() + dy);
        }

        void kill(){
            double centerX = (getStartX() + getEndX()) / 2;
            double centerY = (getStartY() + getEndY()) / 2;

            double sizeX = 40, sizeY = 44;

            for (Asteroid asteroid1 : asteroid) {
                if(centerX > asteroid1.getX() && centerX < (asteroid1.getX() + sizeX)
                        && centerY > asteroid1.getY() && centerY < (asteroid1.getY() + sizeY) && enabled) {

                    asteroid1.ifDestroyed();
                    score++;

                    enabled = false;
                }
            }
        }
    }
}
