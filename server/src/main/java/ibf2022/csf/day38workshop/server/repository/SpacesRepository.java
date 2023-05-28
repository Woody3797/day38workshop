package ibf2022.csf.day38workshop.server.repository;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
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
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

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
        S3Object result = s3.getObject(getReq);
        ObjectMetadata metadata = result.getObjectMetadata();
        String contentType = metadata.getContentType();
        Map<String, String> userdata = metadata.getUserMetadata();
        System.out.println(userdata.toString());
        S3ObjectInputStream is = result.getObjectContent();
        byte[] picture;
        try {
            picture = is.readAllBytes();
            StringBuilder sb = new StringBuilder();
            String encoded = Base64.getEncoder().encodeToString(picture);
            String imageData = sb.append("data:").append(contentType).append(";base64,").append(encoded).toString();

            return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-name", userdata.get("filename"))
                        .body(Json.createObjectBuilder()
                        .add("image", imageData)
                        .build().toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

}