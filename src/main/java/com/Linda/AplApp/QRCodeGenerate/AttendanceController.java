package com.Linda.AplApp.QRCodeGenerate;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Controller
public class AttendanceController {

    private static final String APPLICATION_NAME = "Your Application Name";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @GetMapping("/register-attendance")
    public ResponseEntity<String> registerAttendance() {
        try {
            InputStream credentialsStream = getClass().getResourceAsStream(CREDENTIALS_FILE_PATH);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(credentialsStream));

            // Skapa en instans av GoogleAuthorizationCodeFlow för OAuth 2.0-autentisering
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    Collections.singleton(CalendarScopes.CALENDAR_EVENTS))
                    .build();

            // Starta autentisering och få tillgång till OAuth 2.0-token
            Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

            // Skapa en instans av Calendar-klienten
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Skapa en händelse i kalendern
            Event event = new Event();
            event.setSummary("Student presence");
            DateTime startDateTime = new DateTime("2023-05-27T00:00:00+00:00");
            DateTime endDateTime = new DateTime("2023-07-01T00:00:00+00:00");
            EventDateTime start = new EventDateTime().setDateTime(startDateTime);
            EventDateTime end = new EventDateTime().setDateTime(endDateTime);
            event.setStart(start);
            event.setEnd(end);

            Event createdEvent = service.events().insert("primary", event).execute();

            // Skriv ut händelse-ID för den skapade händelsen
            System.out.println("Event created: " + createdEvent.getId());

            return ResponseEntity.ok("Attendance registered in calendar.");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register attendance.");
        }
    }
}

