package com.armedia.acm.data.model;

import com.armedia.acm.data.annotations.Encrypted;

import javax.persistence.Column;

/**
 * Created by nebojsha on 06.08.2015.
 */

public class TestEntity {

    @Column(name = "id")
    private Long id;

    @Encrypted
    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Encrypted
    @Column(name = "gender")
    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
