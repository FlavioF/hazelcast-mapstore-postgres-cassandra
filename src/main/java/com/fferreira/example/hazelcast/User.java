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

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

  private String firtName;
  private String lastName;
  private String country;

  public User() {
  }

  public User(String firtName, String lastName, String country) {
    this.firtName = firtName;
    this.lastName = lastName;
    this.country = country;
  }

  public String getFirtName() {
    return firtName;
  }

  public void setFirtName(String firtName) {
    this.firtName = firtName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @Override
  public String toString() {
    return "User{" + "firtName=" + firtName + ", lastName=" + lastName
        + ", country=" + country + '}';
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 89 * hash + Objects.hashCode(this.firtName);
    hash = 89 * hash + Objects.hashCode(this.lastName);
    hash = 89 * hash + Objects.hashCode(this.country);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    final User user = (User) obj;
    return Objects.equals(this.firtName, user.firtName)
        && Objects.equals(this.lastName, user.lastName)
        && Objects.equals(this.country, user.country);
  }

}
