package org.awesomeagile.integrations.hackpad;

import org.awesomeagile.model.document.PadIdentity;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public interface HackpadClient {

  String getHackpad(PadIdentity padIdentity);

  PadIdentity createHackpad(String title);

  void updateHackpad(PadIdentity padIdentity, String content);

  String fullUrl(String apiUrl);
}
