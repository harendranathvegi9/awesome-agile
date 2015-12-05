package org.awesomeagile.model.document;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class HackpadDocumentTemplate {

  private final String title;
  private final PadIdentity padIdentity;

  public HackpadDocumentTemplate(String title, PadIdentity padIdentity) {
    this.title = title;
    this.padIdentity = padIdentity;
  }

  public String getTitle() {

    return title;
  }

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
