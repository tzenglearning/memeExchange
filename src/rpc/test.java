package rpc;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.file.Paths;

public class test {
  public static void main(String[] args) {
    // The ID of your GCP project
    String projectId = "memegenerator-293602";

    // The ID of your GCS bucket
    String bucketName = "meme_generator";

    // The ID of your GCS object
    String objectName = "test.png";

    // The path to which the file should be downloaded
    String destFilePath = "/Users/tzenglearning/Downloads/test1.png";
    
    
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    Blob blob = storage.get(BlobId.of(bucketName, objectName));
    blob.downloadTo(Paths.get(destFilePath));

    System.out.println(
        "Downloaded object "
            + objectName
            + " from bucket name "
            + bucketName
            + " to "
            + destFilePath);
  }
}