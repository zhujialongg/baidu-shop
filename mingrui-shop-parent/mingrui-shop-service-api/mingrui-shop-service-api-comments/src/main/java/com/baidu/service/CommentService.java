package com.baidu.service;

import com.baidu.base.Result;
import com.baidu.entity.Review;
import com.baidu.util.CommentRequestParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@Api(tags = "评论接口")
public interface CommentService  {



    @ApiOperation(value = "发布评论信息")
    @PostMapping(value = "commment/findReviewBySpuId")
    Result<JSONObject> addReview(@RequestBody Review  review);


    @ApiOperation(value = "根据id删除评论信息")
    @DeleteMapping(value = "commment/findReviewBySpuId/{id}")
    Result<JSONObject> deleteReview(@PathVariable("id") String id);

    @ApiOperation(value = "查询评论信息")
    @GetMapping
    Result<Review> searchReview();

}
