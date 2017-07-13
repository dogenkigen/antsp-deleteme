package com.mlaskows.tsplib;

import com.mlaskows.config.AcoConfig;
import com.mlaskows.config.AcoConfigFactory;
import com.mlaskows.datamodel.Solution;
import com.mlaskows.matrices.StaticMatricesBuilder;
import com.mlaskows.matrices.StaticMatricesHolder;
import com.mlaskows.solvers.AntSystemSolver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by mlaskows on 24/06/2017.
 */
public class AntSystemSolverTest implements SolverTest {


    @Test
    public void testAustraliaSolution() throws IOException {
        final Item item = getItem("australia.tsp");
        StaticMatricesHolder matrices = new StaticMatricesBuilder(item)
                .withHeuristicInformationMatrix()
                .withNearestNeighbors(5)
                .build();
        final AcoConfig config = AcoConfigFactory.createDefaultAntSystemConfig(matrices
                .getDistanceMatrix().length);
        final AntSystemSolver solver = new AntSystemSolver(config, matrices);
        final Solution solution = solver.getSolution();

        //FIXME this fails randomly since algorithm is based on random values.
        final List<Integer> expectedTour = List.of(0, 2, 1, 4, 5, 3);
        Assert.assertEquals(solution.getTour(), expectedTour);
        Assert.assertEquals(solution.getTourLength(), 6095);
    }

    @Test
    public void testAli535Solution() throws IOException {
        final Item item = getItem("ali535.tsp");
        final AcoConfig config =
                AcoConfigFactory.createDefaultAntSystemConfig(item.getDimension());
        final StaticMatricesHolder matrices = new StaticMatricesBuilder(item)
                .withHeuristicInformationMatrix()
                .withNearestNeighbors(config.getNearestNeighbourFactor())
                .build();
        final AntSystemSolver solver = new AntSystemSolver(config, matrices);
        final Solution solution = solver.getSolution();

        // We assume here that solution will be better than for nearest
        // neighbour algorithm.
        Assert.assertTrue(solution.getTourLength() < 224358);
    }

}
