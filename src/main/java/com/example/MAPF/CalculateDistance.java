package com.example.MAPF;
import java.util.*;

public class CalculateDistance {
    public static List<Integer> findDist(int[] curNode, int[] destNode, int[] originNode) {
        List<Integer> result = new ArrayList<>();
        result.add(Math.abs(curNode[0]-originNode[0])+Math.abs(curNode[1]-originNode[1]));
        result.add(Math.abs(destNode[0]-curNode[0])+Math.abs(destNode[1]-curNode[1]));
        return result;
    }
}
