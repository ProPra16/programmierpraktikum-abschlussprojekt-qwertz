package attd.core;

public final class Codes {

    private Code testCode, classCode;
    private AcceptanceCode acceptanceCode;

    public Codes(Code testCode, Code classCode, AcceptanceCode acceptanceCode) {
        this.testCode = testCode;
        this.classCode = classCode;
        this.acceptanceCode = acceptanceCode;
    }

    public Code getTestCode() {
        return testCode;
    }

    public Code getClassCode() {
        return classCode;
    }

    public AcceptanceCode getAcceptanceCode() {
        return acceptanceCode;
    }
}