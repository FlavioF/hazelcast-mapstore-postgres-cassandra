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
package com.fferreira.example.hazelcast.mapstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.MapStore;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastMapStore<V extends Serializable> implements
    MapStore<String, V> {

  static final Logger log = LoggerFactory.getLogger(HazelcastMapStore.class);

  private HazelcastDao<EntryEntity> dao;
  private Class<V> valueClass;
  private Class<?>[] subClass;

  private final ObjectMapper mapper = new ObjectMapper();

  public HazelcastMapStore(Class<V> valueClass) {
    this.valueClass = valueClass;
  }

  public HazelcastMapStore(Class<V> valueClass, Class<?>... subClass) {
    this(valueClass);
    this.subClass = subClass;
  }

  public HazelcastMapStore(HazelcastDao<EntryEntity> dao, Class<V> valueClass,
      Class<?>... subClass) {
    this(valueClass, subClass);
    this.dao = dao;
  }

  @Override
  public void store(final String key, final V value) {
    log.info("Storing key " + key + " with value " + value);
    try {
      dao.persist(new EntryEntity(key, mapper.writeValueAsString(value)));
    } catch (JsonProcessingException ex) {
      log.error("Error parsing given object " + value, ex);
    }
  }

  @Override
  public void storeAll(final  Map<String, V> map) {
    map.entrySet().stream().
        forEach((entrySet) -> {
          store(entrySet.getKey(), entrySet.getValue());
    });
  }

  @Override
  public void delete(final String key) {
    log.info("Deleting key " + key);
    dao.remove(key);
  }

  @Override
  public void deleteAll(final Collection<String> keys) {
    keys.stream().
        forEach((key) -> {
          delete(key);
    });
  }

  @Override
  public V load(final String key) {
    log.info("Loading");
    final EntryEntity entry = dao.find(key);
    return entry == null ? null : fromJson(entry.getMessage());
  }

  @Override
  public Map<String, V> loadAll(Collection<String> keys) {
    log.info("Loading All");
    final Map<String, V> map = new HashMap<>();
    dao.findAll(keys).stream().
        forEach((entry) -> {
          map.put(entry.getId(), fromJson(entry.getMessage()));
    });
    return map;
  }

  @Override
  public Set<String> loadAllKeys() {
    final List<EntryEntity> list = dao.findAll();
    final Set<String> set = new HashSet<>();
     list.stream().
     forEach((item) -> {
      set.add(item.getId());
     });
    return set;
  }

  private V fromJson(final String json) {

    try {
      return subClass != null ? mapper.readValue(json, mapper.getTypeFactory()
          .constructParametricType(valueClass, subClass)) : mapper.readValue(
          json, valueClass);
    } catch (IOException ex) {
      log.error("Error deserializing object " + json, ex);
    }
    return null;
  }

  public HazelcastDao<EntryEntity> getDao() {
    return dao;
  }

  public void setDao(HazelcastDao<EntryEntity> dao) {
    this.dao = dao;
  }

}
