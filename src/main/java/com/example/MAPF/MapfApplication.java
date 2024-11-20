package com.example.MAPF;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class MapfApplication {

	public static void main(String[] args) {

		SpringApplication.run(MapfApplication.class, args);
	}
//	@GetMapping
//	public static String getMapState() {
//	}
// post end points to add agents
// post end points to add task to agents(i.e from curPos go to any pos checks if pos is valid non-obstacles)

// put end points to update map state(remove/add obstacles)


}
