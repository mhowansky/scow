package org.wiredwidgets.cow.server.transform.graph.builder;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.wiredwidgets.cow.server.api.model.v2.Activities;
import org.wiredwidgets.cow.server.api.model.v2.Activity;
import org.wiredwidgets.cow.server.api.model.v2.Decision;
import org.wiredwidgets.cow.server.api.model.v2.Option;
import org.wiredwidgets.cow.server.transform.graph.ActivityEdge;
import org.wiredwidgets.cow.server.transform.graph.ActivityGraph;
import org.wiredwidgets.cow.server.transform.graph.activity.DecisionTask;
import org.wiredwidgets.cow.server.transform.graph.activity.ExclusiveGatewayActivity;
import org.wiredwidgets.cow.server.transform.graph.activity.GatewayActivity;

@Component
public class DecisionGraphBuilder extends AbstractGraphBuilder<Decision> {
	
	private static Logger log = Logger.getLogger(DecisionGraphBuilder.class);

	@Override
	public boolean buildGraph(Decision decision, ActivityGraph graph) {
		
		DecisionTask dt = new DecisionTask(decision.getTask());
		graph.addVertex(dt);
		moveIncomingEdges(graph, decision, dt);
		
		GatewayActivity diverging = new ExclusiveGatewayActivity();
		diverging.setDirection(GatewayActivity.DIVERGING);
		diverging.setName("diverging");
		graph.addVertex(diverging);
		graph.addEdge(dt, diverging);
		
		GatewayActivity converging = new ExclusiveGatewayActivity();
		converging.setDirection(GatewayActivity.CONVERGING);
		converging.setName("converging");
		graph.addVertex(converging);
		
		moveOutgoingEdges(graph, decision, converging);
		
		for (Option option : decision.getOptions()) {
			
			ActivityEdge optionEdge = null;
			if (option.getActivity() != null) {
				Activity optionActivity = option.getActivity().getValue();
				graph.addVertex(optionActivity);
				optionEdge = graph.addEdge(diverging, optionActivity);
				
				// tie edge back to the DecisionTask
				// this is used for variable name handling
				optionEdge.setVarSource(dt);
				
				graph.addEdge(optionActivity, converging);
			}
			else {
				// Provide support for a "do nothing" path directly from diverging to converging
				optionEdge = graph.addEdge(diverging, converging);
				optionEdge.setVarSource(dt);
			}
			
			// attach the option name to the outbound edge
			optionEdge.setExpression(option.getName());
			
			// we will need the options as inputs to the task
			dt.addOption(option.getName());
		}

		graph.removeVertex(decision);
		return true;
	}

	@Override
	public Class<Decision> getType() {
		return Decision.class;
	}

}