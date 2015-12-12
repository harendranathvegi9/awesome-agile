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


import com.google.common.base.MoreObjects;

import org.awesomeagile.model.document.DocumentType;
import org.awesomeagile.model.team.User;

import java.util.Map;
import java.util.Objects;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class Dashboard {

  private User user;
  private Map<DocumentType, String> documents;

  public Dashboard(User user,
      Map<DocumentType, String> documents) {
    this.user = user;
    this.documents = documents;
  }

  public Dashboard() {
  }

  public User getUser() {
    return user;
  }

  public Dashboard setUser(User user) {
    this.user = user;
    return this;
  }

  public Map<DocumentType, String> getDocuments() {
    return documents;
  }

  public Dashboard setDocuments(
      Map<DocumentType, String> documents) {
    this.documents = documents;
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
    Dashboard dashboard = (Dashboard) o;
    return Objects.equals(user, dashboard.user)
        && Objects.equals(documents, dashboard.documents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, documents);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("user", user)
        .add("documents", documents)
        .toString();
  }
}
