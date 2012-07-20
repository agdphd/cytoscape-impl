/*
 File: SelectFirstNeighborsNodeViewTaskFactory.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.task.internal.group;

import java.util.List;

import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.task.edit.GroupNodesTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class GroupNodesTaskFactoryImpl extends AbstractNetworkViewTaskFactory 
                                       implements NodeViewTaskFactory, GroupNodesTaskFactory {
	private CyGroupManager mgr;
	private CyGroupFactory groupFactory;

	public GroupNodesTaskFactoryImpl(CyGroupManager mgr, CyGroupFactory groupFactory) {
		super();
		this.mgr = mgr;
		this.groupFactory = groupFactory;
	}

	public TaskIterator createTaskIterator(CyNetworkView view) {
		return new TaskIterator(new GroupNodesTask(view, mgr, groupFactory));
	}

	public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView view) {
		return new TaskIterator(new GroupNodesTask(view, mgr, groupFactory));
	}

	public boolean isReady(CyNetworkView netView) {
		if (netView == null) 
			return false;

		// Get all of the selected nodes
		CyNetwork net = netView.getModel();
		final List<CyNode> selNodes = CyTableUtil.getNodesInState(net, CyNetwork.SELECTED, true);
		if (selNodes.size() > 1)
			return true;
		return false; 
	}

	public boolean isReady(View<CyNode> nodeView, CyNetworkView netView) {
		if (nodeView == null || netView == null) {
			return false;
		}

		// Get all of the selected nodes
		CyNetwork net = netView.getModel();
		final List<CyNode> selNodes = CyTableUtil.getNodesInState(net, CyNetwork.SELECTED, true);
		if (selNodes.size() > 1)
			return true;
		return false; 
	}
}
