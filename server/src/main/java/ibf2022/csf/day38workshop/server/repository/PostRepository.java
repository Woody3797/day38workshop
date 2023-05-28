package ibf2022.csf.day38workshop.server.repository;

import java.io.IOException;

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

        mongoTemplate.upsert(query, update, "posts");
    }

    public String updateLikes(int likes, int dislikes) {
        Long post = redisTemplate.opsForHash().size(SpacesRepository.key);
        if (post == 0) {
            redisTemplate.opsForHash().put(SpacesRepository.key, "likes", String.valueOf(likes));
            redisTemplate.opsForHash().put(SpacesRepository.key, "dislikes", String.valueOf(dislikes));
            System.out.println(">>>>>>>>>>>>>>>>>>>>>> key "+SpacesRepository.key);
        } else {
            redisTemplate.opsForHash().increment(SpacesRepository.key, "likes", likes);
            redisTemplate.opsForHash().increment(SpacesRepository.key, "dislikes", dislikes);
        }
        return SpacesRepository.key;
    }

    public Post getLikes(String key) {
        Integer likes = (Integer) redisTemplate.opsForHash().get(key, "likes");
        Integer dislikes = (Integer) redisTemplate.opsForHash().get(key, "dislikes");
        Post post = new Post("", likes, dislikes, key);
        return post;
    }

}
