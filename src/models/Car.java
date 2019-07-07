package models;

import java.util.Locale;

public class Car {
    public int id;
    public int velocity;
    public int maxVelocity;
    public double brakingProbability;
    public int roadPosition;
    public int nextVelocity;

    public Car(int id, int velocity, int maxVelocity, double brakingProbability, int roadPosition) {
        this.id = id;
        this.velocity = velocity;
        this.maxVelocity = maxVelocity;
        this.brakingProbability = brakingProbability;
        this.roadPosition = roadPosition;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%d %d %.2f", id, roadPosition, 0.2);
    }

}
