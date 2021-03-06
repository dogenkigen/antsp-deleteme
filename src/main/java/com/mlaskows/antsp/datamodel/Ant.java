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

package com.mlaskows.antsp.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Maciej Laskowski
 */
public class Ant implements Comparable<Ant> {

    private int tourLength;
    private final List<Integer> tour;
    private final boolean visited[];

    public Ant(int problemSize, int initialPosition) {
        tour = new ArrayList<>(problemSize + 1);
        visited = new boolean[problemSize];
        visit(initialPosition, 0);
    }

    public Ant(Solution solution) {
        tourLength = solution.getTourLength();
        tour = new ArrayList<>(solution.getTour());
        visited = new boolean[solution.getTour().size()];
        for (Integer index : tour) {
            visited[index] = true;
        }
    }

    public void visit(int index, int stepLength) {
        tourLength = tourLength + stepLength;
        visited[index] = true;
        tour.add(index);
    }

    public boolean isVisited(int index) {
        return visited[index];
    }

    public boolean notVisited(int index) {
        return !visited[index];
    }

    public int getTourLength() {
        return tourLength;
    }

    public int getCurrentIndex() {
        return tour.get(tour.size() - 1);
    }

    public int getFirstIndex() {
        return tour.get(0);
    }

    public List<Integer> getTour() {
        return Collections.unmodifiableList(tour);
    }

    public int getTourSize() {
        return tour.size();
    }

    public Solution getSolution() {
        return new Solution(getTour(), tourLength);
    }

    public boolean hasBetterSolutionThen(Ant ant) {
        return this.getTourLength() < ant.getTourLength();
    }

    @Override
    public int compareTo(Ant o) {
        return this.getTourLength() - o.getTourLength();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ant ant = (Ant) o;

        if (tourLength != ant.tourLength) return false;
        if (tour != null ? !tour.equals(ant.tour) : ant.tour != null)
            return false;
        return Arrays.equals(visited, ant.visited);
    }

    @Override
    public int hashCode() {
        int result = tourLength;
        result = 31 * result + (tour != null ? tour.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(visited);
        return result;
    }
}
