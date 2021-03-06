package services;
import java.util.ArrayList;

/**
 * Path of a Journey 
 */
public class JourneyPath{
    /**
     * Start coordinate
     */
    Coordinate start;
    /**
     * End coordinate
     */
    Coordinate end;

    /**
     * Constructor
     * @param path: the ArrayList of Coordinate correspinding to the jounrey
     */
    public JourneyPath( ArrayList<Coordinate> path){
        double lat = path.get(0).getX();
        double lon = path.get(0).getY();
        start = Constants.CoordinateTransformation(lat, lon);
        lat = path.get(path.size()-1).getX();
        lon = path.get(path.size()-1).getY();
        end = Constants.CoordinateTransformation(lat, lon);
    }

    /**
     * Return the start point of the path.
     * @return Return the start point of the path.
     */
    public Coordinate getStart(){
        return start;
    }

    /**
     * Return the end point of the path.
     * @return  Return the end point of the path.
     */
    public Coordinate getEnd(){
        return end;
    }
     /**
     * Override the toString method
     * @return  the string correspinding to this class
     */
    @Override
    public String toString(){
        return start.toString() + "___" + end.toString();
    }

    /**
     * Override the equals method to check if two journeys are similar
     * @return  true if they are similar. False otherwise
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof JourneyPath){
            JourneyPath j = (JourneyPath) o;
            return j.end.isSame(end) && j.start.isSame(start);
        }
        return false;
    }

    /**
     * Override the hash code.
     */
    @Override 
    public int hashCode(){
        return toString().hashCode();
    }
}