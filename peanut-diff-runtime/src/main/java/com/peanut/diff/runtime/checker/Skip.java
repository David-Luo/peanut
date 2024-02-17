package com.peanut.diff.runtime.checker;

import java.util.Optional;

import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.diff.runtime.Difference;

public class Skip<T>   implements ChangeChecker<T> {
    @Override
    public Optional<Difference<T>> check(T left, T right) {
        return Optional.empty();
    }
}
