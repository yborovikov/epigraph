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

package ws.epigraph.invocation;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.util.EBean;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DefaultOperationInvocationContext implements OperationInvocationContext {
  private final boolean debug;
  private final @NotNull EBean storage;

  public DefaultOperationInvocationContext(final boolean debug, final @NotNull EBean storage) {
    this.debug = debug;
    this.storage = storage;
  }

  @Override
  public boolean isDebug() {
    return debug;
  }

  @Override
  public @NotNull EBean storage() {
    return storage;
  }
}
