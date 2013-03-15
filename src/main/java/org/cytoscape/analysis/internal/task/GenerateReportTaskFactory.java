package org.cytoscape.analysis.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class GenerateReportTaskFactory extends AbstractNetworkTaskFactory {

	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		return new TaskIterator(new GenerateReportTask(network));
	}

}
