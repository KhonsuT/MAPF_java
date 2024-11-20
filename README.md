Author: Derek Chen
# Decentralized Multi-Agent Path Finding Simulation
This Project is a visualizer tool that I created to help me understand the concepts of multi-agent path finding in a warehouse env

# Project includes
## Visualizer
Visualizer is hosted on localhost:8080 with a few basic feature
## Test
Astar path finding algo test cases


# Have Fun 
Explore the possibility of robot movements! 



# PathPlanningLogic

1. Each Robot is initialized with 3 sets of tasks(random positions on the grid)
2. Each Task has three levels of priority (LOW, MEDIUM, HIGH) and each bot completes them in order
3. The initial path for each task assume that there is no other agents in the map(Most optimal)
4. Subsequent repathing accounts for other agents
5. Each task has 4 tries, after exceeding 3 retries it would set current task to LOW priority and complete other tasks in the list
6. After all tasks complete it would return to home position(simulating going back to charging stations)



# Build
## Spring Boot
This project is written in spring boot, follow Maven standard build-run workflow

## Requirements
JAVA 17
