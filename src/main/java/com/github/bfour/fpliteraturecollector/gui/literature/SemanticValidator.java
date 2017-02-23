package com.github.bfour.fpliteraturecollector.gui.literature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.TagService;
import com.github.bfour.jlib.commons.logic.AndExpression;
import com.github.bfour.jlib.commons.logic.AtomicExpression;
import com.github.bfour.jlib.commons.logic.BooleanExpression;
import com.github.bfour.jlib.commons.logic.ContainsAnyOfExpression;
import com.github.bfour.jlib.commons.logic.ContainsExpression;
import com.github.bfour.jlib.commons.logic.EqualsExpression;
import com.github.bfour.jlib.commons.logic.GreaterThanOrEqualExpression;
import com.github.bfour.jlib.commons.logic.LogicException;
import com.github.bfour.jlib.commons.logic.NotExpression;
import com.github.bfour.jlib.commons.logic.RegularExpression;
import com.github.bfour.jlib.commons.logic.translation.MathTranslator;
import com.github.bfour.jlib.commons.logic.translation.TranslationException;
import com.github.bfour.jlib.commons.services.ServiceException;

public class SemanticValidator {

	private TagService tagServ;

	private BooleanExpression isCompleteExpression;
	private BooleanExpression isValidExpression;

	public BooleanExpression TOO_OLD;
	public BooleanExpression POOR_QUALITY;
	public BooleanExpression INACCESSIBLE;
	public BooleanExpression MAIN_SELECTION;

	private MathTranslator translator;
	private MathTranslator tracingTranslator;

	private Map<String, BooleanExpression> topicMapForCompleteness = new HashMap<>();
	private Map<String, BooleanExpression> topicMapForValidity = new HashMap<>();

