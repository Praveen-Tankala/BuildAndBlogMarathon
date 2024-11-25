package org.smartstorage.Utility;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
public class SecretManagerUtil {


    @Autowired
    AESEncryptionUtils aesEncryptionUtil;



    public String getTheSecreteValue(String projectId, String secreteId) {
        String secretValue = "";
        try {
            String secretToken = aesEncryptionUtil.decrypt();
            GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(secretToken.getBytes()));
            SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            try (SecretManagerServiceClient client = SecretManagerServiceClient.create(settings)) {
                String secretJSONL = "projects/"+projectId+"/secrets/"+secreteId+"/versions/latest";
                // Access the secret payload.
                AccessSecretVersionResponse responseJSONL = client.accessSecretVersion(secretJSONL);
                secretValue = responseJSONL.getPayload().getData().toStringUtf8();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secretValue;
    }

}
