package org.cytoscape.analysis.internal.task;

import org.cytoscape.analysis.EdgeOrganizer;
import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class GenerateNetworkFilesTaskFactory extends AbstractTaskFactory {

	private final CyNetworkManager networkManager;
	private final NetworkRandomizer randomizer;
	private final EdgeOrganizer organizer;

	public GenerateNetworkFilesTaskFactory(final CyNetworkManager networkManager, final NetworkRandomizer randomizer,
			final EdgeOrganizer organizer) {
		this.networkManager = networkManager;
		this.randomizer = randomizer;
		this.organizer = organizer;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new GenerateNetworkFilesTask(networkManager, randomizer, organizer));
	}

}
