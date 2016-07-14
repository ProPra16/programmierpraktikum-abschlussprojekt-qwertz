package attd.core;

import java.util.List;

public final  class Catalog {

	private final String name;
	private final List<Exercise> exercises;
	
	public Catalog(String name, List<Exercise> exercises) {
		this.name = name;
		this.exercises = exercises;
	}

	public String getName() {
		return name;
	}

	public List<Exercise> getExercises() {
		return exercises;
	}
}