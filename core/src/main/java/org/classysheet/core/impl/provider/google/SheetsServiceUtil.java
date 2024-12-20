package org.classysheet.core.impl.provider.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import java.io.IOException;
import java.security.GeneralSecurityException;
public class SheetsServiceUtil {

    private static final String APPLICATION_NAME = "Google Sheets Example";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = GoogleAuthorizeUtil.authorize();
        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential
        ).setApplicationName(APPLICATION_NAME)
                .build();
    }
}
