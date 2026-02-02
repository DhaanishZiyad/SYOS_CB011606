package com.sypos.domain.valueobjects;

import java.util.Objects;

public final class ItemCode {

    private final String value;

    public ItemCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Item code cannot be null or empty");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemCode)) return false;
        ItemCode itemCode = (ItemCode) o;
        return value.equals(itemCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
