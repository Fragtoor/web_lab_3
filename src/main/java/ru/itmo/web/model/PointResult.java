package ru.itmo.web.model;

import java.io.Serializable;

public class PointResult implements Serializable {
    private final double x;
    private final double y;
    private final double r;
    private final boolean hit;
    private final String currentTime;
    private final String executionTime;

    public PointResult(double x, double y, double r, boolean hit, String currentTime, String executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.currentTime = currentTime;
        this.executionTime = executionTime;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
    public boolean isHit() { return hit; }
    public String getCurrentTime() { return currentTime; }
    public String getExecutionTime() { return executionTime; }
}
