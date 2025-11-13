package io.github.fernandapcaetano.phastfin_backend.statement.domain.entity;

import io.github.fernandapcaetano.phastfin_backend.commons.domain.Base;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"user\"")
public class User extends Base {
    private final String email;

    protected User(String email) {
        super();
        this.email = email;
    }

    public static User create(String email){
        return new User(email);
    }

    public String getEmail() { return email; }
}
