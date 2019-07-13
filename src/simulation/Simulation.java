package simulation;

import models.Car;
import models.Cell;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.IntStream;

public class Simulation {

    private static int nCars = 50;
    private static int freewayLength = 50;
    private static int freewayLanes = 5;
    private static int initialVelocity = 0;
    private static int maxVelocity = 10;
    private static double brakingProbability = 0.1;

    private static Car[] cars;
    private static Cell[][] freeway;

    private static int simulationTime = 0;
    private static int maxTime = 100;


    public static void main(String args[]) throws Exception{
        initFreeway();
        initCarsInFreeway();

        PrintWriter writer = new PrintWriter("data/length_" + freewayLength + "_lanes_" + freewayLanes + "_cars_" + nCars + "_simulation.xyz");
        long now = System.currentTimeMillis();
        while(simulationTime < maxTime) {

            IntStream.range(0, cars.length).forEach(i -> {
                cars[i].nextVelocity = getNextVelocity(cars[i]);
            });

            IntStream.range(0, cars.length).forEach(i -> {
                moveCar(cars[i]);
            });

            simulationTime++;
            writeState(writer);
        }
        long after = System.currentTimeMillis();
        System.out.println("ElapsedTime: " + (after - now));
        writer.close();
    }

    private static void initFreeway() {
        freeway = new Cell[freewayLanes][freewayLength];
        for (int i = 0; i < freewayLanes; i++) {
            for (int j = 0; j < freewayLength; j++) {
                freeway[i][j] = new Cell();
            }
        }
    }

    private static void initCarsInFreeway(){
        cars = new Car[nCars];
        int randomLane;
        int randomLanePosition;
        for (int i = 0; i < nCars; i++){
            do {
                randomLane = getRandomLane();
                randomLanePosition = getRandomLanePosition();
            } while(freeway[randomLane][randomLanePosition].hasCar());
            cars[i] = new Car(i, initialVelocity, maxVelocity, brakingProbability, randomLane, randomLanePosition);
            freeway[randomLane][randomLanePosition].setCar(cars[i]);
        }

    }

    private static int getRandomLane() {
        return (int) Math.floor(Math.random() * freewayLanes);
    }

    private static int getRandomLanePosition() {
        return (int) Math.floor(Math.random() * freewayLength);
    }

    private static void moveCar(Car car){
        freeway[car.lane][car.lanePosition].removeCar();
        car.velocity = car.nextVelocity;
        car.nextVelocity = car.velocity;
        if(car.lanePosition + car.velocity >= freewayLength) {
            int amountTillLaneEnd = freewayLength - (car.lanePosition + 1);
            car.lanePosition = car.velocity - amountTillLaneEnd - 1;
        } else {
            car.lanePosition += car.velocity;
        }
        freeway[car.lane][car.lanePosition].setCar(car);
    }

    private static int getNextVelocity(Car car){
        int nextVelocity = maxVelocityRule(car);
        nextVelocity = slowingDownRule(car, nextVelocity);
        nextVelocity = randomBrakeRule(car, nextVelocity);
        return nextVelocity;
    }

    private static int maxVelocityRule(Car car){
        return Math.min(car.velocity + 1, car.maxVelocity);
    }

    private static int slowingDownRule(Car car, int nextVelocity){
        int nextCarPossibleLanePosition = car.lanePosition;

        do {
            nextCarPossibleLanePosition++;
            if (nextCarPossibleLanePosition == freewayLength){
                nextCarPossibleLanePosition = 0;
            }
        } while(!freeway[car.lane][nextCarPossibleLanePosition].hasCar() && nextCarPossibleLanePosition != car.lanePosition);

        if(nextCarPossibleLanePosition == car.lanePosition) {
            return nextVelocity;
        }

        int nextCarLanePosition = nextCarPossibleLanePosition;
        int diffLanePosition;
        if(nextCarLanePosition - car.lanePosition < 0) {
            diffLanePosition = freewayLength - (car.lanePosition + 1) + nextCarLanePosition;
        } else {
            diffLanePosition = nextCarLanePosition - car.lanePosition - 1;
        }

        return Math.min(nextVelocity, diffLanePosition);
    }

    private static int randomBrakeRule(Car car, int nextVelocity){
        return (nextVelocity > 0 && Math.random() < car.brakingProbability) ? nextVelocity - 1 : nextVelocity;
    }

    private static void writeState(PrintWriter writer) {
        writer.println(cars.length + 2);
        writer.println();
        writer.println("-1 -1 -1 0.0001");
        writer.println(String.format(Locale.ENGLISH, "-2 %d %d 0.0001", freewayLanes , freewayLength ));
        IntStream.range(0, cars.length).forEach(i -> {
            writer.println(cars[i]);
        });
    }
}
