package com.example.MAPF.controller;

import com.example.MAPF.runner.SimulationRunner;
import com.example.MAPF.service.Agent;
import com.example.MAPF.service.MapEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api")
public class MapController {
    @Autowired
    private MapEnv mapEnv;
    @Autowired
    private SimulationRunner simulationRunner;

    @GetMapping("/getMap")
    public int[][] getMap() {
        return mapEnv.getGrid();
    }

    @GetMapping("/reset")
    public void reRunSim() {
        try {
            simulationRunner.reset();
        }catch (Exception e) {
            System.err.println(e);
        }
    }

    @GetMapping("/start")
    public void startSim() {
        try {
            simulationRunner.start();
        }catch (Exception e) {
            System.err.println(e);
        }
    }

    //add remove obstacle
    //add remove agent

    @PutMapping("/addAgent")
    public ResponseEntity<String> handleAddAgentRequest(@RequestBody Map<String, int[]> newAgent) {
        try {
            int x = newAgent.get("pos")[0];
            int y = newAgent.get("pos")[1];
            int agentID = newAgent.get("agentID")[0];
            if(x<mapEnv.getGrid().length&&y< mapEnv.getGrid()[0].length) {
                mapEnv.addAgent(agentID, new Agent(new int[]{x,y},mapEnv,agentID));
                return ResponseEntity.ok("Success");
            }
            else {
                throw new InvalidParameterException("Incorrect Index");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return ResponseEntity.status(404).body("Unable to add agent");
    }
    @PutMapping("/removeAgent")
    public ResponseEntity<String> handleRemoveAgentRequest(@RequestBody Map<String, Integer> removeAgent) {
        try {
            mapEnv.removeAgent(removeAgent.get("agentID"));
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            System.out.println(e);
        }
        return ResponseEntity.status(404).body("Unable to remove agent");
    }

    @PutMapping("/updateMapSize")
    public ResponseEntity<String> handleUpdateMapSizeRequest(@RequestBody Map<String, Integer> mapSize) {
        int width = mapSize.get("width");
        int height = mapSize.get("height");
        mapEnv.updateMapSize(width, height);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Map Updated");
        return ResponseEntity.ok(response.toString());
    }

    @MessageMapping("/updateMap")
    @SendTo("/topic/mapState")
    public int[][] updateMap() {
        return mapEnv.getGrid();
    }

    @PutMapping("/addObstacle")
    public ResponseEntity<String> handleAddObstacleRequest(@RequestBody Map<String, int[]> payload) {
        mapEnv.addObstacle(payload.get("pos"));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Obstacle Added");
        return ResponseEntity.ok(response.toString());
    }

    @PutMapping("/removeObstacle")
    public ResponseEntity<String> handleRemoveObstacleRequest(@RequestBody Map<String, int[]> payload) {
        mapEnv.removeObstacle(payload.get("pos"));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Obstacle Removed");
        return ResponseEntity.ok(response.toString());
    }
}
