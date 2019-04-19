package services;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

/**
 * Class for computing some statistic on array of double.
 */
public class Stat {
    /**
     * 
     * @param arr an array
     * @param x   the number we look for in array
     * @param n   a valid index of the array
     * @return the number of occurence of x in arr[0:n]
     */
    public static int count(double arr[], int x, int n) {
        Arrays.sort(arr);

        // index of first occurrence of x in arr[0..n-1]
        int i;

        // index of last occurrence of x in arr[0..n-1]
        int j;

        /* get the index of first occurrence of x */
        i = first(arr, 0, n - 1, x);

        /* If x doesn't exist in arr[] then return -1 */
        if (i == -1)
            return i;

        /*
         * Else get the index of last occurrence of x. Note that we are only looking in
         * the subarray after first occurrence
         */
        j = last(arr, i, n - 1, x, n);

        /* return count */
        return j - i + 1;
    }

    /**
     * 
     * @param arr  a SORTED array
     * @param low  a valid index in array
     * @param high a valid index in array
     * @param x    number we look for
     * @param n    a valid index of the array
     * @return returns the index of FIRST occurrence of x in arr[low,hight], or -1
     *         if low > high
     */
    static public int first(double arr[], int low, int high, int x) {
        if (high >= low) {
            /* low + (high - low)/2; */
            int mid = (low + high) / 2;
            if ((mid == 0 || x > arr[mid - 1]) && arr[mid] == x)
                return mid;
            else if (x > arr[mid])
                return first(arr, (mid + 1), high, x);
            else
                return first(arr, low, (mid - 1), x);
        }
        return -1;
    }

    /**
     * 
     * @param arr  a SORTED array
     * @param low  a valid index of array
     * @param high a valid index of array
     * @param x    a number
     * @param n
     * @return
     */
    /*
     * if x is present in arr[] then returns the index of LAST occurrence of x in
     * arr[0..n-1], otherwise returns -1
     */

    public static int last(double arr[], int low, int high, int x, int n) {
        if (high >= low) {
            /* low + (high - low)/2; */
            int mid = (low + high) / 2;
            if ((mid == n - 1 || x < arr[mid + 1]) && arr[mid] == x)
                return mid;
            else if (x < arr[mid])
                return last(arr, low, (mid - 1), x, n);
            else
                return last(arr, (mid + 1), high, x, n);
        }
        return -1;
    }

    /**
     * 
     * @param array
     * @return return the mean and standard deviation of array
     */
    public static double[] meanStd(int[] array) {
        double mean = 0;
        double var = 0;
        for (int i = 0; i < array.length; i++) {
            mean += array[i];
        }
        mean /= array.length;
        for (int i = 0; i < array.length; i++) {
            var += (array[i] - mean) * (array[i] - mean);
        }
        var /= (array.length - 1);
        double[] out = { mean, Math.sqrt(var) };
        return out;
    }

    /**
     * Make directional statistic on point inside a cluster.
     * 
     * @param c      a cluster
     * @param period a positive integer
     * @return the circular of the cluster for point lying in a circular space of
     *         cadinality period.
     */
    public static double[] clusterStat(Cluster<DoublePoint> c, int period) {
        CircularDist comparator = new CircularDist(period);
        List<DoublePoint> l = c.getPoints();
        Iterator<DoublePoint> ite = l.iterator();
        Complex imean = new Complex(0, 0);
        double var = 0;

        // Compute the circular average in complex plane.
        while (ite.hasNext()) {
            double point = ite.next().getPoint()[0];
            double angle = point * 2 * Math.PI / period;
            imean = imean.add(ComplexUtils.polar2Complex(1, angle));
        }
        imean.divide(l.size());
        double theta = imean.getArgument();
        double mean = 0;

        // map the mean back to real domain.
        if (theta < 0) {
            mean = period + period * theta / (2 * Math.PI);
        } else {
            mean = period * theta / (2 * Math.PI);
        }
        ite = l.iterator();
        double[] mean_arr = { mean };

        // compute the circular spread.
        while (ite.hasNext()) {
            DoublePoint p = ite.next();
            double dist = comparator.compute(mean_arr, p.getPoint());
            var += dist;
        }
        var /= (l.size() - 1);
        double[] out = { mean, var };
        return out;
    }

    /**
     * 
     * @param array
     * @return return the mean of array
     */
    public static double mean(double[] array) {
        double mean = 0;
        for (int i = 0; i < array.length; i++) {
            mean += array[i];
        }
        return mean / array.length;
    }
}
