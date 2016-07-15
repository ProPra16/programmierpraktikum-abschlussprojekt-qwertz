package attd.core;

public enum State {
	writeFailingTest {
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "Schreibe einen fehlschlagenden Test";
		}
	},
	makeTheTestPass {
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "Bringe den Test zum Bestehen.";
		}
	},
	refactor {
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "Verbessere den Code";
		}
	},
	acceptanceTest {
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "Schreibe einen Akzeptanztest";
		}
	}

}
