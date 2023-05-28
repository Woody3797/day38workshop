package ibf2022.csf.day38workshop.server.controller;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ibf2022.csf.day38workshop.server.model.Post;
import ibf2022.csf.day38workshop.server.repository.SpacesRepository;
import ibf2022.csf.day38workshop.server.service.UploadService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Controller
@RequestMapping
@CrossOrigin(origins = "*")
public class UploadController {
    
    @Autowired
    private SpacesRepository spacesRepository;

    @Autowired
    private UploadService uploadService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<String> upload(@RequestPart String comments, @RequestPart MultipartFile imageFile) {
        
        try {
            URL url = spacesRepository.upload(imageFile);
            uploadService.upload(comments, imageFile);
            System.out.println("URL: " + url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ResponseEntity.ok("{}");
    }

    @PostMapping(path = "/updatelikes")
    public ResponseEntity<String> updateLikes(@RequestPart String key, @RequestPart String likes, @RequestPart String dislikes) {
        uploadService.updateLikes(key, Integer.parseInt(likes), Integer.parseInt(dislikes));

        JsonObject jo = Json.createObjectBuilder()
        .add("likes", likes)
        .add("dislikes", dislikes)
        .build();

        return ResponseEntity.ok(jo.toString());
    }

    @GetMapping(path = "/getimage")
    @ResponseBody
    public ResponseEntity<String> getImage(@RequestParam String key) {
        return uploadService.getImage(key);
    }

    @GetMapping(path = "/getdetails")
    @ResponseBody
    public ResponseEntity<String> getLikes(@RequestParam String key) {
        Post post = uploadService.getPostDetails(key);
        return ResponseEntity.ok().body(post.toJson().toString());
    }

    @GetMapping(path = "/getfiles")
    @ResponseBody
    public ResponseEntity<String> getAllFilesFromS3() {
        return uploadService.getFilesFromS3();
    }

}
