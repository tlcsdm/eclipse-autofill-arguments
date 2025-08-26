package com.tlcsdm.eclipse.autofiller.test;

public class Demo {

	public void foo(int count, String name, Object obj) {
		// Do nothing
	}

	private void str(String person) {
		// Do nothing
	}

	void test() {
		// foo();
		// str();
		// Caller.caller();
		// new Caller(5).print()
		// new Inner(120).print()
	}

	static class Inner {

		private final int offset;

		Inner(int offset) {
			this.offset = offset;
		}

		public void print(String name) {

		}

	}

}
