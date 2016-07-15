package attd.core;

import java.time.Duration;
import java.util.Collection;

import vk.core.api.TestFailure;
import vk.core.api.TestResult;

public class CustomTestResult implements TestResult{
    private Collection<TestFailure> testfailures;
    private int successfultests;
    private int numberoffailedtests;
    private int numberofignoredtests;
    private Duration duration;
    private boolean compilable;

    public CustomTestResult(boolean compiles,TestResult testResult){
        compilable =compiles;
        if(compiles){
        this.successfultests = testResult.getNumberOfSuccessfulTests();
        this.numberoffailedtests = testResult.getNumberOfFailedTests();
        this.numberofignoredtests = testResult.getNumberOfIgnoredTests();
        this.duration = testResult.getTestDuration();
        this.testfailures = testResult.getTestFailures();
        }

    }
    public boolean compiled(){return compilable;}

    @Override
    public int getNumberOfSuccessfulTests() {
        return successfultests;
    }

    @Override
    public int getNumberOfFailedTests() {
        return numberoffailedtests;
    }

    @Override
    public int getNumberOfIgnoredTests() {
        return numberofignoredtests;
    }

    @Override
    public Duration getTestDuration() {
        return duration;
    }

    @Override
    public Collection<TestFailure> getTestFailures() {
        return testfailures;
    }

}
