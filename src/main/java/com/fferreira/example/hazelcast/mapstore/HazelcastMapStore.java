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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hazelcast.core.MapStore;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastMapStore<K extends Serializable, V extends Serializable>
    implements MapStore<K, V> {

  static final Logger log = LoggerFactory.getLogger(HazelcastMapStore.class);

  private HazelcastDao<EntryEntity> dao;
  private final ObjectMapper mapper;

  private Class<K> keyClass;
  private Class<V> valueClass;

  public HazelcastMapStore(Class<K> keyClass, Class<V> valueClass) {
    mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    this.keyClass = keyClass;
    this.valueClass = valueClass;
  }

  public HazelcastMapStore(Class<K> keyClass, Class<V> valueClass,
      HazelcastDao<EntryEntity> dao) {
    this(keyClass, valueClass);
    this.dao = dao;
  }

  @Override
  public void store(final K key, final V value) {
    log.info("Storing key " + key + " with value " + value);
    try {
      dao.persist(new EntryEntity(keyToJson(key), mapper
          .writeValueAsString(value)));
    } catch (JsonProcessingException ex) {
      log.error("Error parsing given object " + value, ex);
    }
  }

  @Override
  public void storeAll(final  Map<K, V> map) {
    map.entrySet().stream().
        forEach((entrySet) -> {
          store(entrySet.getKey(), entrySet.getValue());
    });
  }

  @Override
  public void delete(final K key) {
    log.info("Deleting key " + key);
    dao.remove(keyToJson(key));
  }

  @Override
  public void deleteAll(final Collection<K> keys) {
    keys.stream().
        forEach((key) -> {
          delete(key);
    });
  }

  @Override
  public V load(final K key) {
    log.info("Loading");
    final EntryEntity entry = dao.find(keyToJson(key));
    return entry == null ? null : valueFromJson(entry.getMessage());
  }

  @Override
  public Map<K, V> loadAll(final Collection<K> keys) {
    log.info("Loading All");
    final Map<K, V> map = new HashMap<>();
    dao.findAll(//
        keys.stream()//
            .map(it -> keyToJson(it))//
            .collect(Collectors.toList())//
        ).stream().
        forEach((entry) -> {
          map.put(keyFromJson(entry.getId()), valueFromJson(entry.getMessage()));
    });
    return map;
  }

  @Override
  public Set<K> loadAllKeys() {
    final List<EntryEntity> list = dao.findAll();
    final Set<K> set = new HashSet<>();
     list.stream().
     forEach((item) -> {
      set.add(keyFromJson(item.getId()));
     });
    return set;
  }

  private V valueFromJson(final String json) {

    try {
      return mapper.readValue(json, valueClass);
    } catch (IOException ex) {
      log.error("Error deserializing object " + json, ex);
    }
    return null;
  }

  private K keyFromJson(final String json) {
    K res = null;
    if (keyClass.equals(String.class)) {
      res = (K) json;
    } else {
      try {
        res = mapper.readValue(json, keyClass);
      } catch (IOException ex) {
        log.error("Error deserializing object " + json, ex);
      }
    }
    return res;
  }

  private String keyToJson(final K key) {
    String res = null;
    if (key.getClass().equals(String.class)) {
      res = (String) key;
    } else {
      try {
        res = mapper.writeValueAsString(key);
      } catch (IOException ex) {
        log.error("Error deserializing object " + key, ex);
      }
    }
    return res;
  }

  public HazelcastDao<EntryEntity> getDao() {
    return dao;
  }

  public void setDao(HazelcastDao<EntryEntity> dao) {
    this.dao = dao;
  }

}
