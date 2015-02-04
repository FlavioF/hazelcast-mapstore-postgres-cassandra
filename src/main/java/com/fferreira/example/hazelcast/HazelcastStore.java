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

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapStore;
import java.util.Properties;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastStore {

  // members
  static final Logger log = LoggerFactory.getLogger(HazelcastStore.class);
  private String hcInstanceName;
  private final MapStore store;
  private final String storeType;

  // public API
  public HazelcastStore(final MapStore store, final String storeType) {
    this.store = store;
    this.storeType = storeType;
    log.info("Creating Hazelcast CEP instance..");
    hcInstanceName = UUID.randomUUID().toString();
    Hazelcast.newHazelcastInstance(getConfig());
    log.info("Created CEP instance.");
  }

  public Config getConfig() {

    final Config cfg = new Config();
    cfg.setInstanceName(hcInstanceName);

    final Properties props = new Properties();
    props.put("hazelcast.rest.enabled", false);
    props.put("hazelcast.logging.type", "slf4j");
    props.put("hazelcast.connect.all.wait.seconds", 45);
    props.put("hazelcast.operation.call.timeout.millis", 30000);

    // group configuration
    cfg.setGroupConfig(new GroupConfig(Constants.HC_GROUP_NAME,
        Constants.HC_GROUP_PASSWORD));
    // network configuration initialization
    final NetworkConfig netCfg = new NetworkConfig();
    netCfg.setPortAutoIncrement(true);
    netCfg.setPort(Constants.HC_PORT);
    // multicast
    final MulticastConfig mcCfg = new MulticastConfig();
    mcCfg.setEnabled(false);
    // tcp
    final TcpIpConfig tcpCfg = new TcpIpConfig();
    tcpCfg.addMember("127.0.0.1");
    tcpCfg.setEnabled(true);
    // network join configuration
    final JoinConfig joinCfg = new JoinConfig();
    joinCfg.setMulticastConfig(mcCfg);
    joinCfg.setTcpIpConfig(tcpCfg);
    netCfg.setJoin(joinCfg);
    // ssl
    netCfg.setSSLConfig(new SSLConfig().setEnabled(false));

    /*
     * Beware, there be dragons..
     * 
     * Hazelcast uses ExecutorService for running parallelized queries (one per
     * partition). Each runnable, when executed, will try and use TCCL which
     * doesn't work good in Karaf. Thank god, Hazelcast provides a way to set
     * the classloader to use.
     * 
     * See
     * http://apache-felix.18485.x6.nabble.com/Can-the-thread-context-classloader
     * -issue-be-solved-at-all-td4835872.html
     */
    cfg.setClassLoader(getClass().getClassLoader());

    // Adding mapstore
    final MapConfig mapCfg = cfg.getMapConfig(storeType);

    final MapStoreConfig mapStoreCfg = new MapStoreConfig();
    mapStoreCfg.setImplementation(store);
    mapStoreCfg.setWriteDelaySeconds(1);
    mapCfg.setMapStoreConfig(mapStoreCfg);
    cfg.addMapConfig(mapCfg);
    return cfg;
  }

  /**
   * Destroys currently allocated instance.
   */
  public void destroy() {

    log.info("Shutting down Hazelcast instance [{}]..", hcInstanceName);
    final HazelcastInstance instance = Hazelcast
        .getHazelcastInstanceByName(hcInstanceName);
    if (instance != null) {
      instance.shutdown();
    }
  }

}
