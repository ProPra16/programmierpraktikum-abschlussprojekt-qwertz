package attd.core;

public final class Version {

	private final String name;
	private final Codes codes;
	private State state;

	public Version(String name, Codes codes, State state) {
		this.name = name;
		this.codes = codes;
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public Codes getCodes() {
		return codes;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
}