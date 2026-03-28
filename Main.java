import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// MAIN CLASS for sets up stuff and runs the game loop
public class Main extends Application {
    private Pane root;
    private Tank player;
    private ArrayList<Tank> enemies;
    private ArrayList<Wall> walls;
    private ArrayList<Bullet> bullets;
    private ArrayList<Explosion> explosions;
    private Text scoreText;
    private Text livesText;
    private Text pauseText;
    private Text gameOverText;
    private int score = 0;
    private int lives = 3;
    private int enemyCount = 10; // max number of enemies
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private static final int VIEW_WIDTH = 800;  // window width
    private static final int VIEW_HEIGHT = 600; // window height
    private static final int MAP_WIDTH = 1200;
    private static final int MAP_HEIGHT = 900;
    private static final int WALL_WIDTH = 16;
    private static final int WALL_HEIGHT = 14;
    private static final Random rand = new Random();
    private boolean xKeyPressed = false; // track X key state
    private Camera camera;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, VIEW_WIDTH, VIEW_HEIGHT, Color.BLACK);
        primaryStage.setTitle("Tank 2025");
        primaryStage.setScene(scene);
        primaryStage.show();

        initializeGame();
        setupInput(scene);
        startGameLoop();
    }

    // sets up the game with player, enemies, walls, and text
    private void initializeGame() {
        enemies = new ArrayList<>();
        walls = new ArrayList<>();
        bullets = new ArrayList<>();
        explosions = new ArrayList<>();
        camera = new Camera(VIEW_WIDTH, VIEW_HEIGHT, MAP_WIDTH, MAP_HEIGHT);

        // create player tank
        try {
            player = new Tank(true, 70, MAP_HEIGHT - 70, new String[]{"yellowTank1.png", "yellowTank2.png"});
            root.getChildren().add(player.getView());
        } catch (Exception e) {
            System.err.println("error: failed to load player tank images - " + e.getMessage());
            System.exit(1);
        }

        // spawn enemy tanks
        for (int i = 0; i < enemyCount; i++) {
            try {
                Tank enemy = new Tank(false, 200 + rand.nextInt(MAP_WIDTH - 200), 40 + rand.nextInt(20), new String[]{"whiteTank1.png", "whiteTank2.png"});
                enemies.add(enemy);
                root.getChildren().add(enemy.getView());
            } catch (Exception e) {
                System.err.println("error: failed to load enemy tank image - " + e.getMessage());
                System.exit(1);
            }
        }

        createWalls();

        // set up UI
        scoreText = new Text(25, 25, "Score: " + score); // top-left
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(new Font(20));
        livesText = new Text(25, 50, "Lives: " + lives); // below score
        livesText.setFill(Color.WHITE);
        livesText.setFont(new Font(20));
        pauseText = new Text(VIEW_WIDTH / 2 - 150, VIEW_HEIGHT / 2 - 100, "Paused\nPress R to Restart\nPress P to Resume");
        pauseText.setFill(Color.WHITE);
        pauseText.setFont(new Font(30));
        pauseText.setVisible(false);
        gameOverText = new Text(VIEW_WIDTH / 2 - 150, VIEW_HEIGHT / 2 - 150, "GAME OVER\nScore: 0\nPress R to Restart\nPress Escape to Exit");
        gameOverText.setFill(Color.WHITE);
        gameOverText.setFont(new Font(30));
        gameOverText.setVisible(false);

        root.getChildren().addAll(scoreText, livesText, pauseText, gameOverText);
    }

    // makes walls
    private void createWalls() {
        // cage walls
        for (int x = 0; x <= MAP_WIDTH; x += WALL_WIDTH) {
            try {
                Wall wall = new Wall(x, 0, "wall.png");
                Wall wall2 = new Wall(x, MAP_HEIGHT, "wall.png");
                Wall wall3 = new Wall(x, WALL_HEIGHT, "wall.png");
                Wall wall4 = new Wall(x, MAP_HEIGHT - WALL_HEIGHT, "wall.png");
                walls.add(wall);
                walls.add(wall2);
                walls.add(wall3);
                walls.add(wall4);
                root.getChildren().addAll(wall.getView(), wall2.getView(), wall3.getView(), wall4.getView());
            } catch (Exception e) {
                System.err.println("error: failed to load wall image - " + e.getMessage());
                System.exit(1);
            }
        }
        for (int y = 0; y <= MAP_HEIGHT + WALL_HEIGHT; y += WALL_HEIGHT) {
            try {
                Wall leftWall = new Wall(0, y, "wall.png");
                Wall rightWall = new Wall(MAP_WIDTH, y, "wall.png");
                Wall leftWall2 = new Wall(WALL_WIDTH, y, "wall.png");
                Wall rightWall2 = new Wall(MAP_WIDTH - WALL_WIDTH, y, "wall.png");
                walls.add(leftWall);
                walls.add(rightWall);
                walls.add(leftWall2);
                walls.add(rightWall2);
                root.getChildren().addAll(leftWall.getView(), rightWall.getView(), leftWall2.getView(), rightWall2.getView());
            } catch (Exception e) {
                System.err.println("error: failed to load wall image - " + e.getMessage());
                System.exit(1);
            }
        }

        // room walls
        for (int x = 0; x <= 100; x += WALL_WIDTH) {
            try {
                Wall wall = new Wall(x, MAP_HEIGHT - 114, "wall.png");
                Wall wall2 = new Wall(x, MAP_HEIGHT - 114 - WALL_HEIGHT, "wall.png");
                walls.add(wall);
                walls.add(wall2);
                root.getChildren().addAll(wall.getView(), wall2.getView());
            } catch (Exception e) {
                System.err.println("error: failed to load wall image - " + e.getMessage());
                System.exit(1);
            }
        }

        for (int y = MAP_HEIGHT - 114; y <= MAP_HEIGHT - 60; y += WALL_HEIGHT) {
            try {
                Wall leftWall = new Wall(WALL_WIDTH * 6, y, "wall.png");
                Wall leftWall2 = new Wall(WALL_WIDTH * 7, y, "wall.png");
                walls.add(leftWall);
                walls.add(leftWall2);
                root.getChildren().addAll(leftWall.getView(), leftWall2.getView());
            } catch (Exception e) {
                System.err.println("error: failed to load wall image - " + e.getMessage());
                System.exit(1);
            }
        }

        // horizontal wall in the middle
        for (int x = MAP_WIDTH/2 - 150; x <= MAP_WIDTH/2 + 150; x += WALL_WIDTH) {
            try {
                Wall wall = new Wall(x, MAP_HEIGHT / 2, "wall.png");
                Wall wall2 = new Wall(x, MAP_HEIGHT / 2 - WALL_HEIGHT, "wall.png");
                walls.add(wall);
                walls.add(wall2);
                root.getChildren().addAll(wall.getView(), wall2.getView());
            } catch (Exception e) {
                System.err.println("error: failed to load wall image - " + e.getMessage());
                System.exit(1);
            }
        }

        // vertical walls
        for (int y = 50; y <= MAP_HEIGHT - 70; y += WALL_HEIGHT) {
            if (y >= MAP_HEIGHT / 2 + 60) {
                try {
                    Wall leftWall = new Wall(MAP_WIDTH / 2 + 100, y, "wall.png");
                    Wall leftWall2 = new Wall(MAP_WIDTH / 2 + 116, y, "wall.png");
                    Wall rightWall = new Wall(MAP_WIDTH / 2 - 100, y, "wall.png");
                    Wall rightWall2 = new Wall(MAP_WIDTH / 2 - 116, y, "wall.png");
                    walls.add(leftWall);
                    walls.add(leftWall2);
                    walls.add(rightWall);
                    walls.add(rightWall2);
                    root.getChildren().addAll(leftWall.getView(), rightWall.getView(), leftWall2.getView(), rightWall2.getView());
                } catch (Exception e) {
                    System.err.println("error: failed to load wall image - " + e.getMessage());
                    System.exit(1);
                }
            }

            if (y >= MAP_HEIGHT / 2 + 116) {
                try {
                    Wall leftWall_ = new Wall(MAP_WIDTH / 2 + 200, y, "wall.png");
                    Wall leftWall_2 = new Wall(MAP_WIDTH / 2 + 216, y, "wall.png");
                    Wall rightWall_ = new Wall(MAP_WIDTH / 2 - 200, y, "wall.png");
                    Wall rightWall_2 = new Wall(MAP_WIDTH / 2 - 216, y, "wall.png");
                    walls.add(leftWall_);
                    walls.add(leftWall_2);
                    walls.add(rightWall_);
                    walls.add(rightWall_2);
                    root.getChildren().addAll(leftWall_.getView(), rightWall_.getView(), leftWall_2.getView(), rightWall_2.getView());
                } catch (Exception e) {
                    System.err.println("error: failed to load wall image - " + e.getMessage());
                    System.exit(1);
                }
            }

            if (y >= MAP_HEIGHT / 2 + 172) {
                try {
                    Wall leftWall_ = new Wall(MAP_WIDTH / 2 + 300, y, "wall.png");
                    Wall leftWall_2 = new Wall(MAP_WIDTH / 2 + 316, y, "wall.png");
                    Wall rightWall_ = new Wall(MAP_WIDTH / 2 - 300, y, "wall.png");
                    Wall rightWall_2 = new Wall(MAP_WIDTH / 2 - 316, y, "wall.png");
                    walls.add(leftWall_);
                    walls.add(leftWall_2);
                    walls.add(rightWall_);
                    walls.add(rightWall_2);
                    root.getChildren().addAll(leftWall_.getView(), rightWall_.getView(), leftWall_2.getView(), rightWall_2.getView());
                } catch (Exception e) {
                    System.err.println("error: failed to load wall image - " + e.getMessage());
                    System.exit(1);
                }
            }

            if (y >= MAP_HEIGHT / 2 + 228) {
                try {
                    Wall leftWall_ = new Wall(MAP_WIDTH / 2 + 400, y, "wall.png");
                    Wall leftWall_2 = new Wall(MAP_WIDTH / 2 + 416, y, "wall.png");
                    Wall rightWall_ = new Wall(MAP_WIDTH / 2 - 400, y, "wall.png");
                    Wall rightWall_2 = new Wall(MAP_WIDTH / 2 - 416, y, "wall.png");
                    walls.add(leftWall_);
                    walls.add(leftWall_2);
                    walls.add(rightWall_);
                    walls.add(rightWall_2);
                    root.getChildren().addAll(leftWall_.getView(), rightWall_.getView(), leftWall_2.getView(), rightWall_2.getView());
                } catch (Exception e) {
                    System.err.println("error: failed to load wall image - " + e.getMessage());
                    System.exit(1);
                }
            }
        }
    }

    // keys // i used list to move based on last pressed key
    private void setupInput(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (isGameOver) {
                if (event.getCode() == KeyCode.R) {
                    resetGame();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    System.exit(0);
                }
                return;
            }

            if (event.getCode() == KeyCode.P) {
                isPaused = !isPaused;
                pauseText.setVisible(isPaused);
                if (isPaused) {
                    player.clearPressedKeys();
                }
            }

            if (isPaused) {
                if (event.getCode() == KeyCode.R) {
                    resetGame();
                }
                return;
            }

            if (event.getCode() == KeyCode.ESCAPE) {
                System.exit(0);
            }

            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN ||
                event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                player.setMoving(event.getCode(), true);
            }

            if (event.getCode() == KeyCode.X && !xKeyPressed) {
                Bullet bullet = player.shoot();
                if (bullet != null) {
                    bullets.add(bullet);
                    root.getChildren().add(bullet.getView());
                }
                xKeyPressed = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            if (!isPaused && !isGameOver) {
                if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN ||
                    event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                    player.setMoving(event.getCode(), false);
                }
                if (event.getCode() == KeyCode.X) {
                    xKeyPressed = false;
                }
            }
        });
    }
        
    // runs the game loop
    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastEnemyMove = 0;
            private long lastEnemyShoot = 0;
            private long lastSpawnTime = 0;
            private boolean isRespawning = false;
            private long respawnStartTime = 0;
            private long lastUpdate = 0;
            private boolean wasPaused = false; // for previous pause state for moment

            @Override
            public void handle(long now) {
                // check if we just unpaused
                if (wasPaused && !isPaused) {
                    lastUpdate = now;
                    lastEnemyMove = now;
                    lastEnemyShoot = now;
                    if (isRespawning) {
                        respawnStartTime = now;
                    }
                }
                wasPaused = isPaused; // updating pause state for the next frame

                if (isPaused || isGameOver) return;

                // delta time in seconds
                double deltaTime = (lastUpdate == 0) ? 0 : (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                // move player tank
                player.move(MAP_WIDTH, MAP_HEIGHT, walls, now, deltaTime);

                // update camera
                if (!isRespawning) {
                    camera.update(player.getX(), player.getY(), now);
                } else {
                    camera.update(70, MAP_HEIGHT - 70, now);
                }

                // move enemies every second
                if (now - lastEnemyMove > 1_000_000_000) {
                    for (Tank enemy : enemies) {
                        enemy.setRandomDirection();
                    }
                    lastEnemyMove = now;
                }
                for (Tank enemy : enemies) {
                    enemy.move(MAP_WIDTH, MAP_HEIGHT, walls, now, deltaTime);
                }

                // check collisions between player and enemy tanks
                if (!isRespawning) {
                    Tank enemyHit = null;
                    for (Tank enemy : enemies) {
                        if (player.collides(enemy.getX(), enemy.getY(), Tank.TANK_WIDTH, Tank.TANK_HEIGHT)) {
                            enemyHit = enemy;
                            break;
                        }
                    }
                    if (enemyHit != null) {
                        lives--;
                        livesText.setText("Lives: " + lives);
                        score += 10;
                        scoreText.setText("Score: " + score);
                        addExplosion(player.getX(), player.getY(), true);
                        addExplosion(enemyHit.getX(), enemyHit.getY(), true);
                        enemies.remove(enemyHit);
                        root.getChildren().remove(enemyHit.getView());

                        // handle player respawn or game over
                        if (lives <= 0) {
                            isGameOver = true;
                            gameOverText.setText("GAME OVER\nScore: " + score + "\nPress R to Restart\nPress Escape to Exit");
                            gameOverText.setVisible(true);
                        } else {
                            isRespawning = true;
                            respawnStartTime = now;
                            player.getView().setVisible(false);
                        }
                    }
                }

                // enemies shoot at random intervals
                if (now - lastEnemyShoot > (400_000_000 + rand.nextInt(300_000_000))) {
                    for (Tank enemy : enemies) {
                        Bullet bullet = enemy.shoot();
                        if (bullet != null) {
                            bullets.add(bullet);
                            root.getChildren().add(bullet.getView());
                        }
                    }
                    lastEnemyShoot = now;
                }

                // handle bullets
                ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
                for (Bullet bullet : bullets) {
                    bullet.move(MAP_WIDTH, MAP_HEIGHT, deltaTime);
                    if (bullet.isOffScreen(MAP_WIDTH, MAP_HEIGHT, camera.getOffsetX(), camera.getOffsetY())) {
                        bulletsToRemove.add(bullet);
                        root.getChildren().remove(bullet.getView());
                        continue;
                    }

                    if (bullet.hitsWalls(walls)) {
                        addExplosion(bullet.getX(), bullet.getY(), false);
                        bulletsToRemove.add(bullet);
                        root.getChildren().remove(bullet.getView());
                        continue;
                    }

                    if (bullet.isPlayerBullet()) {
                        Tank enemyHit = bullet.hitsTank(enemies);
                        if (enemyHit != null) {
                            bulletsToRemove.add(bullet);
                            root.getChildren().remove(bullet.getView());
                            enemies.remove(enemyHit);
                            root.getChildren().remove(enemyHit.getView());
                            score += 10;
                            scoreText.setText("Score: " + score);
                            addExplosion(enemyHit.getX(), enemyHit.getY(), true);
                        }
                    } else {
                        if (bullet.hitsTank(player) && !isRespawning) {
                            bulletsToRemove.add(bullet);
                            root.getChildren().remove(bullet.getView());
                            lives--;
                            livesText.setText("Lives: " + lives);
                            addExplosion(player.getX(), player.getY(), true);
                            if (lives <= 0) {
                                isGameOver = true;
                                gameOverText.setText("GAME OVER\nScore: " + score + "\nPress R to Restart\nPress Escape to Exit");
                                gameOverText.setVisible(true);
                            } else {
                                isRespawning = true;
                                respawnStartTime = now;
                                player.getView().setVisible(false);
                            }
                        }
                    }
                }
                bullets.removeAll(bulletsToRemove);

                // handle enemy spawning
                if (enemies.size() < enemyCount && now - lastSpawnTime > (3_000_000_000L + Math.abs(rand.nextLong() % 5_000_000_000L))) { // 3-8 seconds
                    try {
                        Tank newEnemy = new Tank(false, 200 + rand.nextInt(MAP_WIDTH - 400), 40 + rand.nextInt(20), new String[]{"whiteTANK1.png", "whiteTANK2.png"});
                        enemies.add(newEnemy);
                        root.getChildren().add(newEnemy.getView());
                        lastSpawnTime = now;
                    } catch (Exception e) {
                        System.err.println("error: failed to load new enemy tank image - " + e.getMessage());
                        System.exit(1);
                    }
                }

                // handle respawn
                if (isRespawning && now - respawnStartTime >= 700_000_000L) {
                    player.setPosition(70, MAP_HEIGHT - 70);
                    player.getView().setVisible(true);
                    isRespawning = false;
                }

                // handle explosions
                ArrayList<Explosion> explosionsToRemove = new ArrayList<>();
                for (Explosion explosion : explosions) {
                    if (now - explosion.startTime > 500_000_000) {
                        explosionsToRemove.add(explosion);
                        root.getChildren().remove(explosion.view);
                    }
                }
                explosions.removeAll(explosionsToRemove);

                // apply camera offset
                applyCameraOffset();
            }
        };

        timer.start();
    }

    // adds an explosion at a spot
    private void addExplosion(double x, double y, boolean isLarge) {
        try {
            Image explosionImage = new Image(Main.class.getResourceAsStream("/assets/" + (isLarge ? "explosion.png" : "smallExplosion.png")));
            ImageView explosionView = new ImageView(explosionImage);
            // set position
            explosionView.setX(x - explosionImage.getWidth() / 2 - camera.getOffsetX());
            explosionView.setY(y - explosionImage.getHeight() / 2 - camera.getOffsetY());
            root.getChildren().add(explosionView);

            // add to explosions list with timestamp
            explosions.add(new Explosion(explosionView, System.nanoTime(), x, y));
        } catch (Exception e) {
            System.err.println("error: failed to load explosion image - " + e.getMessage());
            System.exit(1);
        }
    }

    // resets the game
    private void resetGame() {
        root.getChildren().clear();
        score = 0;
        lives = 3;
        isPaused = false;
        isGameOver = false;
        camera.reset();
        initializeGame();
    }

    // apply camera offset to objects
    private void applyCameraOffset() {
        player.getView().setX(player.getX() - player.getView().getImage().getWidth() / 2 - camera.getOffsetX());
        player.getView().setY(player.getY() - player.getView().getImage().getHeight() / 2 - camera.getOffsetY());
        for (Tank enemy : enemies) {
            enemy.getView().setX(enemy.getX() - enemy.getView().getImage().getWidth() / 2 - camera.getOffsetX());
            enemy.getView().setY(enemy.getY() - enemy.getView().getImage().getHeight() / 2 - camera.getOffsetY());
        }
        for (Bullet bullet : bullets) {
            bullet.getView().setX(bullet.getX() - bullet.getView().getImage().getWidth() / 2 - camera.getOffsetX());
            bullet.getView().setY(bullet.getY() - bullet.getView().getImage().getHeight() / 2 - camera.getOffsetY());
        }
        for (Wall wall : walls) {
            wall.getView().setX(wall.getX() - wall.getView().getImage().getWidth() / 2 - camera.getOffsetX());
            wall.getView().setY(wall.getY() - wall.getView().getImage().getHeight() / 2 - camera.getOffsetY());
        }
        for (Explosion explosion : explosions) {
            explosion.view.setX(explosion.getX() - explosion.view.getImage().getWidth() / 2 - camera.getOffsetX());
            explosion.view.setY(explosion.getY() - explosion.view.getImage().getHeight() / 2 - camera.getOffsetY());
        }
        // UI elements not inclueded
    }

    // starts the app
    public static void main(String[] args) {
        launch(args);
    }
}

