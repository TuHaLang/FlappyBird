package sample;

import image.Res;
import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import model.Bird;
import model.Cloud;
import model.TwoTubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    @FXML
    Pane nenDat, nenTroi;


    SimpleDoubleProperty y = new SimpleDoubleProperty(200);


    private final double width = 800, height = 500; // set screen size

    private boolean gameOver = true;
    private boolean incrementOnce = true;
    int score = 0;
    int highScore = 0;
    double FPS_30 = 30;
    int counter_30FPS = 0, counter_3FPS = 0;
    Bird bird;
    //di chuyển lên
    TranslateTransition jump;
    //ngã xuống
    TranslateTransition fall;
    //xoay hình chim
    RotateTransition rotator;
    //list các cặp cột trên dưới
    ArrayList<TwoTubes> listOfTubes = new ArrayList<>();
    //list mây
    ArrayList<Cloud> listOfClouds = new ArrayList<>();
    //lable điểm
    ScoreLabel scoreLabel = new ScoreLabel(width, 0);
    //vòng lặp game
    Timeline gameLoop;
    //load ảnh bird
    Res res = new Res();

    public void startGame(ActionEvent actionEvent){
        initGame();
        gameOver = false;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        nenTroi.setOnMouseClicked(e -> {
            if (!gameOver) //nếu game chưa kết thúc thì gọi hàm jumpflappy() cho chim nhảy lên
            {
                jumpflappy();
            } else //ngược lại thì khởi tạo lại game
            {
                initializeGame();
            }
        });
        nenDat.setStyle("-fx-background-image: url(/image/nenDat.png)");
    }


    private void updateCounters() {
        if (counter_30FPS % 4 == 0) {
            bird.refreshBird();
            counter_30FPS = 1;
        }
        counter_30FPS++;
    }

    private void jumpflappy() {
        //xoay chim lên trên
        rotator.setDuration(Duration.millis(100));//thời gian xoay
        rotator.setToAngle(-40);
        rotator.stop();
        rotator.play();

        //di chuyển chim lên 1 đoạn
        jump.setByY(-50);
        jump.setCycleCount(1);
        bird.jumping = true;

        //dừng ngã và bay lên
        fall.stop();
        jump.stop();
        jump.play();

        //kết thúc click chuột
        jump.setOnFinished((finishedEvent) -> {
            rotator.setDuration(Duration.millis(500));
            rotator.setToAngle(40);
            rotator.stop();
            rotator.play();
            bird.jumping = false;
            fall.play();
        });
    }

    private void checkCollisions() //Hàm kiểm tra va chạm
    {
        TwoTubes tube = listOfTubes.get(0);
        if (tube.getTranslateX() < 35 && incrementOnce) {
            score++;
            scoreLabel.setText("Score: " + score);
            incrementOnce = false;
        }

        //lấy phần giao giữa chim và các ống nước
        Path p1 = (Path) Shape.intersect(bird.getBounds(), tube.topBody);
        Path p2 = (Path) Shape.intersect(bird.getBounds(), tube.topHead);
        Path p3 = (Path) Shape.intersect(bird.getBounds(), tube.lowerBody);
        Path p4 = (Path) Shape.intersect(bird.getBounds(), tube.lowerHead);

        //nếu 1 trong các phần giao khác rống tức là chim đã va chạm
        boolean intersection = !(p1.getElements().isEmpty()
                && p2.getElements().isEmpty()
                && p3.getElements().isEmpty()
                && p4.getElements().isEmpty());

        //kiểm tra chim chạm đất hoặc trời :D
        if (bird.getBounds().getCenterY() + bird.getBounds().getRadiusY() > height || bird.getBounds().getCenterY() - bird.getBounds().getRadiusY() < 0) {
            intersection = true;
        }

        //kết thúc game
        if (intersection) {
            GameOverLabel gameOverLabel = new GameOverLabel(width / 2 - 80, height / 2);
            highScore = highScore < score ? score : highScore;
            gameOverLabel.setText("\tYour Score: " + score + "\n\tHigh Score: " + highScore +"\nPlease click mouse to play again !!!!");
            saveHighScore();
            nenTroi.getChildren().add(gameOverLabel);
            nenTroi.getChildren().get(1).setOpacity(0);
            gameOver = true;
            gameLoop.stop();
        }
    }

    void initializeGame() //Khởi tạo lại game
    {
        listOfTubes.clear();
        listOfClouds.clear();
        nenTroi.getChildren().clear();
        bird.getGraphics().setTranslateX(100);
        bird.getGraphics().setTranslateY(100);
        scoreLabel.setOpacity(0.8);
        scoreLabel.setText("Score: 0");
        nenTroi.getChildren().addAll(bird.getGraphics(), scoreLabel);

        //set vị trí hiển thị cho cloud
        for (int i = 0; i < 5; i++) {
            Cloud cloud = new Cloud();
            cloud.setX(Math.random() * width);
            cloud.setY(Math.random() * height * 0.5 + 0.1);
            listOfClouds.add(cloud);
            nenTroi.getChildren().add(cloud);
        }

        //hiển thị các ống nước
        for (int i = 0; i < 5; i++) {
            SimpleDoubleProperty y = new SimpleDoubleProperty(0);
            y.set(nenTroi.getHeight() * Math.random() / 2.0);
            TwoTubes tube = new TwoTubes(y, nenTroi, false, false);
            tube.setTranslateX(i * (width / 4 + 10) + 400);
            listOfTubes.add(tube);
            nenTroi.getChildren().add(tube);
        }
        score = 0;
        //cho phép tăng điểm
        incrementOnce = true;
        //game chưa kết thúc
        gameOver = false;
        //chim không nhảy lên
        bird.jumping = false;
        //bắt đầu chơi lại
        fall.stop();
        fall.play();
        gameLoop.play();
    }


    void initGame()
    {
        bird = new Bird(res.birdImgs);
        rotator = new RotateTransition(Duration.millis(500), bird.getGraphics());
        jump = new TranslateTransition(Duration.millis(450), bird.getGraphics());
        fall = new TranslateTransition(Duration.millis(5 * height), bird.getGraphics());
        jump.setInterpolator(Interpolator.LINEAR);
        //chim ngã tự động dịch chuyển xuống khi fall được play
        fall.setByY(height + 20);
        rotator.setCycleCount(1);
        bird.getGraphics().setRotationAxis(Rotate.Z_AXIS);

        //Tạo chuyển động cho game
        gameLoop = new Timeline(new KeyFrame(Duration.millis(1000 / FPS_30), new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                updateCounters();
                checkCollisions();
                //nếu ống chuyển động đến hết bên trái của Pane
                if (listOfTubes.get(0).getTranslateX() <= -width / 12.3) {
                    //xóa ống khỏi List
                    listOfTubes.remove(0);
                    //tạo thêm ống
                    SimpleDoubleProperty y = new SimpleDoubleProperty(0);
                    y.set(nenTroi.getHeight() * Math.random() / 2.0);
                    TwoTubes tube;
                    //random các loại cột
                    if (Math.random() < 0.4) {
                        tube = new TwoTubes(y, nenTroi, true, false);
                    } else if (Math.random() > 0.9) {
                        tube = new TwoTubes(y, nenTroi, true, true);
                    } else {
                        tube = new TwoTubes(y, nenTroi, false, false);
                    }

                    //chuyển ống vừa thêm vào bên phải Pane
                    tube.setTranslateX(listOfTubes.get(listOfTubes.size() - 1).getTranslateX()
                            + (width / 4 + 10));

                    //thêm ống vào list
                    listOfTubes.add(tube);

                    incrementOnce = true;
                    //remove ống và thêm lại
                    nenTroi.getChildren().remove(7);
                    nenTroi.getChildren().add(tube);
                }
                for (int i = 0; i < listOfTubes.size(); i++) {
                    //Tạo mây
                    if (listOfClouds.get(i).getX() < -listOfClouds.get(i).getImage().getWidth()
                            * listOfClouds.get(i).getScaleX())
                    {
                        listOfClouds.get(i).setX(listOfClouds.get(i).getX() + width
                                + listOfClouds.get(i).getImage().getWidth()
                                * listOfClouds.get(i).getScaleX());
                    }
                    //Di chuyển mây và các ống nước
                    listOfClouds.get(i).setX(listOfClouds.get(i).getX() - 1);
                    listOfTubes.get(i).setTranslateX(listOfTubes.get(i).getTranslateX() - 2);
                }
            }
        }));

        gameLoop.setCycleCount(-1);
        initializeGame();
        loadHighScore();
    }

    //load điểm cao từ file
    void loadHighScore() {
        try {
            highScore = new DataInputStream(new FileInputStream("highScore.score")).readInt();
        } catch (Exception e) {
        }
    }

    //lưu điểm cao
    void saveHighScore() {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream("highScore.score"));
            out.writeInt(score);
            out.flush();
        } catch (Exception e) {
        }
    }

}
