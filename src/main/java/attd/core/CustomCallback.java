package attd.core;

public interface CustomCallback<paramA, paramB, returnType> {
	returnType call(paramA paramA, paramB paramB);
}
