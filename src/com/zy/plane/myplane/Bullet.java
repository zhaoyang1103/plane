package com.zy.plane.myplane;

import com.zy.plane.ShootGame;

public class Bullet extends FlyingObject {
    private int speed = 3;

    public Bullet(int x, int y) {
        image = ShootGame.bullet;
        width = image.getWidth();
        height = image.getHeight();
        this.x = x;
        this.y = y;
    }

    @Override
    public void step() {
        y -= speed;//子弹向上走
    }

    @Override
    public boolean outOfBounds() {
        return this.y<=-this.height;

    }
}
