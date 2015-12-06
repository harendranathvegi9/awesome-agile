package org.awesomeagile.integrations.hackpad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class PadIdentityTest {

  @Test
  public void testGetIdentity() throws Exception {
    String idOne = RandomStringUtils.randomAlphanumeric(8);
    PadIdentity one = new PadIdentity(idOne);
    assertEquals(idOne, one.getPadId());
  }

  @Test
  public void testEquals() throws Exception {
    String idOne = RandomStringUtils.randomAlphanumeric(8);
    String idTwo = RandomStringUtils.randomAlphanumeric(8);
    PadIdentity one = new PadIdentity(idOne);
    PadIdentity two = new PadIdentity(idOne);
    PadIdentity three = new PadIdentity(idTwo);
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
    String idOne = RandomStringUtils.randomAlphanumeric(8);
    PadIdentity one = new PadIdentity(idOne);
    PadIdentity two = new PadIdentity(idOne);
    assertEquals(one.hashCode(), two.hashCode());
  }

  @Test
  public void testToString() throws Exception {
    String idOne = RandomStringUtils.randomAlphanumeric(8);
    PadIdentity one = new PadIdentity(idOne);
    assertTrue(one.toString().contains(idOne));
  }

}