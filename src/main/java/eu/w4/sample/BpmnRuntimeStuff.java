package eu.w4.sample;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.w4.common.configuration.ConfigurationParameter;
import eu.w4.engine.client.User;
import eu.w4.engine.client.bpmn.w4.infrastructure.DefinitionsIdentifier;
import eu.w4.engine.client.bpmn.w4.process.ProcessIdentifier;
import eu.w4.engine.client.bpmn.w4.runtime.ActivityInstance;
import eu.w4.engine.client.bpmn.w4.runtime.DataEntry;
import eu.w4.engine.client.bpmn.w4.runtime.ProcessInstance;
import eu.w4.engine.client.bpmn.w4.runtime.ProcessInstanceAttachment;
import eu.w4.engine.client.bpmn.w4.runtime.ProcessInstanceIdentifier;
import eu.w4.engine.client.bpmn.w4.runtime.UserTaskInstance;
import eu.w4.engine.client.bpmn.w4.runtime.UserTaskInstanceAttachment;
import eu.w4.engine.client.configuration.NetworkConfigurationParameter;
import eu.w4.engine.client.service.ActivityService;
import eu.w4.engine.client.service.AuthenticationService;
import eu.w4.engine.client.service.EngineService;
import eu.w4.engine.client.service.EngineServiceFactory;
import eu.w4.engine.client.service.ObjectFactory;
import eu.w4.engine.client.service.ProcessService;
import eu.w4.engine.client.service.UserService;

