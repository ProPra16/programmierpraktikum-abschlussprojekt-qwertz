package attd.core;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import vk.core.api.CompilationUnit;
import vk.core.api.CompileError;
import vk.core.api.CompilerFactory;
import vk.core.api.CompilerResult;
import vk.core.api.JavaStringCompiler;

public class JavaStringCompilerWrapper {


    public CustomTestResult getTestResult(String code, String test) {
        CompilationUnit c = new CompilationUnit(classNameParser(code), code, false);
        CompilationUnit d = new CompilationUnit(classNameParser(test), test, true);

        JavaStringCompiler compiler = CompilerFactory.getCompiler(c, d);

        try{

            compiler.compileAndRunTests();

            if (!compiler.getCompilerResult().hasCompileErrors()) {
                return new CustomTestResult(true, compiler.getTestResult());
            }
        }catch (Exception e){
            
        }
        return new CustomTestResult(false,null);
    }


    public CompilerResult getCompilerResult(String test){
        CompilationUnit d = new CompilationUnit(classNameParser(test), test, true);

        JavaStringCompiler compiler = CompilerFactory.getCompiler(d);
        List<CompileError> errors = new ArrayList<>();
        List<CompileError> filtered = new ArrayList<>();

                	
					if (classNameParser(test)!=null) {
					    compiler.compileAndRunTests();
					    errors.addAll(compiler.getCompilerResult().getCompilerErrorsForCompilationUnit(d));
					    compiler.getCompilerResult().getCompilerErrorsForCompilationUnit(d).stream().filter(predicate()).forEach(k -> filtered.add(k));


					    if(test.contains("@Test") && test.contains("assert") && filtered.size()==0){

					        return new CompilerResult() {
					            @Override
					            public boolean hasCompileErrors() {
					                return false;
					            }

					            @Override
					            public Duration getCompileDuration() {
					                return compiler.getCompilerResult().getCompileDuration();
					            }

					            @Override
					            public Collection<CompileError> getCompilerErrorsForCompilationUnit(CompilationUnit cu) {
					                return compiler.getCompilerResult().getCompilerErrorsForCompilationUnit(d);
					            }
					        };
					    }else{
							return new CompilerResult() {
								@Override
								public boolean hasCompileErrors() {
									return true;
								}

								@Override
								public Duration getCompileDuration() {
									return compiler.getCompilerResult().getCompileDuration();
								}

								@Override
								public Collection<CompileError> getCompilerErrorsForCompilationUnit(CompilationUnit cu) {
									return filtered;
								}
							};
						}

					}

        return new CompilerResult() {
			
			@Override
			public boolean hasCompileErrors() {
				// TODO Auto-generated method stub
				return true;
			}
		
			
			@Override
			public Collection<CompileError> getCompilerErrorsForCompilationUnit(CompilationUnit cu) {
				List<CompileError>tCompileErrors  = new ArrayList<>();
				tCompileErrors.add(new CompileError() {
					
					@Override
					public String getMessage() {
						// TODO Auto-generated method stub
						return "Der Klassenname wurde nicht gefunden";
					}
					
					@Override
					public long getLineNumber() {
						// TODO Auto-generated method stub
						return 0;
					}
					
					@Override
					public long getColumnNumber() {
						// TODO Auto-generated method stub
						return 0;
					}
					
					@Override
					public String getCodeLineContainingTheError() {
						// TODO Auto-generated method stub
						return "";
					}
				});
				return tCompileErrors;
			}
			
			@Override
			public Duration getCompileDuration() {
				// TODO Auto-generated method stub
				return Duration.ZERO;
			}
		};
    }


    private Predicate<? super CompileError> predicate() {
        return new Predicate<CompileError>() {
            @Override
            public boolean test(CompileError compileError) {
                String e = compileError.getMessage();
                return !(e.contains("cannot find symbol") && (e.contains("class") || e.contains("variable") || e.contains("method")));
            }
        };
    }

    private String classNameParser(String code) {

        try {

            return code.subSequence(code.indexOf("class") + 6, code.indexOf("{")).toString().replace(" ", "");
        } catch (StringIndexOutOfBoundsException e) {

            return null;
        }
    }
}