package org.cytoscape.analysis.internal;

import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.analysis.EdgeOrganizer;
import org.cytoscape.analysis.NetworkRandomizer;
import org.cytoscape.analysis.internal.task.OrganizeEdgesTaskFactory;
import org.cytoscape.analysis.internal.task.RandomizedNetworkTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.work.ServiceProperties;
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
		final CyApplicationManager applicationManager = getService(bc, CyApplicationManager.class);

		final NetworkRandomizer randomizer = new ShuffleTargetRandomizer(namingUtil);
		Properties properties = new Properties();
		registerService(bc, randomizer, NetworkRandomizer.class, properties);

		final EdgeOrganizer organizer = new EdgeOrganizerImpl();
		final OrganizeEdgesTaskFactory organizeEdgesTaskFactory = new OrganizeEdgesTaskFactory(applicationManager,
				organizer);

		final RandomizedNetworkTaskFactory randomizedNetworkTaskFactory = new RandomizedNetworkTaskFactory(manager,
				randomizer);

		final Properties randomizedNetworkTaskFactoryProps = new Properties();
		randomizedNetworkTaskFactoryProps.setProperty(ServiceProperties.ID, "randomizedNetworkTaskFactory");
		randomizedNetworkTaskFactoryProps.setProperty(PREFERRED_MENU, "Tools.Randomize Network");
		randomizedNetworkTaskFactoryProps.setProperty(MENU_GRAVITY, "1.1");
		randomizedNetworkTaskFactoryProps.setProperty(TITLE, "Shuffle Edges");
		randomizedNetworkTaskFactoryProps.setProperty(ENABLE_FOR, "network");
		registerAllServices(bc, randomizedNetworkTaskFactory, randomizedNetworkTaskFactoryProps);

		final Properties organizeEdgesTaskFactoryProps = new Properties();
		organizeEdgesTaskFactoryProps.setProperty(ServiceProperties.ID, "organizeEdgesTaskFactory");
		organizeEdgesTaskFactoryProps.setProperty(PREFERRED_MENU, "Tools.Tag Edges");
		organizeEdgesTaskFactoryProps.setProperty(MENU_GRAVITY, "1.2");
		organizeEdgesTaskFactoryProps.setProperty(TITLE, "Tagging");
		organizeEdgesTaskFactoryProps.setProperty(ENABLE_FOR, "network");
		registerAllServices(bc, organizeEdgesTaskFactory, organizeEdgesTaskFactoryProps);
	}
}
