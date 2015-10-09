package com.github.bfour.fpliteraturecollector.test;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
 * =================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -///////////////////////////////-
 */


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.service.AuthorService;

public class TestDataCreator {

	public TestDataCreator() {
		// TODO Auto-generated constructor stub
	}

	public static List<Author> createAuthorList1() {
		List<Author> authorList = new LinkedList<Author>();
		authorList.add(new Author("Tapio", "M.", "Saari", null, null));
		authorList.add(new Author("T.", "M.", "Saari", null, null));
		authorList.add(new Author("", "", "Saari", null, null));
		authorList.add(new Author("Ilmari", "", "Sinivuokko", null, null));
		authorList.add(new Author("Friðrik Reetta", "", "Wuopio", null, null));
		authorList.add(new Author("Áki Brynhildur", "", "Jokela", null, null));
		authorList.add(new Author("Alan", "", "Turing", null, null));
		authorList.add(new Author("藤本", "", "雄大", null, null));
		authorList.add(new Author("D", "", "Giansanti", "9VI8yUIAAAAJ", null));
		authorList.add(new Author("Mojtaba", "", "Ghadiri", null, "47198206"));
		return authorList;
	}

	public static List<Literature> createLiteratureList1(AuthorService authServ)
			throws ServiceException {

		List<Author> authorList1 = new ArrayList<>(2);
		authorList1.add(new Author("Bob", "", "Sponge", null, null));
		authorList1.add(new Author("Patrick", "", "Star", null, null));
		for (Author person : authorList1)
			authorList1.set(authorList1.indexOf(person),
					authServ.create(person));

		List<Author> authorList2 = new ArrayList<>(1);
		authorList2.add(new Author("Mariela", "", "Castro-Espín", null, null));
		for (Author person : authorList2)
			authorList2.set(authorList2.indexOf(person),
					authServ.create(person));

		List<Author> authorList3 = new ArrayList<>(5);
		authorList3.add(new Author("Mariela", "", "Castro Espín", null, null));
		authorList3.add(new Author("Raúl", "", "Castro", null, null));
		authorList3.add(new Author("Fidel", "", "Castro", null, null));
		authorList3
				.add(new Author("José", "Alberto", "Mujica Cordano", null, null));
		authorList3.add(new Author("Cristina", "Fernández", "de Kirchner", null,
				null));
		for (Author person : authorList3)
			authorList3.set(authorList3.indexOf(person),
					authServ.create(person));

		List<Author> authorList4 = new ArrayList<>(0);
		for (Author person : authorList4)
			authorList4.set(authorList4.indexOf(person),
					authServ.create(person));

		List<Literature> literatureList = new LinkedList<Literature>();

		LiteratureBuilder litBuilder = new LiteratureBuilder();
		litBuilder.setTitle("Psychological Disorders in Bikini Bottom");
		litBuilder.setAuthors(new HashSet<>(authorList1));
		literatureList.add(litBuilder.getObject());

		LiteratureBuilder litBuilder2 = new LiteratureBuilder();
		litBuilder2.setTitle("Mariette Pathy Allen: Transcuba");
		litBuilder2.setAuthors(new HashSet<>(authorList2));
		litBuilder2.setISBN(new ISBN("978-0988983137"));
		literatureList.add(litBuilder2.getObject());

		LiteratureBuilder litBuilder3 = new LiteratureBuilder();
		litBuilder3
				.setTitle("LDL Receptor-Related Protein 5 (LRP5) Affects Bone Accrual and Eye Development");
		litBuilder3.setAuthors(new HashSet<>(authorList4));
		litBuilder3.setDOI("10.1016/S0092-8674(01)00571-2");
		literatureList.add(litBuilder3.getObject());

		return literatureList;

	}

}
