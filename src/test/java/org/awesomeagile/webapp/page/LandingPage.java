package org.awesomeagile.webapp.page;

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

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Encapsulates AwesomeAgile landing page for WebDriver-based testing purposes.
 *
 * @author sbelov@google.com (Stan Belov)
 */
public class LandingPage {

  private static final int TIMEOUT = 5;
  @FindBy(id = "login")
  private WebElement loginButton;

  @FindBy(className = "btn-google")
  private WebElement googleButton;

  @FindBy(id = "userName")
  private WebElement userName;

  @FindBy(id = "btnCreateDefReady")
  private WebElement createDefinitionOfReadyButton;

  @FindBy(id = "btnViewDefReady")
  private WebElement viewDefinitionOfReadyButton;

  @FindBy(id = "btnCreateDefDone")
  private WebElement createDefinitionOfDoneButton;

  @FindBy(id = "btnViewDefDone")
  private WebElement viewDefinitionOfDoneButton;

  private final WebDriver driver;
  private final WebDriverWait wait;

  public LandingPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, TIMEOUT);
  }

  public LandingPage loginWithGoogle(String endPoint) {
    driver.get(endPoint);
    wait.until(ExpectedConditions.visibilityOf(loginButton));
    loginButton.click();
    wait.until(ExpectedConditions.visibilityOf(googleButton));
    googleButton.click();
    wait.until(ExpectedConditions.urlContains(endPoint));
    wait.until(ExpectedConditions.visibilityOf(userName));
    return this;
  }

  public LandingPage createDefinitionOfReady() {
    wait.until(ExpectedConditions.visibilityOf(createDefinitionOfReadyButton));
    createDefinitionOfReadyButton.click();
    wait.until(ExpectedConditions.visibilityOf(viewDefinitionOfReadyButton));
    return this;
  }

  public LandingPage createDefinitionOfDone() {
    wait.until(ExpectedConditions.visibilityOf(createDefinitionOfDoneButton));
    createDefinitionOfDoneButton.click();
    wait.until(ExpectedConditions.visibilityOf(viewDefinitionOfDoneButton));
    return this;
  }

  public LandingPage viewDefinitionOfReady() {
    int windowCount = driver.getWindowHandles().size();
    viewDefinitionOfReadyButton.click();
    wait.until(ExpectedConditions.numberOfWindowsToBe(windowCount + 1));
    return this;
  }

  public LandingPage viewDefinitionOfDone() {
    int windowCount = driver.getWindowHandles().size();
    viewDefinitionOfDoneButton.click();
    wait.until(ExpectedConditions.numberOfWindowsToBe(windowCount + 1));
    return this;
  }

  public String getUserName() {
    return userName.getText();
  }

  public boolean isLoginButtonVisible() {
    try {
      return loginButton.isDisplayed();
    } catch (NoSuchElementException ex) {
      return false;
    }
  }

  public LandingPage waitForDefinitionOfReady() {
    wait.until(ExpectedConditions.visibilityOf(viewDefinitionOfReadyButton));
    return this;
  }

  public boolean isDefinitionOfReadyViewable() {
    try {
      return viewDefinitionOfReadyButton.isDisplayed() &&
          !createDefinitionOfReadyButton.isDisplayed();
    } catch (NoSuchElementException ex) {
      return false;
    }
  }

  public LandingPage waitForDefinitionOfDone() {
    wait.until(ExpectedConditions.visibilityOf(viewDefinitionOfDoneButton));
    return this;
  }

  public boolean isDefinitionOfDoneViewable() {
    try {
      return viewDefinitionOfDoneButton.isDisplayed() &&
          !createDefinitionOfDoneButton.isDisplayed();
    } catch (NoSuchElementException ex) {
      return false;
    }
  }
}
