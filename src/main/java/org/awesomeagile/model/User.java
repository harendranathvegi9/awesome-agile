package org.awesomeagile.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "aa_user")
@SequenceGenerator(name = "idgen", sequenceName = "aa_user_id_seq")
public class User extends AbstractAuditable<Long> {

    @NotEmpty
    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    @NotNull
    @Column(nullable = false)
    private Boolean enabled;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}

