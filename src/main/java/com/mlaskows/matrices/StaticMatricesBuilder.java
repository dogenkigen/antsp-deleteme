package com.mlaskows.matrices;

import com.mlaskows.datamodel.Step;
import com.mlaskows.tsplib.DistanceCalculationMethodFactory;
import com.mlaskows.tsplib.Item;
import com.mlaskows.tsplib.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * Created by maciej_laskowski on 05.07.2017.
 */
public class StaticMatricesBuilder {
    private int[][] distanceMatrix;
    private int[][] nearestNeighbors;
    private int nnFactor;
    private double[][] heuristicInformationMatrix;
    private final int problemSize;
    private final Item item;
    private final BiFunction<Node, Node, Integer> distanceCalculationMethod;

    public StaticMatricesBuilder(Item item) {
        this.problemSize = item.getDimension();
        this.distanceMatrix = new int[this.problemSize][this.problemSize];
        this.item = item;
        this.distanceCalculationMethod = DistanceCalculationMethodFactory
                .getDistanceCalculationMethod(item.getEdgeWeightType());
    }

    public StaticMatricesBuilder withNearestNeighbors(int nnFactor) {
        this.nnFactor = nnFactor;
        nearestNeighbors = new int[problemSize][nnFactor];
        return this;
    }

    /**
     * The heuristic information nij is typically inversely proportional to
     * the distance between cities i and j, a straightforward choice being
     * nij = 1/dij
     */
    public StaticMatricesBuilder withHeuristicInformationMatrix() {
        heuristicInformationMatrix = new double[problemSize][problemSize];
        return this;
    }

    public StaticMatricesHolder build() {
        calculateMatrices();
        return new StaticMatricesHolder(distanceMatrix, nearestNeighbors,
                heuristicInformationMatrix);
    }

    private void calculateMatrices() {
        List<Node> nodes = item.getNodes();
        for (int i = 0; i < problemSize; i++) {
            for (int j = i; j < problemSize; j++) {
                int distance = getDistance(nodes.get(i), nodes.get(j));
                fill(distanceMatrix, i, j, distance);
                if (heuristicInformationMatrix != null) {
                    fill(heuristicInformationMatrix, i, j, (1.0 / ((double) distance + 0.1)));
                }
            }

        }
        if (nearestNeighbors != null) {
            for (int i = 0; i < problemSize; i++) {
                nearestNeighbors[i] = getNearestNeighbourRow(distanceMatrix[i]);
            }
        }
    }

    private int getDistance(Node nodeI, Node nodeJ) {
        int distance;
        if (nodeI.equals(nodeJ)) {
            distance = Integer.MAX_VALUE;
        } else {
            distance = distanceCalculationMethod.apply(nodeI, nodeJ);
        }
        return distance;
    }

    private void fill(int[][] matrix, int i, int j, int value) {
        matrix[i][j] = value;
        matrix[j][i] = value;
    }

    private void fill(double[][] matrix, int i, int j, double value) {
        matrix[i][j] = value;
        matrix[j][i] = value;
    }

    private int[] getNearestNeighbourRow(int[] distances) {
        // TODO consider performance improvement
        return IntStream.range(0, problemSize)
                .mapToObj(index -> new Step(index, distances[index]))
                .sorted()
                .limit(nnFactor)
                .mapToInt(step -> step.getTo())
                .toArray();
    }

}