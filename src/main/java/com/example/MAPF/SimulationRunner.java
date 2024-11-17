package com.example.MAPF;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

public class SimulationRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        try {
            MapEnv map = new MapEnv();
            Agent a1 = new Agent(new int[]{0,0}, map, 9,3);
            Agent a2 = new Agent(new int[]{0,2}, map, 8,1);
            map.addAgent(a1.getAgentID(), a1.getPos());
            map.addAgent(a2.getAgentID(), a2.getPos());
            a1.setPath(Arrays.asList(new int[]{1,0},new int[]{2,0},new int[]{2,1}));
            a2.setPath(Arrays.asList(new int[]{1,2},new int[]{2,2},new int[]{2,1}));
            new Thread(a1).start();
            new Thread(a2).start();
            Thread.sleep(5000);
            a1.setPath(Arrays.asList(new int[]{a1.getPos()[0]+1,a1.getPos()[1]},new int[]{a1.getPos()[0]+2,a1.getPos()[1]}));
            a2.setPath(Arrays.asList(new int[]{a2.getPos()[0],a2.getPos()[1]+1},new int[]{a2.getPos()[0],a2.getPos()[1]+2}));
            new Thread(a1).start();
            new Thread(a2).start();
            Thread.sleep(1000);
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
