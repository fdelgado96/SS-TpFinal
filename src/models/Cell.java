package models;

public class Cell {

    private Car car;

    public Cell() {
    }

    public Cell(Car car) {
        this.car = car;
    }

    public boolean hasCar() {
        return car != null;
    }

    public void removeCar() {
        car = null;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
