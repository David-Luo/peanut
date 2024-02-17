package com.peanut.diff.runtime;

import java.util.List;
import java.util.stream.Collectors;
@SuppressWarnings({ "rawtypes"})
public class ListDifference<E> extends Difference<List<E>>{
    private List<IndexDifference> differences;
    public ListDifference() {
    }
    public ListDifference(List<E> before, List<E> after, List<IndexDifference> differences) {
        super(before, after, ChangeType.modify);
        this.differences = differences;
    }
    public List<IndexDifference> getDifferences() {
        return differences;
    }

    public static class IndexDifference<E> {
        private int index;
        private Difference<E> difference;
        public IndexDifference(Difference<E> difference, int index){
            this.difference = difference;
            this.index = index;
        }
        public int getIndex() {
            return index;
        }
        public Difference<E> getDifference() {
            return difference;
        }
        @Override
        public String toString() {
            return "\""+index+ "\":"+difference;
        }
    }

    @Override
    public String toString() {
        return "{"+differences.stream().map(Object::toString).collect(Collectors.joining(",")) +"}";
    }
    
}
