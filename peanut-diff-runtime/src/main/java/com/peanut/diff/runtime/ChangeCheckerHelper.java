package com.peanut.diff.runtime;

import com.peanut.diff.runtime.spi.ServiceLocator;

import java.util.Objects;
import java.util.Optional;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ChangeCheckerHelper {
    public static <T> Optional<Difference> diff(T bean1, T bean2){
        ChangeChecker checker = ChangeCheckerHelper.getChangeChecker(bean1, bean2);
        return checker.check(bean1, bean2);
    }

    private static <T> ChangeChecker<T> getChangeChecker(T left, T right) {
        if (Objects.isNull(left) || Objects.isNull(right)) {
            return ServiceLocator.getDefault();
        }
        if (!isSameClass(left, right)) {
            throw new RuntimeException(String.format("can`t compare between %s and %s", left.getClass().toString(), right.getClass().toString()));
        }
        return ServiceLocator.getBeanChecker(left.getClass());
    }
    private static boolean isSameClass(Object left, Object right){
        if(Objects.isNull(left) || Objects.isNull(right)){
            return true;
        }
        return left.getClass() == right.getClass();
    }
}