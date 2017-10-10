package com.mlaskows.antsp.solvers;

import com.mlaskows.BaseWithTspTest;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.datamodel.matrices.StaticMatrices;
import com.mlaskows.antsp.datamodel.matrices.StaticMatricesBuilder;
import com.mlaskows.antsp.solvers.heuristic.NearestNeighbourSolver;
import com.mlaskows.tsplib.datamodel.item.Tsp;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by mlaskows on 18/06/2017.
 */
public class NearestNeighbourSolverTest implements BaseWithTspTest {


    @Test
    public void testAustraliaSolution() throws IOException {
        final Solution solution = getSolution("tsplib/australia.tsp");

        final List<Integer> expectedTour = List.of(0, 2, 1, 4, 5, 3, 0);

        assertEquals(solution.getTour(), expectedTour);
        assertEquals(10320, solution.getTourLength());
    }

    @Test
    public void testAli535Solution() throws IOException {
        final Solution solution = getSolution("tsplib/ali535.tsp");

        assertEquals(242009, solution.getTourLength());
    }

    @Test
    public void testAtt532() throws IOException {
        final Solution solution = getSolution("tsplib/att532.tsp");

        assertEquals(35516, solution.getTourLength());
    }

    @Test
    public void testBerlin52() throws IOException {
        final Solution solution = getSolution("tsplib/berlin52.tsp");

        assertEquals(8980, solution.getTourLength());
    }

    private Solution getSolution(String fileName) throws IOException {
        final Tsp tsp = getTsp(fileName);
        StaticMatrices matricesHolder = new StaticMatricesBuilder(tsp).build();

        final NearestNeighbourSolver solver = new NearestNeighbourSolver
                (matricesHolder);
        return solver.getSolution();
    }

}
