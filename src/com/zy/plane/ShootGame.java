package com.zy.plane;

import com.zy.plane.imple.Award;
import com.zy.plane.imple.Enemy;
import com.zy.plane.music.AudioPlayer;
import com.zy.plane.myplane.*;
import javazoom.jl.decoder.JavaLayerException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

public class ShootGame extends JPanel {
    public static final int WIDTH = 400 + 75;
    public static final int HEIGHT = 700;
    public static BufferedImage background;
    public static BufferedImage start;
    public static BufferedImage pause;
    public static BufferedImage gameOver;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero0;
    public static BufferedImage hero1;

    public static BufferedImage destory;


    private static BufferedImage changeBullet;

    private static BufferedImage changehero;

    private static BufferedImage backall;

    static {
        try {
            background = ImageIO.read(ShootGame.class.getResource("./images/background/background_2.png"));
            destory = ImageIO.read(ShootGame.class.getResource("./images/blast/blast_3.png"));
            start = ImageIO.read(ShootGame.class.getResource("./images/start.png"));
            pause = ImageIO.read(ShootGame.class.getResource("./images/Pause.png"));
            gameOver = ImageIO.read(ShootGame.class.getResource("./images/gameover.png"));

            airplane = ImageIO.read(ShootGame.class.getResource("./images/LittlePlane/plane2.png"));
            bee = ImageIO.read(ShootGame.class.getResource("./images/LittlePlane/plane3.png"));
            bullet = ImageIO.read(ShootGame.class.getResource("./images/bullet/bullet_1.png"));
            hero0 = ImageIO.read(ShootGame.class.getResource("./images/1.png"));
            hero1 = ImageIO.read(ShootGame.class.getResource("./images/2.png"));
            changeBullet = ImageIO.read(ShootGame.class.getResource("./images/changeBullet.jpg"));
            changehero = ImageIO.read(ShootGame.class.getResource("./images/changehero.jpg"));
            backall = ImageIO.read(ShootGame.class.getResource("./images/backall.jpg"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private Hero hero = new Hero();

    private FlyingObject[] flyings = {};
    private ArrayList<FlyingObject> desList = new ArrayList<>();
    private Bullet[] bullets = {};
    public static final int START = 0;
    public static final int RUNNING = 1;
    public static final int PAUSE = 2;
    public static final int GAME_OVER = 3;
    private int state = START;
    private static AudioPlayer audioPlayer;
    private static ArrayList<String> bulletsa1 = new ArrayList<>();
    private static ArrayList<String> backs = new ArrayList<>();
    private static ArrayList<String[]> heroes = new ArrayList<>();

    //生成敌人
    public FlyingObject nextOne() {

        Random rand = new Random();
        int type = rand.nextInt(20);
        if (type < 4) {
            return new Bee();
        } else return new AirPlane();
    }

    int flyIndex = 0; //敌人的入场数量

    public void enterAction() {
        flyIndex++;  //每10mm加一
        if (flyIndex % 40 == 0) {
            FlyingObject object = nextOne();
            flyings = Arrays.copyOf(flyings, flyings.length + 1);//数组扩容
            flyings[flyings.length - 1] = object;   //将敌机加入数字尾部
        }

    }

    public void stepAction() {
        hero.step();
        for (int i = 0; i < flyings.length; i++) {
            flyings[i].step();
        }
        for (int i = 0; i < bullets.length; i++) {
            bullets[i].step();

        }
    }

    //子弹计数
    int shootIndex = 0;

    public void shootAction() {
        shootIndex++;
        if (shootIndex % 30 == 0) {
            Bullet[] bs = hero.shoot();
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);

        }

    }

    public void outOfBoundsAction() {
        int index = 0;
        FlyingObject[] flyingLives = new FlyingObject[flyings.length];
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];  //拿到飞行物
            if (!f.outOfBounds()) {
                flyingLives[index] = f;
                index++;
            }
        }

        flyings = Arrays.copyOf(flyingLives, index);  //将不越界的第二年赋值到flyings
        index = 0;

        Bullet[] bulletLives = new Bullet[bullets.length];
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            if (!b.outOfBounds()) {
                bulletLives[index] = b;
                index++;

            }
        }
        bullets = Arrays.copyOf(bulletLives, index);

    }

