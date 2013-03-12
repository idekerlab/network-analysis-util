package org.cytoscape.analysis.internal.task;

import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;

public class RandomizeNetworkTask extends AbstractNetworkTask {

	private final CyNetworkManager networkManager;
	private final NetworkRandomizer randomizer;

	@ProvidesTitle
	public String getTitle() {
		return "Randomize Connections";
	}

	RandomizeNetworkTask(final CyNetwork network, final CyNetworkManager networkManager,
			final NetworkRandomizer randomizer) {
		super(network);
		this.randomizer = randomizer;
		this.networkManager = networkManager;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		final CyNetwork randomizedNetwork = randomizer.randomize(network);
		networkManager.addNetwork(randomizedNetwork);
	}
}
