/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.fferreira.example.hazelcast;

import com.hazelcast.query.Predicate;
import java.util.Map;

public class EventWithMessagePredicate implements
    Predicate<String, String> {

  private final String event;

  public EventWithMessagePredicate(String event) {
    this.event = event;
  }

  @Override
  public boolean apply(Map.Entry<String, String> mapEntry) {
    return mapEntry.getValue().equalsIgnoreCase(event);
  }

}
