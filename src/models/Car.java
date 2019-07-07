package models;

public class Car {
    public int speed;
    public int maxSpeed;
    public double breakingProbability;
    public int separationDistance;

    public Car(int speed, int maxSpeed, double breakingProbability, int separationDistance) {
        this.speed = speed;
        this.maxSpeed = maxSpeed;
        this.breakingProbability = breakingProbability;
        this.separationDistance = separationDistance;
    }
}
