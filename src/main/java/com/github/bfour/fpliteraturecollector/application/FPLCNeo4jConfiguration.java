package com.github.bfour.fpliteraturecollector.application;

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


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.core.TypeRepresentationStrategy;
import org.springframework.data.neo4j.support.typerepresentation.NoopRelationshipTypeRepresentationStrategy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.bfour.fpliteraturecollector.service.converters.ColorToStringConverter;
import com.github.bfour.fpliteraturecollector.service.converters.CrawlerToStringConverter;
import com.github.bfour.fpliteraturecollector.service.converters.PathToStringConverter;
import com.github.bfour.fpliteraturecollector.service.converters.StringToColorConverter;
import com.github.bfour.fpliteraturecollector.service.converters.StringToCrawlerConverter;
import com.github.bfour.fpliteraturecollector.service.converters.StringToPathConverter;

// tag::config[]
@EnableTransactionManagement
//@Import(RepositoryRestMvcConfiguration.class)
@EnableScheduling
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.github.bfour.fpliteraturecollector"})
@Configuration
@EnableNeo4jRepositories(basePackages = "com.github.bfour.fpliteraturecollector")
public class FPLCNeo4jConfiguration extends Neo4jConfiguration {
    public FPLCNeo4jConfiguration() {
        setBasePackage("com.github.bfour.fpliteraturecollector.domain");
    }

    @Bean
    public GraphDatabaseService graphDatabaseService() {
    	return new GraphDatabaseFactory().newEmbeddedDatabase("testDB");
    }

    @Override
    public TypeRepresentationStrategy<Relationship> relationshipTypeRepresentationStrategy() throws Exception {
        return new NoopRelationshipTypeRepresentationStrategy();
    }
    
    @Bean
    protected ConversionService neo4jConversionService() throws Exception {
        ConversionService conversionService = super.neo4jConversionService();
        ConverterRegistry registry = (ConverterRegistry) conversionService;
        registry.addConverter(new StringToCrawlerConverter());
        registry.addConverter(new CrawlerToStringConverter());
        registry.addConverter(new StringToPathConverter());
        registry.addConverter(new PathToStringConverter());
        registry.addConverter(new StringToColorConverter());
        registry.addConverter(new ColorToStringConverter());
        return conversionService;
    }
    
}	