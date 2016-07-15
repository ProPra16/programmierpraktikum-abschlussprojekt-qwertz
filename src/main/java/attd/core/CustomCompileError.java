package attd.core;

import vk.core.api.CompilationUnit;
import vk.core.api.CompileError;

public class CustomCompileError implements CompileError{
	
	public CustomCompileError(CompilationUnit cu) {
		
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
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCodeLineContainingTheError() {
		// TODO Auto-generated method stub
		return null;
	}

}
