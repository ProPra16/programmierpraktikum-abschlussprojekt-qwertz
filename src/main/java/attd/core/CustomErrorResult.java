package attd.core;

import vk.core.api.CompileError;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by student on 15/07/16.
 */
public class CustomErrorResult{


    private Collection<CompileError> compilerErrors;
    private Duration compileDuration;
    private boolean hascompileErrors;

    public CustomErrorResult(Collection<CompileError> compilerErrorsForCompilationUnit){
        this.compilerErrors = compilerErrorsForCompilationUnit;
    }

    public CustomErrorResult(Collection<CompileError> compilerErrorsForCompilationUnit1,Collection<CompileError> compilerErrorsForCompilationUnit2){
        this.compilerErrors = new ArrayList<>();
        compilerErrors.addAll(compilerErrorsForCompilationUnit1);
        compilerErrors.addAll(compilerErrorsForCompilationUnit2);
    }

    public Collection<CompileError> getCompilerErrors() {
        return compilerErrors;
    }

    public Duration getCompileDuration() {
        return compileDuration;
    }


    public boolean hasCompileErrors() {
        return hascompileErrors;
    }
}
