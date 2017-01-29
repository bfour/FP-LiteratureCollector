package com.github.bfour.fpliteraturecollector.gui.literature;

import java.util.HashMap;
import java.util.Map;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.TagService;
import com.github.bfour.jlib.commons.services.ServiceException;
import com.github.bfour.jlib.search.SearchException;
import com.github.bfour.jlib.search.lang.AndExpression;
import com.github.bfour.jlib.search.lang.AtomicExpression;
import com.github.bfour.jlib.search.lang.BooleanExpression;
import com.github.bfour.jlib.search.lang.ContainsExpression;
import com.github.bfour.jlib.search.lang.EqualsExpression;
import com.github.bfour.jlib.search.lang.ImpliesExpression;
import com.github.bfour.jlib.search.lang.LessThanOrEqualExpression;
import com.github.bfour.jlib.search.lang.NotExpression;
import com.github.bfour.jlib.search.lang.OrExpression;

public class SemanticValidator {

	private static SemanticValidator instance;

	private BooleanExpression isCompleteExpression;
	private AndExpression isValidExpression;

	private Map<String, BooleanExpression> topicMapForCompleteness = new HashMap<>();
	private Map<String, BooleanExpression> topicMapForValidity = new HashMap<>();

	private SemanticValidator(ServiceManager servMan) {

		TagService tagServ = servMan.getTagService();
		isCompleteExpression = new AtomicExpression(true);
		isValidExpression = new AndExpression();

		try {

			// ====================
			// === COMPLETENESS ===
			// ====================
			topicMapForCompleteness.put("Year", new NotExpression(
					new EqualsExpression("year", null)));
			topicMapForCompleteness.put("Quality", new ContainsExpression(
					"tags", tagServ.getByPrefix("Quality")));
			topicMapForCompleteness.put("Access", new ContainsExpression(
					"tags", tagServ.getByPrefix("Access")));
			topicMapForCompleteness.put("Topic", new ContainsExpression("tags",
					tagServ.getByPrefix("Topic")));
			topicMapForCompleteness.put("App", new ContainsExpression("tags",
					tagServ.getByPrefix("App")));

			// add all completeness-expressions to main completeness expression
			AndExpression andExpr = new AndExpression();
			for (BooleanExpression expr : topicMapForCompleteness.values())
				andExpr.addExpression(expr);
			isCompleteExpression = new AndExpression().addExpression(
					isCompleteExpression).addExpression(andExpr);

			// add further rules to completeness
			isCompleteExpression = new OrExpression()
					.addExpression(new EqualsExpression("year", null))
					.addExpression(new LessThanOrEqualExpression("year", 2013))
					.addExpression(
							new AndExpression()
									.addExpression(
											new NotExpression(
													new ContainsExpression(
															"tags",
															tagServ.getByName("Quality: OK"))))
									.addExpression(
											new ContainsExpression(
													"tags",
													tagServ.getByPrefix("Quality"))))
					.addExpression(
							new ContainsExpression("tags", tagServ
									.getByName("Topic: off-topic")))
					.addExpression(
							new ContainsExpression("tags", tagServ
									.getByName("Access: inaccessible")))
					.addExpression(isCompleteExpression);

			// ================
			// === VALIDITY ===
			// ================

			// Quality OK -> !Quality bad
			isValidExpression.addExpression(new ImpliesExpression(
					new ContainsExpression("tags", tagServ
							.getByName("Quality: OK")), new NotExpression(
							new ContainsExpression("tags", tagServ
									.getByName("Quality: illegal language")))));
			isValidExpression.addExpression(new ImpliesExpression(
					new ContainsExpression("tags", tagServ
							.getByName("Quality: OK")),
					new NotExpression(new ContainsExpression("tags", tagServ
							.getByName("Quality: inappropriate format")))));
			isValidExpression
					.addExpression(new ImpliesExpression(
							new ContainsExpression("tags", tagServ
									.getByName("Quality: OK")),
							new NotExpression(
									new ContainsExpression(
											"tags",
											tagServ.getByName("Quality: insufficient language proficiency")))));
			isValidExpression.addExpression(new ImpliesExpression(
					new ContainsExpression("tags", tagServ
							.getByName("Quality: OK")), new NotExpression(
							new ContainsExpression("tags", tagServ
									.getByName("Quality: non-scientific")))));
			isValidExpression.addExpression(new ImpliesExpression(
					new ContainsExpression("tags", tagServ
							.getByName("Quality: OK")), new NotExpression(
							new ContainsExpression("tags", tagServ
									.getByName("Quality: poor")))));

			// accessible -> !inaccessible
			isValidExpression.addExpression(new ImpliesExpression(
					new ContainsExpression("tags", tagServ
							.getByName("Access: OK")), new NotExpression(
							new ContainsExpression("tags", tagServ
									.getByName("Access: inacessible")))));

			// topic defined -> !off-topic
			isValidExpression.addExpression(new ImpliesExpression(
					new ContainsExpression("tags", tagServ
							.getByName("Topic: C")), new NotExpression(
							new ContainsExpression("tags", tagServ
									.getByName("Access: inacessible")))));

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

	public boolean isComplete(Literature lit) throws SearchException {
		isCompleteExpression.setVariables(lit.getSearchData());
		return isCompleteExpression.evaluate();
	}

	public boolean isValid(Literature lit) throws SearchException {
		isValidExpression.setVariables(lit.getSearchData());
		return isValidExpression.evaluate();
	}

	/**
	 * Evaluate completeness by topic (eg. quality, accessibility ...)
	 * 
	 * @param topic
	 * @param lit
	 * @return
	 * @throws SearchException
	 */
	public boolean isTopicComplete(String topic, Literature lit)
			throws SearchException {
		BooleanExpression expr = topicMapForCompleteness.get(topic);
		expr.setVariables(lit.getSearchData());
		return expr.evaluate();
	}

	/**
	 * Evaluate validity by topic (eg. quality, accessibility ...)
	 * 
	 * @param lit
	 * @return
	 * @throws SearchException
	 */
	public boolean isTopicValid(String topic, Literature lit)
			throws SearchException {
		BooleanExpression expr = topicMapForValidity.get(topic);
		expr.setVariables(lit.getSearchData());
		return expr.evaluate();
	}

}
