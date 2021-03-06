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

package org.wiredwidgets.cow.server.transform.graph.builder;

import org.springframework.stereotype.Component;
import org.wiredwidgets.cow.server.api.model.v2.Activity;
import org.wiredwidgets.cow.server.api.model.v2.Loop;
import org.wiredwidgets.cow.server.api.model.v2.Process;
import org.wiredwidgets.cow.server.transform.graph.ActivityEdge;
import org.wiredwidgets.cow.server.transform.graph.ActivityGraph;
import org.wiredwidgets.cow.server.transform.graph.activity.DecisionTask;
import org.wiredwidgets.cow.server.transform.graph.activity.ExclusiveGatewayActivity;
import org.wiredwidgets.cow.server.transform.graph.activity.GatewayActivity;

@Component
public class LoopGraphBuilder extends AbstractGraphBuilder<Loop> {
	
	// private static Logger log = LoggerFactory.getLogger(LoopGraphBuilder.class);

	@Override
	protected void buildInternal(Loop loop, ActivityGraph graph, Process process) {
		
		// a loop consists of a sequence of:
		// (1) converging gateway
		// (2) Activity
		// (3) Decision Task (choose repeat or done)
		// (4) Diverging gateway
		
		GatewayActivity converging = new ExclusiveGatewayActivity();
		converging.setDirection(GatewayActivity.CONVERGING);
		converging.setName(getConvergingGatewayName(loop));
		graph.addVertex(converging);
		moveIncomingEdges(graph, loop, converging);
		
		Activity loopActivity = loop.getActivity().getValue();
		graph.addVertex(loopActivity);
		graph.addEdge(converging, loopActivity);
		
		DecisionTask dt = new DecisionTask(loop.getLoopTask());
		// replace the original task with the DecisionTask
		loop.setLoopTask(dt);
		dt.setQuestion(loop.getQuestion());
		dt.addOption(loop.getDoneName());
		dt.addOption(loop.getRepeatName());
		
		graph.addVertex(dt);
		graph.addEdge(loopActivity, dt);
		
		GatewayActivity diverging = new ExclusiveGatewayActivity();
		diverging.setDirection(GatewayActivity.DIVERGING);
		diverging.setName(getDivergingGatewayName(loop));
		graph.addVertex(diverging);
		graph.addEdge(dt, diverging);
		
		// the "done" path
		moveOutgoingEdges(graph, loop, diverging);
		// assume we can have only one outgoing edge
		ActivityEdge doneEdge = graph.outgoingEdgesOf(diverging).iterator().next();
		doneEdge.setExpression(loop.getDoneName());
		doneEdge.setVarSource(dt);
		
		// the "repeat" path
		ActivityEdge repeatEdge = graph.addEdge(diverging, converging);
		repeatEdge.setExpression(loop.getRepeatName());
		repeatEdge.setVarSource(dt);
		
		// build the activity
		factory.buildGraph(loopActivity, graph, process);
		
		graph.removeVertex(loop);
	}

	@Override
	public Class<Loop> getType() {
		return Loop.class;
	}
	
	public static String getDivergingGatewayName(Loop loop) {
		return loop.getName() + ":diverging";
	}
	
	public static String getConvergingGatewayName(Loop loop) {
		return loop.getName() + ":converging";
	}

}
