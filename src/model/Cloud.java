package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Cloud extends ImageView {

    public Cloud() {
        setImage(new Image(Cloud.class.getResourceAsStream("/image/cloud.png")));
        setScaleX(Math.random() / 2.0 + 0.5);
        setScaleY(Math.random() / 2.0 + 0.5);
        setOpacity(0.5);
    }

}
