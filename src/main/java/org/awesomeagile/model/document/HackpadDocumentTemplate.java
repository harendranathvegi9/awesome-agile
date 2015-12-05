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
 * A Document template stored within Hackpad
 * @author sbelov@google.com (Stan Belov)
 */
public class HackpadDocumentTemplate {

  private final String title;
  private final PadIdentity padIdentity;

  /**
   * Create a Hackpad Document Template
   * @param title The name of the document.
   * @param padIdentity The padId of the document on Hackpad (relative to the
   *    URL set for the hackpad client)
   */
  public HackpadDocumentTemplate(String title, PadIdentity padIdentity) {
    this.title = title;
    this.padIdentity = padIdentity;
  }

  /**
   * Get the Hackpad Document Template title
   * @return The Title in String format
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get a PadIdentity instance referring to the document template
   * @return an instance of PadIdentity from which the padId can be retrieved
   */
  public PadIdentity getPadIdentity() {
    return padIdentity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HackpadDocumentTemplate that = (HackpadDocumentTemplate) o;
    return Objects.equals(title, that.title)
        && Objects.equals(padIdentity, that.padIdentity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, padIdentity);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("title", title)
        .add("padIdentity", padIdentity)
        .toString();
  }
}
