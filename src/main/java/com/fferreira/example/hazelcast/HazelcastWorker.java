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

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.OperationTimeoutException;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastWorker {

  // members
  static final Logger log = LoggerFactory.getLogger(HazelcastWorker.class);
  private final IMap<String, String> subscribedEvents;
  private final HazelcastInstance hcInstance;

  public HazelcastWorker(final String datastore) {
    log.info("Creating Hazelcast CEP worker..");
    hcInstance = HazelcastClient.newHazelcastClient(getConfig());
    subscribedEvents = hcInstance.getMap(datastore);
    log.info("Created CEP worker.");
  }

  public void addSubscriber(final String id, String message) {
    log.info("Storing {} for event {}", message, id);
    subscribedEvents.put(id, message);
  }

  public void removeSubscriber(final String id) {
    log.info("Removing event {}", id);
    subscribedEvents.remove(id);
  }

  public Set<String> getSubscribers() {
    return subscribedEvents.keySet();
  }

  public String getEvent(final String id) {
    return subscribedEvents.get(id);
  }

  public Set<String> getEventsWithMessage(final String message) {

    log.info("Finding events with message {}.", message);
    // retrieve interested subscriber types
    Set<String> events = Collections.EMPTY_SET;

    try {
      final EventWithMessagePredicate predicate = new EventWithMessagePredicate(
          message);
      events = subscribedEvents.keySet(predicate);
      log.info("Found {} events with message {} .", events.size(), message);

    } catch (OperationTimeoutException ote) {
      log.error("Hazelcast cluster is borked, so return empty set", ote);
    }

    return events;
  }

  public void destroy() {

    log.info("Shutting down Hazelcast client [{}]..", hcInstance.getName());
    if (hcInstance != null) {
      hcInstance.shutdown();
    }
  }

  // private API
  private ClientConfig getConfig() {
    final ClientConfig clientConfig = new ClientConfig();
    clientConfig.addAddress(String.format("127.0.0.1:%s", Constants.HC_PORT));
    clientConfig.setGroupConfig(new GroupConfig(Constants.HC_GROUP_NAME,
        Constants.HC_GROUP_PASSWORD));
    clientConfig.setClassLoader(getClass().getClassLoader());

    return clientConfig;
  }
}
