package com.biit.ks.core.models;

import com.biit.server.controllers.models.ElementDTO;

public class MyEntityDTO extends ElementDTO {
    private String name = "";

    public MyEntityDTO() {
        super();
    }

    public MyEntityDTO(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
