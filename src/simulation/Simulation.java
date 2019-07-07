package simulation;

import models.Automaton;
import models.Car;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.IntStream;

public class Simulation {

    private static int nCars = 100;
    private static int roadLength = 100;
    private static int initialVelocity = 0;
    private static int maxVelocity = 10;

    private static ArrayList<Car> cars;

    private static int simulationTime = 0;
    private static int maxTime = 100;


    public static void main(String args[]) throws Exception{
        cars = new ArrayList<>(nCars);

        IntStream.range(0, nCars).forEach(i -> {
            cars.add(new Car(i, initialVelocity, maxVelocity, 0.05, i));
        });

        PrintWriter writer = new PrintWriter("data/" + roadLength + "_" + nCars + "_simulation.xyz");

        while(simulationTime < maxTime) {

            IntStream.range(0, cars.size()).forEach(i -> {
                cars.get(i).nextVelocity = getNextVelocity(cars, i);
                System.out.println(cars.get(i).nextVelocity);
            });

            cars.forEach(Simulation::moveCar);

            simulationTime++;
            writeState(writer);
        }
        System.out.println(cars);
        writer.close();
    }

    private static void moveCar(Car car){
        System.out.println("-------");
        System.out.println("initialPos: " + car.roadPosition);
        System.out.println("nextVelocity: " + car.nextVelocity);
        car.velocity = car.nextVelocity;
        car.nextVelocity = car.velocity;
        if(car.roadPosition + car.velocity >= roadLength) {
            int amountTillRoadEnd = roadLength - (car.roadPosition + 1);
            car.roadPosition = car.velocity - amountTillRoadEnd - 1;
        } else {
            car.roadPosition += car.velocity;
        }

        System.out.println("finalPos: " + car.roadPosition);
    }

    private static int getNextVelocity(ArrayList<Car> cars, int carIndex){
        int nextVelocity = maxVelocityRule(cars.get(carIndex));
        nextVelocity = slowingDownRule(nextVelocity, cars, carIndex);
        nextVelocity = randomBrakeRule(cars.get(carIndex), nextVelocity);
        return nextVelocity;
    }

    private static int maxVelocityRule(Car car){
        return Math.min(car.velocity + 1, car.maxVelocity);
    }

    private static int slowingDownRule(int nextVelocity, ArrayList<Car> cars, int carIndex){
        int diffRoadPosition;
        int roadPosition = cars.get(carIndex).roadPosition;
        int otherRoadPosition;
        if(carIndex + 1 == cars.size()) {
            otherRoadPosition = cars.get(0).roadPosition;
        } else {
            otherRoadPosition = cars.get(carIndex + 1).roadPosition;
        }

        if(otherRoadPosition - roadPosition < 0) {
            diffRoadPosition = roadLength - roadPosition + otherRoadPosition + 1;
        } else {
            diffRoadPosition = otherRoadPosition - roadPosition;
        }

        return Math.min(nextVelocity, diffRoadPosition);
    }

    private static int randomBrakeRule(Car car, int nextVelocity){
        return (nextVelocity > 0 && Math.random() < car.brakingProbability) ? nextVelocity - 1 : nextVelocity;
    }

    private static void writeState(PrintWriter writer) {
        writer.println(cars.size() + 2);
        writer.println();
        writer.println("-1 0 0.0001");
        writer.println(String.format(Locale.ENGLISH, "-2 %d 0.0001", roadLength - 1 ));
        cars.stream().parallel().forEach(writer::println);
    }
}
