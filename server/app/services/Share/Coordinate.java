package services;

public final class Coordinate {
    private final double x;
    private final double y;

    public Coordinate(double first, double second) {
        this.x = first;
        this.y = second;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isSame(Coordinate other) {
        double x = other.getX();
        double y = other.getY();
        if (x - Constants.COORDINATE_ERROR_ACCEPTED <= this.x 
                && x + Constants.COORDINATE_ERROR_ACCEPTED >= this.x
                && y - Constants.COORDINATE_ERROR_ACCEPTED <= this.y
                && y + Constants.COORDINATE_ERROR_ACCEPTED >= this.y

        )
            return true;
        return false;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

}
