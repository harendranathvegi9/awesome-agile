package org.awesomeagile.testutils;

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
