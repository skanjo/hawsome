/*
 * Copyright (C) 2017 Samer Kanjo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawsome.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

public class Version {

  private static final Version instance = new Version();

  private Properties allProperties;
  private String buildVersion;
  private String buildDate;
  private String commitIdDescribe;

  private Version() {
    load();
  }

  private void load() {
    URL url = Version.class.getClassLoader().getResource("git.properties");
    if (url == null) {
      buildVersion = "Unknown";
      buildDate = new Date().toString();

    } else {
      allProperties = new Properties();
      try (InputStream is = url.openStream()) {
        allProperties.load(is);
        buildVersion = allProperties.getProperty("git.build.version", "Unknown");
        buildDate = allProperties.getProperty("git.build.time", new Date().toString());
        commitIdDescribe = allProperties.getProperty("git.commit.id.describe", "non-git build");

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static String buildVersion() {
    return instance.buildVersion;
  }

  public static String buildDate() {
    return instance.buildDate;
  }

  public static String gitCommitIdDescribe() {
    return instance.commitIdDescribe;
  }

}
