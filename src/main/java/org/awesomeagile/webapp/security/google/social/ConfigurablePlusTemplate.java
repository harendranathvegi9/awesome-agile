package org.awesomeagile.webapp.security.google.social;

/*
 * ================================================================================================
 * Awesome Agile
 * %%
 * Copyright (C) 2015 Mark Warren, Phillip Heller, Matt Kubej, Linghong Chen, Stanislav Belov, Qanit Al
 * Copyright (C) 2011 Gabriel Axel
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

import static org.springframework.util.StringUtils.hasText;

import org.springframework.social.google.api.impl.AbstractGoogleApiOperations;
import org.springframework.social.google.api.plus.ActivitiesPage;
import org.springframework.social.google.api.plus.Activity;
import org.springframework.social.google.api.plus.ActivityComment;
import org.springframework.social.google.api.plus.ActivityCommentsPage;
import org.springframework.social.google.api.plus.ActivityQueryBuilder;
import org.springframework.social.google.api.plus.PeoplePage;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.api.plus.PersonQueryBuilder;
import org.springframework.social.google.api.plus.PlusOperations;
import org.springframework.social.google.api.plus.impl.ActivityQueryBuilderImpl;
import org.springframework.social.google.api.plus.impl.PersonQueryBuilderImpl;
import org.springframework.social.google.api.plus.moments.Moment;
import org.springframework.social.google.api.plus.moments.MomentQueryBuilder;
import org.springframework.social.google.api.plus.moments.MomentsPage;
import org.springframework.social.google.api.plus.moments.impl.MomentQueryBuilderImpl;
import org.springframework.web.client.RestTemplate;

/**
 * {@link PlusOperations} implementation.
 * @author sbelov@google.com (Stan Belov)
 */
public class ConfigurablePlusTemplate extends AbstractGoogleApiOperations
    implements PlusOperations {

  private static final String PEOPLE_URL = "plus/v1/people/";
  private static final String ACTIVITIES_PUBLIC = "/activities/public";
  private static final String ACTIVITIES_URL = "plus/v1/activities/";

  private static final String COMMENTS_URL = "plus/v1/comments/";
  private static final String COMMENTS = "/comments";

  private static final String PLUSONERS = "/people/plusoners";
  private static final String RESHARERS = "/people/resharers";

  private static final String MOMENTS_URL = PEOPLE_URL + "me/moments/vault";

  private final String baseUrl;

  public ConfigurablePlusTemplate(RestTemplate restTemplate, boolean isAuthorized, String baseUrl) {
    super(restTemplate, isAuthorized);
    this.baseUrl = baseUrl;
  }

  @Override
  public Activity getActivity(String id) {
    return getEntity(activitiesBaseUrl().append(id).toString(), Activity.class);
  }

  @Override
  public ActivitiesPage getActivities(String userId, String pageToken) {
    StringBuilder sb = peopleBaseUrl()
        .append(userId)
        .append(ACTIVITIES_PUBLIC);
    if(pageToken != null) {
      sb.append("?pageToken=").append(pageToken);
    }
    return getEntity(sb.toString(), ActivitiesPage.class);
  }

  @Override
  public ActivitiesPage getActivities(String userId) {
    return getActivities(userId, null);
  }

  @Override
  public ActivitiesPage searchPublicActivities(String query, String pageToken) {
    return activityQuery().searchFor(query).fromPage(pageToken).getPage();
  }

  @Override
  public ActivityQueryBuilder activityQuery() {
    return new ActivityQueryBuilderImpl(restTemplate);
  }

  @Override
  public ActivityComment getComment(String id) {
    return getEntity(commentsBaseUrl().append(id).toString(), ActivityComment.class);
  }

  @Override
  public ActivityCommentsPage getComments(String activityId, String pageToken) {
    StringBuilder sb = activitiesBaseUrl().append(activityId).append(COMMENTS);
    if(hasText(pageToken)) {
      sb.append("?pageToken=").append(pageToken);
    }
    return getEntity(sb.toString(), ActivityCommentsPage.class);
  }

  @Override
  public Person getPerson(String id) {
    return getEntity(peopleBaseUrl().append(id).toString(), Person.class);
  }

  @Override
  public Person getGoogleProfile() {
    return getPerson("me");
  }

  @Override
  public PersonQueryBuilder personQuery() {
    return new PersonQueryBuilderImpl(restTemplate);
  }

  @Override
  public PeoplePage getPeopleInCircles(String id, String pageToken) {
    StringBuilder sb = peopleBaseUrl().append(id).append("/people/visible");
    if(hasText(pageToken)) {
      sb.append("?pageToken=").append(pageToken);
    }
    return getEntity(sb.toString(), PeoplePage.class);
  }

  @Override
  public PeoplePage searchPeople(String query, String pageToken) {
    return personQuery().searchFor(query).fromPage(pageToken).getPage();
  }

  @Override
  public PeoplePage getActivityPlusOners(String activityId, String pageToken) {
    return getEntity(activitiesBaseUrl().append(activityId).append(PLUSONERS).toString(),
        PeoplePage.class);
  }

  @Override
  public PeoplePage getActivityResharers(String activityId, String pageToken) {
    return getEntity(activitiesBaseUrl().append(activityId).append(RESHARERS).toString(),
        PeoplePage.class);
  }

  @Override
  public Moment insertMoment(Moment moment) {
    return saveEntity(currentUserMomentsBaseUrl().toString(), moment);
  }

  @Override
  public MomentQueryBuilder momentQuery() {
    return new MomentQueryBuilderImpl(currentUserMomentsBaseUrl().toString(), restTemplate);
  }

  @Override
  public MomentsPage getMoments(String pageToken) {
    return momentQuery().getPage();
  }

  @Override
  public void deleteMoment(String id) {
    deleteEntity(baseUrl + "plus/v1/moments", id);
  }

  private StringBuilder peopleBaseUrl() {
    return new StringBuilder(baseUrl)
        .append(PEOPLE_URL);
  }

  private StringBuilder activitiesBaseUrl() {
    return new StringBuilder(baseUrl).append(ACTIVITIES_URL);
  }

  private StringBuilder currentUserMomentsBaseUrl() {
    return new StringBuilder(baseUrl).append(MOMENTS_URL);
  }

  private StringBuilder commentsBaseUrl() {
    return new StringBuilder(baseUrl).append(COMMENTS_URL);
  }
}
