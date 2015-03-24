import java.util.List;

public class Main {
	public static void main(String argv[]) {
		World w = new World();
		
		Block A = new Block("A");
		Block B = new Block("B");
		Block C = new Block("C");
		Block D = new Block("D");
		
		w.addBlock(A, null);
		w.addBlock(B, A);
		w.addBlock(C, null);
		w.addBlock(D, null);
		
		World finalWorld = new World();
		finalWorld.addBlock(D, null);
		finalWorld.addBlock(A, D);
		finalWorld.addBlock(C, A);
		finalWorld.addBlock(B, C);
		
		System.out.println("Initial World");
		System.out.println(w);
		System.out.println();
		System.out.println("Desired World");
		System.out.println(finalWorld);
		System.out.println();
		
		Agent a = new Agent(w);
		a.setDesiredState(finalWorld);
		a.setDesires(Agent.createDesiresBasedOnWorld(finalWorld));
		System.out.println("Agent desires:");
		System.out.println(a.desires);
		System.out.println();
		while (!a.areDesiresStatisfied()) {
			System.out.println("Current State:");
			System.out.println(a.currentState);
			System.out.println("Current Plan:");
			List<Predicate> unsatisfiedDesires = a.getUnsatisfiedDesires(); // Intentions
			a.makePlan(unsatisfiedDesires);
			System.out.println(a.currentPlan);
			boolean execution = a.executePlan();
			if (execution) {
				System.out.println("Something went wrong with the plan.");
				System.out.println(a.currentState);
				break;
			}

		}
	}
	 
}
