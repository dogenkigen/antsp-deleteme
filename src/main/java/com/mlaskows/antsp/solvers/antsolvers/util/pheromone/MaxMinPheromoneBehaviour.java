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

package com.mlaskows.antsp.solvers.antsolvers.util.pheromone;

import com.mlaskows.antsp.config.MaxMinConfig;
import com.mlaskows.antsp.datamodel.data.StaticData;
import com.mlaskows.antsp.solvers.antsolvers.util.pheromone.deposit.MaxMinDepositBehaviour;
import com.mlaskows.antsp.solvers.antsolvers.util.pheromone.init.MaxMinInitializeBehaviour;

public class MaxMinPheromoneBehaviour extends GenericPheromoneBehaviour {

    public MaxMinPheromoneBehaviour(StaticData data,
                                    MaxMinConfig config) {
        super(data, config, new MaxMinDepositBehaviour(data, config),
                new MaxMinInitializeBehaviour());
    }

}
