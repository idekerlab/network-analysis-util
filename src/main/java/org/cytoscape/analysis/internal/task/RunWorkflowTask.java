package org.cytoscape.analysis.internal.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.analysis.EdgeOrganizer;
import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.analysis.internal.EdgeOrganizerImpl;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.task.create.NewNetworkSelectedNodesOnlyTaskFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class RunWorkflowTask extends AbstractTask {

	@ProvidesTitle
	public String getTitle() {
		return "Workflow Runner";
	}

	@Tunable(description = "Number of Iteration:")
	public Integer numIteration = 10;

	@Tunable(description = "Cluster Name Column:")
	public String columnName = "Cluster Name";

	@Tunable(description = "Parent Network:")
	public ListSingleSelection<String> parentNetwork;

	@Tunable(description = "Child Network:")
	public ListSingleSelection<String> childNetwork;

	private final CyNetworkManager networkManager;
	private final NetworkRandomizer randomizer;
	private final NewNetworkSelectedNodesOnlyTaskFactory fromSelection;
	private final EdgeOrganizer organizer;
	private final CyApplicationManager applicationManager;

	private final String userHomeDir = System.getProperty("user.home");

	private final Map<String, CyNetwork> networkNameMap = new HashMap<String, CyNetwork>();

	public RunWorkflowTask(final CyApplicationManager applicationManager, final EdgeOrganizer organizer,
			final NewNetworkSelectedNodesOnlyTaskFactory fromSelection, final NetworkRandomizer randomizer,
			final CyNetworkManager networkManager) {
		this.networkManager = networkManager;
		this.randomizer = randomizer;
		this.fromSelection = fromSelection;
		this.applicationManager = applicationManager;
		this.organizer = organizer;

		final Set<CyNetwork> networks = networkManager.getNetworkSet();

		final List<String> networkNames = new ArrayList<String>();
		String networkName = null;
		for (final CyNetwork network : networks) {
			networkName = network.getRow(network).get(CyNetwork.NAME, String.class);
			networkNames.add(networkName);
			networkNameMap.put(networkName, network);
		}

		parentNetwork = new ListSingleSelection<String>(networkNames);
		childNetwork = new ListSingleSelection<String>(networkNames);

		if (networkNames.size() != 0) {
			parentNetwork.setSelectedValue(networkName);
		}
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		final Date date = new Date();
		final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		final File newdir = new File(userHomeDir + "/randomization-reports-" + sdf1.format(date));
		newdir.mkdir();

		final String parentName = parentNetwork.getSelectedValue();
		final String childName = childNetwork.getSelectedValue();

		final CyNetwork parent = networkNameMap.get(parentName);
		final CyNetwork child = networkNameMap.get(childName);

		final Set<CyNode> clusterNodes = new HashSet<CyNode>(child.getNodeList());

		for (int i = 0; i < numIteration; i++) {
			final CyNetwork randomizedNetwork = randomizer.randomize(parent);
			selectClusterNodes(clusterNodes, randomizedNetwork);
			TaskIterator itr = fromSelection.createTaskIterator(randomizedNetwork);
			Task selectionTask = itr.next();
			selectionTask.run(taskMonitor);
			organizer.tagEdges(applicationManager.getCurrentNetwork(), columnName);
			writeReport(applicationManager.getCurrentNetwork(), newdir);
		}

	}

	private final void writeReport(CyNetwork network, File dir) throws IOException {
		String networkNameString = network.getSUID().toString();
		File file = new File(dir, networkNameString +".txt");
		FileWriter filewriter = new FileWriter(file);

		for(CyEdge edge: network.getEdgeList()) {
			final CyNode source = edge.getSource();
			final CyNode target = edge.getTarget();
			
			String sourceName = network.getRow(source).get(CyNetwork.NAME, String.class);
			String targetName = network.getRow(target).get(CyNetwork.NAME, String.class);
			String connectionType = network.getRow(edge).get(EdgeOrganizerImpl.CONNECTION_TYPE_STRING, String.class);
			String edgeType = network.getRow(edge).get(EdgeOrganizerImpl.EDGE_TYPE_STRING, String.class);
			
			filewriter.write(sourceName + "\t" + targetName + "\t" + edgeType + "\t" + connectionType + "\n");
		}

		filewriter.close();
	}

	private final void selectClusterNodes(final Set<CyNode> clusterNodes, final CyNetwork randomizedNetwork) {
		for (final CyNode node : randomizedNetwork.getNodeList()) {
			if (clusterNodes.contains(node))
				randomizedNetwork.getRow(node).set(CyNetwork.SELECTED, true);
		}
	}

}