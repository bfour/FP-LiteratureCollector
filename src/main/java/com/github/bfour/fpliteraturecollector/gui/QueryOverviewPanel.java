package com.github.bfour.fpliteraturecollector.gui;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpliteraturecollector.gui.components.ScrollableJPanel;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class QueryOverviewPanel extends JXPanel implements FeedbackProvider {

	private static final long serialVersionUID = 5529685995539560855L;

	private JComponent container;
	
	public QueryOverviewPanel(ServiceManager servMan) {
		
		container = new ScrollableJPanel();
		container.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		JScrollPane wrapper = new JScrollPane(container);
		wrapper.setBorder(null);	
		add(wrapper, "cell 0 0,grow");
		
		
		
	}

	@Override
	public void addFeedbackListener(FeedbackListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFeedbackListener(FeedbackListener arg0) {
		// TODO Auto-generated method stub
		
	}

}
