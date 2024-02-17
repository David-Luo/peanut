package com.peanut.diff.runtime.checker;


import com.peanut.diff.runtime.ChangeCheckerHelper;
import com.peanut.diff.runtime.Difference;
import com.peanut.diff.runtime.ListDifference;
import com.peanut.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ListSequenceChecker<E> extends DefaultDiffChecker<List<E>> {
    @Override
    public Optional<Difference<List<E>>> check(List<E> before, List<E> after) {
        Optional<Difference<List<E>>> result = super.check(before, after);

        if(result.isPresent() || isBothNull(before,after)){
            return result;
        }

        List<ListDifference.IndexDifference> differences=new ArrayList<>();
        int min = Math.min(before.size(), after.size());
        for (int i = 0; i < min; i++) {
            Optional<Difference> diff = ChangeCheckerHelper.diff(before.get(i), after.get(i));
            if (diff.isPresent()) {
                differences.add(new ListDifference.IndexDifference(diff.get(),i));
            }
        }
        int max = Math.max(before.size(), after.size());
        boolean isAdd = before.size()==min;
        Difference.ChangeType changeType=isAdd?Difference.ChangeType.add:Difference.ChangeType.del;

        for (int i = min; i < max; i++) {
            differences.add(new ListDifference.IndexDifference(new Difference<E>(isAdd?null:before.get(i), isAdd?after.get(i):null, changeType),i));
        }

        if(differences.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(new ListDifference(before,after,differences));
    }

    @Override
    protected boolean isNull(List<E> t){
        return CollectionUtils.isEmpty(t);
    }
}
