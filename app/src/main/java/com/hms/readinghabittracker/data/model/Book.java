/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 * Generated by the CloudDB ObjectType compiler.  DO NOT EDIT!
 */
package com.hms.readinghabittracker.data.model;

import com.huawei.agconnect.cloud.database.annotations.PrimaryKeys;
import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.Text;

import java.util.Date;

/**
 * Definition of ObjectType Book.
 *
 * @since 2022-09-11
 */
@PrimaryKeys({"id"})
public final class Book extends CloudDBZoneObject {
    private Long id;

    private String title;

    private String author;

    private Integer pages;

    private byte[] image;

    private Long userId;

    private Long collectionId;

    public Book(Long id, String title, String author, Integer pages, byte[] image, Long userId, Long collectionId) {
        super(Book.class);
        this.id = id;
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.image = image;
        this.userId = userId;
        this.collectionId = collectionId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getPages() {
        return pages;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Long getCollectionId() {
        return collectionId;
    }

}
