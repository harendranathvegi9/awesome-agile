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

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * A model to store attributes about documents created within AwesomeAgile
 * @author sbelov@google.com (Stan Belov)
 */
public class CreatedDocument {
  private final String url;

  /**
   * Construct a new CreatedDocument object
   * @param url The URL where the document can be found
   */
  public CreatedDocument(String url) {
    this.url = url;
  }

  /**
   * Get a CreatedDocument's URL
   * @return The document's URL in String format.
   */
  public String getUrl() {
    return url;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreatedDocument that = (CreatedDocument) o;
    return Objects.equals(url, that.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("url", url)
        .toString();
  }
}
