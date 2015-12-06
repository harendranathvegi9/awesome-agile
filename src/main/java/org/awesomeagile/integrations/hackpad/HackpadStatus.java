package org.awesomeagile.integrations.hackpad;

/*
 * ================================================================================================
 * Awesome Agile
 * %%
 * Copyright (C) 2015 Mark Warren, Phillip Heller, Matt Kubej, Linghong Chen, Stanislav Belov, Qanit Al
 * %%
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
 * ------------------------------------------------------------------------------------------------
 */

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * An object to represent status responses for Hackpad API calls
 * @author phheller
 *
 */
public class HackpadStatus {
    @JsonProperty
    private boolean success;

    public HackpadStatus(boolean success) {
        this.success = success;
    }

    /**
     * Empty constructor for JSON deserialization
     */
    public HackpadStatus() {
    }

    /**
     * Indicate call success
     * @return True on a successful call, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HackpadStatus that = (HackpadStatus) o;
        return Objects.equals(success, that.success);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("success", success).toString();
    }
}
