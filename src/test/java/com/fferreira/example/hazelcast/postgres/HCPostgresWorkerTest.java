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
package com.fferreira.example.hazelcast.postgres;

import com.fferreira.example.hazelcast.EventEntity;
import com.fferreira.example.hazelcast.Constants;
import com.fferreira.example.hazelcast.HazelcastStore;
import com.fferreira.example.hazelcast.HazelcastWorker;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(suiteName = "Hazelcast Postgres Worker Test", priority = 1)
public class HCPostgresWorkerTest {

  private static final String CREATE_GROUP = "create_group";
  private static final String RUD_GROUP = "rud_group";
  private static final String COLD_START_GROUP = "cold_start_group";

  private EventEntityDao dao;
  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;
  private EntityTransaction tx;

  private HazelcastWorker worker;
  private HazelcastStore store;
  private HCPostgresMapStore mapStore;

  // data to be shared in test
  private String id;
  private String event;
  private int counter = 0;

  @BeforeClass
  public void setUpClass() throws Exception {
    entityManagerFactory = Persistence
        .createEntityManagerFactory("postgres_test");
    entityManager = entityManagerFactory.createEntityManager();
    entityManager.setFlushMode(FlushModeType.COMMIT);
    entityManager.clear();
    tx = entityManager.getTransaction();

    // provision daos
    dao = new EventEntityDao();
    dao.setEntityManager(entityManager);

    mapStore = new HCPostgresMapStore();
    mapStore.setDao(dao);
    store = new HazelcastStore(mapStore, Constants.POSTGRES_MAP_STORE);
    worker = new HazelcastWorker(Constants.POSTGRES_MAP_STORE);
  }

  @AfterClass
  public void tearDownClass() throws Exception {
    entityManager.close();
    entityManagerFactory.close();
    // stoping hazelcast instance and client
    store.destroy();
    worker.destroy();
  }

  @Test(groups = CREATE_GROUP, invocationCount = 3)
  public void test_add_event() throws InterruptedException {
    counter++;
    id = "event_postgres_" + counter + "_" + UUID.randomUUID();
    event = "Event Postgres Data " + counter;
    try {
      tx.begin();
      worker.addSubscriber(id, event);
      // just give time to it since it is async
      Thread.sleep(2000);
      tx.commit();
    } catch (Exception ex) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw ex;
    }
    assertEquals(dao.count(), counter);
  }

  @Test(dependsOnGroups = CREATE_GROUP, groups = RUD_GROUP)
  public void test_remove_subscriber() throws Exception {
    try {
      tx.begin();
      worker.removeSubscriber(id);
      // just give time to it since it is async
      Thread.sleep(2000);
      counter--;
      tx.commit();
    } catch (Exception ex) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw ex;
    }

    assertEquals(dao.count(), counter);

    // getting valid values to id and event
    final EventEntity res = dao.findAll().get(0);
    id = res.getId();
    event = res.getEventData();
  }

  @Test(dependsOnGroups = CREATE_GROUP, groups = RUD_GROUP)
  public void test_get_event_data() {
    assertEquals(worker.getEvent(id), event);
  }

  @Test(dependsOnGroups = RUD_GROUP, groups = COLD_START_GROUP)
  public void test_get_event_data_after_instance_down() {
    store.destroy();
    worker.destroy();

    // initializing a new instance to test cold start
    store = new HazelcastStore(mapStore, Constants.POSTGRES_MAP_STORE);
    worker = new HazelcastWorker(Constants.POSTGRES_MAP_STORE);

    assertEquals(worker.getEvent(id), event);
  }

}
