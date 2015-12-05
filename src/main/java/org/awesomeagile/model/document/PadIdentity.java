package org.awesomeagile.model.document;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author sbelov@google.com (Stan Belov)
 */
public class PadIdentity {

    @JsonProperty
    private String padId;

    public PadIdentity() {
    }

    public PadIdentity(String padId) {
        this.padId = padId;
    }

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
