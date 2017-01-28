package com.github.bfour.fpliteraturecollector.gui.literature;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjsearch.SearchException;
import com.github.bfour.fpjsearch.fpjsearch.AndExpression;
import com.github.bfour.fpjsearch.fpjsearch.ContainsExpression;
import com.github.bfour.fpjsearch.fpjsearch.ImpliesExpression;
import com.github.bfour.fpjsearch.fpjsearch.NotExpression;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.TagService;

public class SemanticValidator {
	
	private static SemanticValidator instance;
	private AndExpression isCompleteExpression;
	private AndExpression isValidExpression;

	private SemanticValidator(ServiceManager servMan) {

		isCompleteExpression = new AndExpression();
		isValidExpression = new AndExpression();
		TagService tagServ = servMan.getTagService();

		// quality
		try {

			// quality tag must be set
			isCompleteExpression.addExpression(new ContainsExpression("tags",
					tagServ.getByPrefix("Quality")));

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

			// access tag must be set
			isCompleteExpression.addExpression(new ContainsExpression("tags",
					tagServ.getByPrefix("Access")));

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static SemanticValidator getInstance(ServiceManager servMan) {
		if (instance == null) instance = new SemanticValidator(servMan);
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
	
}
