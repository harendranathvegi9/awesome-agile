package org.awesomeagile.integrations.hackpad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class HackpadStatusTest {

  @Test
  public void testIsSuccess() throws Exception {
    HackpadStatus hackpadStatus = new HackpadStatus(true);
    assertTrue(hackpadStatus.isSuccess());
    hackpadStatus = new HackpadStatus(false);
    assertFalse(hackpadStatus.isSuccess());
  }

  @Test
  public void testEquals() throws Exception {
    HackpadStatus one = new HackpadStatus(true);
    HackpadStatus two = new HackpadStatus(true);
    HackpadStatus three = new HackpadStatus(false);
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
    HackpadStatus one = new HackpadStatus(true);
    HackpadStatus two = new HackpadStatus(true);
    assertEquals(one.hashCode(), two.hashCode());
  }

  @Test
  public void testToString() throws Exception {
    HackpadStatus one = new HackpadStatus(true);
    assertTrue(one.toString().contains("true"));
  }
}