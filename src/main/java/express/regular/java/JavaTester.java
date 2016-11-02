package express.regular.java;

import express.regular.common.*;
import express.regular.exception.InvalidConfigException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaTester extends Tester {

    public static final String CONFIG_TYPE = "test_type";
    public static final String CONFIG_REGEX = "regex";
    public static final String CONFIG_REPLACE = "replace";

    public static final String CONFIG_CASE_INSENSITIVE = "CASE_INSENSITIVE";
    public static final String CONFIG_CANON_EQ = "CANON_EQ";
    public static final String CONFIG_COMMENTS = "COMMENTS";
    public static final String CONFIG_DOTALL = "DOTALL";
    public static final String CONFIG_LITERAL = "LITERAL";
    public static final String CONFIG_MULTILINE = "MULTILINE";
    public static final String CONFIG_UNICODE_CASE = "UNICODE_CASE";
    public static final String CONFIG_UNICODE_CHARACTER_CLASS = "UNICODE_CHARACTER_CLASS";
    public static final String CONFIG_UNIX_LINES = "UNIX_LINES";

    public static final String TYPE_MATCH = "match";
    public static final String TYPE_GROUP = "group";
    public static final String TYPE_REPLACE = "replace";

    private TestResult testRegexMatching(Pattern pattern, List<String> testStrings) throws IOException {
        TestResult testResult = new TestResult();
        testResult.setType(TestResult.Type.MATCH);

        MatchResult matchResult = new MatchResult();

        for(int i = 0; i < testStrings.size(); i++) {
            String testString = testStrings.get(i);
            Matcher matcher = pattern.matcher(testString);
            matchResult.getResultList().add(matcher.matches());
        }
        testResult.setResult(matchResult);
        return testResult;
    }

    private TestResult testRegexGroup(Pattern pattern, List<String> testStrings) throws IOException {
        TestResult testResult = new TestResult();
        testResult.setType(TestResult.Type.GROUP);

        GroupResult groupResult = new GroupResult();

        for(int i = 0; i < testStrings.size(); i++) {
            String testString = testStrings.get(i);
            Matcher matcher = pattern.matcher(testString);
            GroupResult.GroupsList groupsList = new GroupResult.GroupsList();
            while (matcher.find()) {
                List<String> groups = new ArrayList<String>(matcher.groupCount());
                for (int j = 0; j <= matcher.groupCount(); j++) {
                    if(i == 0) {
                        groupResult.getColumns().add("Group #" + j);
                    }
                    groups.add(matcher.group(j));
                }
                groupsList.addGroups(groups);
            }
            if(groupsList.size() > 0) {
                groupResult.getResultList().add(groupsList);
            } else {
                groupResult.getResultList().add(null);
            }

        }

        testResult.setResult(groupResult);
        return testResult;
    }

    private TestResult testRegexReplace(Pattern pattern, List<String> testStrings, String replace) {
        TestResult testResult = new TestResult();
        testResult.setType(TestResult.Type.STRING);

        StringResult stringResult = new StringResult();

        for(int i = 0; i < testStrings.size(); i++) {
            String testString = testStrings.get(i);
            Matcher matcher = pattern.matcher(testString);
            String replacedString = matcher.replaceAll(replace);
            stringResult.getResultList().add(replacedString);
        }

        testResult.setResult(stringResult);
        return testResult;
    }

    public TestResult testRegex(Map<String, Object> configMap, List<String> testStrings) throws Exception {
        String testType = (String) configMap.get(CONFIG_TYPE);
        if(testType == null) {
            throw new InvalidConfigException(String.format("'%s' parameter doesn't exists.", CONFIG_TYPE));
        }

        String regex = (String) configMap.get(CONFIG_REGEX);

        int flags[] = new int[9];
        flags[0] = String.valueOf(configMap.get(CONFIG_CASE_INSENSITIVE)).equalsIgnoreCase("true") ?
                Pattern.CASE_INSENSITIVE : 0;
        flags[1] = String.valueOf(configMap.get(CONFIG_CANON_EQ)).equalsIgnoreCase("true") ?
                Pattern.CANON_EQ : 0;
        flags[2] = String.valueOf(configMap.get(CONFIG_COMMENTS)).equalsIgnoreCase("true") ?
                Pattern.COMMENTS : 0;
        flags[3] = String.valueOf(configMap.get(CONFIG_DOTALL)).equalsIgnoreCase("true") ?
                Pattern.DOTALL : 0;
        flags[4] = String.valueOf(configMap.get(CONFIG_LITERAL)).equalsIgnoreCase("true") ?
                Pattern.LITERAL : 0;
        flags[5] = String.valueOf(configMap.get(CONFIG_MULTILINE)).equalsIgnoreCase("true") ?
                Pattern.MULTILINE : 0;
        flags[6] = String.valueOf(configMap.get(CONFIG_UNICODE_CASE)).equalsIgnoreCase("true") ?
                Pattern.UNICODE_CASE : 0;
        flags[7] = String.valueOf(configMap.get(CONFIG_UNICODE_CHARACTER_CLASS)).equalsIgnoreCase("true") ?
                Pattern.UNICODE_CHARACTER_CLASS : 0;
        flags[8] = String.valueOf(configMap.get(CONFIG_UNIX_LINES)).equalsIgnoreCase("true") ?
                Pattern.UNIX_LINES : 0;

        int flag = 0;
        for(int i = 0 ; i < flags.length ; i++) {
            flag |= flags[i];
        }

        Pattern pattern = Pattern.compile(regex, flag);
        if (testType.equals(TYPE_MATCH)) {
            return testRegexMatching(pattern, testStrings);
        } else if (testType.equals(TYPE_GROUP)) {
            return testRegexGroup(pattern, testStrings);
        } else if (testType.equals(TYPE_REPLACE)) {
            String replace = (String) configMap.get(CONFIG_REPLACE);
            return testRegexReplace(pattern, testStrings, replace);
        } else {
            throw new InvalidConfigException(String.format("Unsupported Test Type: %s", testType));
        }
    }


    public static void main(String args[]) {
        Tester tester = new JavaTester();
        tester.testMain(args[0], args[1]);
    }
}
