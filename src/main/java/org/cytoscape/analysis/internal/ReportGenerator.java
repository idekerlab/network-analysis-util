package org.cytoscape.analysis.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;

/**
 * Analyze network and create report as network attributes
 * 
 */
public class ReportGenerator {

	public void analyze(final CyNetwork network, final double threshold) {
		final List<CyNode> nodes = network.getNodeList();

		final Map<String, Set<CyNode>> clusterMap = new HashMap<String, Set<CyNode>>();
		for (CyNode node : nodes) {
			final String clusterName = network.getRow(node).get("Cluster Name", String.class);
			if (clusterName != null) {
				Set<CyNode> cluster = clusterMap.get(clusterName);
				if (cluster == null)
					cluster = new HashSet<CyNode>();
				cluster.add(node);
				clusterMap.put(clusterName, cluster);
			}
		}

		for (String clusterName : clusterMap.keySet())
			processCluster(network, threshold, clusterMap.get(clusterName), clusterName);
	}

	/**
	 * a. the total #edges in/total #nodes
	 * b. the total #edges out/total #nodes
	 * 
	 * c. the %nodes that have > x% of the edges in
	 * d. the %nodes that have > x% of the edges out
	 * 
	 * where a. and b. represent the average hub size and c. and d. represent
	 * the % of large hubs.
	 * 
	 * @param cluster
	 */
	private void processCluster(final CyNetwork network, final double threshold, final Set<CyNode> cluster, final String clusterName) {

		final Set<CyEdge> inSet = new HashSet<CyEdge>();
		final Set<CyEdge> outSet = new HashSet<CyEdge>();

		for (CyNode node : cluster) {
			final List<CyEdge> edges = network.getAdjacentEdgeList(node, Type.ANY);

			for (CyEdge edge : edges) {
				final String edgeType = network.getRow(edge)
						.get(EdgeOrganizerImpl.CONNECTION_TYPE_STRING, String.class);
				if (edgeType != null) {
					if (edgeType.equals(EdgeOrganizerImpl.EDGE_TYPE_INTERNAL))
						inSet.add(edge);
					else
						outSet.add(edge);
				}
			}
		}

		calcurate(network, threshold, cluster, inSet, outSet, clusterName);
	}

	private void calcurate(final CyNetwork network, final double threshold, final Set<CyNode> cluster,
			final Set<CyEdge> inSet, final Set<CyEdge> outSet, final String clusterName) {
		final int clusterSize = cluster.size();
		final double inRatio = (double) inSet.size() / (double) clusterSize;
		final double outRatio = (double) outSet.size() / (double) clusterSize;

		final Map<CyNode, Double> ratioEdgesIn = new HashMap<CyNode, Double>();
		final Map<CyNode, Double> ratioEdgesOut = new HashMap<CyNode, Double>();

		for (CyNode node : cluster) {
			final List<CyEdge> edges = network.getAdjacentEdgeList(node, Type.ANY);

			int inCount = 0;
			int outCount = 0;

			for (CyEdge edge : edges) {
				final String edgeType = network.getRow(edge)
						.get(EdgeOrganizerImpl.CONNECTION_TYPE_STRING, String.class);
				if (edgeType != null) {
					if (edgeType.equals(EdgeOrganizerImpl.EDGE_TYPE_INTERNAL))
						inCount++;
					else
						outCount++;
				}
			}
			ratioEdgesIn.put(node, inCount / (double) inSet.size());
			ratioEdgesOut.put(node, outCount / (double) outSet.size());
		}

		int largeHubCountIn = 0;
		int largeHubCountOut = 0;
		for (final CyNode node : cluster) {
			if (ratioEdgesIn.get(node) > threshold) {
				largeHubCountIn++;
			}

			if (ratioEdgesOut.get(node) > threshold) {
				largeHubCountOut++;
			}
		}

		final double hubRatioIn = largeHubCountIn / (double) cluster.size();
		final double hubRatioOut = largeHubCountOut / (double) cluster.size();

		// Create new network attribute
		final CyTable table = network.getDefaultNetworkTable();
		if(table.getColumn(clusterName) == null)
			table.createListColumn(clusterName, Double.class, false);
		
		// Put result of a, b, c, and d in order.
		final List<Double> resultArray = new ArrayList<Double>();
		resultArray.add(inRatio);
		resultArray.add(outRatio);
		resultArray.add(hubRatioIn);
		resultArray.add(hubRatioOut);
		table.getRow(network.getSUID()).set(clusterName, resultArray);

	}
}
