package com.github.bfour.fpliteraturecollector.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import com.github.bfour.fpliteraturecollector.application.Application;
import com.github.bfour.fpliteraturecollector.application.FPLCNeo4jConfiguration;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.gui.literature.SemanticValidator;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.jlib.commons.lang.Quadruple;
import com.github.bfour.jlib.commons.logic.LogicException;
import com.github.bfour.jlib.commons.logic.translation.TranslationException;
import com.github.bfour.jlib.commons.services.ServiceException;

@Import(FPLCNeo4jConfiguration.class)
public class SemanticValidatorTest {

	private static ServiceManager servMan;
	private static SemanticValidator val;
	private static List<Quadruple<Literature, Boolean, Boolean, String>> tagSetCompleteValidTuples;

	@BeforeClass
	public static void init() throws ServiceException {

		// https://vvirlan.wordpress.com/2014/12/10/solved-caused-by-java-awt-headlessexception-when-trying-to-create-a-swingawt-frame-from-spring-boot/
		SpringApplicationBuilder builder = new SpringApplicationBuilder(
				Application.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run();
		servMan = context.getBean(ServiceManager.class);
		context.getAutowireCapableBeanFactory().autowireBeanProperties(servMan,
				AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		val = new SemanticValidator(servMan, false);
		tagSetCompleteValidTuples = new ArrayList<>();

		// print model
		val.print();

		// incomplete but valid
		tagSetCompleteValidTuples.add(new Quadruple<>(getWithTags(""), false,
				true, "empty lit"));
		tagSetCompleteValidTuples.add(new Quadruple<>(
				getWithTags("Admin: revised"), false, true, "only revised"));
		tagSetCompleteValidTuples.add(new Quadruple<>(getWithTags("Topic: UX",
				"Quality: poor"), false, true, "incomplete but valid"));
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised", "App: yes", "Prototype: yes",
						"Quality: OK", "Topic: communication")).setYear(2015)
				.getObject(), false, true,
				"has health prototype but no attributes"));
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised", "App: yes",
						"Communication: Bluetooth", "Device: Smartphone",
						"Empirically Validated: no", "Health: yes",
						"OS: Android", "Prototype: yes", "Quality: OK",
						"Topic: communication")).setYear(2015).getObject(),
				false, true, "missing architecture"));
		tagSetCompleteValidTuples.add(new Quadruple<>(
				new LiteratureBuilder(
						getWithTags("Admin: revised", "App: yes",
								"Architecture: sensor > backend",
								"Architecture: smart device",
								"Communication: Bluetooth",
								"Device: Smartphone",
								"Empirically Validated: no", "OS: Android",
								"Prototype: yes", "Quality: OK",
								"Topic: communication")).setYear(2015)
						.getObject(), false, true, "missing health"));

		// incomplete and invalid
		tagSetCompleteValidTuples.add(new Quadruple<>(getWithTags(
				"Quality: OK", "Quality: poor"), false, false,
				"incomplete and invalid"));
		tagSetCompleteValidTuples.add(new Quadruple<>(getWithTags(
				"Topic: off-topic", "Health: yes", "Topic: communication"),
				false, false, "incomplete and invalid"));

		// complete but invalid
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised", "App: yes",
						"Architecture: sensor > backend",
						"Architecture: smart device",
						"Communication: Bluetooth", "Device: Smartphone",
						"Empirically Validated: no", "Health: yes",
						"OS: Android", "Prototype: yes", "Quality: OK",
						"Topic: communication",
						"Used SmartDevice Component: compass",
						"Wearable Type: patch", "Wearable Location: head",
						"Target Group: patients", "App: no")).setYear(2015)
				.getObject(), true, false, "App: yes and no"));
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised", "App: yes",
						"Architecture: sensor > backend",
						"Architecture: smart device",
						"Communication: Bluetooth", "Device: Smartphone",
						"Empirically Validated: no", "Health: yes",
						"OS: Android", "Prototype: yes", "Quality: OK",
						"Topic: communication",
						"Used SmartDevice Component: compass",
						"Wearable Type: patch", "Wearable Location: head",
						"Target Group: patients", "Health: no")).setYear(2015)
				.getObject(), true, false, "Health and App: yes and no"));
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised", "App: yes",
						"Architecture: sensor > backend",
						"Architecture: smart device",
						"Communication: Bluetooth", "Device: Smartphone",
						"Empirically Validated: no", "Health: yes",
						"OS: Android", "Prototype: yes", "Quality: OK",
						"Topic: communication",
						"Used SmartDevice Component: compass",
						"Wearable Type: patch", "Wearable Location: head",
						"Target Group: patients", "Quality: poor")).setYear(
				2015).getObject(), true, false, "Quality poor but OK"));
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised", "App: yes",
						"Architecture: sensor > backend",
						"Architecture: smart device",
						"Communication: Bluetooth", "Device: Smartphone",
						"Empirically Validated: no", "Health: yes",
						"OS: Android", "Prototype: yes", "Quality: OK",
						"Topic: communication",
						"Used SmartDevice Component: compass",
						"Wearable Type: patch", "Wearable Location: head",
						"Target Group: patients",
						"Quality: inappropriate format")).setYear(2015)
				.getObject(), true, false, "Quality poor but OK"));
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised", "App: yes",
						"Architecture: sensor > backend",
						"Architecture: smart device",
						"Communication: Bluetooth", "Device: Smartphone",
						"Empirically Validated: no", "Health: yes",
						"OS: Android", "Prototype: yes", "Quality: OK",
						"Topic: communication",
						"Used SmartDevice Component: compass",
						"Wearable Type: patch", "Wearable Location: head",
						"Target Group: patients",
						"Quality: insufficient language proficiency")).setYear(
				2015).getObject(), true, false, "Quality poor but OK"));
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised", "App: yes",
						"Architecture: sensor > backend",
						"Architecture: smart device",
						"Communication: Bluetooth", "Device: Smartphone",
						"Empirically Validated: no", "Health: yes",
						"OS: Android", "Prototype: yes", "Quality: OK",
						"Topic: communication",
						"Used SmartDevice Component: compass",
						"Wearable Type: patch", "Wearable Location: head",
						"Target Group: patients", "App: no", "Health: no"))
				.setYear(2015).getObject(), true, false,
				"Health and App: yes and no"));

		// valid and complete
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised")).setYear(1900).getObject(), true,
				true, "too old"));
		tagSetCompleteValidTuples
				.add(new Quadruple<>(getWithTags("Admin: revised",
						"Topic: off-topic"), true, true, "off-topic"));
		tagSetCompleteValidTuples
				.add(new Quadruple<>(getWithTags("Admin: revised",
						"Quality: poor"), true, true, "poor quality"));
		tagSetCompleteValidTuples.add(new Quadruple<>(new LiteratureBuilder(
				getWithTags("Admin: revised", "App: yes",
						"Architecture: sensor > backend",
						"Architecture: smart device",
						"Communication: Bluetooth", "Device: Smartphone",
						"Empirically Validated: no", "Health: yes",
						"OS: Android", "Prototype: yes", "Quality: OK",
						"Topic: communication",
						"Used SmartDevice Component: compass",
						"Wearable Type: patch", "Wearable Location: head",
						"Target Group: patients")).setYear(2015).getObject(),
				true, true, "has prototype"));
		tagSetCompleteValidTuples.add(new Quadruple<>(
				new LiteratureBuilder(
						getWithTags("Admin: revised", "App: no",
								"Architecture: sensor > backend",
								"Communication: Bluetooth",
								"Empirically Validated: no", "Health: yes",
								"Prototype: yes", "Quality: OK",
								"Topic: communication")).setYear(2015)
						.getObject(), true, true,
				"has prototype, but does not involve smart device"));

	}

	@Test
	public void testCompleteness() throws ServiceException, LogicException {
		for (Quadruple<Literature, Boolean, Boolean, String> row : tagSetCompleteValidTuples) {
			System.out.println("\n\ntesting for completeness: " + row.getD()
					+ " ... ");
			try {
				assertEquals(val.isComplete(row.getA()), row.getB());
			} catch (AssertionError e) {
				System.out.println("FAILED");
				try {
					System.out.println(val.getCompletenessEvaluationTrace());
				} catch (TranslationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				throw new AssertionError(e);
			}
			System.out.println("OK");
		}
	}

	@Test
	public void testValidty() throws ServiceException, LogicException {
		for (Quadruple<Literature, Boolean, Boolean, String> row : tagSetCompleteValidTuples) {
			System.out.println("\n\ntesting for validity: " + row.getD()
					+ " ... ");
			try {
				assertEquals(val.isValid(row.getA()), row.getC());
			} catch (AssertionError e) {
				System.out.println("FAILED");
				try {
					System.out.println(val.getValidityEvaluationTrace());
				} catch (TranslationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				throw new AssertionError(e);
			}
			System.out.println("OK");
		}
	}

	private static Literature getWithTags(String... tagNames)
			throws ServiceException {
		Set<Tag> tags = new HashSet<>(tagNames.length);
		for (String tagName : tagNames)
			tags.add(servMan.getTagService().getByName(tagName));
		return new LiteratureBuilder().setTitle("Literature").setTags(tags)
				.getObject();
	}

}
