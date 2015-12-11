package org.awesomeagile.model.document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.awesomeagile.model.AbstractAuditable;
import org.awesomeagile.model.team.User;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "document", schema = "teams")
@SequenceGenerator(name = "idgen", sequenceName = "teams.document_id_seq")
@EntityListeners(AuditingEntityListener.class)
public class Document extends AbstractAuditable<Long> {
    
    @NotEmpty
    @Column(unique = true, nullable = false, updatable = false)
    private String url;
    
    @NotEmpty
    @Column(nullable = false, updatable=false)
    private DocumentType documentType;
    
    @NotEmpty
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;
    
    public Document() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
