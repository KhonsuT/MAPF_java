package com.example.MAPF;

import com.example.MAPF.service.Astar;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AstarTest {
    @Test
    void AstarTest_grid_path_1D_vertical() {
        int[] test_input_originNode = {0,0};
        int[] test_input_destNode = {3,0};
        int[][] test_input_grid = new int[4][1];

        List<int[]> test_output_path = Arrays.asList(new int[]{3,0}, new int[]{2,0}, new int[]{1,0}, new int[]{0,0});
        List<int[]> actual_path = Astar.AstarAlgo(test_input_originNode,test_input_destNode,test_input_grid);
        Collections.reverse(test_output_path);
//        actual_path.stream().map(Arrays::toString).forEach(System.out::println);
        assertTrue(Arrays.deepEquals(actual_path.toArray(), test_output_path.toArray()));

    }
    @Test
    void AstarTest_grid_path_1D_horizontal() {
        int[] test_input_originNode = {0,0};
        int[] test_input_destNode = {0,3};
        int[][] test_input_grid = new int[1][4];

        List<int[]> test_output_path = Arrays.asList(new int[]{0,3}, new int[]{0,2}, new int[]{0,1}, new int[]{0,0});
        List<int[]> actual_path = Astar.AstarAlgo(test_input_originNode,test_input_destNode,test_input_grid);
        Collections.reverse(test_output_path);
//        actual_path.stream().map(Arrays::toString).forEach(System.out::println);
        assertTrue(Arrays.deepEquals(actual_path.toArray(), test_output_path.toArray()));

    }
    @Test
    void AstarTest_grid_path_2D() {
        // {
        // {0,1,1,1},
        // {0,0,1,1},
        // {1,0,1,1},
        // {1,0,0,0}
        // }
        int[] test_input_originNode = {0,0};
        int[] test_input_destNode = {3,3};
        int[][] test_input_grid = {
                 {0,1,1,1},
                 {0,0,1,1},
                 {1,0,1,1},
                 {1,0,0,0}
                 };


        List<int[]> test_output_path = Arrays.asList(new int[]{3,3}, new int[]{3,2}, new int[]{3,1}, new int[]{2,1},new int[]{1,1},new int[]{1,0},new int[]{0,0});
        List<int[]> actual_path = Astar.AstarAlgo(test_input_originNode,test_input_destNode,test_input_grid);
        Collections.reverse(test_output_path);
//        actual_path.stream().map(Arrays::toString).forEach(System.out::println);
        assertTrue(Arrays.deepEquals(actual_path.toArray(), test_output_path.toArray()));

    }

    @Test
    void AstarTest_grid_path_2D_no_path() {
        // {
        // {0,1,1,1},
        // {0,0,1,1},
        // {1,0,1,1},
        // {1,0,1,0}
        // }
        int[] test_input_originNode = {0,0};
        int[] test_input_destNode = {3,3};
        int[][] test_input_grid = {
                {0,1,1,1},
                {0,0,1,1},
                {1,0,1,1},
                {1,0,1,0}
        };


        List<int[]> test_output_path = new ArrayList<>();
        List<int[]> actual_path = Astar.AstarAlgo(test_input_originNode,test_input_destNode,test_input_grid);
        Collections.reverse(test_output_path);
//        actual_path.stream().map(Arrays::toString).forEach(System.out::println);
        assertTrue(Arrays.deepEquals(actual_path.toArray(), test_output_path.toArray()));

    }

    @Test
    void AstarTest_grid_path_2D_reversed() {
        // {
        // {0,1,1,1},
        // {0,0,1,1},
        // {1,0,1,1},
        // {1,0,0,0}
        // }
        int[] test_input_originNode = {3,3};
        int[] test_input_destNode = {0,0};
        int[][] test_input_grid = {
                {0,1,1,1},
                {0,0,1,1},
                {1,0,1,1},
                {1,0,0,0}
        };


        List<int[]> test_output_path = Arrays.asList(new int[]{3,3}, new int[]{3,2}, new int[]{3,1}, new int[]{2,1},new int[]{1,1},new int[]{1,0},new int[]{0,0});
        List<int[]> actual_path = Astar.AstarAlgo(test_input_originNode,test_input_destNode,test_input_grid);
//        actual_path.stream().map(Arrays::toString).forEach(System.out::println);
        assertTrue(Arrays.deepEquals(actual_path.toArray(), test_output_path.toArray()));

    }

}