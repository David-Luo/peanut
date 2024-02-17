package com.peanut.diff.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class ChangeCheckerHelperTest {

    @Test
    public void testDiffString() {
        System.out.println(ChangeCheckerHelper.diff("123", "1234"));
    }
    @Test
    public void testDiffMap() {
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("1", "123");
        map1.put("2", "2");
        map1.put("3", null);
        map1.put("5", "dddd");
        
        Map<String,Object> map2 = new HashMap<String,Object>();
        map2.put("1", "234");
        map2.put("2", "2");
        map2.put("3", "gggg");
        map2.put("4", "rrrr");
        
        System.out.println(ChangeCheckerHelper.diff(map1, map2));
    }
    @Test
    public void testDiffList(){
        List<String> list1 = Stream.of("123", "345", "678").collect(Collectors.toList());
        List<String> list2 = Stream.of("dss", "345").collect(Collectors.toList());
        System.out.println(ChangeCheckerHelper.diff(list1, list2));
    }
}
