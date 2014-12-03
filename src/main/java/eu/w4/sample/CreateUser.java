package eu.w4.sample;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import eu.w4.common.configuration.ConfigurationParameter;
import eu.w4.engine.client.Language;
import eu.w4.engine.client.UserIdentifier;
import eu.w4.engine.client.configuration.NetworkConfigurationParameter;
import eu.w4.engine.client.service.AuthenticationService;
import eu.w4.engine.client.service.EngineService;
import eu.w4.engine.client.service.EngineServiceFactory;
import eu.w4.engine.client.service.LanguageService;
import eu.w4.engine.client.service.UserService;

// This sample retrieves the default language from the server
// then it creates a new user that will use the default language
public class CreateUser implements SampleConstants
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

    // Retrieve the language service
    final LanguageService languageIdentifier = engineService.getLanguageService();

    // Get the default language
    final Language language = languageIdentifier.getDefaultLanguage(principal);

    // Print out the default language
    System.out.println("Default language locale is: " + language.getIdentifier().getLocale());

    // Retrieve the user service
    final UserService userService = engineService.getUserService();

    // Create a new user and get its identifier as a result
    final UserIdentifier userIdentifier =
      userService.createUser(principal, // the previously created Principal
                             null, // default tenant (ie global tenant)
                             "my user", // the user name
                             "some password", // the user password
                             language.getIdentifier(), // default language identifier
                             null, // no properties for this user
                             true); // the user is active

    // Print out the unique id of the newly created user
    System.out.println("Created user with id: " + userIdentifier.getId());
  }
}
