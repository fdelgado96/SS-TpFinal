package models;

import java.util.Locale;

public class Car {
    public int id;
    public int velocity;
    public int maxVelocity;
    public double brakingProbability;
    public int nextVelocity;
    public int lanePosition;
    public int lane;

    public Car(int id, int velocity, int maxVelocity, double brakingProbability, int lane, int lanePosition) {
        this.id = id;
        this.velocity = velocity;
        this.maxVelocity = maxVelocity;
        this.brakingProbability = brakingProbability;
        this.lane = lane;
        this.lanePosition = lanePosition;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%d %d %d %.2f", id, lane, lanePosition, 0.2);
    }

}
