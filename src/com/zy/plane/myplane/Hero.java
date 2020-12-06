package com.zy.plane.myplane;

import com.zy.plane.ShootGame;

import java.awt.image.BufferedImage;

public class Hero extends FlyingObject {
    private int doubleFire;
    private int life;
    private BufferedImage[] images;
    private int index;

    public Hero() {
        image = ShootGame.hero0;
        width = image.getWidth();
        height = image.getHeight();

        x = 150;
        y = 400;
        doubleFire = 10000;
        life = 3;
        images = new BufferedImage[]{ShootGame.hero0,
                ShootGame.hero1};
        index = 0;
    }

    public void setImages(BufferedImage[] images) {
        this.images = images;
    }

    @Override
    public void step() {
        image = images[index++ / 10 % images.length]; //发火和不发货状态切换
    }

    public Bullet[] shoot() {
        int xStep = this.width / 4;   //子弹 1/4英雄机的宽
        int yStep = 20;
        if (doubleFire > 0) {
            Bullet[] bs = new Bullet[2];
            bs[0] = new Bullet(this.x + 1 * xStep, this.y - yStep);//左子弹位置 实例化
            bs[1] = new Bullet(this.x + 3 * xStep, this.y - yStep);//左子弹位置 实例化
            return bs;
        } else {
            Bullet[] bs = new Bullet[1];
            bs[0] = new Bullet(this.x + 2 * xStep, this.y - yStep); //没有火力值就把子弹放中间

            return bs;
        }

    }

    //英雄机器的位置 随鼠标的移动而改变
    public void moveTo(int x, int y) {
        this.x = x - this.width / 2;
        this.y = y - this.height / 2;
        if(x>=ShootGame.WIDTH-this.width*2)
        {
            this.x=ShootGame.WIDTH-this.width*2;
        }

    }

    @Override
    public boolean outOfBounds() {
        return false;
    }

    public void addLife() {
        life++;
    }

    //获取英雄机器的生命
    public int getLife() {
        return life;
    }

    //丢失一条命
    public void subtractLife() {
        life--;
    }

    public void addDoubleFire() {
        doubleFire++;
    }

    public void clearDoubleFire() {
        doubleFire = 0;
    }

    public boolean hit(FlyingObject other) {
        int x1 = other.x - this.width / 2;      //
        int y1 = other.y - this.height / 2;
        int x2 = other.x + other.width + this.width / 2;
        int y2 = other.y + other.height + this.height / 2;
        int x = this.x + this.width / 2;
        int y = this.y + this.height / 2;
        boolean flag = x >= x1 && x <= x2 && y >= y1 && y <= y2;
        if (flag) {
            System.out.println(flag + "和敌机碰撞正确");
        }
        return flag;

    }
}
