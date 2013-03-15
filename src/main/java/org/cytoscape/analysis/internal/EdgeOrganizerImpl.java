package org.cytoscape.analysis.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.analysis.EdgeOrganizer;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

public class EdgeOrganizerImpl implements EdgeOrganizer {

	public static final String EDGE_TYPE_STRING = "Cluster Edge Type";
	public static final String CONNECTION_TYPE_STRING = "Connection Type";
	
	public static final String EDGE_TYPE_INTERNAL = "internal";
	public static final String EDGE_TYPE_BETWEEN = "between-clusters";
	
	private Set<CyNode> clusterNodes;
	private String groupColumnName;

	@Override
	public void tagEdges(final CyNetwork network, final String groupColumnName) {
		final CyColumn groupColumn = network.getDefaultNodeTable().getColumn(groupColumnName);
		this.groupColumnName = groupColumnName;
		
		if (groupColumn == null)
			throw new IllegalArgumentException("Column " + groupColumnName + " does not exist.");

		if (network.getDefaultEdgeTable().getColumn(EDGE_TYPE_STRING) == null)
			network.getDefaultEdgeTable().createColumn(EDGE_TYPE_STRING, String.class, false);
		if (network.getDefaultEdgeTable().getColumn(CONNECTION_TYPE_STRING) == null)
			network.getDefaultEdgeTable().createColumn(CONNECTION_TYPE_STRING, String.class, false);

		final Map<String, Set<CyNode>> clusters = getClusters(network, groupColumn);
		organizeEdges(network, clusters);
	}

	private final Map<String, Set<CyNode>> getClusters(final CyNetwork network, final CyColumn groupColumn) {
		final Map<String, Set<CyNode>> clusters = new HashMap<String, Set<CyNode>>();
		clusterNodes = new HashSet<CyNode>();

		final List<CyRow> rows = groupColumn.getTable().getAllRows();
		for (final CyRow row : rows) {
			final String groupValue = row.get(groupColumn.getName(), String.class);
			if (groupValue == null)
				continue;

			final Long suid = row.get(CyIdentifiable.SUID, Long.class);
			final CyNode memberNode = network.getNode(suid);

			Set<CyNode> cluster = clusters.get(groupValue);
			if (cluster == null) {
				// Need to create new set
				cluster = new HashSet<CyNode>();
			}

			cluster.add(memberNode);
			clusterNodes.add(memberNode);
			clusters.put(groupValue, cluster);
		}

		return clusters;

	}

	private final void organizeEdges(final CyNetwork network, final Map<String, Set<CyNode>> clusters) {
		for (final String clusterName : clusters.keySet()) {
			final Set<CyNode> cluster = clusters.get(clusterName);
			for (final CyNode node : cluster) {
				final List<CyEdge> connectingEdges = network.getAdjacentEdgeList(node, Type.ANY);
				analyzeEdge(clusterName, connectingEdges, cluster, clusterNodes, network);
			}
		}

	}

	private final void analyzeEdge(final String clusterName, final List<CyEdge> edges, final Set<CyNode> cluster,
			final Set<CyNode> clusterNodes, final CyNetwork network) {
		for (final CyEdge edge : edges) {
			final CyNode source = edge.getSource();
			final CyNode target = edge.getTarget();

			// Check status of edge
			if (cluster.contains(source) && cluster.contains(target)) {
				// Internal Edge
				network.getRow(edge).set(EDGE_TYPE_STRING, clusterName);
				network.getRow(edge).set(CONNECTION_TYPE_STRING, EDGE_TYPE_INTERNAL);
			} else if (clusterNodes.contains(source) && clusterNodes.contains(target)) {
				// Inter-cluster nodes
				final String sourceCluester = network.getRow(source).get(groupColumnName, String.class);
				final String targetCluester = network.getRow(target).get(groupColumnName, String.class);
				
				if(sourceCluester != null && targetCluester != null) {
					network.getRow(edge).set(EDGE_TYPE_STRING, sourceCluester + "-->" + targetCluester);
				} else if(sourceCluester != null && targetCluester == null) {
					network.getRow(edge).set(EDGE_TYPE_STRING, sourceCluester + "-->" + "NONE_CLUSTER");
				} else if(sourceCluester == null && targetCluester != null) {
					network.getRow(edge).set(EDGE_TYPE_STRING, "NONE_CLUSTER" + "-->" + targetCluester);
				} else {
					network.getRow(edge).set(EDGE_TYPE_STRING, "INVALID!");
				}
				
				network.getRow(edge).set(CONNECTION_TYPE_STRING, EDGE_TYPE_BETWEEN);
			}

		}
	}
}
