package org.awesomeagile.model;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * @author sbelov@google.com (Stan Belov)
 */
@MappedSuperclass
public class AbstractPersistable<PK extends Serializable> implements Persistable<PK> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
  private PK id;

  @Transient
  public boolean isNew() {
    return null == this.getId();
  }

  @Override
  public PK getId() {
    return id;
  }

  public void setId(PK id) {
    this.id = id;
  }

}
