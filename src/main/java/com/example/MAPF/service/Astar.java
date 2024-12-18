package com.example.MAPF.service;

import com.example.MAPF.util.CalculateDistance;

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

    public int getF() {return this.g+this.h;}
    public int getG() {return this.g;}
    public int getH() {return this.h;}
    public int getCondition() {return this.condition;}

    public void setG(int g) {this.g = g;}
    public void setH(int h) {this.h = h;}
    public void setParentPos(int[] pPos) {this.pPos = pPos;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node other = (Node) obj;
        return Arrays.equals(this.pos, other.pos);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.pos); // Consistent with equals
    }


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
                gridN[i][j] = new Node(new int[]{i,j}, new int[]{-1,-1}, Integer.MAX_VALUE, CalculateDistance.findDist(new int[]{i,j}, destNode),grid[i][j]);
            }
        }
        gridN[originNode[0]][originNode[1]].setG(0);
        gridN[originNode[0]][originNode[1]].setParentPos(null);
        // priority queue
        PriorityQueue<Node> pq = new PriorityQueue<>((node1,node2)->{
            if(node1.getF()!= node2.getF()){
                return Integer.compare(node1.getF(),node2.getF());
            }
            return Integer.compare(node1.getG(), node2.getG());
        });
        pq.offer(gridN[originNode[0]][originNode[1]]);
        // Set for visited Node
        Set<Node> visited = new HashSet<>();

        //Search
        Node curNode = null;
        while (!pq.isEmpty()) {
            curNode = pq.poll();

            if (Arrays.equals(curNode.getPos(),destNode)) break;

            if (visited.contains(curNode)) continue;
            visited.add(curNode);

            int curF = curNode.getF();
            int curG = curNode.getG();
            int curH = curNode.getH();

            for (Node node: findNeighbor(curNode, gridN)) {
                if (!visited.contains(node)){
                    int tG = curNode.getG()+1;
                    if (tG< node.getG()){
                        node.setParentPos(curNode.getPos());
                        node.setG(curNode.getG()+1);
                        pq.offer(node);
                    }
                }

            }
        }


        List<int[]> path = new ArrayList<>();
        if(!Arrays.equals(curNode.getPos(),destNode)) return path;
        while (curNode!=null) {
            path.add(curNode.getPos());
            curNode = (curNode.getParentPos()==null)?null:gridN[curNode.getParentPos()[0]][curNode.getParentPos()[1]];
        }
        Collections.reverse(path);
        return path;
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
        return (row<0 || col < 0 || row >= maxRow || col >= maxCol);
    }
}
