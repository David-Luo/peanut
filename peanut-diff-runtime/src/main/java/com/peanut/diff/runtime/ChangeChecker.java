package com.peanut.diff.runtime;

import java.util.Optional;

public interface ChangeChecker<T> {
    Optional<Difference<T>> check(T left, T right);

}
