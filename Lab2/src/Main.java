import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String argv[]) {
		World w = new World(2);
		
		Block A = new Block("A");
		Block B = new Block("B");
		Block C = new Block("C");
		Block D = new Block("D");
		Block E = new Block("E");
		Block F = new Block("F");
		Block G = new Block("G");
		Block H = new Block("H");
		
		w.addBlock(A, null);
		w.addBlock(B, A);
		w.addBlock(C, null);
		w.addBlock(D, null);
		w.addBlock(E, null);
		w.addBlock(F, null);
		w.addBlock(G, E);
		//w.addBlock(H, F);
		
		World finalWorld = new World(2);
		finalWorld.addBlock(D, null);
		finalWorld.addBlock(A, D);
		finalWorld.addBlock(C, null);
		finalWorld.addBlock(B, C);
		finalWorld.addBlock(E, A);
		finalWorld.addBlock(F, B);
		finalWorld.addBlock(G, F);
		//finalWorld.addBlock(H, G);
		
		System.out.println("Initial World");
		System.out.println(w);
		System.out.println();
		System.out.println("Desired World");
		System.out.println(finalWorld);
		System.out.println(); 
		Agent a = new Agent(w, 0);
		Agent b = new Agent(w, 1);
		
		a.setDesiredState(finalWorld);
		a.setDesires(Agent.createDesiresBasedOnWorld(finalWorld, 0));
		b.setDesires(Agent.createDesiresBasedOnWorld(finalWorld, 1));
		
		System.out.println("Agent A desires:");
		System.out.println(a.desires);
		System.out.println("Agent B desires:");
		System.out.println(b.desires);
		System.out.println();
		int times[] = new int[2];
		times[0] = 0;
		times[1] = 0;
		//int difficulty;
		Arbiter arb = new Arbiter();
		while (!a.areDesiresStatisfied() || !b.areDesiresStatisfied()) {
			System.out.println("\n---------------------------");
			System.out.println("Current State:");
			System.out.println(a.currentState);
			System.out.println();
			if (a.currentState != b.currentState)
				throw new RuntimeException();

			List<Predicate> unsatisfiedDesiresA = a.getUnsatisfiedDesires(); // Intentions
			//System.out.println("Unsatistfied Desires A:");
			//System.out.println(unsatisfiedDesiresA);
			a.makePlan(unsatisfiedDesiresA);
			List<Predicate> unsatisfiedDesiresB = b.getUnsatisfiedDesires(); // Intentions
			//System.out.println("Unsatistfied Desires B:");
			//System.out.println(unsatisfiedDesiresB);
			b.makePlan(unsatisfiedDesiresB);

			System.out.println("Current Plan A:");
			System.out.println(a.currentPlan);
			System.out.println("Current Plan B:");
			System.out.println(b.currentPlan);
			Agent executor = null;
			int executorIdx = -1;
			if (a.currentPlan.size() > 0 && b.currentPlan.size() > 0 && arb.isConflict(a.currentState, a.currentPlan.get(0), b.currentPlan.get(0)))
				executorIdx = arb.chooseExecutor();
			if (executorIdx == -1) {
				times[0] ++;
				times[1] ++; // Both agents do something.
				boolean executionA = a.executePlan();
				if (executionA) {
					//System.out.println("Something went wrong with the plan on A.");
					//System.out.println(a.currentState);
				}
				boolean executionB = b.executePlan();
				if (executionB) {
					//System.out.println("Something went wrong with the plan on B.");
					//System.out.println(b.currentState);
				}
				if (executionA && executionB)
					break;
			} else {
				int otherIdx = executorIdx == 1 ? 0 : 1;
				times[executorIdx] ++; // Executor always does something.
				times[otherIdx]++; // Here other either waits or does something. 
				System.out.println("!!!CONFLICT!!! ARBITER DECIDED THAT " + (executorIdx == 1 ? "_A_" : "_B_") + " WILL HAVE TO CHANGE PLANS");
				executor = executorIdx == 1 ? b : a;
				
				Agent other = executorIdx == 1 ? a : b;
				Action execAction = executor.getCurrentPlan().get(0);
				boolean execution = executor.executePlan();
				if (execution) {
					System.out.println("Something went wrong with the plan of the executor." + executorIdx);
					System.out.println(executor.currentState);
					break;
				}
				List<Predicate> unsatisfiedDesiresOther = other.getUnsatisfiedDesires(); // Intentions
				//System.out.println("Unsatistfied Desires B:");
				//System.out.println(unsatisfiedDesiresB);

				System.out.println("NEW PLAN FOR " + (executorIdx == 1 ? "A" : "B"));
				//System.out.println(other.currentState);
				other.makePlan(unsatisfiedDesiresOther);
				System.out.println(other.currentPlan);
				
				if (other.currentPlan.size() > 0) {
					Action otherAction = other.currentPlan.get(0);
					ArrayList<Block> involvedBlocks = new ArrayList<>();
					involvedBlocks.add(execAction.getOp1());
					if (execAction.getOp2() != null) {
						involvedBlocks.add(execAction.getOp2());
					}
					for (int i = 0; i < involvedBlocks.size(); i++) {
						if (involvedBlocks.get(i).equals(otherAction.getOp1()) ||
							(otherAction.getOp2() != null && involvedBlocks.get(i).equals(otherAction.getOp2()))) {
							System.out.println("OTHER WAITED A TURN");
							times[executorIdx]--; // Same as having other robot wait a turn and then move.
							break;
						}
					}
					execution = other.executePlan();
				}
				

				
				if (execution) {
					System.out.println("Something went wrong with the plan of the executor. OTHER" + executorIdx);
					System.out.println(other.currentState);
					break;
				}
			}
		}
		System.out.println(a.currentState);
		System.out.println(times[0]);
		System.out.println(times[1]);
	}
	 
}
