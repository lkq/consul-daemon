package com.github.lkq.smesh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParamTest {

    @Test
    void willPassPositiveCheckWithPositiveValue() {
        Param.isPositive(1, "test");
    }

    @Test
    void willFailPositiveCheckWithZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Param.isPositive(0, "test"));
    }

    @Test
    void willFailPositiveCheckWithNegativeValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Param.isPositive(-1, "test"));
    }

    @Test
    void willPassNullCheckWithNonNullReference() {
        Object obj = new Object();
        Param.isNotNull(obj, "test");
    }

    @Test
    void willFailNullCheckWithNullReference() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Param.isNotNull(null, "test"));
    }
}