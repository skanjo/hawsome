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

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.Option;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Hawsome {

  public static void main(String[] args) {
    try {
      final CLI cli = createCLI();
      final CommandLine line = parseArgs(args, cli);

      if (line.isAskingForHelp()) {
        System.out.println(usage(cli));
        System.exit(0);
      }

      if (line.isFlagEnabled("version")) {
        System.out.println(version());
        System.exit(0);
      }

      final JsonObject config = loadOrCreateDefaultConfig(line.getOptionValue("conf"));

      final VertxOptions vOpt = new VertxOptions();
      vOpt.setBlockedThreadCheckInterval(60000);

      final Vertx v = Vertx.vertx(vOpt);

      final DeploymentOptions dOpt = new DeploymentOptions();
      dOpt.setConfig(config);

      v.deployVerticle(new HttpServerVerticle(), dOpt);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> v.close(event -> {
        if (event.succeeded()) {
          System.out.println("shut 'er down");
        } else {
          System.out.println("Failed to shutdown gracefully");
        }
      })));
    } catch (Exception e) {
      System.err.println(e.getMessage());
      usage(createCLI());
    }
  }

  private static CommandLine parseArgs(String[] args, CLI cli) {
    CommandLine line = null;
    try {
      line = cli.parse(Arrays.asList(args));

    } catch (CLIException e) {
      System.err.println(e.getMessage());
      throw new RuntimeException(usage(cli));
    }

    return line;
  }

  private static CLI createCLI() {
    return CLI.create("java -server -jar hawsome.jar")
      .setSummary("An awesome java web server! Haw haw!")
      .addOption(new Option()
        .setShortName("conf")
        .setDescription("path to configuration file"))
      .addOption(new Option()
        .setShortName("version")
        .setDescription("Display the version")
        .setFlag(true)
        .setHelp(false))
      .addOption(new Option()
        .setShortName("help")
        .setDescription("Display this message")
        .setFlag(true)
        .setHelp(true));
  }

  private static String usage(CLI cli) {
    StringBuilder builder = new StringBuilder();
    cli.usage(builder);
    return builder.toString();
  }

  private static String version() {
    return "Hawsome version " + Version.buildVersion();
  }

  private static JsonObject loadOrCreateDefaultConfig(String confFilePath) {
    final JsonObject config;
    if (confFilePath == null) {
      config = new JsonObject();
    } else {
      final File f = new File(confFilePath);
      if (f.exists() && f.canRead()) {
        try {
          final Path p = FileSystems.getDefault().getPath(f.getAbsolutePath());
          byte[] bytes = Files.readAllBytes(p);
          Buffer buff = Buffer.buffer(bytes);
          config = new JsonObject(buff);
        } catch (IOException e) {
          throw new RuntimeException("Unable to read file " + confFilePath);
        }

      } else {
        throw new RuntimeException("Config file " + confFilePath + " does not exist or is not readable.");
      }
    }

    return config;
  }

}
