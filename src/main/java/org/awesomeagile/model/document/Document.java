package org.awesomeagile.model.document;

/*
 * ================================================================================================
 * Awesome Agile
 * %%
 * Copyright (C) 2015 Mark Warren, Phillip Heller, Matt Kubej, Linghong Chen, Stanislav Belov, Qanit Al
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------------------------------
 */

import javax.persistence.*;

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
    @Enumerated(EnumType.STRING)
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

    public Document setUrl(String url) {
        this.url = url;
        return this;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public Document setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Document setUser(User user) {
        this.user = user;
        return this;
    }

}
