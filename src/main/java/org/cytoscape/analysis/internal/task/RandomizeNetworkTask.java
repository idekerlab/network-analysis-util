package org.cytoscape.analysis.internal.task;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class RandomizeNetworkTask extends AbstractTask {

	@ProvidesTitle
	public String getTitle() {
		return "Randomize Connections";
	}

	@Tunable(description = "Select Original Network")
	public ListSingleSelection<CyNetwork> networkList;
	

	RandomizeNetworkTask(final CyNetworkManager networkManager, final NetworkRandomizer randomizer) {
		this.randomizer = randomizer;

		final List<CyNetwork> networks = new ArrayList<CyNetwork>(networkManager.getNetworkSet());
		
		networkList = new ListSingleSelection<CyNetwork>(networks);
		if (!networks.isEmpty())
			networkList.setSelectedValue(networks.get(0));
	}

	private final NetworkRandomizer randomizer;


	@Override
	public void run(TaskMonitor tm) throws Exception {
		final CyNetwork network = networkList.getSelectedValue();
		randomizer.randomize(network);
	}
}
