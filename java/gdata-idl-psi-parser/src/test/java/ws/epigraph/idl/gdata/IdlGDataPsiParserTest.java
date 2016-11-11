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

package ws.epigraph.idl.gdata;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.gdata.GDataValue;
import ws.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import ws.epigraph.idl.parser.psi.IdlDataValue;
import ws.epigraph.psi.EpigraphPsiUtil;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlGDataPsiParserTest {
  @Test
  public void testGDataVarParsing() {
    String expression = "fooType < a: { a1 : \"a\", a2 : <>}, \n" +
                        "b: mapType( 1 : 1, 2: <tag: true>), \n" +
                        "c: listType[1,2,false,\"33\", <q:#w>], \n" +
                        "d: #enum, \n" +
                        "e: q.w.someType@3.14, \n" +
                        "f: false,\n" +
                        "g: \"foobar\">";
    String expected =
        "fooType<a: {a1: \"a\", a2: <>}, " +
        "b: mapType(1: 1, 2: <tag: true>), " +
        "c: listType[1, 2, false, \"33\", <q: #w>], " +
        "d: #enum, " +
        "e: q.w.someType@3.14, " +
        "f: false, " +
        "g: \"foobar\">";
    testParse(expression, expected);
  }

  private void testParse(String dataStr, String expectedToString) {
    GDataValue gDataValue = parseGDataValue(dataStr);
    assertEquals(expectedToString, gDataValue.toString());
  }

  public static GDataValue parseGDataValue(String dataStr) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull IdlDataValue dataValue = EpigraphPsiUtil.parseText(
        dataStr,
        IdlSubParserDefinitions.DATA_VALUE,
        errorsAccumulator
    );

    failIfHasErrors(dataValue, errorsAccumulator);

    return runPsiParser(errors -> IdlGDataPsiParser.parseValue(dataValue, errors));
  }
}