// CAMERA CLASS for handling view offset with lerp
class Camera {
    private double offsetX, offsetY;
    private double targetX, targetY;
    private final int viewWidth, viewHeight;
    private final int mapWidth, mapHeight;
    private static final double LERP_FACTOR = 0.03; // smoothness factor for lerp

    public Camera(int viewWidth, int viewHeight, int mapWidth, int mapHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.offsetX = 0;
        this.offsetY = 0;
        this.targetX = 0;
        this.targetY = 0;
    }

    public void update(double playerX, double playerY, long currentTime) {
        targetX = playerX - viewWidth / 2;
        targetY = playerY - viewHeight / 2;

        // clamping
        targetX = Math.max(0, Math.min(targetX, mapWidth - viewWidth));
        targetY = Math.max(0, Math.min(targetY, mapHeight - viewHeight));

        // lerp
        offsetX += (targetX - offsetX) * LERP_FACTOR;
        offsetY += (targetY - offsetY) * LERP_FACTOR;
    }

    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public void reset() { offsetX = 0; offsetY = 0; targetX = 0; targetY = 0; }
}

// EXPLOSION CLASS for explosion effects
class Explosion {
    ImageView view;
    long startTime;
    private double x, y;

    Explosion(ImageView view, long startTime, double x, double y) {
        this.view = view;
        this.startTime = startTime;
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}

// TANK CLASS for player or enemies
class Tank {
    private double x, y;
    private double speed = 200;
    private ImageView view;
    private boolean isPlayer;
    private List<KeyCode> pressedKeys = new ArrayList<>();
    private KeyCode lastMovedDirection;
    private List<Image> frames; // store animation frames
    private int currentFrame = 0; // current frame index
    private long lastFrameSwitch = 0; // time of last frame switch
    private static final long FRAME_DURATION = 50_000_000; // 0.05 seconds per frame
    private static final Random rand = new Random();
    public static final double TANK_WIDTH = 32; // tank size is 32x32
    public static final double TANK_HEIGHT = 32;

