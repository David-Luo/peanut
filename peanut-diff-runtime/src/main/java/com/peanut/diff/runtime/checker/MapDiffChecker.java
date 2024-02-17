package com.peanut.diff.runtime.checker;

import com.peanut.diff.runtime.*;
import com.peanut.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapDiffChecker<E> extends DefaultDiffChecker<Map<?,E>> {
    @Override
    public Optional<Difference<Map<?,E>>> check(Map<?,E> before, Map<?,E> after) {
        Optional<Difference<Map<?,E>>> result = super.check(before, after);

        if(result.isPresent() || isBothNull(before,after)){
            return result;
        }

        Map<String, Difference> entryChange = new HashMap<>();

        for (Map.Entry<?, E> e : before.entrySet()) {
            Object key = e.getKey();
            E value = e.getValue();
            if(key==null){
                throw new IllegalArgumentException("Map的key值不能为空");
            }
            if (after.containsKey(key)){
                Optional<Difference> diff = ChangeCheckerHelper.diff(value, after.get(key));
                if(diff.isPresent()){
                    entryChange.put(key.toString(), diff.get());
                }
                continue;
            }
            // before值为空，after没有key
            if (value == null) {
                continue;
            }
            // before有值，after没有key
            entryChange.put(key.toString(), new Difference(value, null, Difference.ChangeType.del));
        }
        //处理新增要素场景
        for (Map.Entry<?, E> e : after.entrySet()) {
            Object key = e.getKey();
            E value = e.getValue();
            if(key==null){
                throw new IllegalArgumentException("Map的key值不能为空");
            }
            if (before.containsKey(key)) {
                continue;
            }
            // after值为空，before没有key
            if (value == null) {
                continue;
            }
            entryChange.put(key.toString(), new Difference(null, value, Difference.ChangeType.add));
        }

        if(entryChange.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new NamedDifference(before, after,entryChange));
    }

    @Override
    protected boolean isNull(Map<?,E> t){
        return CollectionUtils.isEmpty(t);
    }

}
