package ibf2022.csf.day38workshop.server.repository;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

@Repository
public class SpacesRepository {
    
    @Autowired
    private AmazonS3 s3;

    public URL upload(String comments, MultipartFile imageFile) throws IOException {
        // Add custom metadata
        Map<String, String> userData = new HashMap<>();
        userData.put("comments", comments);
        userData.put("filename", imageFile.getOriginalFilename());
        userData.put("uploadDate", LocalDate.now().toString());

        // Add object's metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        metadata.setUserMetadata(userData);

        // Generate a random filename
        String key = UUID.randomUUID().toString().substring(0, 8);

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
}