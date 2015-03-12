package com.github.bfour.fpliteraturecollector.service.database.DAO;

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


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.github.bfour.fpjcommons.model.Entity;
import com.github.bfour.fpjcommons.services.DatalayerException;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class GraphUtils {

	public static <T extends Entity> void setCollectionPropertyOnVertex(
			Vertex vertex, String edgeName, Collection<T> collectionToBeSet,
			AbstractOrientDBDAO<T> collectionItemDAO) throws DatalayerException {

		// get current items of collection in vertex
		List<T> vertexItems = new LinkedList<T>();
		Iterable<Edge> vertexItemEdges = vertex.getEdges(Direction.OUT,
				edgeName);
		for (Edge vertexItemEdge : vertexItemEdges) {
			vertexItems.add(collectionItemDAO.vertexToEntity(vertexItemEdge
					.getVertex(Direction.IN)));
		}

		// determine items to remove and add in vertex
		List<T> itemsToRemove = new LinkedList<>();
		for (T vertexItem : vertexItems) {
			if (!collectionToBeSet.contains(vertexItem))
				itemsToRemove.add(vertexItem);
		}
		List<T> itemsToAdd = new LinkedList<>();
		for (T collectionToBeSetItem : collectionToBeSet) {
			if (!vertexItems.contains(collectionToBeSetItem))
				itemsToAdd.add(collectionToBeSetItem);
		}

		// remove and add edges adjacent to vertex
		vertexItemEdges = vertex.getEdges(Direction.OUT, edgeName);
		for (Edge vertexItemEdge : vertexItemEdges) {
			T p = collectionItemDAO.vertexToEntity(vertexItemEdge
					.getVertex(Direction.IN));
			if (itemsToRemove.contains(p))
				vertexItemEdge.remove();
		}
		for (T itemToAdd : itemsToAdd) {
			Vertex itemVertex = collectionItemDAO.getVertexForEntity(itemToAdd);
			vertex.addEdge(edgeName, itemVertex);
		}

	}

	public static <T extends Entity> List<T> getCollectionFromVertexProperty(
			Vertex v, String edgeName, AbstractOrientDBDAO<T> DAO) throws DatalayerException
			 {
		
		List<T> items = new LinkedList<>();
		Iterable<Edge> edgeIter = v.getEdges(Direction.OUT, edgeName);
		for (Edge itemEdge : edgeIter) {
			Vertex itemVertex = itemEdge.getVertex(Direction.IN);
			T item = DAO.vertexToEntity(itemVertex);
			items.add(item);
		}
		return items;
		
	}

}