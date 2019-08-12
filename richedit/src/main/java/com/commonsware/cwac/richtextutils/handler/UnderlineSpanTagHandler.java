/***
 Copyright (c) 2015 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.commonsware.cwac.richtextutils.handler;

import android.text.style.UnderlineSpan;
import com.commonsware.cwac.richtextutils.SpanTagHandler;
import org.xml.sax.Attributes;

public class UnderlineSpanTagHandler extends SpanTagHandler.Simple<UnderlineSpan> {
  private static final String[] TAGS={"u"};

  public UnderlineSpanTagHandler() {
    super("<u>", "</u>");
  }

  public String[] getSupportedTags() {
    return(TAGS);
  }

  public Class getSupportedCharacterStyle() {
    return(UnderlineSpan.class);
  }

  @Override
  public UnderlineSpan buildSpanForTag(String name, Attributes a, String context) {
    return(new UnderlineSpan());
  }
}
