package navin.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Guilherme on 11/09/2016.
 */
public class Util {
    public static double getReverseDegree(double degree){
        return degree+180%360;
    }
    public static <T> List<T> asList(T[] array) {
        if(array==null) return null;
        return Arrays.asList(array);
    }
}
