package com.example.MAPF.runner;

import com.example.MAPF.service.Agent;
import com.example.MAPF.service.MapEnv;
import com.example.MAPF.service.Task;
import com.example.MAPF.service.TaskPriority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SimulationRunner implements CommandLineRunner {

    @Autowired
    private MapEnv map;


    @Override
    public void run(String... args) throws Exception {

    }
    public void start() throws Exception {
        try {
            //Generate Random Tasks for each agent
            int row = map.getGrid().length;
            int col = map.getGrid()[0].length;
            for (Agent agent: map.getActiveAgents().values()) {
                agent.addNewTask(new Task(new int[]{new Random().nextInt(row),new Random().nextInt(col)}, TaskPriority.HIGH));
                agent.addNewTask(new Task(new int[]{new Random().nextInt(row),new Random().nextInt(col)}, TaskPriority.LOW));
                agent.addNewTask(new Task(new int[]{new Random().nextInt(row),new Random().nextInt(col)}, TaskPriority.MEDIUM));
            }
            for (Agent agent: map.getActiveAgents().values()) {
                Thread agentThread = new Thread(agent);
                map.addActiveThread(agentThread);
                agentThread.start();
            }
        } catch(Exception e) {
            System.err.println(e);
        }
    }
    public void reset() {
        map.resetMap();
    }


}
