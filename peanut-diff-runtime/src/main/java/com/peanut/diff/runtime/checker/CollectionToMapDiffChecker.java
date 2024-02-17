package com.peanut.diff.runtime.checker;

import com.peanut.diff.runtime.*;
import com.peanut.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基础的集合校验
 *
 * @author heshan.lwx
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class CollectionToMapDiffChecker<E> extends DefaultDiffChecker<Collection<E>> implements CollectionDiffChecker<E> {
    @Override
    public Optional<Difference<Collection<E>>> check(Collection<E> before, Collection<E> after) {
        Optional<Difference<Collection<E>>> result = super.check(before, after);
        
        if(result.isPresent() || isBothNull(before,after)){
            return result;
        }
        
        Map<Object, E> beforeMap = toMap(before);
        Map<Object, E> afterMap = toMap(after);
        Optional<Difference> mapDiff = ChangeCheckerHelper.diff(beforeMap, afterMap);
        if(mapDiff.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(new NamedDifference(before,after,((NamedDifference)mapDiff.get()).getAttributeChange()));
    }
    protected Map toMap(Collection<E> collection) {
        return collection.stream().filter(e ->
                !Objects.isNull(this.keyMapper(e)))
                .collect(Collectors.toMap(this::keyMapper,
                        Function.identity(),(v,v1)->v));
    }

    protected abstract Object keyMapper(E source);

    @Override
    protected boolean isNull(Collection<E> t){
        return CollectionUtils.isEmpty(t);
    }
}