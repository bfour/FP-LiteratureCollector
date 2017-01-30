package com.github.bfour.fpliteraturecollector.gui.literature;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.TagService;
import com.github.bfour.jlib.commons.logic.AndExpression;
import com.github.bfour.jlib.commons.logic.AtomicExpression;
import com.github.bfour.jlib.commons.logic.BooleanExpression;
import com.github.bfour.jlib.commons.logic.ContainsAnyOfExpression;
import com.github.bfour.jlib.commons.logic.ContainsExpression;
import com.github.bfour.jlib.commons.logic.EqualsExpression;
import com.github.bfour.jlib.commons.logic.LogicException;
import com.github.bfour.jlib.commons.logic.NotExpression;
import com.github.bfour.jlib.commons.logic.translation.MathTranslator;
import com.github.bfour.jlib.commons.logic.translation.TranslationException;
import com.github.bfour.jlib.commons.services.ServiceException;

public class SemanticValidator {

	private static SemanticValidator instance;

	private BooleanExpression isCompleteExpression;
	private BooleanExpression isValidExpression;

	private Map<String, BooleanExpression> topicMapForCompleteness = new HashMap<>();
	private Map<String, BooleanExpression> topicMapForValidity = new HashMap<>();

	private SemanticValidator(ServiceManager servMan) {

		TagService tagServ = servMan.getTagService();
		isCompleteExpression = new AtomicExpression(true);

		try {

			// ====================
			// === COMPLETENESS ===
			// ====================
			topicMapForCompleteness.put("Year", new NotExpression(
					new EqualsExpression("year", null)));
			topicMapForCompleteness.put("Quality", new ContainsAnyOfExpression(
					"tags", tagServ.getByPrefix("Quality")));
			topicMapForCompleteness.put("Access", new ContainsAnyOfExpression(
					"tags", tagServ.getByPrefix("Access")));
			topicMapForCompleteness.put("Topic", new ContainsAnyOfExpression(
					"tags", tagServ.getByPrefix("Topic")));
			topicMapForCompleteness.put("App", new ContainsAnyOfExpression(
					"tags", tagServ.getByPrefix("App")));

			// add all completeness-expressions to main completeness expression
			AndExpression andExpr = new AndExpression();
			for (BooleanExpression expr : topicMapForCompleteness.values())
				andExpr.addExpression(expr);
			isCompleteExpression = new AndExpression().addExpression(
					isCompleteExpression).addExpression(andExpr);

			// add further rules to completeness
			// @formatter:off
			isCompleteExpression = isCompleteExpression
					.orContains("tags", tagServ.getByName("Topic: off-topic"))
					.orContains("tags", tagServ.getByName("Access: inaccessible"))
					.or(new ContainsExpression("tags", tagServ.getByName("Quality: OK")).negate().
							andContainsAnyOf("tags", tagServ.getByPrefix("Quality")));
			
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
			
			Set<Tag> nonOfftopicTags = tagServ.getByPrefix("Topic");
			nonOfftopicTags.remove(tagServ.getByName("Topic: off-topic"));
			
			isValidExpression = new AndExpression()
				.and(new ContainsExpression("tags", tagServ.getByName("Quality: OK"))
					.impliesNot(new ContainsAnyOfExpression("tags", nonQualityOKTags)))
				// accessible -> !inaccessible
				.and(new ContainsExpression("tags", tagServ.getByName("Access: OK"))
					.impliesNot(new ContainsAnyOfExpression("tags", nonQualityOKTags)))
				// topic defined -> !off-topic
				.and(new ContainsExpression("tags", tagServ.getByName("Topic: off-topic"))
					.impliesNot(new ContainsAnyOfExpression("tags", nonOfftopicTags)));
					
			// @formatter:on

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static SemanticValidator getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new SemanticValidator(servMan);
		return instance;
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
	public boolean isTopicValid(String topic, Literature lit) throws LogicException {
		BooleanExpression expr = topicMapForValidity.get(topic);
		expr.setVariables(lit.getSearchData());
		return expr.evaluate();
	}

	private MathTranslator translator = new MathTranslator();

	public void print() {
		try {
			System.out.println(isCompleteExpression.translate(translator));
			System.out.println(isValidExpression.translate(translator));
		} catch (TranslationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
