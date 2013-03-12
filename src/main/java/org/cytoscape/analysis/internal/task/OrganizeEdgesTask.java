package org.cytoscape.analysis.internal.task;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.analysis.EdgeOrganizer;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class OrganizeEdgesTask extends AbstractNetworkTask {

	@ProvidesTitle
	public String getTitle() {
		return "Tagging Edges Based on Clusters";
	}

	@Tunable(description = "Group Column")
	public ListSingleSelection<String> columns;

	private final EdgeOrganizer organizer;

	public OrganizeEdgesTask(final EdgeOrganizer organizer, final CyNetwork network) {
		super(network);
		this.organizer = organizer;

		final List<CyColumn> columnList = new ArrayList<CyColumn>(this.network.getDefaultNodeTable().getColumns());
		final List<String> columnNames = new ArrayList<String>();
		for(CyColumn col: columnList)
			columnNames.add(col.getName());
		
		columns = new ListSingleSelection<String>(columnNames);
		if (columnList.size() != 0)
			columns.setSelectedValue(null);
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		String columnName = columns.getSelectedValue();
		if(columnName == null) {
			// Use default
			columnName = "Cluster Name";
		}
		
		final CyColumn column = network.getDefaultNodeTable().getColumn(columnName);
		if(column == null)
			throw new IllegalStateException("Could not find column: " + columnName);
		
		
		System.out.println("####### Group Col name = " + columnName);

		organizer.tagEdges(network, columnName);
	}
}
