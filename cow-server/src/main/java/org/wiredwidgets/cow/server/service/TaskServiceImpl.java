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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.service;

import static org.wiredwidgets.cow.server.transform.graph.bpmn20.Bpmn20ProcessBuilder.VARIABLES_PROPERTY;
import static org.wiredwidgets.cow.server.transform.graph.bpmn20.DecisionTaskNodeBuilder.DECISION_VAR_NAME;
import static org.wiredwidgets.cow.server.transform.graph.bpmn20.UserTaskNodeBuilder.TASK_OUTPUT_VARIABLES_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityNotFoundException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.task.Content;
import org.jbpm.task.Deadline;
import org.jbpm.task.Deadlines;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wiredwidgets.cow.server.api.model.v2.Activity;
import org.wiredwidgets.cow.server.api.service.HistoryActivity;
import org.wiredwidgets.cow.server.api.service.HistoryTask;
import org.wiredwidgets.cow.server.api.service.Participation;
import org.wiredwidgets.cow.server.api.service.Task;
import org.wiredwidgets.cow.server.repo.TaskRepository;
import org.wiredwidgets.cow.server.transform.graph.bpmn20.AbstractUserTaskNodeBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author FITZPATRICK
 */
@Transactional
@Component
public class TaskServiceImpl extends AbstractCowServiceImpl implements TaskService {
	
	@Autowired
	TaskRepository taskRepo;
	
	@Autowired
	UsersService usersService;
	
	@Autowired
	org.jbpm.task.TaskService taskClient;
	
	@Autowired
	ObjectMapper objectMapper;
	
	private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
	
