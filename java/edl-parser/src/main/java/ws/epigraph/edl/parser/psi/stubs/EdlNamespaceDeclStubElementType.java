/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.edl.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import ws.epigraph.edl.parser.EdlLanguage;
import ws.epigraph.edl.parser.psi.EdlNamespaceDecl;
import ws.epigraph.edl.parser.psi.impl.EdlNamespaceDeclImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlNamespaceDeclStubElementType extends IStubElementType<EdlNamespaceDeclStub, EdlNamespaceDecl> {
  public EdlNamespaceDeclStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, EdlLanguage.INSTANCE);
  }

  @Override
  public EdlNamespaceDecl createPsi(@NotNull EdlNamespaceDeclStub stub) {
    return new EdlNamespaceDeclImpl(stub, this);
  }

  @Override
  public EdlNamespaceDeclStub createStub(@NotNull EdlNamespaceDecl namespaceDecl, StubElement parentStub) {
    return new EdlNamespaceDeclStubImpl(parentStub, namespaceDecl.getFqn());
  }

  @Override
  public @NotNull String getExternalId() {
    return "epigraph_edl.namespace";
  }

  @Override
  public void serialize(@NotNull EdlNamespaceDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @Override
  public @NotNull EdlNamespaceDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void indexStub(@NotNull EdlNamespaceDeclStub stub, @NotNull IndexSink sink) {
  }
}