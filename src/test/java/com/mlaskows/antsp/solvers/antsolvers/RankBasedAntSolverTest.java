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

package com.mlaskows.antsp.solvers.antsolvers;

import com.mlaskows.antsp.config.AcoConfigFactory;
import com.mlaskows.antsp.config.RankedBasedConfig;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.datamodel.data.StaticDataBuilder;
import com.mlaskows.antsp.datamodel.data.StaticData;
import com.mlaskows.BaseWithTspTest;
import com.mlaskows.tsplib.datamodel.item.Tsp;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class RankBasedAntSolverTest implements BaseWithTspTest {

    @Test
    public void testAli535Solution() throws IOException {
        final Tsp tsp = getTsp("tsplib/ali535.tsp");
        final RankedBasedConfig config =
                AcoConfigFactory.createDefaultRankedBasedConfig(tsp.getDimension());
        final StaticData data = new StaticDataBuilder(tsp)
                .withHeuristicInformationMatrix()
                .withHeuristicSolution()
                .withNearestNeighbors(config.getNearestNeighbourFactor())
                .build();
        final RankBasedAntSolver solver = new RankBasedAntSolver(data, config);
        final Solution solution = solver.getSolution();

        List<Integer> nonImprovementPeriods = solution.getStatistics().get()
                .getNonImprovementPeriods();
        Assert.assertEquals((int) nonImprovementPeriods.get(nonImprovementPeriods.size() - 1),
                config.getMaxStagnationCount());
        // We assume here that solution will be better than for nearest
        // neighbour algorithm.
        Assert.assertTrue(solution.getTourLength() < 256086);
    }

}
