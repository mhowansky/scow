package org.wiredwidgets.cow.server.transform.graph.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.wiredwidgets.cow.server.api.model.v2.Activity;
import org.wiredwidgets.cow.server.api.model.v2.Process;
import org.wiredwidgets.cow.server.transform.graph.ActivityEdge;
import org.wiredwidgets.cow.server.transform.graph.ActivityGraph;

public abstract class AbstractGraphBuilder<T extends Activity> implements ActivityGraphBuilder<T> {
	
	@Autowired
	ActivityGraphBuilderFactory factory;
	
	private static Logger log = Logger.getLogger(AbstractGraphBuilder.class);
	
	/**
	 * Return false by default.  This is called only in case there is more
	 * than one builder for a given Activity type.  In that case the builders
	 * should implement Supports() to determine which one should be chosen
	 */
	@Override
	public boolean supports(Activity activity) {
		return false;
	}
	
	/**
	 * Most subclasses do not need the Process argument, so the default
	 * implementation simply calls the buildGraph without a Process argument.
	 * Subclasses can override this method if they need a Process as input
	 */
	@Override
	public boolean buildGraph(T activity, ActivityGraph graph, Process process) {
		return buildGraph(activity, graph);
	}
	
	
	/**
	 * Most subclasses do not need a Process argument so will implement this
	 * method rather than the one that includes a Process
	 * @param activity
	 * @param graph
	 * @return
	 */
	public abstract boolean buildGraph(T activity, ActivityGraph graph);
	
	public abstract Class<T> getType();
	
//	protected void build(Activity activity, DirectedGraph<Activity, ActivityEdge> graph) {
//		ActivityGraphBuilder builder = factory.getBuilder(activity);
//		log.info("activity: " + activity.getClass().getSimpleName());
//		log.info("factory: " + builder.getClass().getSimpleName());
//		builder.buildGraph(activity, graph);
//	}
	
	/**
	 * Move incoming edges from one vertex to another
	 * @param graph
	 * @param sourceVertex
	 * @param targetVertex
	 */
	protected void moveIncomingEdges(ActivityGraph graph, Activity sourceVertex, Activity targetVertex) {
		// build a new collection to iterate so we can remove edges
		List<ActivityEdge> edges = new ArrayList<ActivityEdge>();
		edges.addAll(graph.incomingEdgesOf(sourceVertex));
		
		for (ActivityEdge edge : edges) {
			Activity source = graph.getEdgeSource(edge);
			log.debug("adding edge from " + source.getClass().getSimpleName() + " to " + targetVertex.getClass().getSimpleName());
			
			ActivityEdge newEdge = graph.addEdge(source, targetVertex);
			newEdge.setExpression(edge.getExpression());
			newEdge.setVarSource(edge.getVarSource());
			
			graph.removeEdge(source, sourceVertex);
		}	
	}
	
	/**
	 * Move outgoing edges from one vertex to another
	 * @param graph
	 * @param sourceVertex
	 * @param targetVertex
	 */
	protected void moveOutgoingEdges(ActivityGraph graph, Activity sourceVertex, Activity targetVertex) {
		
		// build a new collection to iterate so we can remove edges
		List<ActivityEdge> edges = new ArrayList<ActivityEdge>();
		edges.addAll(graph.outgoingEdgesOf(sourceVertex));		
		
		for (ActivityEdge edge : edges) {
			Activity target = graph.getEdgeTarget(edge);
			log.debug("adding edge from " + targetVertex.getClass().getSimpleName() + " to " + target.getClass().getSimpleName());
			
			ActivityEdge newEdge = graph.addEdge(targetVertex, target);
			newEdge.setExpression(edge.getExpression());
			newEdge.setVarSource(edge.getVarSource());
			
			graph.removeEdge(sourceVertex, target);
		}		
	}	

}