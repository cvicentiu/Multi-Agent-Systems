
public class Arbiter {
	int currentAgent;
	
	public Arbiter() {
		currentAgent = 0;
	}
	
	static boolean isConflict(World w, Action a, Action b) {
		if (!(a.canExecuteAction(w) && b.canExecuteAction(w)))
			return false; // No conflict if one or both actions are invalid.
		World w1 = w.clone();
		World w2 = w.clone();
		a.executeAction(w1);
		boolean resultA = b.executeAction(w1); // Result of a going first.
		
		b.executeAction(w2);
		boolean resultB = a.executeAction(w2); // Result of b going first.
		
		if (!resultA || !resultB)
			return true; // One action invalidates the other.
		
		if (!w1.equals(w2))
			return true; // Action order matters!
		
		return false;
	}
	
	public int chooseExecutor() {
		int result = currentAgent;
		currentAgent += 1;
		currentAgent %= 2;
		return result;
	}
	
}