    // create a tank
    public Tank(boolean isPlayer, double x, double y, String[] imageNames) throws Exception {
        this.isPlayer = isPlayer;
        this.x = x;
        this.y = y;
        frames = new ArrayList<>();

        // load all frames
        for (String imageName : imageNames) {
            try {
                Image image = new Image(Main.class.getResourceAsStream("/assets/" + imageName));
                frames.add(image);
            } catch (Exception e) {
                throw new Exception("failed to load image: " + imageName, e);
            }
        }

        if (frames.isEmpty()) {
            throw new Exception("no frames loaded for tank");
        }

        view = new ImageView(frames.get(0));
        view.setX(x - frames.get(0).getWidth() / 2);
        view.setY(y - frames.get(0).getHeight() / 2);
        lastMovedDirection = KeyCode.RIGHT; // default to right
        updateRotation();
    }

    // handle key press or release
    public void setMoving(KeyCode code, boolean value) {
        if (value) {
            if (!pressedKeys.contains(code)) {
                pressedKeys.add(code);
            }
            updateRotation();
        } else {
            pressedKeys.remove(code);
            updateRotation();
        }
    }

    // clear pressed keys when pausing
    public void clearPressedKeys() {
        pressedKeys.clear();
    }

    // set random direction for enemies
    public void setRandomDirection() {
        pressedKeys.clear();
        int direction = rand.nextInt(4);
        if (direction == 0) {
            pressedKeys.add(KeyCode.UP);
            lastMovedDirection = KeyCode.UP;
        } else if (direction == 1) {
            pressedKeys.add(KeyCode.DOWN);
            lastMovedDirection = KeyCode.DOWN;
        } else if (direction == 2) {
            pressedKeys.add(KeyCode.LEFT);
            lastMovedDirection = KeyCode.LEFT;
        } else {
            pressedKeys.add(KeyCode.RIGHT);
            lastMovedDirection = KeyCode.RIGHT;
        }
        updateRotation();
    }

