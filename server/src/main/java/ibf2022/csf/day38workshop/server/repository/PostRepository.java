package ibf2022.csf.day38workshop.server.repository;

import java.io.IOException;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import ibf2022.csf.day38workshop.server.model.Post;

@Repository
public class PostRepository {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void uploadPostToMongo(String comments, MultipartFile imageFile) throws IOException {
        Query query = new Query(Criteria.where("_id").is(SpacesRepository.key));
        Update update = new Update().set("_id", SpacesRepository.key).set("comments", comments).set("image", imageFile.getBytes());
        updateLikes(SpacesRepository.key, 0, 0);
        mongoTemplate.upsert(query, update, "posts");
    }

    public String updateLikes(String key, int likes, int dislikes) {
        if (likes == 0 && dislikes == 0) {
            redisTemplate.opsForHash().put(key, "likes", String.valueOf(0));
            redisTemplate.opsForHash().put(key, "dislikes", String.valueOf(0));
        }
        Long post = redisTemplate.opsForHash().size(key);
        if (post == 0) {
            redisTemplate.opsForHash().put(key, "likes", String.valueOf(0));
            redisTemplate.opsForHash().put(key, "dislikes", String.valueOf(0));
            System.out.println(">>>>>>>>>>>>>>>>>>>>>> key "+ key);
        } else if (post > 0) {
            redisTemplate.opsForHash().increment(key, "likes", likes);
            redisTemplate.opsForHash().increment(key, "dislikes", dislikes);
        }
        
        return key;
    }

    @SuppressWarnings("null")
    public Post getPostDetails(String key) {
        Post post = new Post();
        Integer likes = Integer.parseInt((String) redisTemplate.opsForHash().get(key, "likes"));
        Integer dislikes = Integer.parseInt((String) redisTemplate.opsForHash().get(key, "dislikes"));

        Query query = new Query(Criteria.where("_id").is(key));
        Document doc = mongoTemplate.findOne(query, Document.class, "posts");
        post.setLikes(likes);
        post.setDislikes(dislikes);
        try {
            post.setComments(doc.getString("comments"));
        } catch (NullPointerException e) {
            e.printStackTrace();
            post.setComments("no comments");
            return post;
        }
        return post;
    }

}