	public SemanticValidator(ServiceManager servMan, boolean useHTML) {

		this.translator = new MathTranslator(false, false);
		this.tracingTranslator = new MathTranslator(true, useHTML);

		this.tagServ = servMan.getTagService();
		isCompleteExpression = new AtomicExpression(true);

		try {

			// ==================
			// === SHORTHANDS ===
			// ==================

			TOO_OLD = new GreaterThanOrEqualExpression("year", 2014).negate()
					.andNot(new EqualsExpression("year", null));
			POOR_QUALITY = hasTag("Quality: OK").negate().and(
					hasAnyOfPrefixedTags("Quality:"));
			INACCESSIBLE = hasTag("Access: inaccessible");
			MAIN_SELECTION = TOO_OLD
					.negate()
					.andNot(POOR_QUALITY)
					.andNot(INACCESSIBLE)
					.and(hasTag("Health: yes"))
					.and(hasTag("App: yes"))
					.andNot(hasTag("Type: concept"))
					.andNot(hasTag("Type: summary/overview"))
					.and(hasAnyOfTags(
							"Architecture: mesh",
							"Architecture: smart device",
							"Architecture: smart device <> backend",
							"Architecture: wearable <> backend <> smart device",
							"Architecture: wearable <> smart device",
							"Architecture: wearable <> smart device <> backend",
							"Architecture: wearable <> smart device <> backend <> static device",
							"Architecture: wearable <> smart device/backend <> backend"));

			// ====================
			// === COMPLETENESS ===
			// ====================
			topicMapForCompleteness.put("Year", new NotExpression(
					new EqualsExpression("year", null)));
			topicMapForCompleteness.put("Quality",
					hasAnyOfPrefixedTags("Quality"));
			// access tag not required (assuming completeness based on other
			// tags implies sufficient accessibility)
			topicMapForCompleteness.put("Access", new AtomicExpression(true));
			topicMapForCompleteness.put("Topic", hasAnyOfPrefixedTags("Topic"));
			topicMapForCompleteness.put("App", hasAnyOfPrefixedTags("App"));
			topicMapForCompleteness.put("Health",
					hasAnyOfPrefixedTags("Health"));
			topicMapForCompleteness
					.put("Prototype",
							hasAnyOfPrefixedTags("Prototype")
									.and(hasTag("Prototype: yes")
											.and(hasTag("App: yes"))
											.implies(
													new AndExpression(
															hasAnyOfPrefixedTags("Architecture"),
															hasAnyOfPrefixedTags("Communication"),
															hasAnyOfPrefixedTags("Device"),
															hasAnyOfPrefixedTags("OS"),
															hasAnyOfPrefixedTags("Empirically Validated"))
															.or(hasTag("Architecture: static device"))
															.or(hasTag("Architecture: wearable <> backend"))
															.or(hasTag("Architecture: wearable device"))
															.or(hasTag("Architecture: wearable device > backend")))));

			// add all completeness-expressions to main completeness expression
			AndExpression andExpr = new AndExpression();
			for (BooleanExpression expr : topicMapForCompleteness.values())
				andExpr.addExpression(expr);
			isCompleteExpression = new AndExpression().addExpression(
					isCompleteExpression).addExpression(andExpr);

			// @formatter:off
			// additional clauses
			isCompleteExpression = (isCompleteExpression
					// is complete if too old (excluded)
					.or(new GreaterThanOrEqualExpression("year", 2014).negate().
							andNot(new EqualsExpression("year", null)))
					// is a summary/overview/secondary literature or merely a concept
					.orContains("tags", tagServ.getByName("Type: summary/overview"))
					.orContains("tags", tagServ.getByName("Type: concept"))
					// is complete if off-topic (excluded)
					.orContains("tags", tagServ.getByName("Topic: off-topic"))
					.orContains("tags", tagServ.getByName("Topic: sports"))
					// is complete if inaccessible (excluded)
					.orContains("tags", tagServ.getByName("Access: inaccessible"))
					// is complete if non-OK quality (excluded)
					.or(hasTag("Quality: OK").negate().
							andContainsAnyOf("tags", tagServ.getByPrefix("Quality")))
					// exclude certain source types
					.or(new EqualsExpression("type", LiteratureType.BOOK))
					.or(new EqualsExpression("type", LiteratureType.BACHELORS_THESIS))
					.or(new EqualsExpression("type", LiteratureType.MASTERS_THESIS))
					.or(new EqualsExpression("type", LiteratureType.PRESENTATION))
					.or(new EqualsExpression("type", LiteratureType.PATENT))
					)
					// must have final revision tag in any case
					.andContains("tags", tagServ.getByName("Admin: revised"));
			
			// items of main selection (included in quantitative study) must have completed certain taggings
			isCompleteExpression = isCompleteExpression.and(
					MAIN_SELECTION.implies(
							hasAnyOfPrefixedTags("Topic: ")
							.and(hasAnyOfPrefixedTags("Architecture: "))
							.and(hasAnyOfPrefixedTags("Communication: "))
							.and(hasAnyOfPrefixedTags("OS: "))
							.and(hasAnyOfPrefixedTags("Device: "))
							.and(hasAnyOfPrefixedTags("Used SmartDevice Component: "))
							.and(hasAnyOfTagsWithRegexMatch("Architecture:.*(wearable).*").implies(
									hasAnyOfPrefixedTags("Wearable Type: ")
									.and(hasAnyOfPrefixedTags("Wearable Location: "))))
							.and(hasAnyOfPrefixedTags("Target Group: "))
							.and(hasTag("Empirically Validated: yes")
									.implies(hasAnyOfPrefixedTags("Evaluation Method:")))
							));
			
			// @formatter:on

			// ================
			// === VALIDITY ===
			// ================

			// Quality OK -> !Quality bad
			// @formatter:off
			Set<Tag> nonQualityOKTags = tagServ.getByPrefix("Quality");
			nonQualityOKTags.remove(tagServ.getByName("Quality: OK"));
			
			Set<Tag> nonAccessibleTags = tagServ.getByPrefix("Access");
			nonAccessibleTags.remove(tagServ.getByName("Access: OK"));
			
			isValidExpression = new AndExpression()
				// quality OK -> ! quality not OK
				.and(hasTag("Quality: OK")
					.impliesNot(new ContainsAnyOfExpression("tags", nonQualityOKTags)))
				// accessible -> !inaccessible
				.and(hasTag("Access: OK")
					.impliesNot(new ContainsAnyOfExpression("tags", nonAccessibleTags)))
				// topic defined -> !off-topic
				.and(hasTag("Topic: off-topic")
					.impliesNot(hasTag("Health: yes")
							.and(hasAnyOfTags("Topic: communication", 
											 "Topic: UX", 
											 "Topic: SW architecture"))))
				// cannot be a prototype and not a prototype
				.and(hasTag("Prototype: no")
					.impliesNot(hasTag("Prototype: yes")))
				// cannot present app and not present app
				.and(hasTag("App: no")
					.impliesNot(hasTag("App: yes")))
				// cannot be about health but not about health
				.and(hasTag("Health: no")
					.impliesNot(hasTag("Health: yes")))	
				// cannot be empirically validated and not validated
				.and(hasTag("Empirically Validated: no")
					.impliesNot(hasTag("Empirically Validated: yes")));
				// cannot have attribute of prototype if no prototype presented
//				.and(hasTag("Prototype: no")
//					.impliesNot(new OrExpression(
//							hasAnyOfPrefixedTags("Empirically Validated"),
//							hasAnyOfPrefixedTags("Architecture"),
//							hasAnyOfPrefixedTags("Communication"),
//							hasAnyOfPrefixedTags("Device"),
//							hasAnyOfPrefixedTags("OS"))));
			
			// @formatter:on

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ContainsExpression hasTag(String tagName) throws ServiceException {
		return new ContainsExpression("tags", tagServ.getByName(tagName));
	}

	public ContainsAnyOfExpression hasAnyOfPrefixedTags(String prefix)
			throws ServiceException {
		return new ContainsAnyOfExpression("tags", tagServ.getByPrefix(prefix));
	}

	public ContainsAnyOfExpression hasAnyOfTagsWithRegexMatch(
			String regexPattern) throws ServiceException {
		return new ContainsAnyOfExpression("tags",
				tagServ.getByRegex(regexPattern));
	}

	public ContainsAnyOfExpression hasAnyOfTags(String... tagNames)
			throws ServiceException {
		Set<Tag> tags = new HashSet<Tag>(tagNames.length);
		for (String tagName : tagNames) {
			tags.add(tagServ.getByName(tagName));
		}
		return new ContainsAnyOfExpression("tags", tags);
	}

	public boolean isComplete(Literature lit) throws LogicException {
		isCompleteExpression.setVariables(lit.getSearchData());
		return isCompleteExpression.evaluate();
	}

	public boolean isValid(Literature lit) throws LogicException {
		isValidExpression.setVariables(lit.getSearchData());
		return isValidExpression.evaluate();
	}

	/**
	 * Evaluate completeness by topic (eg. quality, accessibility ...)
	 * 
	 * @param topic
	 * @param lit
	 * @return
	 * @throws LogicException
	 */
	public boolean isTopicComplete(String topic, Literature lit)
			throws LogicException {
		BooleanExpression expr = topicMapForCompleteness.get(topic);
		expr.setVariables(lit.getSearchData());
		return expr.evaluate();
	}

	/**
	 * Evaluate validity by topic (eg. quality, accessibility ...)
	 * 
	 * @param lit
	 * @return
	 * @throws LogicException
	 */
	public boolean isTopicValid(String topic, Literature lit)
			throws LogicException {
		BooleanExpression expr = topicMapForValidity.get(topic);
		expr.setVariables(lit.getSearchData());
		return expr.evaluate();
	}

	public String getCompletenessEvaluationTrace() throws TranslationException {
		return "<html>" + isCompleteExpression.translate(tracingTranslator)
				+ "</html>";
	}

	public String getValidityEvaluationTrace() throws TranslationException {
		return "<html>" + isValidExpression.translate(tracingTranslator)
				+ "</html>";
	}

	public void print() {
		try {
			System.out.println("====================");
			System.out.println("=== COMPLETENESS ===");
			System.out.println("====================");
			System.out.println(isCompleteExpression.translate(translator));
			System.out.println("");
			System.out.println("================");
			System.out.println("=== VALIDITY ===");
			System.out.println("================");
			System.out.println(isValidExpression.translate(translator));
		} catch (TranslationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
