/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.client;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ws.epigraph.server.http.servlet.AsyncEpigraphServlet;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;

import javax.servlet.ServletConfig;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AsyncServletGeneratedClientTest extends AbstractGeneratedClientTest {
  private static Server jettyServer;

  @BeforeClass
  public static void start() throws Exception {
    jettyServer = new Server(PORT);

    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping(TestServlet.class, "/*");

    jettyServer.setHandler(handler);
    jettyServer.start();
  }

  @AfterClass
  public static void stop() throws Exception {
    jettyServer.stop();
  }

  public static class TestServlet extends AsyncEpigraphServlet {
    @Override
    protected @NotNull Service initService(final ServletConfig config) throws ServiceInitializationException {
      return buildUsersService();
    }
  }
}
