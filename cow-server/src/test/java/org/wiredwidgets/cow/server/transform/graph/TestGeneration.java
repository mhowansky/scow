/**
 * Approved for Public Release: 10-4800. Distribution Unlimited.
 * Copyright 2014 The MITRE Corporation,
 * Licensed under the Apache License,
 * Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.wiredwidgets.cow.server.transform.graph;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.spec.bpmn._20100524.model.Definitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wiredwidgets.cow.server.api.model.v2.Activity;
import org.wiredwidgets.cow.server.api.model.v2.Process;
import org.wiredwidgets.cow.server.transform.graph.ActivityEdge;
import org.wiredwidgets.cow.server.transform.graph.bpmn20.Bpmn20ProcessBuilder;
import org.wiredwidgets.cow.server.transform.graph.builder.GraphBuilder;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/jaxb-test-context.xml")
public class TestGeneration {
	
	@Autowired
	Jaxb2Marshaller marshaller;
	
	@Autowired
	Bpmn20ProcessBuilder builder;
	
	@Autowired
	GraphBuilder graphBuilder;
		
	private static Logger log = Logger.getLogger(TestGeneration.class);
	
	@Test
	public void testNewGraph() {
		Process process = unmarshalFromClassPathResource("v2-simple.xml", Process.class);
		DirectedGraph<Activity, ActivityEdge> graph = graphBuilder.buildGraph(process);
		
		
		DepthFirstIterator<Activity, ActivityEdge> it = new DepthFirstIterator<Activity, ActivityEdge>(graph);
		
		while (it.hasNext()) {
			Activity activity = it.next();
			for (ActivityEdge edge : graph.outgoingEdgesOf(activity)) {
				log.info(activity.getName() + "->" + graph.getEdgeTarget(edge).getName());
			}
		}
		
		assertTrue(true);
		
	}
	
	@Test
	public void testSamples() {
		testProcess("user-tasks-only.xml");
		testProcess("parallel-list-test.xml");
		testProcess("csar_high_level.xml");
		testProcess("day_in_life.xml");
		testProcess("Picnic.xml");
		testProcess("bypass.xml");
		testProcess("v2-decision.xml");
		testProcess("exit-test.xml");
		testProcess("decisionTest.xml");
		testProcess("exit-test.xml");
		testProcess("exit-test.xml");
		assertTrue(true);
	}
				
	private void testProcess(String fileName) {
		Process process = unmarshalFromClassPathResource(fileName, Process.class);
		Definitions defs = builder.build(process);
		String xml = marshalToString(defs);
		log.info("xml: " + xml);
	}		
	
	private <T> T unmarshalFromClassPathResource(String resourceName, Class<T> type) {
		try {
			ClassPathResource cpr = new ClassPathResource(resourceName);
			return (T) marshaller.unmarshal(new StreamSource(cpr.getInputStream()));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String marshalToString(Object object) {
		StringWriter sw = new StringWriter();
		marshaller.marshal(object, new StreamResult(sw));
		return sw.toString();
	}

}
