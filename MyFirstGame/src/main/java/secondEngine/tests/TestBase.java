package secondEngine.tests;

public abstract class TestBase {
    protected static void test(boolean testResult, String testName, String reason) {
        assert testResult : testName + " test failed: " + reason;
    }

    protected static void printTestResults(int testsPassed, String testName) {
        System.out.println("Passed all " + testsPassed + " " + testName + " tests");
    }
}
