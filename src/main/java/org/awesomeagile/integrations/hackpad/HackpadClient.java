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

/**
 * @author sbelov@google.com (Stan Belov)
 */
public interface HackpadClient {

  /**
   * Returns the content of a Hackpad
   * @param padIdentity The padId returned from a Hackpad create call.
   * @return The Hackpad content
   */
  String getHackpad(PadIdentity padIdentity);

  /**
   * Creates a Hackpad
   * @param title The title of the Hackpad, which will be embedded in the URL
   * @return a PadIdentity object, from which the padId can be retrieved
   */
  PadIdentity createHackpad(String title);

  /**
   * Update a Hackpad's content
   * @param padIdentity The padId returned from a Hackpad create call.
   * @param content The content that will replace the Hackpad's current content.
   */
  void updateHackpad(PadIdentity padIdentity, String content);

  /**
   * Construct an absolute URL for a Hackpad resource
   * @param apiUrl The API path, relative to the client base URL.
   * @return The concatenation of the client base URL and the API path.
   */
  String fullUrl(String apiUrl);
}
