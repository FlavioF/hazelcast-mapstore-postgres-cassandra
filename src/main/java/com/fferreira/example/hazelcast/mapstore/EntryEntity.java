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

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * This entity represents an user file
 */
@Entity
@Table(name = "HZEntry")
public class EntryEntity implements Serializable {

  private static final long serialVersionUID = 6387663732432366117L;

  @Id
  private String id;

  @Version
  private Long version;

  private String message;

  public EntryEntity() {
  }

  public EntryEntity(String id, String message) {
    this.id = id;
    this.message = message;
  }
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }



}
