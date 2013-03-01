package org.cytoscape.analysis.internal;

import java.util.Properties;

import org.cytoscape.analysis.SampleAnalyzer;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		SampleAnalyzer sampleAnalyzer = new SampleAnalyzerImpl();
		
		Properties properties = new Properties();
		registerService(context, sampleAnalyzer, SampleAnalyzer.class, properties);
	}

}
