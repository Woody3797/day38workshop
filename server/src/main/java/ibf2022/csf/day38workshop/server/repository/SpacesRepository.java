package ibf2022.csf.day38workshop.server.repository;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import jakarta.json.Json;

@Repository
public class SpacesRepository {
    
    @Autowired
    private AmazonS3 s3;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static String key = UUID.randomUUID().toString().substring(0, 8);

    // Saving image in S3
    public URL upload(MultipartFile imageFile) throws IOException {
        // Generate a random filename and add extension
        // key = UUID.randomUUID().toString().substring(0, 8);

        // Add custom metadata
        Map<String, String> userData = new HashMap<>();
        userData.put("filename", key);
        userData.put("upload-date", LocalDate.now().toString());

        // Add object's metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        metadata.setUserMetadata(userData);

        // woodybucket - bucket name
        // key - key
        // file.getInputStream() - actual bytes
        // metadata
        PutObjectRequest putReq = new PutObjectRequest("woodybucket", key, imageFile.getInputStream(), metadata);

        // Make the file publically accessible
        putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);

        PutObjectResult result = s3.putObject(putReq);
        System.out.println(">> result: " + result);

        return s3.getUrl("woodybucket", key);
    }


    // Saving image in SQL
    private static final String SQL_SAVE_PHOTO = """
            INSERT into posts(post_id, picture) values (?, ?)
            """;

    public int uploadSQL(MultipartFile imageFile) {
        try {
            int rows = jdbcTemplate.update(SQL_SAVE_PHOTO, key, imageFile.getBytes());
            return rows;
        } catch (DataAccessException | IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Get image from S3
    public ResponseEntity<String> getImage(String key) {
        GetObjectRequest getReq = new GetObjectRequest("woodybucket", key);
        S3Object s3Object = s3.getObject(getReq);
        ObjectMetadata metadata = s3Object.getObjectMetadata();
        String contentType = metadata.getContentType();
        Map<String, String> userdata = metadata.getUserMetadata();
        S3ObjectInputStream is = s3Object.getObjectContent();
        try {
            byte[] picture = is.readAllBytes();
            StringBuilder sb = new StringBuilder();
            String encoded = Base64.getEncoder().encodeToString(picture);
            String imageData = sb.append("data:").append(contentType).append(";base64,").append(encoded).toString();

            return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-amz-meta-Filename", userdata.get("filename"))
                        .body(Json.createObjectBuilder()
                        .add("image", imageData)
                        .build().toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Get list of files from S3
    public List<String> getFilesFromS3() {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName("woodybucket");
        List<String> keys = new ArrayList<>();
        ObjectListing objects = s3.listObjects(listObjectsRequest);

        List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();

        for (S3ObjectSummary obj : objectSummaries) {
            if (!obj.getKey().endsWith("/")) {
                keys.add(obj.getKey());
            }
        }
        objects = s3.listNextBatchOfObjects(objects);
        System.out.println(keys.toString());
        return keys;
    }

}