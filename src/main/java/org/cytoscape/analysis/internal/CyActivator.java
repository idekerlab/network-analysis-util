package org.cytoscape.analysis.internal;

import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.analysis.internal.task.RandomizedNetworkTaskFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	@Override
	public void start(BundleContext bc) throws Exception {
		final CyNetworkManager manager = getService(bc, CyNetworkManager.class);
		final CyNetworkNaming namingUtil = getService(bc, CyNetworkNaming.class);

		final NetworkRandomizer randomizer = new ShuffleTargetRandomizer(manager, namingUtil);
		Properties properties = new Properties();
		registerService(bc, randomizer, NetworkRandomizer.class, properties);

		RandomizedNetworkTaskFactory randomizedNetworkTaskFactory = new RandomizedNetworkTaskFactory(manager,
				randomizer);

		Properties randomizedNetworkTaskFactoryProps = new Properties();
		randomizedNetworkTaskFactoryProps.setProperty(PREFERRED_MENU, "Tools.Randomize Network");
		randomizedNetworkTaskFactoryProps.setProperty(MENU_GRAVITY, "1.1");
		randomizedNetworkTaskFactoryProps.setProperty(TITLE, "Shuffle Edges");
		randomizedNetworkTaskFactoryProps.setProperty(ENABLE_FOR, "network");
		registerService(bc, randomizedNetworkTaskFactory, TaskFactory.class, randomizedNetworkTaskFactoryProps);

	}
}
