package org.awesomeagile.webapp.test.google;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class Person {

  public static class Name {
    @JsonProperty
    private final String givenName;

    @JsonProperty
    private final String familyName;

    public Name(String givenName, String familyName) {
      this.givenName = givenName;
      this.familyName = familyName;
    }
  }

  public static class Image {
    @JsonProperty
    private final String url;

    public Image(String url) {
      this.url = url;
    }
  }

  public static class Email {

    @JsonProperty
    private final  String value;

    @JsonProperty
    private final String type;

    public Email(String value, String type) {
      this.value = value;
      this.type = type;
    }
  }

  @JsonProperty
  private String id;

  @JsonProperty
  private Name name;

  @JsonProperty
  private String displayName;

  @JsonProperty
  private Image image;

  @JsonProperty
  private String thumbnailUrl;

  @JsonProperty
  private List<Email> emails;

  public String getId() {
    return id;
  }

  public Person setId(String id) {
    this.id = id;
    return this;
  }

  public Name getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Image getImage() {
    return image;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public List<Email> getEmails() {
    return emails;
  }

  public Person setEmails(List<Email> emails) {
    this.emails = emails;
    return this;
  }

  public Person setName(Name name) {
    this.name = name;
    return this;
  }

  public Person setDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public Person setImage(Image image) {
    this.image = image;
    return this;
  }

  public Person setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
    return this;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("displayName", displayName)
        .add("image", image)
        .add("thumbnailUrl", thumbnailUrl)
        .add("emails", emails)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person person = (Person) o;
    return Objects.equals(name, person.name)
        && Objects.equals(displayName, person.displayName)
        && Objects.equals(image, person.image)
        && Objects.equals(thumbnailUrl, person.thumbnailUrl)
        && Objects.equals(emails, person.emails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, displayName, image, thumbnailUrl, emails);
  }
}