// This sample shows the full execution of a (simple) BPMN.
// /!\ Beware: The BPMN file (MyFirstBpmn.bpmn) provided with this sample must be imported to the server,
// you can use the sample SendBpmnToServer, the BPMN+ Admin or the BPMN+ Composer to perform this.
public class BpmnRuntimeStuff implements SampleConstants
{
  public static void main(final String[] args) throws Exception
  {
    // Retrieve engine service
    final Map<ConfigurationParameter, String> parameters = new HashMap<ConfigurationParameter, String>();
    parameters.put(NetworkConfigurationParameter.RMI__REGISTRY_HOST, SERVER_HOST);
    parameters.put(NetworkConfigurationParameter.RMI__REGISTRY_PORT, SERVER_PORT);
    final EngineService engineService = EngineServiceFactory.getEngineService(parameters);

    // Authenticate
    final AuthenticationService authenticationService = engineService.getAuthenticationService();
    final Principal principal =
      authenticationService.login(UserService.ADMIN_USER_NAME, ADMIN_PASSWORD);

    // Retrieve the object factory
    // The object factory can be used to create new instances of API objects
    final ObjectFactory objectFactory = engineService.getObjectFactory();

    // Create a new definitions identifier
    final DefinitionsIdentifier definitionsIdentifier = objectFactory.newDefinitionsIdentifier();
    // Set the definitions id
    definitionsIdentifier.setId(DEFINITIONS_ID);

    // Create a new process identifier
    final ProcessIdentifier processIdentifier = objectFactory.newProcessIdentifier();
    // Indicate in which definitions this process is defined (by setting the definitions identifier)
    processIdentifier.setDefinitionsIdentifier(definitionsIdentifier);
    // Set the process id
    processIdentifier.setId(PROCESS_ID);

    // Retrieve the process service
    final ProcessService processService = engineService.getProcessService();

    // Instantiate the process to create a new process instance
    final ProcessInstanceIdentifier processInstanceIdentifier =
      processService.instantiateProcess(principal, // the principal
                                        null, // no collaboration in that case
                                        processIdentifier, // the identifier of the process
                                        null, // no specific name, let the Engine decide
                                        null, // no intent to make the name unique
                                        null, // no data provided
                                        null); // not expecting the Engine to initialize the data

    // Print out the unique id of the newly created instance
    System.out.println("My process instance id: " + processInstanceIdentifier.getId());
    // Print out the name the engine generated for the created instance
    System.out.println("My process instance name: " + processInstanceIdentifier.getName());

    // Create a new user task instance attachment
    final UserTaskInstanceAttachment userTaskInstanceAttachment =
      objectFactory.newUserTaskInstanceAttachment();
    // Indicate we want to attach the actual owner
    userTaskInstanceAttachment.setActualOwnerAttached(true);

    // Retrieve the activity service
    final ActivityService activityService = engineService.getActivityService();

    // Get all activity instances of the newly created process instance
    // The result list should contain one activity instance representing the user task named "My First User Task"
    final List<ActivityInstance> activityInstances =
      activityService.getActivityInstances(principal,
                                           processInstanceIdentifier, // the process instance identifier
                                           userTaskInstanceAttachment, // attachment defined earlier
                                           null); // no need to sort, we are expecting only one result

    // Check that we have one activity instance
    if (activityInstances.size() != 1)
    {
      throw new Exception("Expecting one and only one activity instance but got [" + activityInstances.size() + "]");
    }

    // Get the activity instance
    ActivityInstance activityInstance = activityInstances.get(0);

    // Print out the activity instance id
    System.out.println("Activity instance id: " + activityInstance.getIdentifier().getId());

    // Print out the activity instance state
    System.out.println("Activity instance state: " + activityInstance.getState());

    // Print out if the activity instance is a user task instance as expected
    System.out.println("Is a user task instance: " + (activityInstance instanceof UserTaskInstance));
    UserTaskInstance userTaskInstance = (UserTaskInstance)activityInstance;

    // Print out there is no actual owner
    System.out.println("Actual owner of this user task instance: " + userTaskInstance.getActualOwner());

    // Claim the user task instance and became the actual owner
    activityService.claimUserTaskInstance(principal, activityInstance.getIdentifier());

    // Get the activity instance actual state from server
    activityInstance = activityService.getActivityInstance(principal,
                                                           activityInstance.getIdentifier(),
                                                           userTaskInstanceAttachment); // attach the actual owner

    // Cast the activity instance again since we know its a user task instance
    userTaskInstance = (UserTaskInstance)activityInstance;

    // Retrieve the actual owner;
    final User actualOwner = userTaskInstance.getActualOwner();

    // Print out the actual owner
    System.out.println("Actual owner: " + actualOwner.getIdentifier().getName());

    // Create a map representing the data entries of the user task
    final Map<String, Object> dataEntries = new HashMap<String, Object>();

    // Set a value for the unique available data
    dataEntries.put(DATA_NAME, "some value for my first data");

    // Complete the activity instance using the data entries
    System.out.println("Complete activity instance with data: " + dataEntries);
    activityService.completeActivityInstance(principal,
                                             activityInstance.getIdentifier(),
                                             dataEntries);

    // Get the activity instance actual state from server
    activityInstance = activityService.getActivityInstance(principal,
                                                           activityInstance.getIdentifier(),
                                                           null);

    // Print out the activity instance state
    System.out.println("Activity instance state: " + activityInstance.getState());

    // Create a new process instance attachment
    final ProcessInstanceAttachment processInstanceAttachment = objectFactory.newProcessInstanceAttachment();
    // Indicate we want the data entries to be attached
    processInstanceAttachment.setDataEntriesAttached(true);

    // Get the process instance from the server
    final ProcessInstance processInstance = processService.getProcessInstance(principal,
                                                                              processInstanceIdentifier,
                                                                              processInstanceAttachment);

    // Print out the process instance state
    System.out.println("Process instance state: " + processInstance.getState());

    // Get the data entries of the process instance
    final Map<String, DataEntry> processInstanceDataEntries = processInstance.getDataEntries();

    // Get the only data entry of this process instance
    final DataEntry dataEntry = processInstanceDataEntries.get(DATA_NAME);

    // Print out the data entry value
    System.out.println("Data entry value: " + dataEntry.getValue());
  }
}
