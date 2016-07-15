package attd.core;

import vk.core.api.TestResult;

/**
 * Created by student on 15/07/16.
 */
public class CustomCompilerResult {

    private CustomErrorResult customErrorResult;
    private TestResult testresult;
    private boolean compileErrors=true;

    public CustomCompilerResult(CustomErrorResult customErrorResult) {
        this.customErrorResult = customErrorResult;

    }

    public CustomCompilerResult(TestResult tests){
        testresult = tests;
        compileErrors = false;
    }

    public CustomErrorResult getCustomErrorResult(){return customErrorResult;}

    public TestResult getTestResult(){return testresult;}

    public boolean hasCompileErrors(){return compileErrors;}
}
