/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkus.security.deployment;

import java.util.Collections;
import java.util.Set;

import io.quarkus.builder.item.MultiBuildItem;

/**
 * A {@link MultiBuildItem} holding cipher transformations to be explicitly
 * registered as security services. Extensions should provide all cipher transformations
 * that are reachable at runtime. Those cipher transformations will be explicitly instantiated
 * at bootstrap so that graal can proceed with security services automatic registration.
 */
public final class CipherTransformationBuildItem extends MultiBuildItem {

    private final Set<String> cipherTransformations;

    public CipherTransformationBuildItem(Set<String> cipherTransformations) {
        this.cipherTransformations = cipherTransformations;
    }

    public Set<String> getCipherTransformations() {
        return Collections.unmodifiableSet(cipherTransformations);
    };

}
