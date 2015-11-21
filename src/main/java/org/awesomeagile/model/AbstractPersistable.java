package org.awesomeagile.model;

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
