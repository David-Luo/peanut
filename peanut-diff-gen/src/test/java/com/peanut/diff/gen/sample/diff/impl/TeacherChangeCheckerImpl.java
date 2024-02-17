package com.peanut.diff.gen.sample.diff.impl;

import com.peanut.diff.gen.sample.bean.Teacher;
import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.diff.runtime.Difference;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import com.peanut.diff.runtime.NamedDifference;
import com.peanut.diff.runtime.spi.ServiceLocator;
import com.peanut.diff.gen.sample.bean.Student;
import com.peanut.diff.runtime.checker.CollectionToMapDiffChecker;

public class TeacherChangeCheckerImpl implements ChangeChecker<Teacher> {

    private ChangeChecker codeChecker = getCodeChecker();

    private ChangeChecker isDisabledChecker = getIsDisabledChecker();

    private ChangeChecker nameChecker = getNameChecker();

    private ChangeChecker salaryChecker = getSalaryChecker();

    private ChangeChecker studentsChecker = getStudentsChecker();

    private ChangeChecker updateTimeChecker = getUpdateTimeChecker();

    private ChangeChecker getCodeChecker() {
        return ServiceLocator.get("com.peanut.diff.runtime.checker.DefaultDiffChecker");
    }

    private ChangeChecker getIsDisabledChecker() {
        return ServiceLocator.get("com.peanut.diff.runtime.checker.DefaultDiffChecker");
    }

    private ChangeChecker getNameChecker() {
        return ServiceLocator.get("com.peanut.diff.runtime.checker.ComparableDiffChecker");
    }

    private ChangeChecker getSalaryChecker() {
        return ServiceLocator.get("com.peanut.diff.runtime.checker.ComparableDiffChecker");
    }

    private ChangeChecker getStudentsChecker() {
        return new CollectionToMapDiffChecker<Student>() {

            @Override()
            protected Object keyMapper(Student source) {
                return source.getCode();
            }
        };
    }

    private ChangeChecker getUpdateTimeChecker() {
        return ServiceLocator.get("com.peanut.diff.runtime.checker.LocalDateTimeChecker");
    }

    @Override()
    public Optional<Difference<Teacher>> check(Teacher left, Teacher right) {
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
        attributeDiff = isDisabledChecker.check(left.isDisabled(), right.isDisabled());
        if (attributeDiff.isPresent()) {
            attributeChange.put("isDisabled", attributeDiff.get());
        }
        attributeDiff = nameChecker.check(left.getName(), right.getName());
        if (attributeDiff.isPresent()) {
            attributeChange.put("name", attributeDiff.get());
        }
        attributeDiff = salaryChecker.check(left.getSalary(), right.getSalary());
        if (attributeDiff.isPresent()) {
            attributeChange.put("salary", attributeDiff.get());
        }
        attributeDiff = studentsChecker.check(left.getStudents(), right.getStudents());
        if (attributeDiff.isPresent()) {
            attributeChange.put("students", attributeDiff.get());
        }
        attributeDiff = updateTimeChecker.check(left.getUpdateTime(), right.getUpdateTime());
        if (attributeDiff.isPresent()) {
            attributeChange.put("updateTime", attributeDiff.get());
        }
        if (attributeChange.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new NamedDifference(left, right, attributeChange));
    }
}

