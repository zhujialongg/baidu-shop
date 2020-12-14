package com.baidu.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Document(indexName = "goods",shards = 1,replicas = 0)
public class GoodsEntity {

    @Id
    private  Long id;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;

    @Field(type= FieldType.Keyword)
    private String category;

    @Field(type= FieldType.Keyword)
    private String brand;

    @Field(type= FieldType.Double)
    private Double price;

    @Field(index = false,type= FieldType.Keyword)
    private String image;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "GoodsEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", image='" + image + '\'' +
                '}';
    }
}
