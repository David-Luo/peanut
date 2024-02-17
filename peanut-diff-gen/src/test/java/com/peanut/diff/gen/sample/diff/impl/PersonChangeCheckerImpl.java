package com.peanut.diff.gen.sample.diff.impl;

import com.peanut.diff.gen.sample.bean.Person;
import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.diff.runtime.Difference;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import com.peanut.diff.runtime.NamedDifference;
import com.peanut.diff.runtime.spi.ServiceLocator;

public class PersonChangeCheckerImpl implements ChangeChecker<Person> {

    private ChangeChecker codeChecker = getCodeChecker();

    private ChangeChecker nameChecker = getNameChecker();

    @Override()
    public Optional<Difference<Person>> check(Person left, Person right) {
        if (left == null && right == null) {
            return Optional.empty();
        }
        if (left != null && right == null) {
            return Optional.of(new NamedDifference(left, right, Difference.ChangeType.del));
        }
        if (left == null && right != null) {
            return Optional.of(new NamedDifference(left, right, Difference.ChangeType.add));
        }
        Map<String, Difference> attributeChange = new HashMap<>();
        Optional<Difference> attributeDiff;
        attributeDiff = codeChecker.check(left.getCode(), right.getCode());
        if (attributeDiff.isPresent()) {
            attributeChange.put("code", attributeDiff.get());
        }
        attributeDiff = nameChecker.check(left.getName(), right.getName());
        if (attributeDiff.isPresent()) {
            attributeChange.put("name", attributeDiff.get());
        }
        if (attributeChange.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new NamedDifference(left, right, attributeChange));
    }

    public ChangeChecker getCodeChecker() {
        return ServiceLocator.getBeanChecker("int");
    }

    public ChangeChecker getNameChecker() {
        return ServiceLocator.getBeanChecker("java.lang.String");
    }
}

