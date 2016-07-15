package attd.core;

import vk.core.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class JavaStringCompilerWrapper {

	private List<CompileError> getClassExeptions(String code) {
		List<CompileError> errors = new ArrayList<>();
		CompileError checkfirst = nullExeption(code);
		if (checkfirst != null) {
			errors.add(checkfirst);
		} else {

			CompilationUnit c = new CompilationUnit(classNameParser(code), code, false);
			JavaStringCompiler javaStringCompiler = CompilerFactory.getCompiler(c);
			javaStringCompiler.compileAndRunTests();

			errors.addAll(javaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(c));
		}
		return errors;
	}

	public List<CompileError> getTestExceptions(String test) {

		List<CompileError> errors = getClassExeptions(test);

		if (test != null && !test.contains("@Test")) {
			errors.add(new CompileError() {
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
					return "Die Klasse enthaelt keine Tests";
				}

				@Override
				public String getCodeLineContainingTheError() {
					return "";
				}
			});
		}

		return errors;

	}

	private CompileError nullExeption(String code) {
		if (code == null || code.replace(" ", "").equals("")) {
			return (new CompileError() {
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
					return "Die Klasse enthaelt keinen kompilierbaren Code";
				}

				@Override
				public String getCodeLineContainingTheError() {
					return "0";
				}
			});

		}
		return null;
	}

	private boolean classCompilable(String code) {
		List<CompileError> errors = getClassExeptions(code);
		if (errors.size() == 0) {
			return true;
		}
		return false;
	}

	public boolean testCompilable(String test) {
		List<CompileError> errors1 = getTestExceptions(test);
		List<CompileError> errors2 = getClassExeptions(test);

		if (errors1.size() == errors2.size() && errors2.stream().filter(filter()).count() == errors1.size()) {
			return true;
		}

		return false;
	}

	private Predicate<CompileError> filter() {
		return new Predicate<CompileError>() {
			@Override
			public boolean test(CompileError compileError) {
				String e = compileError.getMessage();
				return e.contains("cannot find symbol") && (e.contains("class") || e.contains("variable") || e.contains("method"));
			}
		};
	}

	public boolean passTests(String code, String test) {

		if (classCompilable(code) && testCompilable(test)) {
			CompilationUnit a = new CompilationUnit(classNameParser(code), code, false);
			CompilationUnit b = new CompilationUnit(classNameParser(test), test, true);
			JavaStringCompiler javaStringCompiler = CompilerFactory.getCompiler(a, b);
			javaStringCompiler.compileAndRunTests();

			if (javaStringCompiler.getCompilerResult().hasCompileErrors()) {
				return false;
			}

			return true;
		}
		return false;
	}

	public List<CompileError> getTestFailures(String code, String test) {
		List<CompileError> errors = new ArrayList<>();

		CompilationUnit a = new CompilationUnit(classNameParser(code), code, false);
		CompilationUnit b = new CompilationUnit(classNameParser(test), test, true);
		JavaStringCompiler javaStringCompiler = CompilerFactory.getCompiler(a, b);
		javaStringCompiler.compileAndRunTests();
		errors.addAll(javaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(a));
		errors.addAll(javaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(b));

		return errors;
	}

	private String classNameParser(String code) {

		try {

			return code.subSequence(code.indexOf("class") + 6, code.indexOf("{")).toString().replace(" ", "");
		} catch (StringIndexOutOfBoundsException e) {

			return "";
		}
	}
}
