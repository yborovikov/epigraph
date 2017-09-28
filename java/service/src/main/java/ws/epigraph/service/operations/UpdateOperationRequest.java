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

package ws.epigraph.service.operations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.projections.req.ReqFieldProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UpdateOperationRequest extends OperationRequest {
  private final @NotNull Data data;
  private final @Nullable ReqFieldProjection updateProjection;

  public UpdateOperationRequest(
      final @Nullable ReqFieldProjection path,
      final @NotNull Data data,
      final @Nullable ReqFieldProjection updateProjection,
      final @NotNull ReqFieldProjection outputProjection) {

    super(path, outputProjection);
    this.data = data;
    this.updateProjection = updateProjection;
  }

  public @Nullable ReqFieldProjection updateProjection() { return updateProjection; }

  public @NotNull Data data() { return data; }
}
