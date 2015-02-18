package com.github.bfour.fpliteraturecollector.service.database;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class FPLCOrientDBGraphService extends AbstractOrientDBGraphService {

	private static FPLCOrientDBGraphService instance;
	
	private FPLCOrientDBGraphService() {
	}
	
	public static FPLCOrientDBGraphService getInstance() {
		if (instance == null) instance = new FPLCOrientDBGraphService();
		return instance;
	}
	
	@Override
	protected void setupSchema(String URL, String user, String password) {

		// TODO finish

		// open as non-transactional
		OrientGraphNoTx db = new OrientGraphNoTx(URL, user, password);

		// setup counters as workaround for sequence
		OrientVertexType counterClass = db.createVertexType("counters");
		counterClass.createProperty("name", OType.STRING);
		counterClass.createProperty("value", OType.LONG);

		db.createKeyIndex("name", Vertex.class, new Parameter<>("type",
				"UNIQUE"), new Parameter<>("class", "counters"));

		// setup literature
		OrientVertexType literatureClass = db.createVertexType("literature");
		literatureClass.createProperty("ID", OType.LONG);
		literatureClass.createProperty("title", OType.STRING);
		literatureClass.createProperty("persons", OType.LINKLIST);
		literatureClass.createProperty("registrationTime", OType.DATETIME);
		literatureClass.createProperty("lastChangeTime", OType.DATETIME);

		db.createKeyIndex("ID", Vertex.class,
				new Parameter<>("type", "UNIQUE"), new Parameter<>("class",
						"literature"));
		db.addVertex("class:counters", "name", "literature", "value", 0);

		// setup persons
		OrientVertexType personClass = db.createVertexType("person");
		personClass.createProperty("ID", OType.LONG);
		personClass.createProperty("firstName", OType.STRING);
		personClass.createProperty("lastName", OType.STRING);

		db.createKeyIndex("ID", Vertex.class,
				new Parameter<>("type", "UNIQUE"), new Parameter<>("class",
						"person"));
		db.addVertex("class:counters", "name", "person", "value", 0);

		// end
		db.shutdown();

	}
}
