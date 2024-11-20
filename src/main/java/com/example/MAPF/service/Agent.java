package com.example.MAPF.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;

public class Agent implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(Agent.class);
    MapEnv map;
    int agentID;
    List<int[]> path = new ArrayList<>();
    List<int[]> history = new ArrayList<>();
    int agentSpeed = 10;
    int[] pos = new int[]{0,0};
    int[] homePos = new int[]{0,0};

    public PriorityQueue<Task> getTasks() {
        return tasks;
    }

    PriorityQueue<Task> tasks = new PriorityQueue<>(Comparator.comparing(task -> task.priority));

    public Agent( int[] pos, MapEnv map, int agentID) {
        this.pos = pos;
        this.map = map;
        this.agentID = agentID;
        this.homePos = pos;
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
    public void addNewTask(Task task) {this.tasks.offer(task);}

    //Send update at each motion
    private void sendUpdate(Timestamp currTimestamp, int[] newPos) throws Exception{
        this.map.updateAgent(agentID,currTimestamp, newPos);
    }

    private boolean executePath(List<int[]> path){
        if(path.isEmpty()) {return false;}
        try {
            for (int[] p: path) {
                sendUpdate(new Timestamp(System.currentTimeMillis()), p);
                history.add(this.pos);
                this.pos = p;
                Thread.sleep((long) ((double) 1000 /(agentSpeed*0.1))); //simulating different robot speed
            }
            return true;
        }catch(Exception e) {
            log.error("e: ", e);
        }finally {
            this.path = new ArrayList<>();
        }
        return false;

    }

    @Override
    public void run() {
        // implements runnable class to run each agent independent of main thread
        // run method will execute the path based on orientation and current position
        if (tasks.isEmpty()) {
            System.err.println("No active tasks.");
            return;
        }
        try {
            while (!tasks.isEmpty()) {
                System.out.println("Agent "+agentID+" starts moving");
                Task curTask = tasks.poll();
                List<int[]> curTaskPath =  Astar.AstarAlgo(this.pos, curTask.targetLocation,this.map.getEmptyGrid());
                if(!curTaskPath.isEmpty()) {
                    curTask.state = TaskState.EXECUTING;
                    int tries = 0;
                    while(curTask.state!=TaskState.FINISHED && curTask.state!=TaskState.CANCELED){
                        if(tries>3) {
                            curTask.state = TaskState.ONHOLD;
                            curTask.priority = TaskPriority.LOW;
                            tasks.offer(curTask);
                            break;
                        }
                        if(executePath(curTaskPath)){
                            curTask.state = TaskState.FINISHED;
                        }
                        else {
                            curTaskPath = Astar.AstarAlgo(this.pos, curTask.targetLocation, this.map.getGrid());
                            System.out.println("Updating Route");
                            Thread.sleep(1000); //pausing to see if a better path is available
                            tries++;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // return to home pos
        try {
            List<int[]> curTaskPath =  Astar.AstarAlgo(this.pos, this.homePos, this.map.getEmptyGrid());
            if(!curTaskPath.isEmpty()) {
                int tries = 0;
                while(this.pos!=this.homePos){
                    if(tries>3) {
                        System.out.println("Agent: "+ agentID + " unable to return to home position");
                        break;
                    }
                    if (executePath(curTaskPath)) {
                        System.out.println("Agent: " + agentID + " finished all tasks and returned home");
                        break;
                    }
                    else {
                        curTaskPath = Astar.AstarAlgo(this.pos, this.homePos, this.map.getGrid());
                        System.out.println("Updating Route");
                        Thread.sleep(1000);
                        tries++;
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}