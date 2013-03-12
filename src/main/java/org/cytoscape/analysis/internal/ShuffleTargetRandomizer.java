package org.cytoscape.analysis.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.VirtualColumnInfo;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.session.CyNetworkNaming;

public class ShuffleTargetRandomizer implements NetworkRandomizer {

	private static final String INTERACTION_VALUE = "randomized";
	private static final String NETWORK_TITLE_PREFIX = "Randomized-Edge-Shuffled";

	private final CyNetworkNaming namingUtil;

	ShuffleTargetRandomizer(final CyNetworkNaming namingUtil) {
		this.namingUtil = namingUtil;
	}

	/**
	 * An implementation of network randomizer
	 */
	@Override
	public CyNetwork randomize(final CyNetwork originalNetwork) {
		final CySubNetwork subnet = (CySubNetwork) originalNetwork;
		final CyRootNetwork rootNet = subnet.getRootNetwork();
		final CySubNetwork randomizedNetwork = rootNet.addSubNetwork();

		final String rootName = rootNet.getRow(rootNet).get(CyNetwork.NAME, String.class);
		final String networkName = namingUtil.getSuggestedNetworkTitle(NETWORK_TITLE_PREFIX + ":" + rootName);

		randomizedNetwork.getRow(randomizedNetwork).set(CyNetwork.NAME, networkName);

		final List<CyEdge> originalEdges = originalNetwork.getEdgeList();
		final List<CyNode> targets = new ArrayList<CyNode>();
		final List<CyNode> sources = new ArrayList<CyNode>();

		// Copy nodes from original
		for (CyNode node : originalNetwork.getNodeList()) {
			randomizedNetwork.addNode(node);
		}

		for (final CyEdge edge : originalEdges) {
			targets.add(edge.getTarget());
			sources.add(edge.getSource());
		}

		final long seed = System.nanoTime();
		Collections.shuffle(targets, new Random(seed));

		int targetIdx = 0;
		for (final CyNode source : sources) {
			final CyNode target = targets.get(targetIdx);
			final String sourceName = originalNetwork.getRow(source).get(CyNetwork.NAME, String.class);
			final String targetName = originalNetwork.getRow(target).get(CyNetwork.NAME, String.class);
			final CyEdge newEdge = randomizedNetwork.addEdge(source, target, true);

			// Add edge attributes
			randomizedNetwork.getRow(newEdge).set(CyEdge.INTERACTION, INTERACTION_VALUE);
			randomizedNetwork.getRow(newEdge).set(CyNetwork.NAME,
					sourceName + " (" + INTERACTION_VALUE + ") " + targetName);

			targetIdx++;
		}

		addColumns(originalNetwork.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS),
				randomizedNetwork.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS));

		return randomizedNetwork;
	}

	/**
	 * Copy virtual columns (shared vars only)
	 * 
	 * @param parentTable
	 * @param subTable
	 */
	private void addColumns(CyTable parentTable, CyTable subTable) {
		List<CyColumn> colsToAdd = new ArrayList<CyColumn>();

		for (CyColumn col : parentTable.getColumns())
			if (subTable.getColumn(col.getName()) == null)
				colsToAdd.add(col);

		for (CyColumn col : colsToAdd) {
			VirtualColumnInfo colInfo = col.getVirtualColumnInfo();
			if (colInfo.isVirtual())
				addVirtualColumn(col, subTable);
		}
	}

	private void addVirtualColumn(CyColumn col, CyTable subTable) {
		VirtualColumnInfo colInfo = col.getVirtualColumnInfo();
		CyColumn checkCol = subTable.getColumn(col.getName());
		if (checkCol == null)
			subTable.addVirtualColumn(col.getName(), colInfo.getSourceColumn(), colInfo.getSourceTable(),
					colInfo.getTargetJoinKey(), col.isImmutable());

		else if (!checkCol.getVirtualColumnInfo().isVirtual()
				|| !checkCol.getVirtualColumnInfo().getSourceTable().equals(colInfo.getSourceTable())
				|| !checkCol.getVirtualColumnInfo().getSourceColumn().equals(colInfo.getSourceColumn()))
			subTable.addVirtualColumn(col.getName(), colInfo.getSourceColumn(), colInfo.getSourceTable(),
					colInfo.getTargetJoinKey(), col.isImmutable());
	}

}
