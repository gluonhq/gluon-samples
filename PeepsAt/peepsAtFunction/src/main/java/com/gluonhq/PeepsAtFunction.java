package com.gluonhq;

import com.microsoft.azure.serverless.functions.ExecutionContext;
import com.microsoft.azure.serverless.functions.HttpRequestMessage;
import com.microsoft.azure.serverless.functions.HttpResponseMessage;
import com.microsoft.azure.serverless.functions.OutputBinding;
import com.microsoft.azure.serverless.functions.annotation.*;

import java.util.Arrays;
import java.util.Optional;

public class PeepsAtFunction {

    @FunctionName("saveLocation")
    public HttpResponseMessage<String> saveLocation(@HttpTrigger(name = "req", methods = {"get", "post"}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
                                                    @BlobInput(name = "blobInput", connection = "StorageAccount", path = "test/foo.txt", dataType = "binary") byte[] blobInput,
                                                    @BlobOutput(name = "blobOutput", connection = "StorageAccount", path = "test/foo.txt") OutputBinding<String> blobOutput,
                                                    final ExecutionContext context) {

        context.getLogger().info("Entering : " + context.getFunctionName());
        final String name = request.getQueryParameters().get("name");
        final String latitude = request.getQueryParameters().get("lat");
        final String longitude = request.getQueryParameters().get("long");

        if (name != null && latitude != null && longitude != null) {
            String userLocation = name + "," + latitude+ "," + longitude;
            if (blobInput == null) {
                blobOutput.setValue(userLocation);
            } else {
                String blob = new String(blobInput);
                final String[] lines = blob.split("\n");
                final Optional<String> user = Arrays.stream(lines)
                        .filter(line -> line.startsWith(name))
                        .findFirst();
                if (user.isPresent()) {
                    blobOutput.setValue(blob.replaceFirst(user.get(), userLocation));
                } else {
                    blobOutput.setValue(blob + "\n" + userLocation);
                }
            }
            context.getLogger().info("Exiting : " + context.getFunctionName());
            return request.createResponse(200, "Success");
        }
        context.getLogger().info("Exiting : " + context.getFunctionName());
        return request.createResponse(400, "Failed");
    }

    @FunctionName("findLocation")
    public HttpResponseMessage<String> findLocation(@HttpTrigger(name = "req", methods = {"get", "post"}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
                                                    @BlobInput(name = "blobInput", connection = "StorageAccount", path = "test/foo.txt", dataType = "binary") byte[] blobInput,
                                                    final ExecutionContext context) {
        context.getLogger().info("Entering : " + context.getFunctionName());
        final String name = request.getQueryParameters().get("name");
        if (name != null) {
            if (blobInput != null) {
                String blob = new String(blobInput);
                final String[] lines = blob.split("\n");
                final Optional<String> userLocation = Arrays.stream(lines)
                        .filter(line -> line.startsWith(name))
                        .findFirst();
                if (userLocation.isPresent()) {
                    context.getLogger().info("Exiting : " + context.getFunctionName());
                    return request.createResponse(200, userLocation.get());
                }
            }
        }
        context.getLogger().info("Exiting : " + context.getFunctionName());
        return request.createResponse(404, "Not Found");
    }
}
