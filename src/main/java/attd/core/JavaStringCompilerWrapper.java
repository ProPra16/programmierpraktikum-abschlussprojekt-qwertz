package attd.core;

import vk.core.api.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by student on 15/07/16.
 */
public class JavaStringCompilerWrapper {

    public CustomCompilerResult getCustomCompilerResult(String code,String test){


        CompilationUnit a = new CompilationUnit(classNameParser(code), code,false);
        CompilationUnit b = new CompilationUnit(classNameParser(test), test,true);

        JavaStringCompiler javaStringCompiler = CompilerFactory.getCompiler(a, b);

        try{
            javaStringCompiler.compileAndRunTests();

            CustomErrorResult tests = checkCompilable(test,javaStringCompiler.getCompilerResult().getCompileDuration());

            if(tests.hasCompileErrors()){
                return new CustomCompilerResult(tests);
            }
            if(javaStringCompiler.getCompilerResult().hasCompileErrors()){
                return new CustomCompilerResult(new CustomErrorResult(javaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(a), javaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(b)));
            }
            return new CustomCompilerResult(javaStringCompiler.getTestResult());

        }catch (Exception e){
            return new CustomCompilerResult(new CustomErrorResult(Arrays.asList(new CompileError() {
                @Override
                public long getLineNumber() {
                    return 0;
                }

                @Override
                public long getColumnNumber() {
                    return 0;
                }

                @Override
                public String getMessage() {
                    return e.getMessage();
                }

                @Override
                public String getCodeLineContainingTheError() {
                    String string="";
                    for(StackTraceElement s : e.getStackTrace()){
                        string+=s.toString();
                    }
                    return string;
                }
            })));

        }
    }



    private CustomErrorResult checkCompilable(String test,Duration duration){
        List<CompileError> compileErrors = new ArrayList<>();
        if(!test.contains("@Test")){
            compileErrors.add(new CompileError() {
                @Override
                public long getLineNumber() {
                    return 0;
                }

                @Override
                public long getColumnNumber() {
                    return 0;
                }

                @Override
                public String getMessage() {
                    return "Die Klasse enthält keine @Test Annotation";
                }

                @Override
                public String getCodeLineContainingTheError() {
                    return "";
                }
            });
        }
        if(!test.contains("assert")){
            compileErrors.add(new CompileError() {
                @Override
                public long getLineNumber() {
                    return 0;
                }

                @Override
                public long getColumnNumber() {
                    return 0;
                }

                @Override
                public String getMessage() {
                    return "Die Klasse enthält keine assert-Methode";
                }

                @Override
                public String getCodeLineContainingTheError() {
                    return "";
                }
            });

        }
        return new CustomErrorResult(compileErrors);

    }




    private String classNameParser(String code) {

        try {

            return code.subSequence(code.indexOf("class") + 6, code.indexOf("{")).toString().replace(" ", "");
        } catch (StringIndexOutOfBoundsException e) {

            return "";
        }
    }

}
