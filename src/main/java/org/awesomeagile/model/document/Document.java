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

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

import org.awesomeagile.model.AbstractAuditable;
import org.awesomeagile.model.team.User;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "document", schema = "teams")
@SequenceGenerator(name = "idgen", sequenceName = "teams.document_id_seq")
@EntityListeners(AuditingEntityListener.class)
public class Document extends AbstractAuditable<Long> {

    public static Function<Document, DocumentType> GET_TYPE =
        new Function<Document, DocumentType>() {
        @Override
        public DocumentType apply(Document input) {
            return input.getDocumentType();
        }
    };
    
    @NotEmpty
    @Column(unique = true, nullable = false, updatable = false)
    private String url;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private DocumentType documentType;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public Document() {
    }

    public Document(DocumentType documentType, String url) {
        this.documentType = documentType;
        this.url = url;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Document document = (Document) o;
        return Objects.equals(url, document.url)
            && Objects.equals(documentType, document.documentType)
            && Objects.equals(user, document.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, documentType, user);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("url", url)
            .add("documentType", documentType)
            .add("user", user)
            .toString();
    }
}
