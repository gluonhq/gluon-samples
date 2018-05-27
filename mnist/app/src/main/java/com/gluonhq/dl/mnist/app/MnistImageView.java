package com.gluonhq.dl.mnist.app;

import com.gluonhq.charm.down.Platform;
import com.gluonhq.charm.glisten.control.Toast;
import com.gluonhq.charm.glisten.mvc.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.util.Duration;

public class MnistImageView extends ImageView {
    
    private static final int MIN_SIZE = 200;
    private static final double MARGIN = 20;
    private double IMAGE_WIDTH;
    private double INNER_WIDTH;
    private double IMAGE_HEIGHT;
    private double INNER_HEIGHT;
    
    private boolean zooming = false;
    private boolean enableDragging = false;
    private double initialMousePressedX, initialMousePressedY;
    private double mouseDownX, mouseDownY;
    
    private final EventHandler<MouseEvent> pressedHandler = e -> {
        initialMousePressedX = e.getX();
        initialMousePressedY = e.getY();
        Point2D mouseDown = getImageCoordinates(initialMousePressedX, initialMousePressedY);
        mouseDownX = mouseDown.getX();
        mouseDownY = mouseDown.getY();
        enableDragging = true;
    };
    
    private final EventHandler<MouseEvent> draggedHandler = e -> {
        if (zooming || !enableDragging) {
            return;
        }
        final Point2D delta = getImageCoordinates(e.getX(), e.getY()).subtract(mouseDownX, mouseDownY);
        translateViewport(delta.getX(), delta.getY());
        Point2D mouseDown = getImageCoordinates(e.getX(), e.getY());
        mouseDownX = mouseDown.getX();
        mouseDownY = mouseDown.getY();
    };
    private final EventHandler<MouseEvent> releasedHandler = e -> enableDragging = false;
    private final EventHandler<ScrollEvent> scrollHandler = e -> zoom(Math.pow(1.01, -e.getDeltaY()), e.getX(), e.getY());
    private final EventHandler<ZoomEvent> zoomStartedHandler = e -> {
        enableDragging = false; 
        zooming = true;
    };
    private final EventHandler<ZoomEvent> zoomFinishedHandler = e -> zooming = false;
    private final EventHandler<ZoomEvent> zoomHandler = e -> zoom(1 / e.getZoomFactor(), e.getX(), e.getY());

    public MnistImageView() {
        setPreserveRatio(true);
    }
    
    public Image getFilteredPicture() {
        return this.getImage();
    }
    
    public void updateImage(View view, Image image) {

        removeListeners();

        this.setImage(image);
        this.setPickOnBounds(true);

        IMAGE_HEIGHT = image.getHeight();
        IMAGE_WIDTH = image.getWidth();
        
        INNER_HEIGHT = 1 * IMAGE_HEIGHT;
        INNER_WIDTH = 1 * IMAGE_WIDTH;
        
        this.fitWidthProperty().bind(view.widthProperty().subtract(MARGIN));
        this.fitHeightProperty().bind(view.heightProperty().subtract(MARGIN));
        double fitScale = Math.max(INNER_WIDTH / (view.getWidth() - MARGIN),
                INNER_HEIGHT / (view.getHeight() - MARGIN));
        this.setViewport(new Rectangle2D(0, 0, (view.getWidth() - MARGIN) * fitScale,
                (view.getWidth() - MARGIN) * fitScale));
        
        addListeners();
        zoomIn();
        
        Toast toast = new Toast("Zoom and center over the number.\nClick Crop when ready");
        toast.show();
    }
    
    private void addListeners() {
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
        
        if (Platform.isDesktop()) {
            this.addEventHandler(ScrollEvent.ANY, scrollHandler);
        } else {
            this.addEventHandler(ZoomEvent.ZOOM_STARTED, zoomStartedHandler);
            this.addEventHandler(ZoomEvent.ZOOM_FINISHED, zoomFinishedHandler);
            this.addEventHandler(ZoomEvent.ZOOM, zoomHandler);
        }
    }
    
    private void removeListeners() {
        this.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
        this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
        this.removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
        if (Platform.isDesktop()) {
            this.removeEventHandler(ScrollEvent.ANY, scrollHandler);
        } else {
            this.removeEventHandler(ZoomEvent.ZOOM_STARTED, zoomStartedHandler);
            this.removeEventHandler(ZoomEvent.ZOOM_FINISHED, zoomFinishedHandler);
            this.removeEventHandler(ZoomEvent.ZOOM, zoomHandler);
        }
    }
    
    private Point2D getImageCoordinates(double eX, double eY) {
        double factorX = eX / this.getBoundsInLocal().getWidth();
        double factorY = eY / this.getBoundsInLocal().getHeight();
        Rectangle2D viewport = this.getViewport();
        return new Point2D(viewport.getMinX() + factorX * viewport.getWidth(), 
                viewport.getMinY() + factorY * viewport.getHeight());
    }
    
    private void translateViewport(double deltaX, double deltaY) {
        Rectangle2D viewport = this.getViewport();
        double minX = clamp(viewport.getMinX() - deltaX, 0, IMAGE_WIDTH - viewport.getWidth());
        double minY = clamp(viewport.getMinY() - deltaY, 0, IMAGE_HEIGHT - viewport.getHeight());
        this.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));
    }

    private void zoom(double factor, double pivotX, double pivotY) {
        Rectangle2D viewport = this.getViewport();
        double scale = clamp(factor,
            Math.min(MIN_SIZE / viewport.getWidth(), MIN_SIZE / viewport.getHeight()),
            Math.max(INNER_WIDTH / viewport.getWidth(), INNER_HEIGHT / viewport.getHeight()));
        Point2D pivot = getImageCoordinates(pivotX, pivotY);

        double newWidth = viewport.getWidth() * scale;
        double newHeight = viewport.getHeight() * scale;

        // to zoom over the pivot, we have for x, y:
        // (x - newMinX) / (x - viewport.getMinX()) = scale
        // solving for newMinX, newMinY:
        double newMinX = clamp(pivot.getX() - (pivot.getX() - viewport.getMinX()) * scale, 
                0, IMAGE_WIDTH - newWidth);
        double newMinY = clamp(pivot.getY() - (pivot.getY() - viewport.getMinY()) * scale, 
                0, IMAGE_HEIGHT - newHeight);

        this.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
    }
    
    // The initial imageview is scaled up, we need to zoom it out
    private void zoomIn() {
        PauseTransition p = new PauseTransition(Duration.millis(30));
        p.setOnFinished(e -> {
            double factor = Math.max(INNER_WIDTH / this.getViewport().getWidth(), 
                                 INNER_HEIGHT / this.getViewport().getHeight());
            zoom(factor, this.getFitWidth()/2, this.getFitHeight()/2);
            translateViewport(-(IMAGE_WIDTH - this.getViewport().getWidth()) / 2, 
                              -(IMAGE_HEIGHT - this.getViewport().getHeight()) / 2);
        });
        p.play();
    }
    
    private double clamp(double value, double min, double max) {
        double minMax = Math.max(0, max);
        return value < min ? min : (value > minMax ? minMax : value);
    }
    
    public File getImageFile() {
        Image image = this.getImage();

        PngEncoderFX encoder = new PngEncoderFX(image, true);
        byte[] bytes = encoder.pngEncode();

        File file = new File("Image-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss")) + ".png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
            return file;
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
            return null;
        }
    }

}
