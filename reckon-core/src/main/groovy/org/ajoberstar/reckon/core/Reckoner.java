/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajoberstar.reckon.core;

import java.util.Optional;
import java.util.function.Function;
import org.eclipse.jgit.lib.Repository;

public final class Reckoner {
  private Reckoner() {
    // do not instantiate
  }

  public static String reckon(
      Repository repo, Function<String, Optional<String>> tagSelector, Scope scope, String stage) {

    Inventory inventory = new InventoryService(repo, tagSelector).get();

    ReckonVersion targetNormal = reckonNormal(inventory, scope);
    ReckonVersion version = reckonStage(inventory, targetNormal, stage);

    if (inventory.getClaimedVersions().contains(version)) {
      throw new IllegalStateException(
          "Reckoned version " + version + " has already been released.");
    }

    return version.toString();
  }

  private static ReckonVersion reckonNormal(Inventory inventory, Scope scope) {
    ReckonVersion targetNormal = inventory.getBaseNormal().incrementNormal(scope);

    // if a version's already being developed on a parallel branch we'll skip it
    if (inventory.getParallelNormals().contains(targetNormal)) {
      targetNormal = targetNormal.incrementNormal(scope);
    }

    if (inventory.getClaimedVersions().contains(targetNormal)) {
      throw new IllegalStateException(
          "Reckoned target normal version " + targetNormal + " has already been released.");
    }
    return targetNormal;
  }

  private static ReckonVersion reckonStage(
      Inventory inventory, ReckonVersion targetNormal, String stage) {
    // TODO implement
    return targetNormal;
  }
}
