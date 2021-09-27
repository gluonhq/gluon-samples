package com.gluonhq.sample;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HelloWeb extends Application {

    private final static int UTC = 2; // fix
    private VBox root;

    @Override
    public void start(Stage stage) {
        ImageView imageView = new ImageView(new Image(HelloWeb.class.getResourceAsStream("openduke.png")));
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);
        RotateTransition rotate = new RotateTransition(Duration.seconds(1), imageView);
        rotate.setOnFinished(f -> playAnimation());
        rotate.setByAngle(360.0);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> rotate.playFromStart());


        root = new VBox(imageView);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(new HBox(createClock(), root), 640, 480);
//        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        pause.play();
    }

    @Override
    public void stop() {
        stopAnimation();
    }

    public static void main(String[] args) {
        launch(HelloWeb.class, args);
    }

    private void playAnimation() {
        timeline.setOnFinished(f -> {
            root.getChildren().setAll(createContent2());
            fade.setOnFinished(f2 -> {
                root.getChildren().setAll(createContent3());
                fill.setOnFinished(f3 -> {
                    root.getChildren().setAll(createContent4());
                    parallel.setOnFinished(f4 -> {
                        root.getChildren().setAll(createContent5());
                        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
                        pauseTransition.setOnFinished(f5 -> {
                            root.getChildren().setAll(createContent6());
                        });
                        pauseTransition.play();
                    });
                    parallel.play();
                });
                fill.play();
            });
            fade.play();
        });
        root.getChildren().setAll(createContent1());
        timeline.play();
    }

    private void stopAnimation() {
        timeline.stop();
        if (fade != null) fade.stop();
        if (fill != null) fill.stop();
        if (parallel != null) parallel.stop();
    }

    // https://gist.github.com/jewelsea/2658491#file-clock-java-L49
    private Node createClock() {
        final Circle border = new Circle(100, 100, 100);
        border.setFill(Color.BLACK);
        final Circle face = new Circle(100, 100, 98);
        face.setFill(Color.WHITESMOKE);
        final Line hourHand = new Line(0, 0, 0, -50);
        hourHand.setTranslateX(100);
        hourHand.setTranslateY(100);
        hourHand.setStroke(Color.BLUE);
        hourHand.setStrokeWidth(4);
        final Line minuteHand = new Line(0, 0, 0, -75);
        minuteHand.setTranslateX(100);
        minuteHand.setTranslateY(100);
        minuteHand.setStroke(Color.GREEN);
        minuteHand.setStrokeWidth(3);
        final Line secondHand = new Line(0, 15, 0, -88);
        secondHand.setTranslateX(100);
        secondHand.setTranslateY(100);
        secondHand.setStroke(Color.ORANGE);
        secondHand.setStrokeWidth(2.5);
        final Circle spindle = new Circle(100, 100, 5);
        Group ticks = new Group();
        for (int i = 0; i < 12; i++) {
            Line tick = new Line(0, -83, 0, -93);
            tick.setTranslateX(100);
            tick.setTranslateY(100);
            tick.setStrokeWidth(2);
            tick.getTransforms().add(new Rotate(i * (360.0 / 12.0)));
            ticks.getChildren().add(tick);
        }
        Group root = new Group(border, face, ticks, spindle, hourHand, minuteHand, secondHand);
        // initial time
        long milliseconds = System.currentTimeMillis();
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24) + UTC;

        final double seedSecondDegrees  = seconds * (360.0 / 60.0);
        final double seedMinuteDegrees  = (minutes + seedSecondDegrees / 360.0) * (360.0 / 60.0);
        final double seedHourDegrees    = (hours + seedMinuteDegrees / 360.0) * (360.0 / 12.0) ;

        final Rotate hourRotate  = new Rotate(seedHourDegrees);
        final Rotate minuteRotate = new Rotate(seedMinuteDegrees);
        final Rotate secondRotate = new Rotate(seedSecondDegrees);
        hourHand.getTransforms().add(hourRotate);
        minuteHand.getTransforms().add(minuteRotate);
        secondHand.getTransforms().add(secondRotate);

        final Timeline hourTime = new Timeline(
                new KeyFrame(Duration.hours(12),
                        new KeyValue(hourRotate.angleProperty(),
                                360 + seedHourDegrees,
                                Interpolator.LINEAR)));

        // the minute hand rotates once an hour.
        final Timeline minuteTime = new Timeline(
                new KeyFrame(Duration.minutes(60),
                        new KeyValue(minuteRotate.angleProperty(),
                                360 + seedMinuteDegrees,
                                Interpolator.LINEAR)));

        // move second hand rotates once a minute.
        final Timeline secondTime = new Timeline(
                new KeyFrame(Duration.seconds(60),
                        new KeyValue(secondRotate.angleProperty(),
                                360 + seedSecondDegrees,
                                Interpolator.LINEAR)));

        hourTime.setCycleCount(Animation.INDEFINITE);
        minuteTime.setCycleCount(Animation.INDEFINITE);
        secondTime.setCycleCount(Animation.INDEFINITE);

        // start the analogueClock.
        secondTime.play();
        minuteTime.play();
        hourTime.play();

        root.getTransforms().add(new Scale(0.5, 0.5));
        return root;
    }

    // Ensemble8 demos
    // https://github.com/openjdk/jfx/tree/master/apps/samples/Ensemble8

    // 1. InterpolatorApp
    private final Timeline timeline = new Timeline();

    public Parent createContent1() {
        Pane root = new Pane();
        root.setPrefSize(245, 230);
        root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

        // create circles by method createMovingCircle listed below
        // default interpolator
        Circle circle1 = createMovingCircle(Interpolator.LINEAR,
                1, 0.7, Color.RED);
        // circle slows down when reached both ends of trajectory
        Circle circle2 = createMovingCircle(Interpolator.EASE_BOTH,
                2, 0.45, Color.VIOLET);
        // circle slows down in the beginning of animation
        Circle circle3 = createMovingCircle(Interpolator.EASE_IN,
                3, 0.2, Color.BLUE);
        // circle slows down in the end of animation
        Circle circle4 = createMovingCircle(Interpolator.EASE_OUT,
                4, 0.35, Color.YELLOW);
        // one can define own behaviour of interpolator by spline method
        Circle circle5 = createMovingCircle(Interpolator.SPLINE(0.5, 0.1, 0.1, 0.5),
                5, 0.7, Color.GREEN);

        root.getChildren().addAll(circle1, circle2, circle3, circle4, circle5);
        return root;
    }

    private Circle createMovingCircle(Interpolator interpolator, int which,
                                      double opacity, Color color) {
        // create a transparent circle
        Circle circle = new Circle(45, 45, 40, color);
        // set initial opacity
        circle.setOpacity(opacity);
        circle.setCenterY((which * 35) + 5);
//         add effect
        circle.setEffect(new Blend());
        // create a timeline for moving the circle
        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        // create a keyValue for horizontal translation of circle to
        // the position 155px with given interpolator
        KeyValue keyValue = new KeyValue(circle.translateXProperty(), 155,
                interpolator);
        // create a keyFrame with duration 4s
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(4), keyValue);
        // add the keyframe to the timeline
        timeline.getKeyFrames().add(keyFrame);
        return circle;
    }

    // 2. FadeTransitionApp

    private FadeTransition fade;

    private Parent createContent2() {
        Pane root = new Pane();
        root.setPrefSize(105, 105);
        root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

        Rectangle rect = new Rectangle(0, 0, 100, 100);
        rect.setArcHeight(20);
        rect.setArcWidth(20);
        rect.setFill(Color.DODGERBLUE);
        root.getChildren().add(rect);

        fade = new FadeTransition(Duration.seconds(2), rect);
        fade.setFromValue(1);
        fade.setToValue(0.2);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);

        return root;
    }

    // 3.- FillTransitionApp

    private FillTransition fill;

    private Parent createContent3() {
        final Pane root = new Pane();
        root.setPrefSize(105, 105);
        root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

        Rectangle rect = new Rectangle(0, 0, 100, 100);
        rect.setArcHeight(20);
        rect.setArcWidth(20);
        rect.setFill(Color.DODGERBLUE);
        root.getChildren().add(rect);

        fill = new FillTransition(Duration.seconds(2), rect,
                Color.RED, Color.DODGERBLUE);
        fill.setCycleCount(2);
        fill.setAutoReverse(true);
        return root;
    }

    // 4.- ParallelTransitionApp

    private ParallelTransition parallel;

    private Parent createContent4() {
        final Pane root = new Pane();
        root.setPrefSize(400, 200);
        root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

        Rectangle rect = new Rectangle(-25,-25,50, 50);
        rect.setArcHeight(15);
        rect.setArcWidth(15);
        rect.setFill(Color.CRIMSON);
        rect.setTranslateX(50);
        rect.setTranslateY(75);
        root.getChildren().add(rect);

        // create parallel transition to do all 4 transitions at the same time
        FadeTransition fade = new FadeTransition(Duration.seconds(3), rect);
        fade.setFromValue(1);
        fade.setToValue(0.3);
        fade.setAutoReverse(true);

        TranslateTransition translate =
                new TranslateTransition(Duration.seconds(2));
        translate.setFromX(50);
        translate.setToX(320);
        translate.setCycleCount(2);
        translate.setAutoReverse(true);

        RotateTransition rotate = new RotateTransition(Duration.seconds(1));
        rotate.setByAngle(180);
        rotate.setCycleCount(2);
        rotate.setAutoReverse(true);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(2));
        scale.setToX(2);
        scale.setToY(2);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        parallel = new ParallelTransition(rect,
                fade, translate, rotate, scale);
        parallel.setCycleCount(2);
        parallel.setAutoReverse(true);

        return root;
    }

    // 5.- ColorApp

    private Parent createContent5() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setSpacing(6);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(
                createRectangle(Color.hsb(  0.0, 1.0, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb( 30.0, 1.0, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb( 60.0, 1.0, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(120.0, 1.0, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(160.0, 1.0, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(200.0, 1.0, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(240.0, 1.0, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(280.0, 1.0, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(320.0, 1.0, 1.0))  // hue, saturation, brightness
        );

        HBox hBox2 = new HBox();
        hBox2.setSpacing(6);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(
                createRectangle(Color.hsb(  0.0, 0.5, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb( 30.0, 0.5, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb( 60.0, 0.5, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(120.0, 0.5, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(160.0, 0.5, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(200.0, 0.5, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(240.0, 0.5, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(280.0, 0.5, 1.0)), // hue, saturation, brightness
                createRectangle(Color.hsb(320.0, 0.5, 1.0))  // hue, saturation, brightness
        );

        HBox hBox3 = new HBox();
        hBox3.setSpacing(6);
        hBox3.setAlignment(Pos.CENTER);
        hBox3.getChildren().addAll(
                createRectangle(Color.hsb(  0.0, 1.0, 0.5)), // hue, saturation, brightness
                createRectangle(Color.hsb( 30.0, 1.0, 0.5)), // hue, saturation, brightness
                createRectangle(Color.hsb( 60.0, 1.0, 0.5)), // hue, saturation, brightness
                createRectangle(Color.hsb(120.0, 1.0, 0.5)), // hue, saturation, brightness
                createRectangle(Color.hsb(160.0, 1.0, 0.5)), // hue, saturation, brightness
                createRectangle(Color.hsb(200.0, 1.0, 0.5)), // hue, saturation, brightness
                createRectangle(Color.hsb(240.0, 1.0, 0.5)), // hue, saturation, brightness
                createRectangle(Color.hsb(280.0, 1.0, 0.5)), // hue, saturation, brightness
                createRectangle(Color.hsb(320.0, 1.0, 0.5))  // hue, saturation, brightness
        );

        HBox hBox4 = new HBox();
        hBox4.setSpacing(6);
        hBox4.setAlignment(Pos.CENTER);
        hBox4.getChildren().addAll(
                createRectangle(Color.BLACK), //predefined color
                createRectangle(Color.hsb(0, 0, 0.1)), //defined by hue - saturation - brightness
                createRectangle(new Color(0.2, 0.2, 0.2, 1)), //define color as new instance of color
                createRectangle(Color.color(0.3, 0.3, 0.3)), //standard constructor
                createRectangle(Color.rgb(102, 102, 102)), //define color by rgb
                createRectangle(Color.web("#777777")), //define color by hex web value
                createRectangle(Color.gray(0.6)), //define gray color
                createRectangle(Color.grayRgb(179)), //define gray color
                createRectangle(Color.grayRgb(179, 0.5)) //opacity can be adjusted in all constructors
        );

        vBox.getChildren().addAll(hBox, hBox2, hBox3, hBox4);
        return vBox;
    }

    private Rectangle createRectangle(Color color) {
        Rectangle rect1 = new Rectangle(0, 45, 30, 30);
        //Fill rectangle with color
        rect1.setFill(color);
        return rect1;
    }

    // 5.- Gluon WebGL

    private Parent createContent6() {
        Pane root = new Pane();

        ImageView logoGluon = new ImageView(new Image(HelloWeb.class.getResourceAsStream("Gluon-Logo.png")));
        logoGluon.setFitHeight(100);
        logoGluon.setPreserveRatio(true);

        ImageView logoFX = new ImageView(new Image(HelloWeb.class.getResourceAsStream("JavaFX_Logo.png")));
        logoFX.setFitHeight(60);
        logoFX.setPreserveRatio(true);

        ImageView logoGL = new ImageView(new Image(HelloWeb.class.getResourceAsStream("WebGL_Logo.svg.png")));
        logoGL.setFitHeight(60);
        logoGL.setPreserveRatio(true);

        RotateTransition rotate = new RotateTransition(Duration.seconds(1), logoGluon);
        rotate.setFromAngle(-45);
        rotate.setToAngle(45);
        rotate.setCycleCount(2);
        rotate.setAutoReverse(true);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(2), logoGluon);
        scale.setToX(1.25);
        scale.setToY(1.25);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        FadeTransition fadeFX = new FadeTransition(Duration.seconds(3), logoFX);
        fadeFX.setFromValue(1);
        fadeFX.setToValue(0.3);
        fadeFX.setAutoReverse(true);

        FadeTransition fadeGL = new FadeTransition(Duration.seconds(3), logoGL);
        fadeGL.setFromValue(1);
        fadeGL.setToValue(0.3);
        fadeGL.setAutoReverse(true);

        HBox box = new HBox(30, logoFX, logoGluon, logoGL);
        box.setPadding(new Insets(30, 20, 30, 0));
        box.setPrefHeight(480);
        box.setAlignment(Pos.CENTER);
        root.getChildren().add(box);

        ParallelTransition parallel = new ParallelTransition();
        parallel.getChildren().addAll(fadeFX, fadeGL, rotate, scale);
        parallel.setCycleCount(Animation.INDEFINITE);
        parallel.setAutoReverse(true);

        parallel.playFromStart();

        return root;
    }

}