    private static TypeDescriptor JBPM_TASK_SUMMARY_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(org.jbpm.task.query.TaskSummary.class));
    private static TypeDescriptor JBPM_TASK_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(org.jbpm.task.Task.class));
    private static TypeDescriptor COW_TASK_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Task.class));
    private static TypeDescriptor COW_HISTORY_TASK_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(HistoryTask.class));
    private static TypeDescriptor COW_HISTORY_ACTIVITY_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(HistoryActivity.class));

    @Transactional(readOnly = true)
    @Override
    public List<Task> findPersonalTasks(String assignee) {
        List<TaskSummary> tempTasks = new ArrayList<TaskSummary>();
        List<TaskSummary> tasks = new ArrayList<TaskSummary>();
        
        // first check to see if the user is valid, otherwise JBPM is not happy
        org.wiredwidgets.cow.server.api.service.User user = usersService.findUser(assignee);
        if (user == null) {
        	// not a user in the system, so cannot have any tasks!
        	return new ArrayList<Task>();
        }

        tempTasks.addAll(taskClient.getTasksAssignedAsPotentialOwner(assignee, "en-UK"));
        
       for (TaskSummary task : tempTasks){
            if (task.getStatus() == Status.Reserved && (task.getActualOwner() != null && task.getActualOwner().getId().equals(assignee))){
                tasks.add(task);
            }
        }
        
        return this.convertTaskSummarys(tasks);
    }

    @Override
    public String createAdHocTask(Task task) {
        org.jbpm.task.Task newTask = this.createOrUpdateTask(task);
        return Long.toString(newTask.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllTasks() {
        List<org.jbpm.task.Task> tasks = (List<org.jbpm.task.Task>) taskClient.query("select t from Task t where t.taskData.status in ('Created', 'Ready', 'Reserved', 'InProgress')", Integer.MAX_VALUE,0);
        return this.convertTasks(tasks);
    }

    @Transactional(readOnly = true)
    @Override
    public Task getTask(Long id) {
    	try {
    		org.jbpm.task.Task task = taskClient.getTask(id);
    		return converter.convert(task, Task.class);
    	}
    	catch (EntityNotFoundException e) {
    		log.error("Task with id: {} not found.", id);
    		return null;
    	}
    }

    @Override
	@Transactional(readOnly = true)
    public HistoryTask getHistoryTask(Long id) {
    	org.jbpm.task.Task task = taskRepo.findOne(id);
    	return converter.convert(task, HistoryTask.class);
    }

    @Override
    public void completeTask(Long id, String assignee, String outcome, Map<String, ?> variables) {
    	// should be handled upstream in controller
    	assert(assignee != null);
    	   
        if (log.isDebugEnabled()) {
            log.debug("{} completing task with ID: {}", assignee, id);
            if (outcome != null) {
            	log.debug("Outcome: {}", outcome);
            }
        	log.debug("Variables passed in: ");
        	for (Entry<String, ?> entry : variables.entrySet()) {
        		log.debug("{}={}", entry.getKey(), entry.getValue());
        	}
        }
          
        org.jbpm.task.Task task = taskClient.getTask(id);
        
        // convert to COW task so we can verify the decision
        Task cowTask = converter.convert(task,  Task.class);
        
        if (cowTask.getOutcomes() != null && cowTask.getOutcomes().size() > 0) {
        	// This is a decision task!
        	if (outcome == null) {
        		throw new RuntimeException("ERROR: no decision provided for a Decision task");
        	}
        	if (!cowTask.getOutcomes().contains(outcome)) {
        		throw new RuntimeException("ERROR: decision value " + outcome + " is not a valid choice.");
        	}
        }
        
        Content inputContent = taskClient.getContent(task.getTaskData().getDocumentContentId());
        
        Map<String, Object> inputMap = (Map<String, Object>) ContentMarshallerHelper.unmarshall(inputContent.getContent(), null);
        
        if (log.isDebugEnabled()) {
        	log.debug("Variables from the task content:");
	        for (Map.Entry<String, Object> entry : inputMap.entrySet()){
	            log.debug("{}={}",  entry.getKey(), entry.getValue());
	        }
        }
        
        Map<String, Object> outputMap = new HashMap<String, Object>();
        
        // put Outcome into the outputMap 
        // The InputMap contains a variable that tells us what key to use
        if (inputMap.get(DECISION_VAR_NAME) != null) {
        	log.debug("Decision outcome: {}", outcome);
        	outputMap.put((String)inputMap.get(DECISION_VAR_NAME), outcome);
        }        
               
        Map<String, Object> outputVarsMap = new HashMap<String, Object>();   
        
        // NOTE: obtaining the map from the Task results in a copy of the map as of the
        // time when the task became available.  It's possible that in the meantime (e.g. due to
        // a parallel task) the map has been altered.  
        // Map<String, Object> inputVarsMap = (Map<String, Object>) inputMap.get(TASK_INPUT_VARIABLES_NAME);
              
        // So, instead, we get the current values directly from the process instance, rather than the values copied into the task
        
        Long processInstanceId = task.getTaskData().getProcessInstanceId();
        
        WorkflowProcessInstance pi = (WorkflowProcessInstance) kSession.getProcessInstance(processInstanceId);
        if (pi == null) {
        	log.error("ProcessInstance not found: {}", processInstanceId);
        	return;
        }
        
        // initialize in case it's null or empty
        Map<String, Map<String, Boolean>> varsInfoMap = new HashMap<String, Map<String, Boolean>>();
        
        String varsJson = (String)inputMap.get(AbstractUserTaskNodeBuilder.TASK_VARIABLES_INFO);
        if (varsJson != null && !varsJson.isEmpty()) {
	        try {
	        	varsInfoMap = objectMapper.readValue(varsJson, Map.class);
	        }
	        catch (Exception e) {
	        	log.error("Json parsing exception", e);
	        }
        }

        // if any required variables were not provided, throw an exception
        verifyRequiredVariables(variables, varsInfoMap);
        
        Map<String, Object> currentProcessVarsMap = (Map<String, Object>) pi.getVariable(VARIABLES_PROPERTY);
        log.debug("Current value of generic map from process instance: {}", currentProcessVarsMap);
        
        if (currentProcessVarsMap != null) {
        	// initialize the output map with the input values
        	log.debug("Initializing current values of generic map: {}", currentProcessVarsMap);
        	outputVarsMap.putAll(currentProcessVarsMap);
        }
        
        if (variables != null && !variables.isEmpty()) {
        	log.debug("Adding variables: {}", variables);
        	// update with any new or modified values
        	for (Entry<String, ?> entry : variables.entrySet()) {
        		if (varsInfoMap.containsKey(entry.getKey()) && varsInfoMap.get(entry.getKey()).get("output").equals(Boolean.TRUE)) {
        			// this is a declared output variable so it goes directly into the process
        			// rather than into the generic map.
        			log.debug("Adding declared variable {}={}", entry.getKey(), entry.getValue());
        			outputMap.put(entry.getKey(), entry.getValue());
        		}
        		else {
        			// TODO: should we check to see whether it was modified?  Otherwise we may be
        			// overwriting a more recently modified value.
        			log.debug("Adding variable {}={} to generic map", entry.getKey(), entry.getValue());
        			outputVarsMap.put(entry.getKey(),  entry.getValue());
        		}
        	}
    	}
        
        // NOTE: even if empty, we still need to add the map as an output
        // otherwise we will set the process level generic map to NULL!
        //if (outputVarsMap.size() > 0) {
        	log.debug("Adding map to output: {}", outputVarsMap);
        	outputMap.put(TASK_OUTPUT_VARIABLES_NAME, outputVarsMap);   
        //}
       
        // The task must be started before we complete it.
        if (task.getTaskData().getStatus().equals(org.jbpm.task.Status.Reserved)) {
            // change status to InProgress
            log.debug("Starting task...");
            taskClient.start(id, assignee); 
        }    
        
        log.debug("Final output map: {}", outputMap);
        if (outputMap.size() == 0) {
        	// pass null rather than an empty map
        	outputMap = null;
        }
        
        // TODO: since we're passing the variables map further down, maybe we don't need to pass it here?  Test this.
        // ContentData contentData = ContentMarshallerHelper.marshal(outputMap, null);
        log.debug("Completing task...");
        taskClient.completeWithResults(id, assignee, outputMap);
        
        // note that we have to pass the variables again. 
        log.debug("Completing workItem...");
        kSession.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), outputMap);
        
        // for debugging
        currentProcessVarsMap = (Map<String, Object>) pi.getVariable(VARIABLES_PROPERTY);
        log.debug("Value of generic map after task completed: {}", currentProcessVarsMap);
        
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllUnassignedTasks() {
        List<TaskSummary> tempTasks;
        List<TaskSummary> tasks = new ArrayList<TaskSummary>();
        
        tempTasks = taskClient.getTasksAssignedAsPotentialOwner("Administrator", "en-UK");
        
        for (TaskSummary task : tempTasks){
            if (task.getStatus() == Status.Ready){
                tasks.add(task);
            }
        }
        
        return this.convertTaskSummarys(tasks);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findGroupTasks(String user) {
    	
        // first check to see if the user is valid, otherwise JBPM is not happy
        org.wiredwidgets.cow.server.api.service.User u = usersService.findUser(user);
        if (u == null) {
        	// not a user in the system, so cannot have any tasks!
        	return new ArrayList<Task>();
        }    	
    	
        List<TaskSummary> tempTasks = new ArrayList<TaskSummary>();
        List<TaskSummary> tasks = new ArrayList<TaskSummary>();
        
        tempTasks.addAll(taskClient.getTasksAssignedAsPotentialOwner(user, "en-UK"));
        
        for (TaskSummary task : tempTasks){
            if (task.getStatus() == Status.Ready || task.getStatus() == Status.Created){
                tasks.add(task);
            }
        }
                
        return this.convertTaskSummarys(tasks);
    }

    @Override
    public void takeTask(Long taskId, String userId) {
        taskClient.claim(taskId, userId);
    }
    
    @Override
    public void updateTask(Task task) {
        this.createOrUpdateTask(task);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllTasksByProcessInstance(Long id) {
        //List<Status> status = Arrays.asList(Status.Completed, Status.Created, Status.Error, Status.Exited, Status.Failed, Status.InProgress, Status.Obsolete, Status.Ready, Status.Reserved, Status.Suspended);
    	List<Status> status = Arrays.asList(Status.Ready);
        return this.convertTaskSummarys(taskClient.getTasksByStatusByProcessId(id, status, "en-UK"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllTasksByProcessKey(Long id) {
        return new ArrayList<Task>();//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTaskParticipatingGroup(Long taskId, String groupId, String type) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTaskParticipatingUser(Long taskId, String userId, String type) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Participation> getTaskParticipations(Long taskId) {
        return new ArrayList<Participation>();//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTaskParticipatingGroup(Long taskId, String groupId, String type) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTaskParticipatingUser(Long taskId, String userId, String type) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTaskAssignment(Long taskId) {

        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional(readOnly = true)
    @Override
    public List<HistoryTask> getHistoryTasks(String assignee, Date startDate, Date endDate) {  	
    	return convertHistoryTasks(taskRepo.findByTaskDataActualOwnerAndTaskDataCompletedOnBetween(
    			new User(assignee), startDate, endDate));
    }

    @Transactional(readOnly = true)
    @Override
    public List<HistoryTask> getHistoryTasks(Long processInstanceId) {
        return convertHistoryTasks(taskRepo.findByTaskDataProcessInstanceId(processInstanceId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<HistoryActivity> getHistoryActivities(Long processInstanceId) {
    	return convertHistoryActivities(taskRepo.findByTaskDataProcessInstanceId(processInstanceId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findOrphanedTasks() {
    	// TODO: implement
        return new ArrayList<Task>();
    }

    @Transactional(readOnly = true)
    @Override
    public Activity getWorkflowActivity(String processInstanceId, String key) {
        Activity activity = null;
        return activity;//throw new UnsupportedOperationException("Not supported yet.");
    }

    private Date convert(XMLGregorianCalendar source) {
        return source.toGregorianCalendar().getTime();
    }

    /*
     * private List<Participation>
     * convertParticipations(List<org.jbpm.api.task.Participation> source) {
     * return (List<Participation>) converter.convert(source,
     * JBPM_PARTICIPATION_LIST, COW_PARTICIPATION_LIST); }
     */
    @SuppressWarnings("unchecked")
	private List<Task> convertTaskSummarys(List<org.jbpm.task.query.TaskSummary> source) {
        return (List<Task>) converter.convert(source, JBPM_TASK_SUMMARY_LIST, COW_TASK_LIST);
    }
    
    @SuppressWarnings("unchecked")
    private List<Task> convertTasks(List<org.jbpm.task.Task> source) {
        return (List<Task>) converter.convert(source, JBPM_TASK_LIST, COW_TASK_LIST);
    }
  
    @SuppressWarnings("unchecked")
	private List<HistoryTask> convertHistoryTasks(List<org.jbpm.task.Task> source) {
		return (List<HistoryTask>) converter.convert(source, JBPM_TASK_LIST, COW_HISTORY_TASK_LIST);
	}

    @SuppressWarnings("unchecked")
    private List<HistoryActivity> convertHistoryActivities(List<org.jbpm.task.Task> source) { 
    	return (List<HistoryActivity>) converter.convert(source, JBPM_TASK_LIST, COW_HISTORY_ACTIVITY_LIST);
    }
     
    
    //TODO: Check if you can update a task. Can you update task by just adding a task with the same ID?
    private org.jbpm.task.Task createOrUpdateTask(Task source) {

        org.jbpm.task.Task target;
        boolean newTask = false;
        if (source.getId() == null) {
            newTask = true;
            target = new org.jbpm.task.Task();
        } else {
        	target = taskClient.getTask(Long.valueOf(source.getId()));
        }
        if (target == null) {
            return null;
        }
        if (source.getAssignee() != null) {
            PeopleAssignments pa = new PeopleAssignments();
            List<OrganizationalEntity> orgEnt = new ArrayList<OrganizationalEntity>();
            org.jbpm.task.User oe = new org.jbpm.task.User();
            oe.setId(source.getAssignee());
            pa.setTaskInitiator(oe);
            orgEnt.add(oe);
            pa.setPotentialOwners(orgEnt);
            target.setPeopleAssignments(pa);

        }
        if (source.getDescription() != null) {
            List<I18NText> desc = new ArrayList<I18NText>();
            desc.add(new I18NText("en-UK", source.getDescription()));
            target.setDescriptions(desc);
        }
        if (source.getDueDate() != null) {
            Deadlines deadlines = new Deadlines();
            List<Deadline> dls = new ArrayList<Deadline>();
            Deadline dl = new Deadline();
            dl.setDate(this.convert(source.getDueDate()));
            dls.add(dl);
            deadlines.setEndDeadlines(dls);
            target.setDeadlines(deadlines);
        }
        if (source.getName() != null) {
            List<I18NText> names = new ArrayList<I18NText>();
            names.add(new I18NText("en-UK", source.getName()));
            target.setNames(names);
        }
        if (source.getPriority() != null) {
            target.setPriority(source.getPriority());
        }

        TaskData td = new TaskData();
        target.setTaskData(td);
        /*
         * if (source.getProgress() != null) {
         * target.setProgress(source.getProgress()); }
         */

        // convert variables
        /*
         * if (source.getVariables() != null &&
         * source.getVariables().getVariables().size() > 0) { Map<String,
         * Object> variables = new HashMap<String, Object>(); for (Variable
         * variable : source.getVariables().getVariables()) {
         * variables.put(variable.getName(), variable.getValue()); }
         * this.taskService.setVariables(target.getId(), variables); }
         */

        if (newTask) {
            //BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
            //taskClient.addTask(target, null, addTaskResponseHandler);
            taskClient.addTask(target, null);
        }

        return target;
    }
    
    /*
     * Determines whether all required variables have been provided
     */
    private void verifyRequiredVariables(Map<String, ?> variables, Map<String, Map<String, Boolean>> varsInfoMap) {
    	log.debug("Checking for required variables...");
    	if (varsInfoMap != null) {
	    	for (String varName : varsInfoMap.keySet()) {
	    		log.debug("{}:{}", varName, varsInfoMap.get(varName).get("required"));
	    		if (varsInfoMap.get(varName).get("required").equals(Boolean.TRUE) && !variables.containsKey(varName)) {
	    			throw new RuntimeException("Required variable " + varName + " was not provided");
	    		}
	    	}
    	}
    }
}
