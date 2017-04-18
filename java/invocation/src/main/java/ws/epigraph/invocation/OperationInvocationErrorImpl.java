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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OperationInvocationErrorImpl implements OperationInvocationError {
  private final @NotNull String message;
  private final @NotNull Status status;

  public OperationInvocationErrorImpl(
      final @NotNull String message,
      final @NotNull Status status) {
    this.message = message;
    this.status = status;
  }

  @Override
  public @NotNull String message() { return message; }

  @Override
  public @NotNull OperationInvocationError.Status status() { return status; }
}
