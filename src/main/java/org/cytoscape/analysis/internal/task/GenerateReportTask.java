package org.cytoscape.analysis.internal.task;

import org.cytoscape.analysis.internal.ReportGenerator;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class GenerateReportTask extends AbstractNetworkTask {

	@Tunable(description="Threshold")
	private double threshold = 0.3d;

	private ReportGenerator reportGenerator = new ReportGenerator();

	public GenerateReportTask(final CyNetwork network) {
		super(network);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		reportGenerator.analyze(network, threshold);
	}
}
