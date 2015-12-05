package org.awesomeagile.model.document;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class CreatedDocument {
  private final String url;

  public CreatedDocument(String url) {
    this.url = url;
  }

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
