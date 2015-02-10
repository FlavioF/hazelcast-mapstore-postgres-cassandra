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
package com.fferreira.example.hazelcast.mapstore.postgres;

import com.fferreira.example.hazelcast.mapstore.EntryEntity;
import com.fferreira.example.hazelcast.mapstore.HazelcastDao;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * DAO for {@link RoleEntity}.
 */
public class EntryEntityDao extends AbstractDao<EntryEntity> implements
    HazelcastDao<EntryEntity> {

  public EntryEntityDao() {
    super(EntryEntity.class);
  }

  @Override
  public List<EntryEntity> findAll(Collection<String> ids) {
    final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    final CriteriaQuery<EntryEntity> cq = cb.createQuery(entityClass);
    final Root<EntryEntity> root = cq.from(entityClass);
    cq.select(root);
    final List<Predicate> orPredicates = new ArrayList<>();
    ids.stream().
        forEach((id) -> {
          orPredicates.add(cb.equal(root.get("id"), id));
    });
    cq.where(cb.or(orPredicates
        .toArray(new Predicate[orPredicates.size()])));

    return getEntityManager().createQuery(cq).getResultList();
  }
}