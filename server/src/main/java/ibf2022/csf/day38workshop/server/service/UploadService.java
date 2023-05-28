package ibf2022.csf.day38workshop.server.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ibf2022.csf.day38workshop.server.model.Post;
import ibf2022.csf.day38workshop.server.repository.PostRepository;
import ibf2022.csf.day38workshop.server.repository.SpacesRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Service
public class UploadService {
    
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SpacesRepository spacesRepository;

    public void upload(String comments, MultipartFile imageFile) throws IOException {

        postRepository.uploadPostToMongo(comments, imageFile);

        spacesRepository.upload(imageFile);
        spacesRepository.uploadSQL(imageFile);
    }

    public void updateLikes(String key, Integer likes, Integer dislikes) {
        postRepository.updateLikes(key, likes, dislikes);
    }

    public ResponseEntity<String> getImage(String key) {
        return spacesRepository.getImage(key);
    }

    public Post getPostDetails(String imageKey) {
        Post post = postRepository.getPostDetails(imageKey);
        return post;
    }

    public ResponseEntity<String> getFilesFromS3() {
        List<String> files = spacesRepository.getFilesFromS3();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (String item: files) {
            jab.add(item);
        }
        JsonArray jsonArr = jab.build();
        JsonObject jo = Json.createObjectBuilder().add("files", jsonArr).build();
        return ResponseEntity.ok().body(jo.toString());
    }
}
