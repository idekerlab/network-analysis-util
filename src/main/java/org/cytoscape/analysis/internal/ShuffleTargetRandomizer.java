package org.cytoscape.analysis.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.session.CyNetworkNaming;

public class ShuffleTargetRandomizer implements NetworkRandomizer {

	private static final String INTERACTION_VALUE = "randomized";
	private static final String NETWORK_TITLE_PREFIX = "Randomized-Edge-Shuffled ";

	private final CyNetworkNaming namingUtil;
	private final CyNetworkManager networkManager;

	ShuffleTargetRandomizer(final CyNetworkManager networkManager, final CyNetworkNaming namingUtil) {
		this.networkManager = networkManager;
		this.namingUtil = namingUtil;
	}

	/**
	 * An implementation of network randomizer
	 */
	@Override
	public CyNetwork randomize(final CyNetwork network) {
		final CySubNetwork subnet = (CySubNetwork) network;
		final CyRootNetwork rootNet = subnet.getRootNetwork();
		final CySubNetwork randomizedNetwork = rootNet.addSubNetwork();
		
		final String rootName = rootNet.getRow(rootNet).get(CyNetwork.NAME, String.class);
		final String networkName = namingUtil.getSuggestedNetworkTitle(rootName);
		
		randomizedNetwork.getRow(randomizedNetwork).set(CyNetwork.NAME, NETWORK_TITLE_PREFIX + ":" + networkName);
		networkManager.addNetwork(randomizedNetwork);

		final List<CyEdge> originalEdges = network.getEdgeList();
		final List<CyNode> targets = new ArrayList<CyNode>();
		final List<CyNode> sources = new ArrayList<CyNode>();

		for (final CyEdge edge : originalEdges) {
			targets.add(edge.getTarget());
			sources.add(edge.getSource());
		}

		final long seed = System.nanoTime();
		Collections.shuffle(targets, new Random(seed));

		final Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();

		int targetIdx = 0;
		for (final CyNode source : sources) {
			final CyNode target = targets.get(targetIdx);
			final String sourceName = network.getRow(source).get(CyNetwork.NAME, String.class);
			final String targetName = network.getRow(target).get(CyNetwork.NAME, String.class);
			final CyNode newSource = getNode(sourceName, source, network, randomizedNetwork, nodeMap);
			final CyNode newTarget = getNode(targetName, target, network, randomizedNetwork, nodeMap);
			final CyEdge newEdge = randomizedNetwork.addEdge(newSource, newTarget, true);

			// Add edge attributes
			randomizedNetwork.getRow(newEdge).set(CyEdge.INTERACTION, INTERACTION_VALUE);
			randomizedNetwork.getRow(newEdge).set(CyNetwork.NAME, sourceName + " (" + INTERACTION_VALUE + ") " + targetName);
			
			targetIdx++;
		}

		return randomizedNetwork;
	}

	private CyNode getNode(final String nodeName, final CyNode originalNode, final CyNetwork network,
			final CyNetwork newNetwork, final Map<String, CyNode> nodeMap) {
		CyNode newNode = nodeMap.get(nodeName);
		if (newNode == null) {
			newNode = newNetwork.addNode();
			nodeMap.put(nodeName, newNode);
		}
		
		// Add node attributes
		newNetwork.getRow(newNode).set(CyNetwork.NAME, nodeName);
		
		return newNode;
	}

}
