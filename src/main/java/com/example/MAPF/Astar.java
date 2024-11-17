package com.example.MAPF;

import java.util.*;

class Node {
    private int[] pos;
    private int[] pPos;
    private int g;
    private int h;
    private int condition;
    public Node( int[] pos, int[] pPos, int g, int h, int condition) {
        this.pos = pos;
        this.pPos = pPos;
        this.g = g;
        this.h = h;
        this.condition = condition;
    }

    public int[] getPos() {
        return this.pos;
    }

    public int[] getParentPos() {
        return this.pPos;
    }

    public int getF() {return this.g+this.f;}
    public int getG() {return this.g;}
    public int getH() {return this.h;}
    public int getCondition() {return this.condition};

    public void setG(int g) {this.g = g;}
    public void setH(int h) {this.h = h;}


}

public class Astar {
    static List<int[]> direction = Arrays.asList(
            new int[]{0,1},
            new int[]{1,0},
            new int[]{0,-1},
            new int[]{-1,0}
    );
    public static List<int[]> AstarAlgo(int[] originNode, int[] destNode, int[][] grid) {
        // do a priority queue where the top is the smallest F. 
        // if a node is visited we will get its preV value and compare with current
        // Nodes carry parent info and cost
        // Div the grid into nodes
        // Node contain[curPos, ParentPos, G-cost, H-cost, F-cost]

        // initialize all points in the grid to be nodes
        Node[][] gridN = new Node[grid.length][grid[0].length];
        for (int i = 0; i<gridN.length; i++) {
            for (int j = 0; j<gridN[0].length;j++) {
                gridN[i][j] = new Node(new int[]{i,j}, new int[2], Integer.MAX_VALUE, CalculateDistance.findDist(new int[]{i,j}, destNode),grid[i][j]);
            }
        }
        gridN[originNode[0]][originNode[1]].setG(0);
        // priority queue
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt((Node node)->node.getF()));
        pq.offer(gridN[originNode[0]][originNode[1]]);
        while (!pq.isEmpty()) {
            Node curNode = pq.poll();
            int curF = curNode.getF();
            int curG = curNode.getG();
            int curH = curNode.getH();

            for (Node node: findNeighbor(curNode, gridN)) {
                if
            }
        }

        return new ArrayList<>();
    }

    private static List<Node> findNeighbor(Node node, Node[][] gridN) {
        List<Node> result = new ArrayList<>();
        for (int[] d: direction) {
            int newRow = node.getPos()[0]+d[0];
            int newCol = node.getPos()[1]+d[1];

            if(!isOutOfBound(gridN,newRow,newCol)&&gridN[newRow][newCol].getCondition()==0) {
                result.add(gridN[newRow][newCol]);
            }
        }
        return result;
    }
    private static boolean isOutOfBound(Node[][] gridN, int row, int col) {
        int maxRow = gridN.length;
        int maxCol = gridN[0].length;
        return (row<maxRow&&col<maxCol&&row>=0&&col>=0);
    }
}
