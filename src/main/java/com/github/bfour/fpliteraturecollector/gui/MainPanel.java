package com.github.bfour.fpliteraturecollector.gui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProviderProxy;
import com.github.bfour.fpjgui.components.PlainToolbar;
import com.github.bfour.fpjgui.design.Icons;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class MainPanel extends JPanel implements FeedbackProvider,
		FeedbackListener {

	private static final long serialVersionUID = 1144467669251836304L;
	private FeedbackProviderProxy feedbackProxy;

	/**
	 * Create the panel.
	 * 
	 * @param serviceManager
	 */
	public MainPanel(final ServiceManager serviceManager) {

		this.feedbackProxy = new FeedbackProviderProxy();

		setLayout(new MigLayout("insets 0", "[grow]", "[]20[grow]"));

		// toolbar
		PlainToolbar toolbar = new PlainToolbar(true);

		JButton availBookButton = new JButton("Availability & Booking",
				Icons.CALENDAR_32.getIcon());
		availBookButton.setIconTextGap(6);
		availBookButton.setMargin(new Insets(4, 16, 4, 16));
		// availBookButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		// availBookButton.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbar.add(availBookButton);

		JButton customersButton = new JButton("Customers",
				Icons.USER_32.getIcon());
		customersButton.setIconTextGap(6);
		customersButton.setMargin(new Insets(4, 16, 4, 16));
		// customersButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		// customersButton.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbar.add(customersButton);

		JButton roomsButton = new JButton("Rooms", Icons.DOOR_32.getIcon());
		roomsButton.setIconTextGap(6);
		roomsButton.setMargin(new Insets(4, 16, 4, 16));
		// roomsButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		// roomsButton.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbar.add(roomsButton);

		JButton roomCategoriesButton = new JButton("Room Categories",
				Icons.THREE_TAGS_32.getIcon());
		roomCategoriesButton.setIconTextGap(6);
		roomCategoriesButton.setMargin(new Insets(4, 16, 4, 16));
		// roomsButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		// roomsButton.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbar.add(roomCategoriesButton);

		JButton receiptsButton = new JButton("Receipts",
				Icons.TABLEMONEY_32.getIcon());
		receiptsButton.setIconTextGap(6);
		receiptsButton.setMargin(new Insets(4, 16, 4, 16));
		// receiptsButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		// receiptsButton.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbar.add(receiptsButton);

		add(toolbar, "cell 0 0, grow");

		// content
		ReservationsOverviewPanel currentReservationsPanel = new ReservationsOverviewPanel(
				serviceManager);
		add(currentReservationsPanel, "cell 0 1,grow");
		currentReservationsPanel.addFeedbackListener(this);

		// logic
		availBookButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AvailabilityAndBookingWindow.getInstance(serviceManager)
						.setVisible(true);
			}
		});

		customersButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CustomerWindow.getInstance(serviceManager).setVisible(true);
			}
		});

		roomsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RoomWindow.getInstance(serviceManager).setVisible(true);
			}
		});

		roomCategoriesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RoomCategoriesWindow.getInstance(serviceManager).setVisible(
						true);
			}
		});

		receiptsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ReceiptWindow.getInstance(serviceManager).setVisible(true);
			}
		});

	}

	@Override
	public void addFeedbackListener(FeedbackListener listener) {
		feedbackProxy.addFeedbackListener(listener);
	}

	@Override
	public void removeFeedbackListener(FeedbackListener listener) {
		feedbackProxy.addFeedbackListener(listener);
	}

	@Override
	public void feedbackBroadcasted(Feedback arg0) {
		feedbackProxy.fireFeedback(arg0);
	}

	@Override
	public void feedbackChanged(Feedback arg0, Feedback arg1) {
		feedbackProxy.changeFeedback(arg0, arg1);
	}

	@Override
	public void feedbackRevoked(Feedback arg0) {
		feedbackProxy.revokeFeedback(arg0);
	}

}