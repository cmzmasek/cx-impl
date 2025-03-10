package org.cytoscape.io.internal.cx_reader;

import java.io.IOException;
import java.io.InputStream;

import org.cytoscape.io.internal.cxio.Cx2Importer;
import org.cytoscape.io.internal.cxio.Settings;
import org.cytoscape.io.internal.cxio.TimingUtil;
import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.util.ListSingleSelection;
import org.ndexbio.model.exceptions.NdexException;
import org.slf4j.LoggerFactory;

public class CytoscapeCx2NetworkReader extends AbstractCyNetworkReader {

	private CyNetwork[] _networks;
	private String _network_collection_name;

	private Cx2Importer cx2Importer;

	private Boolean createView = null;
	
	// Warning: HACK. Cytoscape doesn't allow access to reader parameters programmatically. This method allows Java reflections 
	// access to it. This method should not be renamed or made private.
	public void setCreateView(final Boolean createView) {
		this.createView = createView;
	}
	
	public CytoscapeCx2NetworkReader(final InputStream input_stream, final String network_collection_name,
			final CyNetworkViewFactory networkview_factory, final CyNetworkFactory network_factory,
			final CyNetworkManager network_manager, final CyRootNetworkManager root_network_manager) {

		super(input_stream, networkview_factory, network_factory, network_manager, root_network_manager);

		cx2Importer = new Cx2Importer(input_stream, true);
		_network_collection_name = network_collection_name;
		
		this._networks=null;
		this.createView = Boolean.TRUE;
	}

	@Override
	public CyNetworkView buildCyNetworkView(final CyNetwork network) {
	
		 
		try {
			return cx2Importer.createView();
		} catch (Exception e) {
			LoggerFactory.getLogger("CX2 Importer").error(e.getMessage());
			return null;
		}
	}

	
	@Override
	public CyNetwork[] getNetworks() {
		return _networks;
	}

	@Override
	public void run(final TaskMonitor taskMonitor) throws IOException, NdexException {

		System.out.println("create view value: " + createView);
		final long t0 = System.currentTimeMillis();

		if (Settings.INSTANCE.isTiming()) {
			TimingUtil.reportTimeDifference(t0, "total time parsing", -1);
		}

		// Throw an error if trying to import CX network into existing collection.
		if (getRootNetwork() != null) {
			System.out.println("CX Support is changing to disallow import into existing collections");
			setRootNetworkList(new ListSingleSelection<String>());
		}

		long t1 = System.currentTimeMillis();
		CyNetwork newSubnetwork =  cx2Importer.importNetwork();
		
		if (cx2Importer.getNetworkName() == null) {
			// Set the name of collection/network to be imported
			if (_network_collection_name == null) {
				_network_collection_name = "Unnamed CX Network";
			}
			cx2Importer.setCollectionName(_network_collection_name);
		}

		
		if (Settings.INSTANCE.isTiming()) {
			TimingUtil.reportTimeDifference(t1, "Time to create NiceCyNetwork", -1);
		}

	/*	if (cx2Importer.getNetworkName() == null) {
			// Set the name of collection/network to be imported
			if (_network_collection_name == null) {
				_network_collection_name = "Unnamed CX Network";
			}
			niceCy.setNetworkName(_network_collection_name);
		}

		t1 = System.currentTimeMillis();
		List<CyNetwork> importedNetworks = niceCy.apply();
		if (Settings.INSTANCE.isTiming()) {
			TimingUtil.reportTimeDifference(t1, "Time to create networks in Cytoscape", -1);
		} */
		_networks = new CyNetwork[1];
		_networks[0] = newSubnetwork;

		if (Settings.INSTANCE.isTiming()) {
			TimingUtil.reportTimeDifference(t0, "total time to build network(s) (not views)", -1);
		}
	}

}