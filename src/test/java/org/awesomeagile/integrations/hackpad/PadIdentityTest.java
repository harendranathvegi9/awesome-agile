package org.awesomeagile.integrations.hackpad;

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