    // update tank rotation based on direction
    private void updateRotation() {
        if (!pressedKeys.isEmpty()) {
            KeyCode lastKey = pressedKeys.get(pressedKeys.size() - 1);
            if (lastKey == KeyCode.UP) {
                view.setRotate(-90);
                lastMovedDirection = KeyCode.UP;
            } else if (lastKey == KeyCode.DOWN) {
                view.setRotate(90);
                lastMovedDirection = KeyCode.DOWN;
            } else if (lastKey == KeyCode.LEFT) {
                view.setRotate(180);
                lastMovedDirection = KeyCode.LEFT;
            } else if (lastKey == KeyCode.RIGHT) {
                view.setRotate(0);
                lastMovedDirection = KeyCode.RIGHT;
            }
        } else {
            if (lastMovedDirection == KeyCode.UP) view.setRotate(-90);
            else if (lastMovedDirection == KeyCode.DOWN) view.setRotate(90);
            else if (lastMovedDirection == KeyCode.LEFT) view.setRotate(180);
            else if (lastMovedDirection == KeyCode.RIGHT) view.setRotate(0);
        }
    }

    // move tank based on pressed keys and update animation
    public void move(double mapWidth, double mapHeight, ArrayList<Wall> walls, long currentTime, double deltaTime) {
        double newX = x;
        double newY = y;
        boolean isMoving = false;

        if (!pressedKeys.isEmpty()) {
            KeyCode lastKey = pressedKeys.get(pressedKeys.size() - 1);
            if (lastKey == KeyCode.UP) newY -= speed * deltaTime;
            else if (lastKey == KeyCode.DOWN) newY += speed * deltaTime;
            else if (lastKey == KeyCode.LEFT) newX -= speed * deltaTime;
            else if (lastKey == KeyCode.RIGHT) newX += speed * deltaTime;

            if (lastKey == KeyCode.UP) lastMovedDirection = KeyCode.UP;
            else if (lastKey == KeyCode.DOWN) lastMovedDirection = KeyCode.DOWN;
            else if (lastKey == KeyCode.LEFT) lastMovedDirection = KeyCode.LEFT;
            else if (lastKey == KeyCode.RIGHT) lastMovedDirection = KeyCode.RIGHT;

            isMoving = true;
        }

        // keep tank in bounds of the full map
        if (newX < 0) newX = 0;
        if (newX > mapWidth) newX = mapWidth;
        if (newY < 0) newY = 0;
        if (newY > mapHeight) newY = mapHeight;

        // check for wall collisions
        for (Wall wall : walls) {
            if (wall.collidesWithTank(newX, newY, TANK_WIDTH, TANK_HEIGHT)) {
                return;
            }
        }

        x = newX;
        y = newY;

        // update animation frame if moving
        if (isMoving && frames.size() > 1 && (currentTime - lastFrameSwitch > FRAME_DURATION)) {
            currentFrame = (currentFrame + 1) % frames.size();
            double rotation = view.getRotate();
            view.setImage(frames.get(currentFrame));
            view.setX(x - frames.get(currentFrame).getWidth() / 2);
            view.setY(y - frames.get(currentFrame).getHeight() / 2);
            view.setRotate(rotation);
            lastFrameSwitch = currentTime;
        } else {
            // update position without changing frame
            view.setX(x - frames.get(currentFrame).getWidth() / 2);
            view.setY(y - frames.get(currentFrame).getHeight() / 2);
        }
    }

