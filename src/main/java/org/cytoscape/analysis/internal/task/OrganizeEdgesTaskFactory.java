package org.cytoscape.analysis.internal.task;

import org.cytoscape.analysis.EdgeOrganizer;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class OrganizeEdgesTaskFactory extends AbstractNetworkTaskFactory implements TaskFactory {

	private final CyApplicationManager manager;
	private final EdgeOrganizer organizer;

	public OrganizeEdgesTaskFactory(final CyApplicationManager manager, final EdgeOrganizer organizer) {
		this.manager = manager;
		this.organizer = organizer;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator();
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		final CyNetwork targetNetwork = manager.getCurrentNetwork();
		return new TaskIterator(new OrganizeEdgesTask(organizer, targetNetwork));
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
