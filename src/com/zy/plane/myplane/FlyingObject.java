package com.zy.plane.myplane;

import com.zy.plane.myplane.Bullet;

import java.awt.image.BufferedImage;

public abstract class FlyingObject {
    public BufferedImage image;  //资源图片
    public int width;//宽 高 尺寸
    public int height;
    public int x;//坐标
    public int y;

    //走一步
    public abstract void step();

    //检测是否出界
    public abstract boolean outOfBounds();

    //检测是否被敌人击中
    public boolean shootBy(Bullet bullet) {
        int x1 = this.x;
        int x2 = this.width + this.x;
        int y1 = this.y;
        int y2 = this.y + this.height;
        int x = bullet.x;
        int y = bullet.y;
        return (x >= x1 && x < x2 && y >= y1 && y <= y2);

    }


}