    //子弹与敌机碰撞
    public void bangAction() {
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            bang(b);
        }


    }

    int score = 0;

    private void bang(Bullet b) {
        int index = -1;
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            if (f.shootBy(b)) {
                index = i; //下标敌人被击中了
                b.y = -100;
                desList.add(f);
                break;//其余敌人不在参与比较
            }
        }
        if (index != -1) {
            FlyingObject one = flyings[index];
            if (one instanceof Enemy) {
                Enemy e = (Enemy) one;
                score += e.getScore();
            }
            if (one instanceof Award) {
                Award a = (Award) one;
                int type = a.getType();
                switch (type) {
                    case Award.DOUBLE_FIRE:
                        hero.addDoubleFire();
                        break;
                    case Award.LIFE:
                        hero.addLife();
                        break;

                }
            }//交换被撞敌人和最后一个敌机元素


            FlyingObject t = flyings[index];
            flyings[index] = flyings[flyings.length - 1];
            flyings[flyings.length - 1] = t;
            flyings = Arrays.copyOf(flyings, flyings.length - 1); //缩小范围


            new Thread() {
                @Override
                public void run() {
                    try {
                        audioPlayer.play();
                        interrupt();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
//                  super.run();
                }
            }.start();


        }
    }

    //英雄和敌机碰撞
    public void hitAction() {
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            if (hero.hit(f)) {
                hero.subtractLife();//
                hero.clearDoubleFire();
                FlyingObject t = flyings[i];  //拿到敌机
                flyings[i] = flyings[flyings.length - 1];//准备清空敌机, 即 交换被碰撞敌机和最后元素敌机 的位置               flyings[flyings.length - 1] = t;
                flyings[flyings.length - 1] = t;
                flyings = Arrays.copyOf(flyings, flyings.length - 1);
            }
        }
    }

    //判断游戏结束
    public void checkGameOverAction() {
        if (hero.getLife() <= 0) {
            state = GAME_OVER;
            desList.add(hero);

        }

    }

    public void action() {
        MouseAdapter l = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (state == RUNNING) {
                    int x = e.getX();
                    int y = e.getY();
                    hero.moveTo(x, y);
                }

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                double x = e.getPoint().getX();
                double y = e.getPoint().getY();
                if (x >= 405 && x <= 455 && y >= 0 && y <= 30)
                    changeBullet8();
                else if (x >= 405 && x <= 455 && y >= 60 && y <= 90) {
                    changeBack();
                    System.out.println("切换英雄和背景");
                    changeHeroes();


                } else {
                    switch (state) {
                        case START:
                            state = RUNNING;
                            break;
                        case RUNNING:
                            state = PAUSE;
                            break;
                        case PAUSE:
                            state = RUNNING;
                            break;
                        case GAME_OVER:
                            score = 0;
                            hero = new Hero();
                            flyings = new FlyingObject[0];
                            bullets = new Bullet[0];
                            state = START;
                    }
                }

            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (state == PAUSE) {
                    state = PAUSE;
                }
            }
        };

        this.addMouseListener(l);
        this.addMouseMotionListener(l);
        Timer timer = new Timer();
        int intervel = 10;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == RUNNING) {
                    enterAction();
                    stepAction();
                    shootAction();
                    outOfBoundsAction();
                    bangAction();
                    hitAction();
                    checkGameOverAction();
                }
                repaint();

            }
        }, intervel, intervel);


    }

    int changeHeroInex = 0;

    private void changeHeroes() {
        changeHeroInex++;
        changeHeroInex %= heroes.size();
        System.out.println(changeHeroInex + "标识");
        try {
            hero0 = ImageIO.read(ShootGame.class.getResource(heroes.get(changeHeroInex)[0]));
            hero1 = ImageIO.read(ShootGame.class.getResource(heroes.get(changeHeroInex)[1]));
            hero.setImages(new BufferedImage[]{hero0, hero1});
            repaint();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    int changeBack = 0;

    private void changeBullet8() {
        bullet8g++;
        int index = bullet8g % bulletsa1.size();
        try {
            bullet = ImageIO.read(ShootGame.class.getResource(bulletsa1.get(index)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeBack() {

        changeBack++;
        changeBack %= backs.size();
        try {
            background = ImageIO.read(ShootGame.class.getResource(backs.get(changeBack)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void paint(Graphics g) {
        g.drawImage(backall, 0, 0, null);
        g.drawImage(background, 0, 0, null);
        paintHero(g);
        paintBullets(g);
        paintFlyingObjects(g);
        paintScoreAndLife(g);
        paintState(g);
        paintBlast(g);
        paintButton(g);
    }

    int blastindex = 0;

    private void paintBlast(Graphics g) {

        blastindex++;
        for (int i = 0; i < desList.size(); i++) {
            FlyingObject f = desList.get(i);
            f.image = destory;
            g.drawImage(f.image, f.x, f.y, null);
        }
        if (blastindex % 50 == 0) {
            desList.clear();
        }


    }

    public void paintHero(Graphics g) {
        g.drawImage(hero.image, hero.x, hero.y, null);
    }

    public void paintFlyingObjects(Graphics g) {
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            g.drawImage(f.image, f.x, f.y, null);
        }

    }

    public void paintBullets(Graphics g) {
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            g.drawImage(b.image, b.x, b.y, null);

        }

    }

    public void paintScoreAndLife(Graphics g) {
        g.setColor(new Color(0xFF000));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g.drawString("分数:" + score, 10, 25);
        g.drawString("生命值" + hero.getLife(), 10, 45);


    }

    public void paintState(Graphics g) {
        switch (state) {
            case START:
                g.drawImage(start, 320, 0, null);
                break;
            case PAUSE:
                g.drawImage(pause, 320, 0, null);
                break;
            case GAME_OVER:
                g.drawImage(gameOver, 320, 0, null);
                break;

        }


    }

    public void paintButton(Graphics g) {
        g.drawImage(changeBullet, 405, 0, null);
        g.drawImage(changehero, 405, 60, null);


    }

    public static void readyMusic() {
        File file = new File(AudioPlayer.bang);
        audioPlayer = new AudioPlayer(file);

    }

    int bullet8g = 0;

    public static void initChangeButton() {
        bulletsa1.add("./images/bullet/bullet_1.png");
        bulletsa1.add("./images/bullet/bullet_2.png");
        bulletsa1.add("./images/bullet/bullet_3.png");
        bulletsa1.add("./images/bullet/bullet_4.png");
        bulletsa1.add("./images/bullet/bullet_5.png");
        bulletsa1.add("./images/bullet/bullet_6.png");
        bulletsa1.add("./images/bullet/bullet_7.png");
        bulletsa1.add("./images/bullet/bullet_8.png");
        backs.add("./images/background/background_1.png");
        backs.add("./images/background/background_2.png");
        backs.add("./images/background/background_3.png");
        backs.add("./images/background/background_4.png");
        String[] s = {"./images/1.png", "./images/1.png"};
        String[] s1 = {"./images/plane_2_1.png", "./images/plane_2_2.png"};
        String[] s2 = {"./images/plane3_1.png", "./images/plane_3_2.png"};
        String[] s3 = {"./images/plane_4_1.png", "./images/plane_4_2.png"};
        String[] s4 = {"./images/plane_5_1.png", "./images/plane_5_2.png"};
        String[] s5 = {"./images/plane_6_1.png", "./images/plane_6_2.png"};
        heroes.add(s);
        heroes.add(s1);
        heroes.add(s2);
        heroes.add(s3);
        heroes.add(s4);
        heroes.add(s5);

    }

    public static void main(String[] ars) {
        initChangeButton();
        readyMusic();
        JFrame frame = new JFrame("Fly");
        ShootGame game = new ShootGame();
        frame.add(game);
        frame.setSize(WIDTH, HEIGHT);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.action();
    }

}