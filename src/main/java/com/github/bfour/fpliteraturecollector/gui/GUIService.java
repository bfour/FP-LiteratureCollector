package com.github.bfour.fpliteraturecollector.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.alee.laf.WebLookAndFeel;
import com.github.bfour.fpjcommons.services.ConfigurationService;
import com.github.bfour.fpjcommons.services.ConfigurationService.ConfigurationOption;
import com.github.bfour.fpjcommons.services.localization.LocalizationService;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.design.Lengths;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

/**
 * Contains the service for the graphical user interface (GUI).
 * 
 * @author Florian Pollak
 *
 */
public class GUIService implements FeedbackListener {

	private static final Logger LOGGER = Logger.getLogger(GUIService.class);
	private Configuration config = ConfigurationService.getUserConfiguration();
	private static GUIService instance;
	private MainWindow mainWindow;
	private Stack<Feedback> feedbackQueue;
	private double scalingFactor;

	private GUIService() {
		initialize();
	}

	/**
	 * Gets an instance of GUIService (singleton).
	 * 
	 * @return an instance of GUIService
	 */
	public static GUIService getInstance() {
		if (instance == null) {
			instance = new GUIService();
		}
		return instance;
	}

	/**
	 * Initializes the GUI-stuff.
	 */
	public void initialize() {
		WebLookAndFeel.initializeManagers();
		initializeLocalization();
		setLookAndFeel();
		setUISettings();
		this.feedbackQueue = new Stack<>();
	}

	/**
	 * Initializes the localization.
	 */
	private void initializeLocalization() {
		// String language =
		// config.getString(ConfigurationOption.LOCALE_LANGUAGE
		// .toString());
		// try {
		// LocalizationService.getInstance(new Locale(language));
		// } catch (ServiceException e) {
		// LOGGER.error("requested localization does not exist: " + language,
		// e);
		// try {
		// try again with default locale enforced
		LocalizationService.getFailsafeInstance();
		// } catch (ServiceException e2) {
		// LOGGER.fatal("falling back to default localization failed", e2);
		// showError("Unable to properly load interface. "
		// + "Files are missing. Please reinstall the application "
		// + "or contact the administrator.");
		// }
		// }
	}

	/**
	 * Does the settings of the GUI.
	 */
	private void setUISettings() {

		UIManager.put("TabbedPane.contentBorderInsets", new Insets(1, 0, 0, 0));
		UIManager.put("TabbedPane.tabAreaInsets", new Insets(2, 15, 0, 15));
		UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", false);

		Color panelBackground = UIManager.getColor("Panel.background");
		UIManager.put("ToolBar.background", panelBackground);
		UIManager.put("ToolBar.border", panelBackground);

		UIManager.put("Button.focus", new Color(0, 0, 0, 0));

		UIManager.put("Button.height", Lengths.SMALL_BUTTON_HEIGHT.getLength());

		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			UIManager.put("Tree.paintLines", Boolean.FALSE);
			// UIManager.put("Tree.leafIcon", new ImageIcon());
			UIManager
					.put("Tree.expandedIcon",
							new ImageIcon(
									GUIService.class
											.getResource("/icons/abstract/win8_tree_arrow_down_10.png")));
			UIManager
					.put("Tree.collapsedIcon",
							new ImageIcon(
									GUIService.class
											.getResource("/icons/abstract/win8_tree_arrow_right_10.png")));
			UIManager.put("Tree.leftChildIndent", 2);
		}

		scalingFactor = ConfigurationService.getUserConfiguration().getDouble(
				ConfigurationOption.SCALING_FACTOR.toString());
		setSize(scalingFactor);

	}

	/**
	 * Sets the "look and feel" of the GUI.
	 */
	private void setLookAndFeel() {
		try {

			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				UIManager
						.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
			} else {
				// TODO (high) improve
				for (javax.swing.UIManager.LookAndFeelInfo lf : javax.swing.UIManager
						.getInstalledLookAndFeels()) {
					if (lf.getClassName().equals(
							"com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
						javax.swing.UIManager.setLookAndFeel(lf.getClassName());
						break;
					}
				}
			}

		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			LOGGER.error("failed to set L&F", e);
		}
	}

	/**
	 * Shows the GUI. Requires the loginWindow and mainWindow to be properly
	 * initialized. Throws exception if either window is not properly
	 * initialized.
	 * 
	 */
	public void show(final ServiceManager serviceManager) {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					mainWindow = MainWindow.getInstance(serviceManager);
				}
			});
		} catch (InvocationTargetException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mainWindow.setVisible(true);
		for (Feedback feedback : feedbackQueue) {
			mainWindow.feedbackBroadcasted(feedback);
		}
		feedbackQueue.clear();
	}

	/**
	 * Shows an error dialog.
	 * 
	 * @param msg
	 *            the error message
	 */
	private void showError(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void setSize(double scalingFactor) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null
					&& value instanceof javax.swing.plaf.FontUIResource) {
				Font font = (Font) value;
				UIManager
						.put(key,
								font.deriveFont((float) (font.getSize2D() * scalingFactor)));
			}
		}
	}

	@Override
	public void feedbackBroadcasted(Feedback feedback) {
		if (mainWindow == null) {
			feedbackQueue.add(feedback);
			return;
		}
		mainWindow.feedbackBroadcasted(feedback);
	}

	@Override
	public void feedbackRevoked(Feedback feedback) {
		if (mainWindow == null) {
			feedbackQueue.remove(feedback);
			return;
		}
		mainWindow.feedbackRevoked(feedback);
	}

	@Override
	public void feedbackChanged(Feedback old, Feedback changed) {
		if (mainWindow == null) {
			feedbackQueue.remove(old);
			feedbackQueue.add(changed);
			return;
		}
		mainWindow.feedbackChanged(old, changed);
	}

	public double getScalingFactor() {
		return scalingFactor;
	}

}