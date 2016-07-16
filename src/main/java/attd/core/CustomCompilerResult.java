package attd.core;

import vk.core.api.CompileError;
import vk.core.api.TestFailure;
import vk.core.api.TestResult;

import java.time.Duration;
import java.util.Collection;

/**
 * Created by student on 15/07/16.
 */
public class CustomCompilerResult {

    private Collection<CompileError> compileErrors;
    private TestResult testresult;
    private boolean hascompileerrors;

    public CustomCompilerResult(Collection<CompileError> compileErrors) {
        this.compileErrors = compileErrors;
        hascompileerrors=compileErrors.size()>0;
    }
    public CustomCompilerResult(TestResult testResult){
        this.testresult=testResult;
        hascompileerrors=false;
    }

    public TestResult getTestResult(){return testresult;}

    public Collection<CompileError> getCompileErrors(){return compileErrors;}

    public boolean hasCompileErrors(){return hascompileerrors;}


}
