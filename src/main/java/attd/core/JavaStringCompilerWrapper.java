package attd.core;

import vk.core.api.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by student on 15/07/16.
 */
public class JavaStringCompilerWrapper {


    public CustomCompilerResult getCustomCompilerResult(String code, String test){


        CompilationUnit a = new CompilationUnit(classNameParser(code), code,false);
        CompilationUnit b = new CompilationUnit(classNameParser(test), test,true);
        JavaStringCompiler javaStringCompiler = CompilerFactory.getCompiler(a, b);

        Collection<CompileError> t = checkTestCompilable(test);
        if(t.size()>0){return new CustomCompilerResult(t);}

        try{
            javaStringCompiler.compileAndRunTests();
            CompilerResult cr = javaStringCompiler.getCompilerResult();

            if(cr.hasCompileErrors()){

                Collection<CompileError> secondTestResult= cr.getCompilerErrorsForCompilationUnit(b).stream().filter(getFilter2(a.getClassName())).collect(Collectors.toList());

                if(secondTestResult.size()>0){

                    return new CustomCompilerResult(secondTestResult);
                }

                //es ist kein classcode vorhanden
                    return new CustomCompilerResult(new TestResult() {
                        @Override
                        public int getNumberOfSuccessfulTests() {
                            return 0;
                        }

                        @Override
                        public int getNumberOfFailedTests() {
                            return 1;
                        }

                        @Override
                        public int getNumberOfIgnoredTests() {
                            return 0;
                        }

                        @Override
                        public Duration getTestDuration() {
                            return Duration.ZERO;
                        }

                        @Override
                        public Collection<TestFailure> getTestFailures() {
                            return null;
                        }
                    });

            }
            return new CustomCompilerResult(javaStringCompiler.getTestResult());

        }catch (Exception e){

            return new CustomCompilerResult(Arrays.asList(new CompileError() {
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
            }));

        }

    }


    private void sack(){

    }




    private Collection<CompileError> checkTestCompilable(String test){
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
        return  compileErrors;

    }

    private Stream<CompileError> getFilteredErrors(Collection<CompileError> compileErrors, Predicate<CompileError> predicate){
        return compileErrors.stream().filter(predicate);
    }

    private Predicate<CompileError> getFilter() {
        return new Predicate<CompileError>() {
            @Override
            public boolean test(CompileError compileError) {
                String error = compileError.getMessage();
                return !(error.contains("cannot find symbol") &&(error.contains("method") || error.contains("class") || error.contains("variable")));
            }
        };
    }

    private  Predicate<CompileError> getFilter2(String classname){
        return  new Predicate<CompileError>() {
            @Override
            public boolean test(CompileError compileError) {
                String error = compileError.getMessage();
                if(classname==null || classname.equals("")){
                    return !(error.contains("cannot find symbol") && error.contains("class"));
                }
                return !(error.contains("cannot find symbol") && error.contains("class") && error.contains(classname));
            }
        };
    }


    private String classNameParser(String code) {

        try {

            return code.subSequence(code.indexOf("class") + 6, code.indexOf("{")).toString().replace(" ", "");
        } catch (StringIndexOutOfBoundsException e) {

            return "";
        }
    }

}
