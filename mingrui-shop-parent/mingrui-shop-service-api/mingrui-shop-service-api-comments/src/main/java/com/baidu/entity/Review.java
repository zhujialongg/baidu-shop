package com.baidu.entity;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;



@Data
@Document(collection = "review")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String _id;


    private String skuid;


    private String content;
    /**
     * 评论时间
     */
    private String calltime;
    /**
     * 评论用户id
     */
    private String userid;
    /**
     * 评论用户昵称
     */
    private String nickname;
    /**
     * 评论的浏览量
     */
    private Integer visits;

    public Review() {
    }

    public Review(String _id, String skuid, String content, String calltime, String userid, String nickname, Integer visits) {
        this._id = _id;
        this.skuid = skuid;
        this.content = content;
        this.calltime = calltime;
        this.userid = userid;
        this.nickname = nickname;
        this.visits = visits;
    }
}
