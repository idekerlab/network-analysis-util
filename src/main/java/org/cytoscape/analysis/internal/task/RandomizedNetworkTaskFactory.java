package org.cytoscape.analysis.internal.task;

import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class RandomizedNetworkTaskFactory extends AbstractNetworkTaskFactory implements TaskFactory {

	final private CyNetworkManager networkManager;
	final private NetworkRandomizer randomizer;
	
	public RandomizedNetworkTaskFactory(final CyNetworkManager networkManager, final NetworkRandomizer randomizer) {
		this.randomizer = randomizer;
		this.networkManager = networkManager;
	}

	@Override
	public TaskIterator createTaskIterator(final CyNetwork network) {
		return new TaskIterator(new RandomizeNetworkTask(network, networkManager, randomizer));
	}

	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

}
