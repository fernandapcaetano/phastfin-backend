package io.github.fernandapcaetano.phastfin_backend.statement.domain.entity;

import io.github.fernandapcaetano.phastfin_backend.commons.domain.Base;
import jakarta.persistence.Entity;
import org.springframework.util.Assert;

@Entity
public class Organization extends Base {

    private String name;
    private String code;
    private String imagePath;

    public Organization(String name, String code, String imagePath) {
        super();
        this.name = name;
        this.code = code;
        this.imagePath = imagePath;
    }

    public static Organization create(String name, String code, String imagePath){
        Assert.notNull(name, "Organization name cannot be null");
        Assert.hasText(name, "Organization name cannot be null");

        Assert.notNull(code, "Organization code cannot be null");
        Assert.hasText(code, "Organization code cannot be null");

        return new Organization(name, code, imagePath);
    }

    public void update(String name, String code, String imagePath){
        if (name != null && !name.isBlank())
            this.name = name;
        if (code != null && !code.isBlank())
            this.code = code;
        if (imagePath != null && !imagePath.isBlank())
            this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public String getCode() { return code; }
    public String getImagePath() { return imagePath; }
}
