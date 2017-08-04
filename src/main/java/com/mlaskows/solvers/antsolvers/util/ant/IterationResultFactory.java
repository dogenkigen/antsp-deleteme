package com.mlaskows.solvers.antsolvers.util.ant;

import com.mlaskows.config.AcoConfig;
import com.mlaskows.datamodel.Ant;
import com.mlaskows.datamodel.IterationResult;
import com.mlaskows.datamodel.matrices.StaticMatricesHolder;

import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class IterationResultFactory {

    private final StaticMatricesHolder matrices;
    private final AcoConfig config;
    private final int problemSize;
    private final SplittableRandom random = new SplittableRandom();
    private Ant bestAntSoFar;

    public IterationResultFactory(StaticMatricesHolder matrices, AcoConfig config) {
        this.matrices = matrices;
        this.config = config;
        this.problemSize = matrices.getProblemSize();
    }

    public IterationResult createIterationResult(double[][] choicesInfo) {
        final List<Ant> sortedAnts = constructAntsSolutionSorted(choicesInfo);
        final Ant iterationBestAnt = sortedAnts.get(0);
        boolean isImprovedIteration = false;
        if (bestAntSoFar == null
                || iterationBestAnt.hasBetterSolutionThen(bestAntSoFar)) {
            bestAntSoFar = iterationBestAnt;
            isImprovedIteration = true;
        }
        return new IterationResult(sortedAnts, bestAntSoFar, isImprovedIteration);
    }

    protected List<Ant> constructAntsSolutionSorted(double[][] choicesInfo) {
        AntMover antMover = new AntMover(matrices, choicesInfo);
        // Iterating should be started from 1 since every ant has already
        // visited one city during initialization.
        final List<Ant> ants = getRandomPlacedAnts(config.getAntsCount())
                .parallel()
                .peek(ant ->
                        IntStream.iterate(1, i -> i < problemSize, i -> i + 1)
                                .forEach(i -> antMover.moveAnt(ant))
                )
                .collect(toList());
        return ants.stream().sorted().collect(toList());
    }

    private Stream<Ant> getRandomPlacedAnts(int antCount) {
        return random.ints(0, antCount)
                .limit(config.getAntsCount())
                .mapToObj(position -> new Ant(problemSize, position));
    }

}
