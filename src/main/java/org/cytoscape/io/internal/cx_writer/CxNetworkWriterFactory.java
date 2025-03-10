package org.cytoscape.io.internal.cx_writer;

import java.io.OutputStream;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

public class CxNetworkWriterFactory implements CyNetworkViewWriterFactory {
    private final CyFileFilter          _filter;
    private final boolean isCX2;

    public CxNetworkWriterFactory(final CyFileFilter filter, boolean inCX2Format) {
        _filter = filter;
        isCX2 = inCX2Format;
    }

    @Override
    public CyWriter createWriter(final OutputStream os, final CyNetwork network) {
        return new CxNetworkWriter(os,
                                   network,
                                   false,
                                   true, isCX2);
    }

    @Override
    public CyFileFilter getFileFilter() {
        return _filter;
    }

    @Override
    public CyWriter createWriter(final OutputStream os, final CyNetworkView view) {
        return new CxNetworkWriter(os,
        						view.getModel(),
                                   view,
                                   true, isCX2);

    }

}
