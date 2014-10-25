package eu.w4.sample;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import eu.w4.common.configuration.ConfigurationParameter;
import eu.w4.engine.client.configuration.NetworkConfigurationParameter;
import eu.w4.engine.client.service.AuthenticationService;
import eu.w4.engine.client.service.EngineService;
import eu.w4.engine.client.service.EngineServiceFactory;
import eu.w4.engine.client.service.UserService;

// The sample shows how to connect to and authenticate on a BPMN+ Engine
public class ConnectionAndAuthentication implements SampleConstants
{
  public static void main(final String[] args) throws Exception
  {
    // Prepare connection parameters for Engine
    final Map<ConfigurationParameter, String> parameters = new HashMap<ConfigurationParameter, String>();
    parameters.put(NetworkConfigurationParameter.RMI__REGISTRY_HOST, SERVER_HOST);
    parameters.put(NetworkConfigurationParameter.RMI__REGISTRY_PORT, SERVER_PORT);

    // Get the Engine service
    final EngineService engineService = EngineServiceFactory.getEngineService(parameters);

    // Print engine version out to check if connection works
    System.out.println("Engine version: " + engineService.getEngineCoreVersion());

    // Retrieve the authentication service
    final AuthenticationService authenticationService = engineService.getAuthenticationService();

    // Authenticate against the engine
    final Principal principal =
      authenticationService.login(UserService.ADMIN_USER_NAME, ADMIN_PASSWORD);

    // Print out the name of the created Principal
    System.out.println("Principal name: " + principal.getName());
  }
}
