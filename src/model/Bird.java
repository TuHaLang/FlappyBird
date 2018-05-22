package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class Bird extends ImageView {

    private ImageView graphics = new ImageView();
    private Image frames[];
    private int frameCounter = 0;
    public boolean jumping = false;
    Ellipse bounds;





    public ImageView getGraphics() {
        return graphics;
    }

    public Ellipse getBounds() {
        return bounds;
    }

    public Bird(Image[] frames) {

        this.frames = frames;
        this.bounds = new Ellipse(frames[0].getWidth() / 2.0, 11.5);
        graphics.setImage(frames[0]);
        bounds.setFill(Color.TRANSPARENT);
        bounds.setStroke(Color.BLACK);
        bounds.centerXProperty().bind(graphics.translateXProperty().add(frames[0].getWidth() / 2.0));
        bounds.centerYProperty().bind(graphics.translateYProperty().add(12.0));
        bounds.rotateProperty().bind(graphics.rotateProperty());
    }

    public void refreshBird() {
        graphics.setImage(frames[frameCounter++]);
        if (frameCounter == 3) {
            frameCounter = 0;
        }
    }
}
