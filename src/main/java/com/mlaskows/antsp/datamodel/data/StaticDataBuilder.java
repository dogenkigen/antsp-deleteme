/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mlaskows.antsp.datamodel.data;

import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.solvers.heuristic.NearestNeighbourSolver;
import com.mlaskows.tsplib.datamodel.types.EdgeWeightType;
import com.mlaskows.tsplib.util.EdgeWeightCalculationMethodFactory;
import com.mlaskows.antsp.datamodel.Step;
import com.mlaskows.tsplib.datamodel.tsp.Tsp;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * Created by maciej_laskowski on 05.07.2017.
 */
public class StaticDataBuilder {
    private int[][] distanceMatrix;
    private int[][] nearestNeighbors;
    private int nnFactor;
    private boolean shouldCalculateHeuristicSolution;
    private double[][] heuristicInformationMatrix;
    private final int problemSize;
    private final Tsp tsp;
    private final BiFunction<Tsp.Node, Tsp.Node, Integer> distanceCalculationMethod;
    private Solution heuristicSolution;

    public StaticDataBuilder(Tsp tsp) {
        this.problemSize = tsp.getDimension();
        this.tsp = tsp;
        final EdgeWeightType edgeWeightType = tsp.getEdgeWeightType();
        if (EdgeWeightType.EXPLICIT.equals(edgeWeightType)) {
            // In this case distances will not be calculated but copied from
            // TSP edge weight data instead.
            this.distanceCalculationMethod = null;
        } else {
            this.distanceCalculationMethod = EdgeWeightCalculationMethodFactory
                    .getEdgeWeightCalculationMethod(edgeWeightType);
        }
    }

    public StaticDataBuilder withNearestNeighbors(int nnFactor) {
        this.nnFactor = nnFactor;
        nearestNeighbors = new int[problemSize][nnFactor];
        return this;
    }

    /**
     * The heuristic information nij is typically inversely proportional to
     * the distance between cities i and j, a straightforward choice being
     * nij = 1/dij
     */
    public StaticDataBuilder withHeuristicInformationMatrix() {
        heuristicInformationMatrix = new double[problemSize][problemSize];
        return this;
    }

    public StaticDataBuilder withHeuristicSolution() {
        shouldCalculateHeuristicSolution = true;
        return this;
    }

    public StaticData build() {
        calculateData();
        return new StaticData(distanceMatrix, nearestNeighbors,
                heuristicInformationMatrix, heuristicSolution);
    }

    private void calculateData() {
        final List<Tsp.Node> nodes = tsp.getNodes().orElse(null);
        final boolean edgeWeightDataIsPresent = tsp.getEdgeWeightData()
                .isPresent();
        if (edgeWeightDataIsPresent) {
            this.distanceMatrix = getWithInfiniteIdentityDistances(tsp.getEdgeWeightData().get());
        } else {
            this.distanceMatrix = new int[this.problemSize][this.problemSize];
        }
        for (int i = 0; i < problemSize; i++) {
            for (int j = i; j < problemSize; j++) {
                if (!edgeWeightDataIsPresent) {
                    fill(distanceMatrix, i, j, calculateDistance(nodes.get(i), nodes.get(j)));
                }
                if (heuristicInformationMatrix != null) {
                    fill(heuristicInformationMatrix, i, j, (1.0
                            / ((double) distanceMatrix[i][j] + 0.1)));
                }
            }

        }
        if (nearestNeighbors != null) {
            calculateNearestNeighbours();
        }
        if (shouldCalculateHeuristicSolution) {
            heuristicSolution = new NearestNeighbourSolver(distanceMatrix).getSolution();
        }
    }

    private void calculateNearestNeighbours() {
        // Parallel NN calculation will be slower for small instances
        // which are calculated fast anyway, so it will make no difference.
        // For big instances this will make huge bust.
        IntStream.iterate(0, i -> i < problemSize, i -> i + 1)
                .parallel()
                .forEach((i) -> nearestNeighbors[i] =
                        getNearestNeighbourRow(distanceMatrix[i]));
    }

    private int[] getNearestNeighbourRow(int[] distances) {
        return IntStream.range(0, problemSize)
                .mapToObj(index -> new Step(index, distances[index]))
                .sorted()
                .limit(nnFactor)
                .mapToInt(step -> step.getTo())
                .toArray();
    }

    private int[][] getWithInfiniteIdentityDistances(int[][] distances) {
        for (int i = 0; i < distances.length; i++) {
            distances[i][i] = Integer.MAX_VALUE;
        }
        return distances;
    }

    private int calculateDistance(Tsp.Node nodeI, Tsp.Node nodeJ) {
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

}
