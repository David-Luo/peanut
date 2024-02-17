package com.peanut.diff.gen;

import com.peanut.diff.gen.sample.bean.Teacher;
import com.peanut.diff.gen.sample.diff.TeacherChangeChecker;
import org.junit.jupiter.api.Test;

public class CheckerSourceCodeTemplateUtilTest {

    @Test
    public void testGenerateFromInterface() throws ClassNotFoundException {
        CheckerSourceCodeBuilderUtil generator = new CheckerSourceCodeBuilderUtil();
        String source = generator.generateFromInterface(TeacherChangeChecker.class.getName());
        System.out.println(source);
    }

    @Test
    public void testBuildFormBeanBeCheck() throws ClassNotFoundException {
        CheckerSourceCodeBuilderUtil generator = new CheckerSourceCodeBuilderUtil();
        String source = generator.buildFormBeanBeCheck(Teacher.class.getName());
        System.out.println(source);
    }
}
