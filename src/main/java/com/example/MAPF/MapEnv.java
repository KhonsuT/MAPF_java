package com.example.MAPF;

import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class UnknownAgentError extends Exception {
    public UnknownAgentError(String errorMessage) {
        super(errorMessage);
    }
}
class DuplicateAgentError extends Exception {
    public DuplicateAgentError(String errorMessage) {
        super(errorMessage);
    }
}
class SpaceOccupiedError extends Exception {
    public SpaceOccupiedError(String errorMessage) {
        super(errorMessage);
    }
}

public class MapEnv {
    //map class that contains the running grid simulating warehouse environment

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    AtomicInteger[][] grid = new AtomicInteger[10][10];
    Map<Integer, int[]> activeAgents = new HashMap<>(); //list of activeAgents, prevent rogue agents
    Map<Integer,Timestamp> history = new HashMap<>(); //for logs and debugging
    public MapEnv(){
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new AtomicInteger(0);  // Initialize with 0 (no agent)
            }
        }
    }

    public MapEnv(AtomicInteger[][] grid){this.grid=grid;}
    public AtomicInteger[][] getGrid () {
        return this.grid;
    }

    public void addObstacle(List<int[]> obstacles) {
        lock.writeLock().lock();
        obstacles.forEach(obstacle -> grid[obstacle[0]][obstacle[1]] = new AtomicInteger(-1));
        lock.writeLock().unlock();
    }
    public void addObstacle(int[] obstacle) {
        lock.writeLock().lock();
        grid[obstacle[0]][obstacle[1]] = new AtomicInteger(-1);
        lock.writeLock().unlock();
    }
    public void removeObstacle(List<int[]> obstacles) {
        lock.writeLock().lock();
        obstacles.forEach(obstacle -> grid[obstacle[0]][obstacle[1]] = new AtomicInteger(0));
        lock.writeLock().unlock();
    }
    public void removeObstacle(int[] obstacle) {
        lock.writeLock().lock();
        grid[obstacle[0]][obstacle[1]] = new AtomicInteger(0);
        lock.writeLock().unlock();
    }
    public void addAgent(int agentID, int[] pos) throws DuplicateAgentError, SpaceOccupiedError {
        lock.writeLock().lock();
        if (activeAgents.containsKey(agentID)) {
            throw new DuplicateAgentError("Agent with id: "+agentID + " already exist");
        }
        if (grid[pos[0]][pos[1]].get()!=0) {
            throw new SpaceOccupiedError("The space is already occupied with another agent, likely Collision occured");
        }
        try {
            activeAgents.put(agentID, pos);
            grid[pos[0]][pos[1]] = new AtomicInteger(agentID);
        } catch (Exception e){System.err.println(e);}
        finally {lock.writeLock().unlock();}
    }

    public void updateAgent(int agentID, Timestamp timestamp, int[] newPos) throws UnknownAgentError,SpaceOccupiedError {
        lock.writeLock().lock();
        if (!activeAgents.containsKey(agentID)) {
            throw new UnknownAgentError("Agent with id: "+agentID + " not found!");
        }
        try {
            if (grid[newPos[0]][newPos[1]].get()!=0 && grid[newPos[0]][newPos[1]].get()!=agentID) {
                throw new SpaceOccupiedError("The space is already occupied with another agent, likely Collision occured");
            }
            int[] oldPos = activeAgents.get(agentID);
            grid[oldPos[0]][oldPos[1]] = new AtomicInteger(0);
            grid[newPos[0]][newPos[1]] = new AtomicInteger(agentID);
            activeAgents.put(agentID, newPos);
            history.put(agentID,timestamp);
            System.out.println("-".repeat(grid.length*3));
            System.out.println(toString());
            System.out.println("-".repeat(grid.length*3));
        }
        finally{lock.writeLock().unlock();}
    }

    @Override
    public String toString() {
        //Prints Grid
        return Arrays.stream(grid).map(row -> Arrays.toString(row)).reduce((row1,row2)->row1+"\n"+row2).orElse("");
    }
}


