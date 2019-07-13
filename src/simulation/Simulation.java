package simulation;

import models.Car;
import models.Cell;

import java.io.PrintWriter;
import java.util.Locale;
import java.util.stream.IntStream;

public class Simulation {

    private static int nCars = 50;
    private static int freewayLength = 50;
    private static int freewayLanes = 5;
    private static int initialVelocity = 0;
    private static int maxVelocity = 10;
    private static double brakingProbability = 0.1;
    private static double considerLaneChangeProbability = 0.2;

    private static Car[] cars;
    private static Cell[][] freeway;

    private static int simulationTime = 0;
    private static int maxTime = 100;


    public static void main(String args[]) throws Exception{
        initFreeway();
        initCarsInFreeway();

        PrintWriter writer = new PrintWriter("data/length_" + freewayLength + "_lanes_" + freewayLanes + "_cars_" + nCars + "_maxvelocity_" + maxVelocity + "_simulation.xyz");
        PrintWriter csvWriter = new PrintWriter("data/length_" + freewayLength + "_lanes_" + freewayLanes + "_cars_" + nCars + "_maxvelocity_" + maxVelocity + "_simulation.csv");
        csvWriter.println("timestep,id,lane,lane_position,velocity");
        writeCSV(csvWriter);

        long now = System.currentTimeMillis();
        while(simulationTime < maxTime) {

            IntStream.range(0, cars.length).forEach(i -> {
                cars[i].nextLane = getNextLane(cars[i]);
            });

            IntStream.range(0, cars.length).forEach(i -> {
                if(cars[i].nextLane != -1 && cars[i].nextLane != cars[i].lane){
                    changeToLane(cars[i], cars[i].nextLane);
                }
            });

            IntStream.range(0, cars.length).forEach(i -> {
                cars[i].nextVelocity = getNextVelocity(cars[i]);
            });

            IntStream.range(0, cars.length).forEach(i -> {
                advanceCar(cars[i]);
            });

            simulationTime++;
            writeState(writer);
            writeCSV(csvWriter);
        }
        long after = System.currentTimeMillis();
        System.out.println("ElapsedTime: " + (after - now));
        writer.close();
        csvWriter.close();
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

    private static int getNextCarDiffLanePosition(Car car, int nextCarLanePosition) {
        int diffLanePosition;
        if(nextCarLanePosition - car.lanePosition < 0) {
            diffLanePosition = freewayLength - car.lanePosition + nextCarLanePosition;
        } else {
            diffLanePosition = nextCarLanePosition - car.lanePosition;
        }
        return diffLanePosition;
    }

    private static int getPrevCarDiffLanePosition(Car car, int prevCarLanePosition) {
        int diffLanePosition;
        if(car.lanePosition - prevCarLanePosition  < 0) {
            diffLanePosition = freewayLength - prevCarLanePosition + car.lanePosition;
        } else {
            diffLanePosition = car.lanePosition - prevCarLanePosition;
        }
        return diffLanePosition;
    }

    private static int getNextLane(Car car){
        if(Math.random() > considerLaneChangeProbability){
            return -1;
        }

        int nextCarCurrentLanePosition = getNextCarLanePosition(car, car.lane);
        if(nextCarCurrentLanePosition == -1) {
            return -1;
        }
        int nextCarDiffCurrentLane = getNextCarDiffLanePosition(car, nextCarCurrentLanePosition);
        if(car.maxVelocity <= nextCarDiffCurrentLane - 1) {
            return -1;
        }

        boolean leftLaneExists = false;
        if(car.lane > 0) {
            leftLaneExists = true;
        }
        boolean rightLaneExists = false;
        if(car.lane < freewayLanes - 1) {
            rightLaneExists = true;
        }
        if(!leftLaneExists && !rightLaneExists){
            return -1;
        }

        int prevCarLeftLanePosition = -2;
        int prevCarRightLanePosition = -2;
        int nextCarLeftLanePosition = -2;
        int nextCarRightLanePosition = -2;

        boolean leftLaneHasCar = false;
        boolean rightLaneHasCar = false;


        if(leftLaneExists){ // Left
            prevCarLeftLanePosition = getPrevCarLanePosition(car, car.lane - 1);
            nextCarLeftLanePosition = getNextCarLanePosition(car, car.lane - 1);
            leftLaneHasCar = freeway[car.lane - 1][car.lanePosition].hasCar();
        }
        if(rightLaneExists){ // Right
            prevCarRightLanePosition = getPrevCarLanePosition(car, car.lane + 1);
            nextCarRightLanePosition = getNextCarLanePosition(car, car.lane + 1);
            rightLaneHasCar = freeway[car.lane + 1][car.lanePosition].hasCar();
        }
        if(leftLaneHasCar && rightLaneHasCar){
            return -1;
        }

        if((leftLaneExists && rightLaneExists) && (!leftLaneHasCar && prevCarLeftLanePosition == -1) && (!rightLaneHasCar && prevCarRightLanePosition == -1)) {
            if(Math.random() < 0.5) { // Change to left lane
                return car.lane - 1;
            }else{  // Change to right lane
                return car.lane + 1;
            }
        }

        if(leftLaneExists && !leftLaneHasCar && prevCarLeftLanePosition == -1) {
            return car.lane - 1;
        }else if(rightLaneExists && !rightLaneHasCar && prevCarRightLanePosition == -1){
            return car.lane + 1;
        }

        int prevCarDiffLeftLane = getPrevCarDiffLanePosition(car, prevCarLeftLanePosition);
        int prevCarDiffRightLane = getPrevCarDiffLanePosition(car, prevCarRightLanePosition);

        int nextCarDiffLeftLane = getNextCarDiffLanePosition(car, nextCarLeftLanePosition);
        int nextCarDiffRightLane = getNextCarDiffLanePosition(car, nextCarRightLanePosition);

        if((leftLaneExists && rightLaneExists) && !leftLaneHasCar && !rightLaneHasCar){
            if(car.maxVelocity > prevCarDiffLeftLane - 1 && car.maxVelocity > prevCarDiffRightLane - 1){
                return -1;
            }
            if(car.maxVelocity <= prevCarDiffLeftLane - 1) {
                if(nextCarDiffLeftLane >= nextCarDiffCurrentLane) {
                    return car.lane - 1;
                }
            }else if(nextCarDiffRightLane >= nextCarDiffCurrentLane){
                return car.lane + 1;
            }
        }

        if(leftLaneExists && !leftLaneHasCar && car.maxVelocity <= prevCarDiffLeftLane - 1 && nextCarDiffLeftLane >= nextCarDiffCurrentLane) {
            return car.lane - 1;
        }else if(rightLaneExists && !rightLaneHasCar && car.maxVelocity <= prevCarDiffRightLane - 1 && nextCarDiffRightLane >= nextCarDiffCurrentLane){
            return car.lane + 1;
        }

        return -1;
    }

    private static void changeToLane(Car car, int lane){
        freeway[car.lane][car.lanePosition].removeCar();
        car.lane = lane;
        freeway[lane][car.lanePosition].setCar(car);
    }

    private static void advanceCar(Car car){
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

    private static int getPrevCarLanePosition(Car car, int lane){
        int prevCarLanePosition = car.lanePosition;

        do {
            prevCarLanePosition--;
            if (prevCarLanePosition < 0){
                prevCarLanePosition = freewayLength - 1;
            }
        } while(!freeway[lane][prevCarLanePosition].hasCar() && prevCarLanePosition != car.lanePosition);
        if(prevCarLanePosition == car.lanePosition){
            return -1;
        }
        return prevCarLanePosition;
    }

    private static int getNextCarLanePosition(Car car, int lane){
        int nextCarLanePosition = car.lanePosition;

        do {
            nextCarLanePosition++;
            if (nextCarLanePosition == freewayLength){
                nextCarLanePosition = 0;
            }
        } while(!freeway[lane][nextCarLanePosition].hasCar() && nextCarLanePosition != car.lanePosition);
        if(nextCarLanePosition == car.lanePosition){
            return -1;
        }
        return nextCarLanePosition;
    }

    private static int slowingDownRule(Car car, int nextVelocity){
        int nextCarLanePosition = getNextCarLanePosition(car, car.lane);
        if(nextCarLanePosition == -1) {
            return nextVelocity;
        }
        return Math.min(nextVelocity, getNextCarDiffLanePosition(car, nextCarLanePosition) - 1);
    }

    private static int randomBrakeRule(Car car, int nextVelocity){
        return (nextVelocity > 0 && Math.random() <= car.brakingProbability) ? nextVelocity - 1 : nextVelocity;
    }

    private static void writeState(PrintWriter writer) {
        writer.println(cars.length + 2);
        writer.println();
        writer.println("-1 -1 -1 0 0.0001");
        writer.println(String.format(Locale.ENGLISH, "-2 %d %d 0 0.0001", freewayLanes , freewayLength ));
        IntStream.range(0, cars.length).forEach(i -> {
            writer.println(cars[i]);
        });
    }

    private static void writeCSV(PrintWriter writer) {
        IntStream.range(0, cars.length).forEach(i -> {
            writer.println(simulationTime + "," + cars[i].toCSVString());
        });
    }
}
