package org.awesomeagile.testutils;

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

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Various networking-related utility methods.
 *
 * @author sbelov@google.com (Stan Belov)
 */
public class NetworkUtils {

  /**
   * Finds the first available TCP server port.
   * @return first available TCP server port number.
   * @throws IOException
   */
  public static int findAvailablePort() throws IOException {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(0);
      return serverSocket.getLocalPort();
    } finally {
      if (serverSocket != null) {
        serverSocket.close();
      }
    }
  }
}
