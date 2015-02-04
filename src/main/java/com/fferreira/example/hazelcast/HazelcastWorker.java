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

import com.fferreira.example.hazelcast.Constants;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastWorker {

  // members
  static final Logger log = LoggerFactory.getLogger(HazelcastWorker.class);
  private IMap<String, String> subscribedEvents;
  private HazelcastInstance hcInstance;

  // public API
  public HazelcastWorker(final String datastore) {

    log.info("Creating Hazelcast CEP worker..");
    hcInstance = HazelcastClient.newHazelcastClient(getConfig());
    subscribedEvents = hcInstance.getMap(datastore);
    log.info("Created CEP worker.");
  }

  /**
   * Registers a subscriber instance and all events related to its type.
   *
   * @param subscriberClassName
   *          the subcriber class name.
   * @param eventsToSubscribe
   *          the events to be registered for this subscriber type.
   */
  public void addSubscriber(final String subscriberClassName,
      String eventsToSubscribe) {

    log.info("Storing {} events for subscriber type {}",
        eventsToSubscribe, subscriberClassName);
    subscribedEvents.put(subscriberClassName, eventsToSubscribe);
  }

  public void removeSubscriber(final String subscriberClassName) {

    log.info("Removing events for subscriber type {}", subscriberClassName);
    subscribedEvents.remove(subscriberClassName);
  }

  public Set<String> getSubscribers() {

    return subscribedEvents.keySet();
  }
  
    public String getEvent(String key){
    return subscribedEvents.get(key);
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
