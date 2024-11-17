package com.example.MAPF;
import java.util.*;

public class CalculateDistance {
    public static int findDist(int[] curNode, int[] destNode) {
        return Math.abs(destNode[0]-curNode[0])+Math.abs(destNode[1]-curNode[1]);
    }
}
