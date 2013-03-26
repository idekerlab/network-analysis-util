package org.cytoscape.analysis.internal.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.analysis.EdgeOrganizer;
import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class GenerateNetworkFilesTask extends AbstractTask {

	@Tunable(description="Number of Iteration: ")
	public int numIteration = 10;
	
	@Tunable(description = "Parent Network: ")
	public ListSingleSelection<String> parentNetwork;
	
	@Tunable(description = "Subset: ")
	public ListSingleSelection<String> subset;
	
	@Tunable(description = "Group Column: ")
	public String columnName;
	
	
	private final CyNetworkManager networkManager;
	private final NetworkRandomizer randomizer;
	private final EdgeOrganizer organizer;
	
	private final Map<String, CyNetwork> title2networkMap;
	
	public GenerateNetworkFilesTask(final CyNetworkManager networkManager, final NetworkRandomizer randomizer, final EdgeOrganizer organizer) {
		this.networkManager = networkManager;
		this.randomizer = randomizer;
		this.organizer = organizer;
		
		this.title2networkMap = new HashMap<String, CyNetwork>();
		
		final List<CyNetwork> networkList = new ArrayList<CyNetwork>(this.networkManager.getNetworkSet());
		final List<String> networkNames = new ArrayList<String>();
		for(final CyNetwork network: networkList) {
			final String networkTitle = network.getRow(network).get(CyNetwork.NAME, String.class);
			networkNames.add(networkTitle);
			title2networkMap.put(networkTitle, network);
		}
		parentNetwork = new ListSingleSelection<String>(networkNames);
		subset = new ListSingleSelection<String>(networkNames);
		
		if (networkList.size() != 0)
			parentNetwork.setSelectedValue(networkNames.get(0));
		
		if (networkList.size() != 0)
			subset.setSelectedValue(networkNames.get(0));
		

	}
	
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		final CyNetwork parent = title2networkMap.get(parentNetwork.getSelectedValue());
		final CyNetwork child = title2networkMap.get(subset.getSelectedValue());
		
		for(int i=0; i<numIteration; i++) {
			this.insertTasksAfterCurrentTask(new RandomizeNetworkTask(parent, networkManager, randomizer));
		}
	}

}
