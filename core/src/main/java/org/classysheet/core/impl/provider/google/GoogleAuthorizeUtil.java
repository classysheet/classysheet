package org.classysheet.core.impl.provider.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleAuthorizeUtil {

    // TODO: A secure way to give CLIENT_ID and CLIENT_SECRET
    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static Credential cachedCredential = null;

    public static Credential authorize() throws IOException, GeneralSecurityException {
        if (cachedCredential != null) {
            return cachedCredential;
        }

        GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
        web.setClientId(CLIENT_ID);
        web.setClientSecret(CLIENT_SECRET);
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);

        List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                scopes
        ).setDataStoreFactory(new MemoryDataStoreFactory())
                .setAccessType("offline")
                .build();

        cachedCredential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return cachedCredential;
    }
}