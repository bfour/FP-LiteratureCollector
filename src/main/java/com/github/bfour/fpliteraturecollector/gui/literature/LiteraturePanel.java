package com.github.bfour.fpliteraturecollector.gui.literature;

import java.awt.Font;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjgui.abstraction.EntityEditPanel;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUITextField;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
import com.github.bfour.fpjgui.design.Colors;
import com.github.bfour.fpjgui.util.ObjectGraphicalValueContainerMapper;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class LiteraturePanel extends
		EntityEditPanel<Literature, LiteratureBuilder> {

	private static final long serialVersionUID = -6108218045598314837L;

	/**
	 * Create the panel.
	 */
	public LiteraturePanel(ServiceManager servMan) {

		super(new BuilderFactory<Literature, LiteratureBuilder>() {
			@Override
			public LiteratureBuilder getBuilder() {
				return new LiteratureBuilder();
			}

			@Override
			public LiteratureBuilder getBuilder(Literature entity) {
				return new LiteratureBuilder(entity);
			}

		}, servMan.getLiteratureService());

		getContentPane().setLayout(
				new MigLayout("insets 0, w 60:80:100", "[grow]",
						"[]0[]8[]0[]8[]"));

		JLabel dummy = new JLabel();
		Font labelFont = dummy.getFont().deriveFont(
				dummy.getFont().getSize() - 2f);

		// ID
		JLabel lblID = new JLabel("ID");
		lblID.setFont(labelFont);
		lblID.setForeground(Colors.VERY_STRONG_GRAY.getColor());
		getContentPane().add(lblID, "cell 0 0");

		FPJGUILabel<String> IDLabel = new FPJGUILabel<String>();
		getContentPane().add(IDLabel, "cell 0 1,growx");

		// Title
		JLabel lblTitle = new JLabel("Title");
		lblTitle.setFont(labelFont);
		lblTitle.setForeground(Colors.VERY_STRONG_GRAY.getColor());
		getContentPane().add(lblTitle, "cell 0 2");

		FPJGUITextField titleField = new FPJGUITextField();
		titleField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String obj) {
				return new ValidationRuleResult(obj != null && !obj.isEmpty(),
						"Name must not be empty.");
			}
		});
		titleField.setValueRequired(true);
		FPJGUILabel<String> nameLabel = new FPJGUILabel<String>();
		ToggleEditFormComponent<String> titleToggle = new ToggleEditFormComponent<String>(
				nameLabel, titleField);
		registerToggleComponent(titleToggle);
		getContentPane().add(titleToggle, "cell 0 3,growx");

		// mappings
		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> IDMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				IDLabel) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getID() + "";
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				try {
					object.setID(Long.parseLong(value));
				} catch (NumberFormatException e) {
				}
			}
		};
		getMappers().add(IDMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> titleMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				titleToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getTitle();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setTitle(value);
			}
		};
		getMappers().add(titleMapper);

	}
}