    // shoot a bullet
    public Bullet shoot() {
        double bulletX = x;
        double bulletY = y;
        double direction = view.getRotate();
        return new Bullet(bulletX, bulletY, direction, isPlayer);
    }

    // check if tank collides with another object
    public boolean collides(double otherX, double otherY, double otherWidth, double otherHeight) {
        double tankLeft = x - TANK_WIDTH / 2;
        double tankRight = x + TANK_WIDTH / 2;
        double tankTop = y - TANK_HEIGHT / 2;
        double tankBottom = y + TANK_HEIGHT / 2;

        double otherLeft = otherX - otherWidth / 2;
        double otherRight = otherX + otherWidth / 2;
        double otherTop = otherY - otherHeight / 2;
        double otherBottom = otherY + otherHeight / 2;

        return tankLeft < otherRight && tankRight > otherLeft &&
               tankTop < otherBottom && tankBottom > otherTop;
    }

    // getters
    public double getX() { return x; }
    public double getY() { return y; }
    public ImageView getView() { return view; }

    // setter for position
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        view.setX(x - frames.get(currentFrame).getWidth() / 2);
        view.setY(y - frames.get(currentFrame).getHeight() / 2);
    }
}

// BULLET CLASS for tank shots
class Bullet {
    private double x, y;
    private double speed = 300;
    private ImageView view;
    private double direction;
    private boolean isPlayerBullet;
    private static final double BULLET_WIDTH = 13; // bullet size is 13x10
    private static final double BULLET_HEIGHT = 10;

