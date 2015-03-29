package com.github.bfour.fpliteraturecollector.gui.literature;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjgui.abstraction.EntityEditPanel;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUILabelPanel;
import com.github.bfour.fpjgui.components.FPJGUIMultilineLabel;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
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
						"[]8[]8[]8[]8[]"));

		// ID
		FPJGUILabel<String> IDLabel = new FPJGUILabel<String>();
		getContentPane().add(new FPJGUILabelPanel("ID", IDLabel),
				"cell 0 0,growx");

		// Title
		FPJGUIMultilineLabel titleField = new FPJGUIMultilineLabel();
		titleField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String obj) {
				return new ValidationRuleResult(obj != null && !obj.isEmpty(),
						"Name must not be empty.");
			}
		});
		titleField.setValueRequired(true);
		FPJGUIMultilineLabel nameLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> titleToggle = new ToggleEditFormComponent<String>(
				nameLabel, titleField);
		registerToggleComponent(titleToggle);
		getContentPane().add(new FPJGUILabelPanel("Title", titleToggle),
				"cell 0 1,growx");

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
