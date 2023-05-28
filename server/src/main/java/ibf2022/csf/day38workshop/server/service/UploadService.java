package ibf2022.csf.day38workshop.server.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ibf2022.csf.day38workshop.server.model.Post;
import ibf2022.csf.day38workshop.server.repository.PostRepository;
import ibf2022.csf.day38workshop.server.repository.SpacesRepository;

@Service
public class UploadService {
    
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SpacesRepository spacesRepository;

    public void upload(String comments, MultipartFile imageFile) throws IOException {

        postRepository.uploadPostToMongo(comments, imageFile);

        spacesRepository.upload(imageFile);
        int rows = spacesRepository.uploadSQL(imageFile);
        System.out.println(rows);
    }

    public void updateLikes(Integer likes, Integer dislikes) {
        postRepository.updateLikes(likes, dislikes);
    }

    public ResponseEntity<String> getImage(String key) {
        return spacesRepository.getImage(key);
    }

    public Post getPost(String key) {
        Post post = postRepository.getLikes(key);
        
        return post;
    }
}