    // create a bullet
    public Bullet(double x, double y, double direction, boolean isPlayerBullet) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.isPlayerBullet = isPlayerBullet;
        try {
            Image image = new Image(Main.class.getResourceAsStream("/assets/bullet.png"));
            view = new ImageView(image);
            view.setX(x - image.getWidth() / 2);
            view.setY(y - image.getHeight() / 2);
            view.setRotate(direction);
        } catch (Exception e) {
            System.err.println("error: failed to load bullet image - " + e.getMessage());
            System.exit(1);
        }
    }

    // move bullet in its direction
    public void move(double mapWidth, double mapHeight, double deltaTime) {
        double radians = Math.toRadians(direction);
        x += speed * Math.cos(radians) * deltaTime;
        y += speed * Math.sin(radians) * deltaTime;
        view.setX(x - view.getImage().getWidth() / 2);
        view.setY(y - view.getImage().getHeight() / 2);
    }

    // check if bullet is off screen
    public boolean isOffScreen(double Width, double Height, double offsetX, double offsetY) {
        double viewLeft = offsetX;
        double viewRight = offsetX + Width;
        double viewTop = offsetY;
        double viewBottom = offsetY + Height;
        return x < viewLeft || x > viewRight || y < viewTop || y > viewBottom;
    }

    // check if bullet hits walls
    public boolean hitsWalls(ArrayList<Wall> walls) {
        for (Wall wall : walls) {
            if (wall.collidesWithBullet(x, y, BULLET_WIDTH, BULLET_HEIGHT)) {
                return true;
            }
        }
        return false;
    }

    // check if bullet hits a tank from a list
    public Tank hitsTank(ArrayList<Tank> tanks) {
        for (Tank tank : tanks) {
            if (tank.collides(x, y, BULLET_WIDTH, BULLET_HEIGHT)) {
                return tank;
            }
        }
        return null;
    }

    // check if bullet hits a single tank
    public boolean hitsTank(Tank tank) {
        return tank.collides(x, y, BULLET_WIDTH, BULLET_HEIGHT);
    }

    // getters
    public double getX() { return x; }
    public double getY() { return y; }
    public ImageView getView() { return view; }
    public boolean isPlayerBullet() { return isPlayerBullet; }
}

