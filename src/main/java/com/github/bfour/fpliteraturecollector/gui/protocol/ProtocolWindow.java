package com.github.bfour.fpliteraturecollector.gui.protocol;

import com.github.bfour.fpjgui.components.FPJGUIWindow;
import com.github.bfour.fpjgui.components.composite.EntityTableBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.fpjgui.components.table.FPJGUITableColumn;
import com.github.bfour.fpjgui.design.PanelDecorator;
import com.github.bfour.fpliteraturecollector.domain.ProtocolEntry;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class ProtocolWindow extends FPJGUIWindow {

	private static final long serialVersionUID = 5147571920321921998L;
	private static ProtocolWindow instance;

	private ProtocolWindow(ServiceManager servMan) {

		super("Protocol", 861, 681);

		EntityTableBrowsePanel<ProtocolEntry> browsePanel = new EntityTableBrowsePanel<ProtocolEntry>(
				ProtocolEntry.class, servMan.getProtocolEntryService(), true);
		browsePanel.getListLikeContainer().addColumn(
				new FPJGUITableColumn<ProtocolEntry>("Time",
						new FPJGUITableFieldGetter<ProtocolEntry>() {
							@Override
							public String get(ProtocolEntry item) {
								return item.getCreationTime() + "";
							}
						}));
		browsePanel.getListLikeContainer().addColumn(
				new FPJGUITableColumn<ProtocolEntry>("Text",
						new FPJGUITableFieldGetter<ProtocolEntry>() {
							@Override
							public String get(ProtocolEntry item) {
								return item.getString();
							}
						}));
		PanelDecorator.decorateWithDropShadow(browsePanel);
		browsePanel.setCreateEntityEnabled(false);
		browsePanel.setDeleteEntityEnabled(false);
		browsePanel.setEditEntityEnabled(false);
		getContentPane().add(browsePanel, "grow");

	}

	public static ProtocolWindow getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new ProtocolWindow(servMan);
		return instance;
	}

}
