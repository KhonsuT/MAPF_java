package com.example.MAPF;
import java.sql.Timestamp;
import java.util.*;

public class Agent implements Runnable{
    MapEnv map;
    int agentID;
    List<int[]> path = new ArrayList<>();
    List<int[]> history = new ArrayList<>();
    int agentSpeed = 1;
    int[] pos = new int[]{0,0};

    public Agent( int[] pos, MapEnv map, int agentID) {
        this.pos = pos;
        this.map = map;
        this.agentID = agentID;
    }
    public Agent( int[] pos, MapEnv map, int agentID, int agentSpeed) {
        this.pos = pos;
        this.map = map;
        this.agentID = agentID;
        this.agentSpeed = agentSpeed;
    }

    //getters
    public int[] getPos() {return pos;}
    public List<int[]> getPath() {return path;}
    public List<int[]> getHistory() {return history;}
    public int getAgentID() {return agentID;}

    //setters
    public void setPath(List<int[]> path) {this.path=path;}
    public void setPos(int[] newPos) {this.pos=newPos;}

    //Send update at each motion
    private void sendUpdate(Timestamp currTimestamp, int[] newPos) throws Exception{
        this.map.updateAgent(agentID,currTimestamp, newPos);
    }

    @Override
    public void run() {
        // extends thread class to run each agent independent of main thread
        // run method will execute the path based on orientation and current position

        //todo agents should perform movement checks to ensure it is safe to do so.
        // in realworld it would be lidar.
        if (path.size()!=0) {
            System.out.println("Agent "+agentID+" starts moving");
            try {
                for (int[] p: path) {
                    sendUpdate(new Timestamp(System.currentTimeMillis()), p);
                    history.add(this.pos);
                    this.pos = p;
                    Thread.sleep(1000/agentSpeed); //simulating differen robot speed
                }
            }catch(Exception e) {
                System.err.println(e);
            }finally {
                this.path = new ArrayList<>();
            }
        }else System.err.println("Path Empty, cancel movement request!");
    }
}