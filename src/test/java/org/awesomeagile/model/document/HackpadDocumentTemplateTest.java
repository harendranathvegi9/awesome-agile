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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.awesomeagile.integrations.hackpad.PadIdentity;
import org.junit.Test;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class HackpadDocumentTemplateTest {

  @Test
  public void testGetTitle() throws Exception {
    PadIdentity padIdOne = padId();
    HackpadDocumentTemplate one = new HackpadDocumentTemplate("title one", padIdOne);
    assertEquals("title one", one.getTitle());
  }

  @Test
  public void testGetPadIdentity() throws Exception {
    PadIdentity padIdOne = padId();
    HackpadDocumentTemplate one = new HackpadDocumentTemplate("title one", padIdOne);
    assertEquals(padIdOne, one.getPadIdentity());
  }

  @Test
  public void testEquals() throws Exception {
    PadIdentity padIdOne = padId();
    PadIdentity padIdTwo = padId();
    HackpadDocumentTemplate one = new HackpadDocumentTemplate("title one", padIdOne);
    HackpadDocumentTemplate two = new HackpadDocumentTemplate("title one", padIdTwo);
    HackpadDocumentTemplate three = new HackpadDocumentTemplate("title one", padIdOne);
    HackpadDocumentTemplate four = new HackpadDocumentTemplate("title two", padIdOne);
    assertTrue(one.equals(one));
    assertTrue(one.equals(three));
    assertTrue(three.equals(one));
    assertFalse(one.equals(two));
    assertFalse(one.equals(four));
    assertFalse(one.equals("jakldf"));
    assertFalse(one.equals(null));
  }

  @Test
  public void testHashCode() throws Exception {
    PadIdentity padIdOne = padId();
    HackpadDocumentTemplate one = new HackpadDocumentTemplate("title one", padIdOne);
    HackpadDocumentTemplate two = new HackpadDocumentTemplate("title one", padIdOne);
    assertEquals(one.hashCode(), two.hashCode());
  }

  @Test
  public void testToString() throws Exception {
    PadIdentity padIdOne = padId();
    HackpadDocumentTemplate one = new HackpadDocumentTemplate("title one", padIdOne);
    assertTrue(one.toString().contains(padIdOne.getPadId()));
    assertTrue(one.toString().contains("title one"));
  }

  private static PadIdentity padId() {
    return new PadIdentity(RandomStringUtils.randomAlphanumeric(8));
  }
}