package org.cytoscape.analysis.internal.task;

import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class RandomizedNetworkTaskFactory extends AbstractTaskFactory {

	final private CyNetworkManager networkManager;
	final private NetworkRandomizer randomizer;
	
	public RandomizedNetworkTaskFactory(final CyNetworkManager networkManager, final NetworkRandomizer randomizer) {
		this.randomizer = randomizer;
		this.networkManager = networkManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new RandomizeNetworkTask(networkManager, randomizer));
	}

}
