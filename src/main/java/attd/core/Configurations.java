package attd.core;

public final class Configurations {

    private final int babyStepsTime;
    private final boolean acceptanceTestEnabled;
    private final boolean babyStepsEnabled;

    public Configurations(int babyStepsTime, boolean acceptanceTestEnabled) {
        this.babyStepsTime = babyStepsTime;
        this.acceptanceTestEnabled = acceptanceTestEnabled;
        babyStepsEnabled = babyStepsTime > 0;
    }

    public boolean BabyStepsEnabled() {
        return babyStepsEnabled;
    }

    public boolean AcceptanceTestEnabled() {
        return acceptanceTestEnabled;
    }

    public int getTime() {
        return babyStepsTime;
    }
}