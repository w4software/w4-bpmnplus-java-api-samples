package eu.w4.sample;

import java.io.InputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import eu.w4.common.configuration.ConfigurationParameter;
import eu.w4.common.io.DefaultRemoteInputStream;
import eu.w4.common.io.RemoteInputStream;
import eu.w4.engine.client.configuration.NetworkConfigurationParameter;
import eu.w4.engine.client.service.AuthenticationService;
import eu.w4.engine.client.service.DefinitionsService;
import eu.w4.engine.client.service.EngineService;
import eu.w4.engine.client.service.EngineServiceFactory;
import eu.w4.engine.client.service.UserService;

// This sample shows how to import a BPMN file to the server
public class SendBpmnToServer implements SampleConstants
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

    // The path to the BPMN to load
    final String resourceName = "/eu/w4/sample/MyFirstBpmn.bpmn";

    // Load the BPMN file (this file can be opened using the BPMN+ Composer)
    final InputStream bpmn = SendBpmnToServer.class.getResourceAsStream(resourceName);
    if (bpmn == null)
    {
      throw new Exception("The bpmn defined as resource [" + resourceName + "] cannot be found, check classpath");
    }
    try
    {
      // Wrap the local input stream to a stream that is network capable
      final RemoteInputStream remoteInputStream = new DefaultRemoteInputStream(bpmn);
      try
      {
        // Retrieve the definitions service
        final DefinitionsService definitionsService = engineService.getDefinitionsService();

        // Import the BPMN on the server
        definitionsService.importBpmn(principal,
                                      remoteInputStream,
                                      null,
                                      null,
                                      null);
      }
      finally
      {
        // Close resource
        remoteInputStream.close();
      }
    }
    finally
    {
      // Close resource
      bpmn.close();
    }

    System.out.println("BPMN successfully sent");
  }
}
