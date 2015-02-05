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
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastWorker {

  // members
  static final Logger log = LoggerFactory.getLogger(HazelcastWorker.class);
  private final IMap<String, User> usersMap;
  private final HazelcastInstance hcInstance;

  public HazelcastWorker(final String datastore) {
    log.info("Creating Hazelcast CEP worker..");
    hcInstance = HazelcastClient.newHazelcastClient(getConfig());
    usersMap = hcInstance.getMap(datastore);
    log.info("Created CEP worker.");
  }

  public void addUser(final String id, final User message) {
    log.info("Storing user {} with id {}", message, id);
    usersMap.put(id, message);
  }

  public void removeUser(final String id) {
    log.info("Removing user {}", id);
    usersMap.remove(id);
  }

  public Set<String> getKeys() {
    return usersMap.keySet();
  }

  public User getUser(final String id) {
    return usersMap.get(id);
  }

  public Collection<User> getUsersByFirstName(final String firstName) {

    log.info("Finding user with firtname {}.", firstName);
    // retrieve interested subscriber types
    Collection<User> users = Collections.EMPTY_SET;

    try {
      final UserByFirstNamePredicate predicate = new UserByFirstNamePredicate(
          firstName);
      users = usersMap.getAll(usersMap.keySet(predicate)).values();
      log.info("Found {} users with firstname {} .", users.size(), firstName);

    } catch (OperationTimeoutException ote) {
      log.error("Hazelcast cluster is borked, so return empty set", ote);
    }

    return users;
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
