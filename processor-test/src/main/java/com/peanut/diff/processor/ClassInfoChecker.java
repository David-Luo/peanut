package com.peanut.diff.processor;

import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.processor.annotation.AutoImplement;
import com.peanut.reflect.model.ClassInfo;

@AutoImplement
public interface ClassInfoChecker extends ChangeChecker<ClassInfo> {
}
