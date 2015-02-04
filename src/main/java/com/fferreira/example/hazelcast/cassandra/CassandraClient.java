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

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.fferreira.example.hazelcast.Constants;
import com.fferreira.example.hazelcast.EventEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraClient {

  static final Logger log = LoggerFactory.getLogger(CassandraClient.class);

  private Cluster cluster;

  public void initialize(String node) {
    cluster = Cluster.builder().addContactPoint(node).build();
    final Metadata metadata = cluster.getMetadata();
    log.info("Connected to cluster: {}", metadata.getClusterName());
    metadata.getAllHosts().stream().
        forEach((host) -> {
          log.info("Datacenter: {}; Host: {}; Rack: {}",
              host.getDatacenter(), host.getAddress(), host.getRack());
    });
  }

  public void persist(final EventEntity value) {
    connect().execute(//
        "INSERT INTO " + Constants.CASSANDRA_KEYSPACE_TABLE_NAME//
            + " (id, data) "//
            + "VALUES ('" + value.getId() + "','" + value.getEventData() //
            + "');");
  }

  public void remove(String key) {
    connect().execute(//
        "DELETE FROM  " + Constants.CASSANDRA_KEYSPACE_TABLE_NAME//
            + " WHERE id = '" + key + "';");
  }

  public EventEntity find(String key) {
    ResultSet res = connect().execute(//
        "SELECT * FROM  " + Constants.CASSANDRA_KEYSPACE_TABLE_NAME//
            + " WHERE id = '" + key + "';");
    if (res.getAvailableWithoutFetching() > 0) {
      final Row r = res.one();
      return new EventEntity(r.getString(0), r.getString(1));
    }
    return null;
  }

  public List<EventEntity> findAll() {
    final ResultSet rowList = connect().execute(
    "SELECT * FROM " + Constants.CASSANDRA_KEYSPACE_TABLE_NAME + ";");
    if(rowList.getAvailableWithoutFetching()>0) {
      final List<EventEntity> result = new  ArrayList<>();
      rowList.all().stream().
          forEach((row) -> {
            result.add(new EventEntity(row.getString(0), row.getString(1)));
      });
      return result;
    }
    return Collections.EMPTY_LIST;
  }

  public Session connect() {
    return cluster.connect();
  }

  public void close() {
    cluster.close();
  }

}
