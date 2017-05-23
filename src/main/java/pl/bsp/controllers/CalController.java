package pl.bsp.controllers;

/**
 * Created by Kamil on 2017-05-21.
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Collections;


@RestController
public class CalController {


    private static FileDataStoreFactory dataStoreFactory;

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static com.google.api.services.calendar.Calendar client;

    static final java.util.List<com.google.api.services.calendar.model.Calendar> addedCalendarsUsingBatch = Lists.newArrayList();

    private static final String APPLICATION_NAME = "";

    private static Credential authorize(String name) throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(UserController.class.getResourceAsStream("/client_secrets.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
                            + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(name);
    }

    @RequestMapping("/cal")
    public Principal user(Principal principal) {
        //   com.google.api.services.calendar.Calendar client;

        try {
            // initialize the transport
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // initialize the data store factory
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

            // authorization
            Credential credential = authorize(principal.getName());



            // set up global Calendar instance
            client = new com.google.api.services.calendar.Calendar.Builder(
                    httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
               /* JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
                HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                ComputeCredential credential = new ComputeCredential.Builder(httpTransport, JSON_FACTORY)
                       .build();
                client =
                        new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).build(); */
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return principal;
    }
}

