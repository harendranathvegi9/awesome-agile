package org.awesomeagile.model.document;

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
 * An object to hold Hackpad Document Identities
 * @author sbelov@google.com (Stan Belov)
 */
public class PadIdentity {

    @JsonProperty
    private String padId;

    public PadIdentity() {
    }

    /**
     * Create an instance of PadIdentity
     * @param padId The padId as returned by Hackpad
     */
    public PadIdentity(String padId) {
        this.padId = padId;
    }

    /**
     * Get the padId for a given document
     * @return the padId as a String
     */
    public String getPadId() {
        return padId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PadIdentity that = (PadIdentity) o;
        return Objects.equals(padId, that.padId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(padId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("padId", padId).toString();
    }
}
