package com.example.MAPF;

import java.util.*;

public class Astar {
    static List<int[]> direction = Arrays.asList(
            new int[]{0,1},
            new int[]{1,0},
            new int[]{0,-1},
            new int[]{-1,0}
    );
    public static List<int[]> AstarAlgo(int[] originNode, int[] destNode, int[][] grid) {
        int[] curNode = originNode;
        while (!Arrays.equals(curNode, destNode)) {
            List<int[]> neighbors = findNeighbor(curNode, grid);
            int[] maxDist = {Integer.MAX_VALUE, Integer.MAX_VALUE};
            for (int[] neighbor: neighbors) {
                List<Integer> dist = CalculateDistance.findDist(curNode,destNode,originNode);
                if (dist.stream().map<maxDist[0]+maxDist[1]) {

                }
            }


        }

        return new ArrayList<>();
    }

    private static List<int[]> findNeighbor(int[] node, int[][] grid) {
        List<int[]> result = new ArrayList<>();
        for (int[] d: direction) {
            int newRow = node[0]+d[0];
            int newCol = node[1]+node[1];

            if(!isOutOfBound(grid,newRow,newCol)&&grid[newRow][newCol]==0) {
                result.add(new int[]{newRow, newCol});
            }
        }
        return result;
    }
    private static boolean isOutOfBound(int[][] grid, int row, int col) {
        int maxRow = grid.length;
        int maxCol = grid[0].length;
        return (row<maxRow&&col<maxCol&&row>=0&&col>=0);
    }
}
