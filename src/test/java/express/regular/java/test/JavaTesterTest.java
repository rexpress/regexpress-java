package express.regular.java.test;

import com.google.gson.Gson;
import express.regular.common.GroupResult;
import express.regular.common.MatchResult;
import express.regular.common.StringResult;
import express.regular.common.TestResult;
import express.regular.java.JavaTester;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaTesterTest {

    @Test
    public void javaMatchTest() {
        Map<String, Object> configMap = new HashMap<String, Object>();
        configMap.put(JavaTester.CONFIG_TYPE, JavaTester.TYPE_MATCH);
        configMap.put(JavaTester.CONFIG_REGEX,  "([a-zA-Z]*) ([a-zA-Z]*) ([a-zA-Z]*)");

        List<String> testMap = Arrays.asList(new String[]{"Hello Test String", "Hello2 Test2 String2"});

        Gson gson = new Gson();
        String configJsonString = gson.toJson(configMap);
        String testJsonString = gson.toJson(testMap);

        JavaTester javaTester = new JavaTester();
        TestResult testResult = javaTester.testMain(configJsonString, testJsonString);
        Assert.assertEquals(testResult.getType(), TestResult.Type.MATCH);
        Assert.assertNotNull(testResult.getResult());
        MatchResult matchResult = (MatchResult) testResult.getResult();
        Assert.assertEquals(matchResult.getResultList().get(0), true);
        Assert.assertEquals(matchResult.getResultList().get(1), false);
    }

    @Test
    public void javaGroupTest() {
        Map<String, Object> configMap = new HashMap<String, Object>();
        configMap.put(JavaTester.CONFIG_TYPE, JavaTester.TYPE_GROUP);
        configMap.put(JavaTester.CONFIG_REGEX,  "([a-zA-Z]*) (?<B>[a-zA-Z]*) (?<C>[a-zA-Z]*)");

        List<String> testStrings = Arrays.asList(new String[]{"Hello Test String", "Hello2 Test2 String2"});

        Gson gson = new Gson();
        String configJsonString = gson.toJson(configMap);
        String testJsonString = gson.toJson(testStrings);

        JavaTester javaTester = new JavaTester();
        TestResult testResult = javaTester.testMain(configJsonString, testJsonString);

        Assert.assertEquals(testResult.getType(), TestResult.Type.GROUP);
        Assert.assertNotNull(testResult.getResult());
        GroupResult groupResult = (GroupResult) testResult.getResult();
        Assert.assertArrayEquals(groupResult.getColumns().toArray(), new String[]{"Group #1", "Group #2", "Group #3"});
        Assert.assertArrayEquals(groupResult.getResultList().get(0).getGroups(0).toArray(), new String[]{"Hello", "Test", "String"});
    }

    @Test
    public void javaReplaceTest() {
        Map<String, Object> configMap = new HashMap<String, Object>();
        configMap.put(JavaTester.CONFIG_TYPE, JavaTester.TYPE_REPLACE);
        configMap.put(JavaTester.CONFIG_REGEX,  "([a-zA-Z]*) ([a-zA-Z]*) ([a-zA-Z]*)");
        configMap.put(JavaTester.CONFIG_REPLACE,  "$1! $2! $3!");

        List<String> testMap = Arrays.asList(new String[]{"Hello Test String", "Hello2 Test2 String2"});

        Gson gson = new Gson();
        String configJsonString = gson.toJson(configMap);
        String testJsonString = gson.toJson(testMap);

        JavaTester javaTester = new JavaTester();
        TestResult testResult = javaTester.testMain(configJsonString, testJsonString);

        Assert.assertEquals(testResult.getType(), TestResult.Type.STRING);
        Assert.assertNotNull(testResult.getResult());
        StringResult stringResult = (StringResult) testResult.getResult();
        Assert.assertEquals(stringResult.getResultList().get(0), "Hello! Test! String!");
        Assert.assertEquals(stringResult.getResultList().get(1), "Hello2 Test2 String2");
    }
}