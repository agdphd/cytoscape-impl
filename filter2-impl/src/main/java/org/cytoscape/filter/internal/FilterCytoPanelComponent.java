package org.cytoscape.filter.internal;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.filter.TransformerManager;
import org.cytoscape.filter.internal.view.FilterPanel;
import org.cytoscape.filter.internal.view.FilterPanelController;
import org.cytoscape.filter.internal.view.TransformerViewManager;

public class FilterCytoPanelComponent implements CytoPanelComponent {

	FilterPanel panel;

	public FilterCytoPanelComponent(TransformerManager transformerManager, TransformerViewManager transformerViewManager, CyApplicationManager applicationManager) {
		FilterPanelController controller = new FilterPanelController(transformerManager, transformerViewManager, applicationManager);
		panel = new FilterPanel(controller);
		panel.setPreferredSize(new Dimension(450, 300));
	}
	
	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public String getTitle() {
		return "Filter (New)";
	}

	@Override
	public Icon getIcon() {
		return null;
	}
}