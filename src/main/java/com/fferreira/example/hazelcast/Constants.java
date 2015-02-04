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

public class Constants {

  public static final String POSTGRES_MAP_STORE = "postgres-map-store";
  public static final String CASSANDRA_MAP_STORE = "cassandra-map-store";

  public static final String CASSANDRA_KEYSPACE = "example";
  public static final String CASSANDRA_TABLE_NAME = "event";
  public static final String CASSANDRA_KEYSPACE_TABLE_NAME = CASSANDRA_KEYSPACE
      + "." + CASSANDRA_TABLE_NAME;

  public static final String HC_GROUP_NAME = "be-cep";
  public static final String HC_GROUP_PASSWORD = "cep";
  public static final int HC_PORT = 5701;
}
