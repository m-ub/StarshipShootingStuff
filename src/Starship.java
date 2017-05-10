import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import javafx.scene.shape.Circle;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class Starship extends JPanel implements KeyListener, ActionListener {

    private final int WIDTH = 800, HEIGHT = 800;

    private Renderer renderer;

    private Rectangle shipPp;

    private ArrayList<Circle> asteroids;

    private ArrayList<Rectangle> shoots;

    private ArrayList<Rectangle> explosions;

    private ArrayList<Rectangle> backgrounds;

    private ArrayList<Rectangle> hearts;

    private Random random;

    private int xMotion = 30, ticks, score, remover,
            audiocontrol2;

    private Image background, background2, ground, ship,
            steroid, bullet, boom, boom2, boom3, heart;

    private ImageIcon backgroundIcon, backgroundIcon2, groundIcon, shipIcon,
            steroidIcon, bulletIcon, boomIcon, boomIcon2, boomIcon3, heartIcon;

    private float alpha = 1f;

    private boolean gameStarted, gameOver;

    private javax.swing.Timer timer = new javax.swing.Timer(20, this);

    private SoundSystem sound;


    public Starship() throws InterruptedException, UnsupportedAudioFileException, LineUnavailableException, IOException {
        JFrame jframe = new JFrame();
        renderer = new Renderer();
        random = new Random();
        sound = new SoundSystem();

        jframe.setTitle("SSS7");
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.setResizable(false);
        setLayout(new BorderLayout());
        jframe.add(renderer);
        jframe.addKeyListener(this);

        jframe.setVisible(true);

        shipPp = new Rectangle(WIDTH / 2 - 10, HEIGHT - 60 - 30, 50, 50);
        asteroids = new ArrayList<>();
        shoots = new ArrayList<>();
        explosions = new ArrayList<>();
        backgrounds = new ArrayList<>();
        hearts = new ArrayList<>();

        backgrounds.add(new Rectangle(0, 0, WIDTH, HEIGHT - 60));
        backgrounds.add(new Rectangle(0, -740, WIDTH, HEIGHT - 60));
        addAsteroid();
        addHearts();
        timer.start();
    }

    //background sounds
    private File backgroundMusic = new File("C:/Users/Uboot/Desktop/StarshipShootingStuff/snd/backgroundMusic.wav");
    //action sounds
    private File pewMusic = new File("C:/Users/Uboot/Desktop/StarshipShootingStuff/snd/pewMusic.wav");
    private File boomMusic = new File("C:/Users/Uboot/Desktop/StarshipShootingStuff/snd/boomMusic.wav");
    private File liveLostMusic = new File("C:/Users/Uboot/Desktop/StarshipShootingStuff/snd/liveLostMusic.wav");


    public void repaint2(Graphics g) {


        backgroundIcon = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\background.jpg");
        background = backgroundIcon.getImage();
        g.drawImage(background, backgrounds.get(0).x, backgrounds.get(0).y, WIDTH, HEIGHT - 60, this);

        backgroundIcon2 = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\background2.jpg");
        background2 = backgroundIcon2.getImage();
        if (backgrounds.size() > 1) {
            g.drawImage(background2, backgrounds.get(1).x, backgrounds.get(1).y, WIDTH, HEIGHT - 60, this);
        }

        groundIcon = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\ground.jpeg");
        ground = groundIcon.getImage();
        g.drawImage(ground, 0, HEIGHT - 60, this);

        shipIcon = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\ship.png");
        ship = shipIcon.getImage();
        g.drawImage(ship, shipPp.x, shipPp.y, shipPp.width, shipPp.height, this);


        for (Rectangle shoot : shoots) {
            paintBullet(g, shoot);
        }

        for (Rectangle heartTt : hearts) {
            paintHearts(g, heartTt);
        }

        for (Circle asteroid : asteroids) {
            paintAsteroid(g, asteroid);
        }
        if (explosions.size() > 0) {
            for (int i = 0; i < explosions.size(); i++) {
                Rectangle expl = explosions.get(i);
                int randImage = random.nextInt(3);
                paintExplosion(g, expl, randImage);
            }
        }
        if (!gameStarted && !gameOver) {
            Font font = new Font("Helvetica", Font.PLAIN, 60);
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString("LEFT or RIGHT to move", 75, HEIGHT / 2 + 100);
            g.drawString("UP or DOWN to shoot", 100, HEIGHT / 2 - 150);
        }

        if (gameOver) {
            Font font = new Font("Helvetica", Font.PLAIN, 60);
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString("GAME OVER", 100, HEIGHT / 2 - 60);
            g.drawString("PRESS ANY BUTTON", 100, HEIGHT / 2 - 120);
        }

        if (!gameOver && gameStarted) {
            //score
            Font font = new Font("Helvetica", Font.PLAIN, 60);
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString("Score:", 0, 50);
            g.drawString(String.valueOf(score), 0, 100);
        }

    }

    private void addAsteroid() {
        int randRadius = random.nextInt(50) + 149;
        int randX = random.nextInt(HEIGHT - randRadius) + randRadius;
        int randY = -199;
        asteroids.add(new Circle(randX, randY, randRadius));
    }

    private void paintAsteroid(Graphics g, Circle asteroid) {
        steroidIcon = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\asteroid.png");
        steroid = steroidIcon.getImage();
        g.drawImage(steroid, (int) asteroid.getCenterX() - (int) asteroid.getRadius(),
                (int) asteroid.getCenterY(), (int) asteroid.getRadius(), (int) asteroid.getRadius(), this);
    }

    private void moveRight() {
        shipPp.x += xMotion;
        if (!gameStarted) {
            gameStarted = true;
        }
    }

    private void moveLeft() {
        shipPp.x -= xMotion;
        if (!gameStarted) {
            gameStarted = true;
        }
    }

    private void addShoot() {
        int shootW = shipPp.width / 2;
        int shootH = 25;
        int shootX = shipPp.x + (shipPp.width / 2);
        int shootY = (HEIGHT - 60 - shipPp.height - 5);
        shoots.add(new Rectangle(shootX, shootY, shootH, shootW));
        if (!gameStarted) {
            gameStarted = true;
        }
    }

    private void paintBullet(Graphics g, Rectangle shoot) {
        bulletIcon = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\laser.png");
        bullet = bulletIcon.getImage();
        g.drawImage(bullet, shoot.x, shoot.y, shoot.width, shoot.height, this);
    }

    private void paintExplosion(Graphics g, Rectangle explosion, int randImage) {
        boomIcon = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\expl1.png");
        boomIcon2 = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\expl2.png");
        boomIcon3 = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\expl3.png");
        boom = boomIcon.getImage();
        boom2 = boomIcon2.getImage();
        boom3 = boomIcon3.getImage();
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        if (randImage == 1) {
            g2d.drawImage(boom, explosion.x, explosion.y, explosion.width, explosion.height, this);
        } else if (randImage == 2) {
            g2d.drawImage(boom2, explosion.x, explosion.y, explosion.width, explosion.height, this);
        } else if (randImage == 3) {
            g2d.drawImage(boom3, explosion.x, explosion.y, explosion.width, explosion.height, this);
        }
    }

    private void addHearts() {
        int heartW = 60;
        int heartH = 60;
        hearts.add(new Rectangle(WIDTH - 1 * heartW - 10, 0, heartW, heartH));
        hearts.add(new Rectangle(WIDTH - 2 * heartW - 10, 0, heartW, heartH));
        hearts.add(new Rectangle(WIDTH - 3 * heartW - 10, 0, heartW, heartH));
        hearts.add(new Rectangle(WIDTH - 4 * heartW - 10, 0, heartW, heartH));
        hearts.add(new Rectangle(WIDTH - 5 * heartW - 10, 0, heartW, heartH));
    }

    private void paintHearts(Graphics g, Rectangle heartTt) {
        heartIcon = new ImageIcon("C:\\Users\\Uboot\\Desktop\\StarshipShootingStuff\\img\\heart.png");
        heart = heartIcon.getImage();
        g.drawImage(heart, heartTt.x, heartTt.y, heartTt.width, heartTt.height, this);
    }


    @Override //aka draw
    public void actionPerformed(ActionEvent e) {
        ticks++;

        new Thread(new Runnable() {
            public void run() {
                if (gameStarted && audiocontrol2 < 1){

                    try {
                        SoundSystem.playSound(backgroundMusic);
                    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e0) {
                        e0.printStackTrace();
                    }
                    audiocontrol2++;
                }
            }


        }).start();



            int backgroundSpeed = 5;

            if (gameStarted) {

                for (Circle asteroid : asteroids) {
                    double fallingSpeed = 8;
                    double pos = asteroid.getCenterY();
                    if (score < 20000) {
                        asteroid.setCenterY(pos + fallingSpeed);
                    } else if (score > 20000 && score < 50000) {
                        asteroid.setCenterY(pos + fallingSpeed * 1.5);
                    } else if (score > 50000 && score < 100000) {
                        asteroid.setCenterY(pos + fallingSpeed * 2.0);
                    }
                }
                //loop asteroids
                if (asteroids.size() == 0) {
                    addAsteroid();
                }
                if (asteroids.size() < 4 && (asteroids.get(asteroids.size() - 1).getCenterY() > 300)) {
                    addAsteroid();
                }
                //delete once fallen
                for (int i = 0; i < asteroids.size(); i++) {
                    Circle asteroid = asteroids.get(i);
                    if (asteroid.getCenterY() + asteroid.getRadius() > HEIGHT - 50) {
                        asteroids.remove(asteroid);
                        hearts.remove(hearts.size() - 1);
                        try {
                            SoundSystem.playSound(liveLostMusic);
                        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e2) {
                            e2.printStackTrace();
                        }
                        if (hearts.size() == 0) {
                            gameStarted = false;
                            gameOver = true;
                            addHearts();
                        }
                    }
                }
                //shoots
                for (Rectangle shoot : shoots) {
                    if (shoot.y > 0) {
                        shoot.y -= 20;
                    }
                }
                for (int i = 0; i < shoots.size(); i++) {
                    Rectangle shoot = shoots.get(i);
                    if (shoot.y < 1) {
                        shoots.remove(shoot);
                    }
                }
                //backgrounds
                if (gameOver) {
                    backgroundSpeed = 0;
                }
                for (Rectangle background : backgrounds) {
                    background.y += backgroundSpeed;
                }
                if (backgrounds.get(0).y == HEIGHT - 60) {
                    backgrounds.get(0).y = -740;
                }
                if (backgrounds.get(1).y == HEIGHT - 60) {
                    backgrounds.get(1).y = -740;
                }

                for (int i = 0; i < asteroids.size(); i++) {
                    Circle asteroid = asteroids.get(i);
                    for (int j = 0; j < shoots.size(); j++) {
                        Rectangle shoot = shoots.get(j);
                        if (shoot.intersectsLine(
                                asteroid.getCenterX() - asteroid.getRadius(),
                                asteroid.getCenterY() + 0.9 * asteroid.getRadius(),
                                asteroid.getCenterX() + asteroid.getRadius(),
                                asteroid.getCenterY() + 0.9 * asteroid.getRadius())) {
                            explosions.add(new Rectangle(shoot.x, shoot.y, 50, 50));
                            try {
                                SoundSystem.playSound(boomMusic);
                            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e4) {
                                e4.printStackTrace();
                            }
                            shoots.remove(shoot);
                            score += random.nextInt(50) + 100;
                            remover++;
                            if (remover == 3) {
                                asteroids.remove(asteroid);
                                remover = 0;
                            }
                        }
                    }
                }

                if (explosions.size() > 1) {
                    explosions.remove(0);
                    alpha = 1;
                }
                if (explosions.size() > 0) {
                    alpha -= 0.05f;
                    if (alpha <= 0) {
                        alpha = 0;
                    }
                }
            }
            if (gameOver) {
                for (int i = 0; i < asteroids.size(); i++) {
                    Circle asteroid = asteroids.get(i);
                    asteroids.remove(asteroid);
                }
            }
            renderer.repaint();
        }


        @Override
        public void keyTyped (KeyEvent e){

            if (e.getKeyCode() == KeyEvent.VK_LEFT && shipPp.x > 0) {
                moveLeft();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && shipPp.x < 0) {
                shipPp.x = 0;
            }


            if (e.getKeyCode() == KeyEvent.VK_RIGHT && shipPp.x < WIDTH - shipPp.width) {
                moveRight();
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && shipPp.x > WIDTH - shipPp.width) {
                shipPp.x = WIDTH - shipPp.width;
            }
        }

        @Override
        public void keyPressed (KeyEvent e){


            if (e.getKeyCode() == KeyEvent.VK_LEFT && shipPp.x > 0) {
                moveLeft();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && shipPp.x < 0) {
                shipPp.x = 0;
            }


            if (e.getKeyCode() == KeyEvent.VK_RIGHT && shipPp.x < WIDTH - shipPp.width) {
                moveRight();
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && shipPp.x > WIDTH - shipPp.width) {
                shipPp.x = WIDTH - shipPp.width;
            }
        }

        @Override
        public void keyReleased (KeyEvent e){

            if (e.getKeyCode() == KeyEvent.VK_LEFT && shipPp.x > 0) {
                moveLeft();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && shipPp.x < 0) {
                shipPp.x = 0;
            }


            if (e.getKeyCode() == KeyEvent.VK_RIGHT && shipPp.x < WIDTH - shipPp.width) {
                moveRight();
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && shipPp.x > WIDTH - shipPp.width) {
                shipPp.x = WIDTH - shipPp.width;
            }

            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                addShoot();
                try {
                    SoundSystem.playSound(pewMusic);
                } catch (IOException | UnsupportedAudioFileException | InterruptedException | LineUnavailableException e1) {
                    e1.printStackTrace();
                }

            }
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ||
                    e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT && gameOver) {
                gameOver = false;
                gameStarted = true;
            }
        }

    }


