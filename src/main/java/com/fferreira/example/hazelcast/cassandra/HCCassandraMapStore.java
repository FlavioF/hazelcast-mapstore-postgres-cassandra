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
package com.fferreira.example.hazelcast.cassandra;

import com.fferreira.example.hazelcast.EventEntity;
import com.hazelcast.core.MapStore;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HCCassandraMapStore implements MapStore<String, String> {

  static final Logger log = LoggerFactory.getLogger(HCCassandraMapStore.class);
  
  private CassandraClient dao;


  @Override
  public void store(String key, String value) {
    log.info("Storing key " + key + " with value " + value);
    dao.persist(new EventEntity(key, value));
  }

  @Override
  public void storeAll(Map<String, String> map) {
    map.entrySet().stream().
        forEach((entrySet) -> {
          store(entrySet.getKey(), entrySet.getValue());
    });
  }

  @Override
  public void delete(String key) {
    log.info("Deleting key " + key);
    dao.remove(key);
  }

  @Override
  public void deleteAll(Collection<String> keys) {
    keys.stream().
        forEach((key) -> {
          delete(key);
    });
  }

  @Override
  public String load(String key) {
    log.info("Loading");
    final EventEntity event = dao.find(key);
    return event == null ? null : event.getEventData();
  }

  @Override
  public Map<String, String> loadAll(Collection<String> keys) {
    log.info("Loading All");
    final List<EventEntity> list = dao.findAll();
    final Map<String, String> map = new HashMap<>();
     list.stream().
     forEach((item) -> {
     map.put(item.getId(), item.getEventData());
     });
    return map;
  }

  @Override
  public Set<String> loadAllKeys() {
     final List<EventEntity> list = dao.findAll();
    final Set<String> set = new HashSet<>();
     list.stream().
     forEach((item) -> {
     set.add(item.getId());
     });
    return set;
  }

  public CassandraClient getDao() {
    return dao;
  }

  public void setDao(
      CassandraClient dao) {
    this.dao = dao;
  }

}
