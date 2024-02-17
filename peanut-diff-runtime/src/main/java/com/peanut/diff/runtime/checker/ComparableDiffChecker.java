package com.peanut.diff.runtime.checker;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ComparableDiffChecker<T extends Comparable> extends DefaultDiffChecker<T> {

    @Override
    public boolean equals(T left, T right){
        return left.compareTo(right)==0;
    }
}
