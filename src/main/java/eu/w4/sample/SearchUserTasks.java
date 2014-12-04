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
import eu.w4.engine.client.bpmn.w4.runtime.ActivityInstanceSort;
import eu.w4.engine.client.bpmn.w4.runtime.ActualOwnerFilter;
import eu.w4.engine.client.bpmn.w4.runtime.DefaultActivityInstanceSortBy;
import eu.w4.engine.client.bpmn.w4.runtime.ExcludedOwnerFilter;
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

		// Prepare a filter on the logged user
		UserFilter userFilter = objectFactory.newUserFilter();
		userFilter.userNameLike(principal.getName());

		// And another on its groups
		GroupFilter groupFilter = objectFactory.newGroupFilter();
		groupFilter.and(userFilter);

		// Create a filter for tasks for which the user is already the actual owner
		ActualOwnerFilter actualOwnerFilter = objectFactory.newActualOwnerFilter();
		actualOwnerFilter.and(userFilter);

		// Create a filter to find whom the user is an effective substitute of
		UserFilter effectiveSubstituteUserFilter = objectFactory.newUserFilter();
		effectiveSubstituteUserFilter.and(userFilter);
		effectiveSubstituteUserFilter.setUserFilterType(UserFilterType.EFFECTIVE_SUBSTITUTE);

		// And create a filter to all tasks for which the user is a substitute of the actual owner
		ActualOwnerFilter effectiveSubstituteActualOwnerFilter = objectFactory.newActualOwnerFilter();
		effectiveSubstituteActualOwnerFilter.and(effectiveSubstituteUserFilter);

		// Create another filter on tasks the user may potentially own
		PotentialOwnerFilter potentialOwnerFilter = objectFactory.newPotentialOwnerFilter();
		potentialOwnerFilter.or(userFilter, groupFilter);

		// But exclude tasks from which the user was excluded
		ExcludedOwnerFilter excludedOwnerFilter = objectFactory.newExcludedOwnerFilter();
		excludedOwnerFilter.or(userFilter, groupFilter);

		// Combine last two in a filter on all tasks the user could potentially own but no one actually own
		UserTaskInstanceFilter potentialOwnerUserTaskInstanceFilter = objectFactory.newUserTaskInstanceFilter();
		potentialOwnerUserTaskInstanceFilter.and(potentialOwnerFilter);
		potentialOwnerUserTaskInstanceFilter.not(excludedOwnerFilter);
		potentialOwnerUserTaskInstanceFilter.userTaskInstanceHasActualOwner(false);

		// Create a global filter for active tasks for which the user
		//  - is either an actual owner,
		//  - is the effective substitute of the actual owner
		//  - could potentially become the actual owner
		UserTaskInstanceFilter mainUserTaskInstanceFilter = objectFactory.newUserTaskInstanceFilter();
		mainUserTaskInstanceFilter.activityInstanceStateIs(InstanceState.ACTIVE);
		mainUserTaskInstanceFilter.or(actualOwnerFilter, effectiveSubstituteActualOwnerFilter, potentialOwnerUserTaskInstanceFilter);

		ActivityService actService = engineService.getActivityService();

		// Check the number of user tasks
		System.out.println(" number of activities : "
				+ actService.getSearchActivityInstancesNumberOfResults(
						principal, mainUserTaskInstanceFilter));

		// Precise data retrieved by the search
		ActivityInstanceAttachment actInstAttachment = objectFactory
				.newActivityInstanceAttachment();

		actInstAttachment.setActivityDescriptorAttached(true);

		// Search the first 10 user tasks
		List<ActivityInstanceSort> sortList = new ArrayList<ActivityInstanceSort>();
		ActivityInstanceSort activitySortByDate = objectFactory.newActivityInstanceSort();
		activitySortByDate.setSortBy(DefaultActivityInstanceSortBy.ACTIVE_DATE);
		activitySortByDate.setSortMode(SortMode.DESCENDING);
		sortList.add(activitySortByDate);
		Integer offset = 0;
		Integer maximumNumberOfResults = 10;
		List<ActivityInstance> actInstances = actService
				.searchActivityInstances(principal, actInstAttachment,
						mainUserTaskInstanceFilter, sortList, offset,
						maximumNumberOfResults);

		// Print out these tasks
		for (ActivityInstance actInstance : actInstances) {
			System.out.println(actInstance.getIdentifier().getId() + " - "
					+ actInstance.getActivityDescriptor().getName() + " - "
					+ actInstance.getActiveDate());
		}

	}
}
