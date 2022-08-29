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
 * Definition of ObjectType Collection.
 *
 * @since 2022-08-29
 */
@PrimaryKeys({"id"})
public final class Collection extends CloudDBZoneObject {
    private Integer id;

    private String name;

    private Long userId;

    public Collection() {
        super(Collection.class);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
