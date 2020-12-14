package com.baidu.shop.mapper;


import com.baidu.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentMapper extends MongoRepository<Review,String> {

}
