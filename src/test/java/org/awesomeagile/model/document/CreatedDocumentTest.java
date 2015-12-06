package org.awesomeagile.model.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class CreatedDocumentTest {

  @Test
  public void testGetUrl() throws Exception {
    String url = RandomStringUtils.randomAlphanumeric(8);
    CreatedDocument createdDocument = new CreatedDocument(url);
    assertEquals(url, createdDocument.getUrl());
  }

  @Test
  public void testEquals() throws Exception {
    String urlOne = RandomStringUtils.randomAlphanumeric(8);
    String urlThree = RandomStringUtils.randomAlphanumeric(8);
    CreatedDocument one = new CreatedDocument(urlOne);
    CreatedDocument two = new CreatedDocument(urlOne);
    CreatedDocument three = new CreatedDocument(urlThree);
    assertTrue(one.equals(one));
    assertTrue(one.equals(two));
    assertTrue(two.equals(one));
    assertFalse(three.equals(one));
    assertFalse(two.equals(three));
    assertFalse(one.equals("123"));
    assertFalse(one.equals(null));
  }

  @Test
  public void testHashCode() throws Exception {
    String urlOne = RandomStringUtils.randomAlphanumeric(8);
    CreatedDocument one = new CreatedDocument(urlOne);
    CreatedDocument two = new CreatedDocument(urlOne);
    assertEquals(one.hashCode(), two.hashCode());
  }

  @Test
  public void testToString() throws Exception {
    String urlOne = RandomStringUtils.randomAlphanumeric(8);
    CreatedDocument one = new CreatedDocument(urlOne);
    assertTrue(one.toString().contains(urlOne));
  }
}