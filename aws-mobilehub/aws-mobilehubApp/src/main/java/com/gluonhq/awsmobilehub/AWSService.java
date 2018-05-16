package com.gluonhq.awsmobilehub;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.StorageService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSService {

    private static final Logger LOGGER = Logger.getLogger(AWSService.class.getName());
    
    /** AWSConfiguration object that represents the awsconfiguration.json file. */
    private AWSConfiguration awsConfiguration;
    
    public AWSService() {
        try {
            awsConfiguration = new AWSConfiguration();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Missing awsconfiguration.json file under /src/main/resources");
        }
        
        AWSMobileClient.getInstance().initialize(awsStartupResult -> {
            LOGGER.log(Level.INFO, "[GLUONAWS] AWSMobileClient is instantiated and you are connected to AWS!");
            
            // Obtain the reference to the AWSCredentialsProvider and AWSConfiguration objects
                AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
                AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

                LOGGER.log(Level.INFO, "[GLUONAWS] credentials:");
                // Use IdentityManager#getUserID to fetch the identity id.
                IdentityManager.getDefaultIdentityManager().getUserID(new IdentityHandler() {
                    @Override
                    public void onIdentityId(String identityId) {
                        LOGGER.log(Level.INFO, "[GLUONAWS] Identity ID = " + identityId);

                        // Use IdentityManager#getCachedUserID to
                        //  fetch the locally cached identity id.
                        final String cachedIdentityId = IdentityManager.getDefaultIdentityManager().getCachedUserID();
                        LOGGER.log(Level.INFO, "[GLUONAWS] cachedIdentityId " + cachedIdentityId);
                    }

                    @Override
                    public void handleError(Exception exception) {
                        LOGGER.log(Level.INFO, "[GLUONAWS] Error in retrieving the identity" + exception);
                    }
                });
        }).execute();
    }
    
    public void uploadFile(String fileName) {

        TransferUtility transferUtility = TransferUtility.builder()
                .awsConfiguration(awsConfiguration)
                .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                .build();
        
        File storage = Services.get(StorageService.class)
                .flatMap(service -> service.getPrivateStorage())
                .orElseThrow(() -> new RuntimeException("[GLUONAWS] Error accessing Private Storage folder"));

        File outputFile = new File(storage, fileName);
        if (! outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }
        
        if (! outputFile.exists()) {
            try {
                copyFile(getClass().getResource(fileName), outputFile);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "[GLUONAWS] Error copying local file " + ex);
            }
        }

        String bucketName = fileName.substring(fileName.lastIndexOf("/") + 1);
        LOGGER.log(Level.INFO, "[GLUONAWS] AWS uploading file " + outputFile.getAbsolutePath() + " to uploads/" + bucketName);
        TransferObserver uploadObserver = transferUtility.upload("uploads/" + bucketName, outputFile);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                    LOGGER.log(Level.INFO, "[GLUONAWS] File uploaded successfully");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                LOGGER.log(Level.INFO, "[GLUONAWS] ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                LOGGER.log(Level.WARNING, "[GLUONAWS] Error uploading file " + ex);
            }

        });
    }

    public void downloadFile(String fileName) {
        TransferUtility transferUtility = TransferUtility.builder()
                    .awsConfiguration(awsConfiguration)
                    .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                    .build();

        String bucketName = fileName.substring(fileName.lastIndexOf("/") + 1);
        
        File publicRoot = Services.get(StorageService.class)
                .flatMap(service -> service.getPublicStorage("S3"))
                .orElseThrow(() -> new RuntimeException("[GLUONAWS] Error accessing Public Storage folder"));
        
        File outputFile = new File(publicRoot, fileName);
        if (! outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        } else {
            outputFile.delete();
        }
        
        LOGGER.log(Level.INFO, "[GLUONAWS] AWS downloading file public/" + bucketName + " to " + outputFile.getAbsolutePath());
        TransferObserver downloadObserver = transferUtility.download("public/" + bucketName, new File(publicRoot, fileName));

        // Attach a listener to the observer to get state update and progress notifications
        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed download.
                    LOGGER.log(Level.INFO, "[GLUONAWS] File downloaded successfully");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                    int percentDone = (int)percentDonef;

                    LOGGER.log(Level.INFO, "[GLUONAWS] ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                LOGGER.log(Level.WARNING, "[GLUONAWS] Error downloading file " + ex);
            }

        });
    }

    private void copyFile(URL url, File outputFile) throws Exception {
        try (InputStream input = url.openStream()) {
            if (input == null) {
                throw new RuntimeException("[GLUONAWS] Internal copy failed: input stream for " + url + " is null");
            }

            try (OutputStream output = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
            }
        }
    }
}
