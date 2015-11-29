package org.awesomeagile.webapp.security.google.social;

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

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.api.plus.moments.AddActivity;

import java.util.Map;

public class ConfigurablePlusTemplateTest extends AbstractGoogleApiTest {

  @Test
  public void getPersonById() {
    mockServer
        .expect(requestTo("https://www.googleapis.com/plus/v1/people/114863353858610846998"))
        .andExpect(method(GET))
        .andRespond(
            withSuccess(jsonResource("person"), APPLICATION_JSON));
    Person person = google.plusOperations().getPerson(
        "114863353858610846998");
    assertPerson(person);
  }

  @Test
  public void getSelfProfile() {
    mockServer
        .expect(requestTo("https://www.googleapis.com/plus/v1/people/me"))
        .andExpect(method(GET))
        .andRespond(
            withSuccess(jsonResource("person"), APPLICATION_JSON));
    Person person = google.plusOperations().getGoogleProfile();
    assertPerson(person);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void searchPeople() {
    google.plusOperations().searchPeople("pivotal", null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getActivityPlusoners() {
    google.plusOperations().getActivityPlusOners("123", null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getActivityResharers() {
    google.plusOperations().getActivityResharers("123", null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getPeopleInCircles() {
    google.plusOperations().getPeopleInCircles("234", null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void searchPublicActivities() {
    google.plusOperations().searchPublicActivities("spring social", null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getActivity() {
    google.plusOperations().getActivity("z13djjbraz2fdfp5g04chb0rkrvwhnmpch4");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void listComments() {
    google.plusOperations().getComments("z12ge3o4orj2sdkbb04chb0rkrvwhnmpch4", null);}

  @Test(expected = UnsupportedOperationException.class)
  public void getComment() {
    google.plusOperations().getComment("z12ge3o4orj2sdkbb04chb0rkrvwhnmpch4.1364410988265921");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void listMoments() {
    google.plusOperations().getMoments(null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void insertMoment() {
    google
        .plusOperations()
        .insertMoment(
            new AddActivity(
                "https://developers.google.com/+/plugins/snippet/examples/thing"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void deleteMoment() {
    google.plusOperations()
        .deleteMoment(
            "Eg0xNDAwNDE2MjQ1ODY2GJa6uYvcss-izgEpCHuQgEQo0AkyAhAUQgcY1OXS0c8e");
  }

  private void assertPerson(Person person) {
    assertNotNull(person);
    assertEquals("114863353858610846998", person.getId());
    assertEquals("Gabriel", person.getGivenName());
    assertEquals("Axel", person.getFamilyName());
    assertEquals("Gabriel Axel", person.getDisplayName());
    assertEquals("male", person.getGender());
    assertEquals(
        "CTO and co-founder of <a href=\"https://www.docollab.com\" rel=\"nofollow\" target=\"_blank\">Docollab</a><br />",
        person.getAboutMe());
    assertEquals("Software Engineer", person.getOccupation());
    assertEquals("single", person.getRelationshipStatus());
    assertEquals(
        "https://lh5.googleusercontent.com/-UyuMuAWmKIM/AAAAAAAAAAI/AAAAAAAAAn0/pMK2DzFNBNI/photo.jpg?sz=50",
        person.getImageUrl());

    Map<String, Boolean> expectedPlacesLived = singletonMap("Israel", true);
    assertEquals(expectedPlacesLived, person.getPlacesLived());
    assertEquals("guznik@gmail.com", person.getAccountEmail());
    assertEquals("https://plus.google.com/+GabrielAxel", person.getUrl());
    assertTrue(person.isPlusUser());
  }
}
