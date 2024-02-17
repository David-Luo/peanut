package com.peanut.diff.gen.sample.diff;

import com.peanut.diff.gen.annotation.DiffStrategy;
import com.peanut.diff.gen.sample.bean.Student;
import com.peanut.diff.gen.sample.bean.Teacher;
import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.diff.runtime.checker.Skip;

import java.time.LocalDateTime;
import java.util.List;

public interface TeacherChangeChecker  extends ChangeChecker<Teacher> {

    @DiffStrategy(key = "code")
    ChangeChecker<List<Student>> getStudentsChecker() ;
    @DiffStrategy(Skip.class)
    ChangeChecker<LocalDateTime> getUpdateTimeChecker();
}
