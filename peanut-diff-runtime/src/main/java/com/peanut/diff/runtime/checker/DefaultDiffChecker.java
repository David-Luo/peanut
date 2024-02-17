package com.peanut.diff.runtime.checker;

import java.util.Optional;

import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.diff.runtime.Difference;
import com.peanut.diff.runtime.Difference.ChangeType;

public class DefaultDiffChecker<T>  implements ChangeChecker<T> {
    @Override
    public Optional<Difference<T>> check(T left, T right){
        if(isBothNull(left,right)){
            return Optional.empty();
        }else if(isNull(left)){
            return Optional.of(new Difference<>(left,right,ChangeType.add));
        }else if(isNull(right)){
            return Optional.of(new Difference<>(left,right,ChangeType.del));
        }else if(!equals(left,right)){
            return Optional.of(new Difference<>(left,right,ChangeType.modify));
        }
        return Optional.empty();
    }
    protected boolean isNull(T t){
        return t==null;
    }
    protected boolean isBothNull(T left, T right){
        return isNull(left)&&isNull(right);
    }
    protected boolean equals(T left, T right){
        return left==right;
    }
}