// WALL CLASS for indestructible blocks
class Wall {
    private double x, y;
    private ImageView view;
    private final int WALL_WIDTH; // wall width
    private final int WALL_HEIGHT; // wall height

    // create a wall
    public Wall(double x, double y, String imageName) throws Exception {
        this.x = x;
        this.y = y;
        Image image;
        try {
            image = new Image(Main.class.getResourceAsStream("/assets/" + imageName));
        } catch (Exception e) {
            throw new Exception("failed to load wall image: " + imageName, e);
        }
        view = new ImageView(image);
        WALL_WIDTH = (int) image.getWidth();
        WALL_HEIGHT = (int) image.getHeight();
        view.setX(x - WALL_WIDTH / 2);
        view.setY(y - WALL_HEIGHT / 2);
    }

    // check collision with tank
    public boolean collidesWithTank(double tankX, double tankY, double tankWidth, double tankHeight) {
        double wallLeft = x - WALL_WIDTH / 2;
        double wallRight = x + WALL_WIDTH / 2;
        double wallTop = y - WALL_HEIGHT / 2;
        double wallBottom = y + WALL_HEIGHT / 2;

        double tankLeft = tankX - tankWidth / 2;
        double tankRight = tankX + tankWidth / 2;
        double tankTop = tankY - tankHeight / 2;
        double tankBottom = tankY + tankHeight / 2;

        return wallLeft < tankRight && wallRight > tankLeft &&
               wallTop < tankBottom && wallBottom > tankTop;
    }

    // check collision with bullet
    public boolean collidesWithBullet(double bulletX, double bulletY, double bulletWidth, double bulletHeight) {
        double wallLeft = x - WALL_WIDTH / 2;
        double wallRight = x + WALL_WIDTH / 2;
        double wallTop = y - WALL_HEIGHT / 2;
        double wallBottom = y + WALL_HEIGHT / 2;

        double bulletLeft = bulletX - bulletWidth / 2;
        double bulletRight = bulletX + bulletWidth / 2;
        double bulletTop = bulletY - bulletHeight / 2;
        double bulletBottom = bulletY + bulletHeight / 2;

        return wallLeft < bulletRight && wallRight > bulletLeft &&
               wallTop < bulletBottom && wallBottom > bulletTop;
    }

    // getter
    public double getX() { return x; }
    public double getY() { return y; }
    public ImageView getView() { return view; }
}