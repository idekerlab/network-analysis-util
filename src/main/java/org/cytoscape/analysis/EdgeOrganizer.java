package org.cytoscape.analysis;

import org.cytoscape.model.CyNetwork;

public interface EdgeOrganizer {
	
	void tagEdges(final CyNetwork network, final String groupColumnName);

}
