package models;

public class Cell {

    private Car car;

    public Cell() {
    }

    public Cell(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
