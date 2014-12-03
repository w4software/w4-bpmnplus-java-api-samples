package eu.w4.sample;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.w4.common.configuration.ConfigurationParameter;
import eu.w4.engine.client.GroupFilter;
import eu.w4.engine.client.SortMode;
import eu.w4.engine.client.UserFilter;
import eu.w4.engine.client.UserFilterType;
import eu.w4.engine.client.bpmn.w4.runtime.ActivityInstance;
import eu.w4.engine.client.bpmn.w4.runtime.ActivityInstanceAttachment;
import eu.w4.engine.client.bpmn.w4.runtime.ActivityInstanceFilter;
import eu.w4.engine.client.bpmn.w4.runtime.ActivityInstanceSort;
import eu.w4.engine.client.bpmn.w4.runtime.DefaultActivityInstanceSortBy;
import eu.w4.engine.client.bpmn.w4.runtime.InstanceState;
import eu.w4.engine.client.bpmn.w4.runtime.PotentialOwnerFilter;
import eu.w4.engine.client.bpmn.w4.runtime.UserTaskInstanceFilter;
import eu.w4.engine.client.configuration.NetworkConfigurationParameter;
import eu.w4.engine.client.service.ActivityService;
import eu.w4.engine.client.service.AuthenticationService;
import eu.w4.engine.client.service.EngineService;
import eu.w4.engine.client.service.EngineServiceFactory;
import eu.w4.engine.client.service.ObjectFactory;
import eu.w4.engine.client.service.UserService;

// This sample shows a simple search of the tasks provided to the admin user 
//  in order to test this application you can adapt the login with the user you want 
//      or reassign task to the admin user using the admin tool.
public class SearchUserTasks implements SampleConstants {
	public static void main(final String[] args) throws Exception {
		// Retrieve engine service
		final Map<ConfigurationParameter, String> parameters = new HashMap<ConfigurationParameter, String>();
		parameters.put(NetworkConfigurationParameter.RMI__REGISTRY_HOST,
				SERVER_HOST);
		parameters.put(NetworkConfigurationParameter.RMI__REGISTRY_PORT,
				SERVER_PORT);
		final EngineService engineService = EngineServiceFactory
				.getEngineService(parameters);

		// Authenticate
		final AuthenticationService authenticationService = engineService
				.getAuthenticationService();
		final Principal principal = authenticationService.login(
				UserService.ADMIN_USER_NAME, ADMIN_PASSWORD);

		// Retrieve the object factory
		// The object factory can be used to create new instances of API objects
		final ObjectFactory objectFactory = engineService.getObjectFactory();

		ActivityService actService = engineService.getActivityService();

		// Create a filter to restrict to the user tasks
		UserTaskInstanceFilter mainActInstFilter = objectFactory
				.newUserTaskInstanceFilter();

		// Create another filter to restrict to only active activities
		ActivityInstanceFilter actInstFilter = objectFactory
				.newActivityInstanceFilter();
		actInstFilter.activityInstanceStateIs(InstanceState.ACTIVE);
	
		// Create another filter to focused on task of the connected user
		PotentialOwnerFilter potentialOwnerFilter = objectFactory
				.newPotentialOwnerFilter();
		
		// directly accessible task 
		UserFilter userFilter = objectFactory.newUserFilter();
		userFilter.setUserFilterType(UserFilterType.EFFECTIVE_SUBSTITUTE);
		
		// And task accessible through groups
		GroupFilter groupFilter = objectFactory.newGroupFilter();
		groupFilter.and(userFilter);
		
		// Combine both filter
		potentialOwnerFilter.or(userFilter, groupFilter);
		
		// Add to the main filter the other filters
		mainActInstFilter.and(actInstFilter, potentialOwnerFilter);

		// Check the number of user tasks
		System.out.println(" number of activities : "
				+ actService.getSearchActivityInstancesNumberOfResults(
						principal, mainActInstFilter));

		// Precise data retrieved by the search
		ActivityInstanceAttachment actInstAttachment = objectFactory
				.newActivityInstanceAttachment();

		actInstAttachment.setActivityDescriptorAttached(true);

		// Search the first 10 user tasks
		List<ActivityInstanceSort> sortList = new ArrayList<ActivityInstanceSort>();
		ActivityInstanceSort activitySortByDate = objectFactory
				.newActivityInstanceSort();
		activitySortByDate.setSortBy(DefaultActivityInstanceSortBy.ACTIVE_DATE);
		activitySortByDate.setSortMode(SortMode.DESCENDING);
		sortList.add(activitySortByDate);
		Integer offset = 0;
		Integer maximumNumberOfResults = 10;
		List<ActivityInstance> actInstances = actService
				.searchActivityInstances(principal, actInstAttachment,
						mainActInstFilter, sortList, offset,
						maximumNumberOfResults);

		// Print out these tasks
		for (ActivityInstance actInstance : actInstances) {
			System.out.println(actInstance.getIdentifier().getId() + " - "
					+ actInstance.getActivityDescriptor().getName() + " - "
					+ actInstance.getActiveDate());
		}

	}
}
