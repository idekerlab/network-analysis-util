package org.cytoscape.analysis.internal.task;

import org.cytoscape.analysis.EdgeOrganizer;
import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.task.create.NewNetworkSelectedNodesOnlyTaskFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class RunWorkflowTaskFactory extends AbstractTaskFactory {

	private final CyNetworkManager networkManager;
	final NetworkRandomizer randomizer;
	private final NewNetworkSelectedNodesOnlyTaskFactory fromSelection;
	private final CyApplicationManager cyApplicationManager;
	private final EdgeOrganizer edgeOrganizer;
	
	
	public RunWorkflowTaskFactory(final CyApplicationManager cyApplicationManager, final EdgeOrganizer edgeOrganizer, final NewNetworkSelectedNodesOnlyTaskFactory fromSelection, final NetworkRandomizer randomizer, final CyNetworkManager networkManager) {
		this.networkManager = networkManager;
		this.randomizer = randomizer;
		this.fromSelection = fromSelection;
		this.cyApplicationManager = cyApplicationManager;
		this.edgeOrganizer = edgeOrganizer;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new RunWorkflowTask(cyApplicationManager, edgeOrganizer, fromSelection, randomizer, networkManager));
	}

}
