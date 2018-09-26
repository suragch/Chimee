package net.studymongolian.chimee;

public class PointFloat {

    private float x;
    private float y;

    PointFloat() {}

    PointFloat(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
