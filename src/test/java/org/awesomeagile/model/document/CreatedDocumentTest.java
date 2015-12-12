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