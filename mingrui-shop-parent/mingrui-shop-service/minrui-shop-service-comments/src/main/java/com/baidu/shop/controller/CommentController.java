package com.baidu.shop.controller;

import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.config.IdWorkerConfig;
import com.baidu.entity.Review;
import com.baidu.service.CommentService;
import com.baidu.shop.mapper.CommentMapper;
import com.baidu.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@RestController
@Slf4j
public class CommentController extends BaseApiService  implements CommentService {


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public Result<Review> searchReview() {
        List<Review> list = commentMapper.findAll();

        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONObject> addReview(Review review) {

        review.set_id(idWorker.nextId() +"");
        review.setNickname(review.getNickname());
        review.setCalltime(review.getCalltime());
        review.setContent(review.getContent());
        review.setUserid(review.getUserid());
        review.setVisits(111);

       commentMapper.save(review);

        return this.setResultSuccess();

    }

    @Override
    public Result<JSONObject> deleteReview(String id) {

        commentMapper.deleteById(id);

        return this.setResultSuccess();
    }
}
