package com.example.MAPF.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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

@Service
public class MapEnv {
    //map class that contains the running grid simulating warehouse environment

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    AtomicInteger[][] grid = new AtomicInteger[1][1];
    int[][] emptyGrid = new int[1][1];
    Map<Integer, Agent> activeAgents = new HashMap<>(); //list of activeAgents, prevent rogue agents
    Map<Integer,Timestamp> history = new HashMap<>(); //for logs and debugging
    List<Thread> activeThreads =  new ArrayList<>();

    public MapEnv(){
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new AtomicInteger(0);  // Initialize with 0 (no agent)
            }
        }
    }

    public Map<Integer, Agent> getActiveAgents() {
        return activeAgents;
    }

    public void updateMapSize(int width, int height) {
        this.grid = new AtomicInteger[height][width];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new AtomicInteger(0);  // Initialize with 0 (no agent)
            }
        }
        this.emptyGrid = new int[width][height];
        postUpdate();
    }

    public MapEnv(AtomicInteger[][] grid){
        this.grid=grid;
        this.emptyGrid = new int[grid.length][grid[0].length];
    }

    private void killAgents() {
        for(Thread thread: activeThreads) {
            thread.stop();
        }
    }

    public void addActiveThread(Thread thread) {
        this.activeThreads.add(thread);
    }

    public void resetMap() {
        killAgents();
        AtomicInteger[][] newGrid = new AtomicInteger[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                newGrid[i][j] = new AtomicInteger(0);  // Initialize with 0 (no agent)
            }
        }
        this.grid = newGrid;
        this.emptyGrid = new int[grid.length][grid[0].length];
        this.activeAgents = new HashMap<>();
        this.history = new HashMap<>();
        postUpdate();
    }
    public int[][] getGrid () {
        int[][] intGrid = new int[this.grid.length][this.grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                intGrid[i][j] = grid[i][j].get();
            }
        }
        return intGrid;
    }

    public int[][] getEmptyGrid () {
        return this.emptyGrid;
    }

    public void addObstacle(List<int[]> obstacles) {
        lock.writeLock().lock();
        obstacles.forEach(obstacle -> {
            grid[obstacle[0]][obstacle[1]] = new AtomicInteger(-1);
            emptyGrid[obstacle[0]][obstacle[1]] = -1;
        });
        postUpdate();
        lock.writeLock().unlock();
    }
    public void addObstacle(int[] obstacle) {
        lock.writeLock().lock();
        grid[obstacle[0]][obstacle[1]] = new AtomicInteger(-1);
        emptyGrid[obstacle[0]][obstacle[1]] = -1;
        postUpdate();
        lock.writeLock().unlock();
    }
    public void removeObstacle(List<int[]> obstacles) {
        lock.writeLock().lock();
        obstacles.forEach(obstacle -> {
            grid[obstacle[0]][obstacle[1]] = new AtomicInteger(0);
            emptyGrid[obstacle[0]][obstacle[1]] = 0;
        });
        postUpdate();
        lock.writeLock().unlock();
    }
    public void removeObstacle(int[] obstacle) {
        lock.writeLock().lock();
        grid[obstacle[0]][obstacle[1]] = new AtomicInteger(0);
        emptyGrid[obstacle[0]][obstacle[1]] = 0;
        postUpdate();
        lock.writeLock().unlock();
    }
    public void addAgent(int agentID, Agent agent) throws DuplicateAgentError, SpaceOccupiedError {
        lock.writeLock().lock();
        if (activeAgents.containsKey(agentID)) {
            throw new DuplicateAgentError("Agent with id: "+agentID + " already exist");
        }
        int[] pos = agent.getPos();
        if (grid[pos[0]][pos[1]].get()!=0) {
            throw new SpaceOccupiedError("The space is already occupied with another agent, likely Collision occured");
        }
        try {
            activeAgents.put(agentID, agent);
            grid[pos[0]][pos[1]] = new AtomicInteger(agentID);
            postUpdate();
        } catch (Exception e){System.err.println(e);}
        finally {lock.writeLock().unlock();}
    }
    public void removeAgent(int agentID) throws UnknownAgentError {
        lock.writeLock().lock();
        if(!activeAgents.containsKey(agentID)) {
            throw new UnknownAgentError("Unknow Agent");
        }
        try {
            int[] pos = activeAgents.get(agentID).getPos();
            activeAgents.remove(agentID);
            grid[pos[0]][pos[1]] = new AtomicInteger(0);
            postUpdate();
        } catch (Exception e){System.err.println(e);}
        finally {lock.writeLock().unlock();}
    }



    public void updateAgent(int agentID, Timestamp timestamp, int[] newPos) throws UnknownAgentError, SpaceOccupiedError, InterruptedException {
        lock.writeLock().lockInterruptibly();
        if (!activeAgents.containsKey(agentID)) {
            throw new UnknownAgentError("Agent with id: "+agentID + " not found!");
        }
        try {
            if (grid[newPos[0]][newPos[1]].get()!=0 && grid[newPos[0]][newPos[1]].get()!=agentID) {
                throw new SpaceOccupiedError("The space is already occupied with another agent, likely Collision occured");
            }
            int[] oldPos = activeAgents.get(agentID).getPos();
            grid[oldPos[0]][oldPos[1]] = new AtomicInteger(0);
            grid[newPos[0]][newPos[1]] = new AtomicInteger(agentID);
            activeAgents.get(agentID).setPos(newPos);
            history.put(agentID,timestamp);
            postUpdate();
//            System.out.println("-".repeat(grid.length*3));
//            System.out.println(toString());
//            System.out.println("-".repeat(grid.length*3));
        }
        finally{lock.writeLock().unlock();}
    }

    public void postUpdate() {messagingTemplate.convertAndSend("/topic/mapState", getGrid());}

    @Override
    public String toString() {
        //Prints Grid
        return Arrays.stream(grid).map(row -> Arrays.toString(row)).reduce((row1,row2)->row1+"\n"+row2).orElse("");
    }
}


