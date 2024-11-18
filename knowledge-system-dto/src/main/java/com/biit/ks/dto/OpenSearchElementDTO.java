package com.biit.ks.dto;

import java.io.Serial;

public abstract class OpenSearchElementDTO<U> extends ElementDTO<U> {

    @Serial
    private static final long serialVersionUID = 329004941572818061L;

    private String name;
    private String description;
    private boolean isPublic = false;

    public OpenSearchElementDTO() {
        super();
    }


    public OpenSearchElementDTO(String name) {
        super();
        setName(name);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
