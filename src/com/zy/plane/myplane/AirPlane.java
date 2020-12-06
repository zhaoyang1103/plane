package com.zy.plane.myplane;

import com.zy.plane.imple.Enemy;
import com.zy.plane.ShootGame;

import java.util.Random;

public class AirPlane extends FlyingObject implements Enemy {
    private int speed = 2;

    public AirPlane() {
        image = ShootGame.airplane;
        width = image.getWidth();
        height = image.getHeight();
        Random rand = new Random();
        x = rand.nextInt(ShootGame.WIDTH - this.width*2); //不能让飞机走最右边
        y =- this.height;


    }

    @Override
    public int getScore() {

        return 5;
    }

    @Override
    public void step() {
        y += speed; //敌机向下走
    }

    @Override
    public boolean outOfBounds() {
        return this.y >= ShootGame.HEIGHT;
    }
}
