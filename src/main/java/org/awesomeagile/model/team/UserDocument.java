package org.awesomeagile.model.team;

import com.google.common.base.MoreObjects;

import org.awesomeagile.model.AbstractAuditable;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@Entity
@Table(name = "user", schema = "teams")
@SequenceGenerator(name = "idgen", sequenceName = "teams.user_id_seq")
@EntityListeners(AuditingEntityListener.class)
public class UserDocument extends AbstractAuditable<Long> {

  @NotEmpty
  @Column(nullable = false)
  private Long userId;

  @NotEmpty
  @Column(nullable = false)
  private String documentType;

  @NotEmpty
  @Column(nullable = false)
  private String documentUrl;

  public Long getUserId() {
    return userId;
  }

  public UserDocument setUserId(Long userId) {
    this.userId = userId;
    return this;
  }

  public String getDocumentType() {
    return documentType;
  }

  public UserDocument setDocumentType(String documentType) {
    this.documentType = documentType;
    return this;
  }

  public String getDocumentUrl() {
    return documentUrl;
  }

  public UserDocument setDocumentUrl(String documentUrl) {
    this.documentUrl = documentUrl;
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
    UserDocument that = (UserDocument) o;
    return Objects.equals(userId, that.userId)
        && Objects.equals(documentType, that.documentType)
        && Objects.equals(documentUrl, that.documentUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, documentType, documentUrl);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("userId", userId)
        .add("documentType", documentType)
        .add("documentUrl", documentUrl)
        .toString();
  }
}
