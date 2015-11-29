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
import org.springframework.web.client.RestTemplate;

/**
 * {@link PlusOperations} implementation.
 * @author sbelov@google.com (Stan Belov)
 */
public class ConfigurablePlusTemplate extends AbstractGoogleApiOperations
    implements PlusOperations {

  private static final String PEOPLE_URL = "plus/v1/people/";

  private final String baseUrl;

  public ConfigurablePlusTemplate(RestTemplate restTemplate, boolean isAuthorized, String baseUrl) {
    super(restTemplate, isAuthorized);
    this.baseUrl = baseUrl;
  }

  @Override
  public Activity getActivity(String id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ActivitiesPage getActivities(String userId, String pageToken) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ActivitiesPage getActivities(String userId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ActivitiesPage searchPublicActivities(String query, String pageToken) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ActivityQueryBuilder activityQuery() {
    return new ActivityQueryBuilderImpl(restTemplate);
  }

  @Override
  public ActivityComment getComment(String id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ActivityCommentsPage getComments(String activityId, String pageToken) {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  @Override
  public PeoplePage searchPeople(String query, String pageToken) {
    throw new UnsupportedOperationException();
  }

  @Override
  public PeoplePage getActivityPlusOners(String activityId, String pageToken) {
    throw new UnsupportedOperationException();
  }

  @Override
  public PeoplePage getActivityResharers(String activityId, String pageToken) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Moment insertMoment(Moment moment) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MomentQueryBuilder momentQuery() {
    throw new UnsupportedOperationException();
  }

  @Override
  public MomentsPage getMoments(String pageToken) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteMoment(String id) {
    throw new UnsupportedOperationException();
  }

  private StringBuilder peopleBaseUrl() {
    return new StringBuilder(baseUrl).append(PEOPLE_URL);
  }
}
