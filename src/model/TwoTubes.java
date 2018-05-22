package model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class TwoTubes extends Group {

    //khai báo các hình chữ nhật là đầu của các ống nước
    public Rectangle topHead, lowerHead;

    //Khai báo các hình chữ nhật là thân của các ống nước
    public Rectangle topBody, lowerBody;

    //Khoảng các giữa ống trên và ống dưới
    double GAP = 120;

    //Tô màu cho đầu và thân ống nước
    Stop[] stops = new Stop[]{new Stop(0, Color.LIGHTGREEN), new Stop(1, Color.DARKGREEN)};
    LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
    Color c2 = new Color(84 / 255.0, 56 / 255.0, 71 / 255.0, 1.0);

    //hiệu ứng dao động cho các ống nước
    double oscillationCenter;
    Timeline animateTube;

    int frames = 0;
    int rotateOffset = 0;


    //Hàm khởi tạo
    public TwoTubes(SimpleDoubleProperty gapLocation, Pane root, boolean animate, boolean rotate)
    {
        if (rotate) {
            setRotationAxis(Rotate.Z_AXIS);
            setRotate(-25 + 50 * Math.random());
            rotateOffset = 80;
            setTranslateY(-40);
        }

        topBody = new Rectangle();
        oscillationCenter = gapLocation.get();

        if (animate) {
            animateTube = new Timeline(new KeyFrame(Duration.millis(33), e -> {
                gapLocation.set(25 * Math.cos(Math.PI / 60 * frames) + oscillationCenter);
                frames = (frames + 1) % 120;
            }));
            animateTube.setCycleCount(-1);
            animateTube.play();
        }

        topBody.widthProperty().bind(root.widthProperty().divide(12.3));
        topBody.heightProperty().bind(gapLocation);
        topHead = new Rectangle();
        topHead.widthProperty().bind(root.widthProperty().divide(11.4));
        topHead.heightProperty().bind(root.heightProperty().divide(12));
        topBody.setX(2.5);
        topHead.yProperty().bind(gapLocation);
        lowerHead = new Rectangle();
        lowerHead.widthProperty().bind(root.widthProperty().divide(11.4));
        lowerHead.heightProperty().bind(root.heightProperty().divide(12));
        lowerBody = new Rectangle();
        lowerBody.widthProperty().bind(root.widthProperty().divide(12.3));
        lowerBody.heightProperty().bind(root.heightProperty().add(-GAP - 50 + rotateOffset).subtract(gapLocation));
        lowerBody.setX(2.5);
        lowerHead.yProperty().bind(gapLocation.add(GAP).add(root.heightProperty().divide(12)));
        lowerBody.yProperty().bind(gapLocation.add(GAP).add(root.heightProperty().divide(6)));
        lowerHead.setFill(lg1);
        lowerBody.setFill(lg1);
        topHead.setFill(lg1);
        topBody.setFill(lg1);
        lowerHead.setStroke(c2);
        lowerBody.setStroke(c2);
        topHead.setStroke(c2);
        topBody.setStroke(c2);
        getChildren().addAll(topBody, topHead, lowerBody, lowerHead);
    }
}
