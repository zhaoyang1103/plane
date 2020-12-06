package com.zy.plane.myplane;

import com.zy.plane.imple.Award;
import com.zy.plane.ShootGame;

import java.util.Random;

public class Bee extends FlyingObject implements Award {
    //小蜜蜂 加分飞行
    private int xSpeed = 1;
    private int ySpeed = 2;
    private int awardType;

    public Bee() {
        image = ShootGame.bee;
        width = image.getWidth();
        height = image.getHeight();
        Random random = new Random();
        x = random.nextInt(ShootGame.WIDTH - this.width);
        y =- this.height;
        awardType = random.nextInt(2);

    }

    @Override
    public int getType() {
        return awardType;
    }

    @Override
    public void step() {
        x += xSpeed;
        y += ySpeed;
        if (x >= ShootGame.WIDTH - this.width*2) {
            xSpeed -= 1;
            if (x <= 0) {
                xSpeed += 1;
            }


        }

    }

    @Override
    public boolean outOfBounds() {
        return this.y >= ShootGame.HEIGHT;
    }
}
