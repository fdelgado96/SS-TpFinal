package models;

import java.util.Locale;

public class Car {
    public int id;
    public int velocity;
    public int maxVelocity;
    public double brakingProbability;
    public int lanePosition;
    public int lane;
    public int nextLane = -1;
    public int nextVelocity;

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
        return String.format(Locale.ENGLISH, "%d %d %d %d 0.2", id, lane, lanePosition, velocity);
    }

}
