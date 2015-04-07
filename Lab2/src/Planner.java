import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


public class Planner {
	public static ArrayList<Action> findAction(Predicate toFulfill) {
		return null;
	}
	
	static class State implements Comparable<State> {
		World w;
		float cost;
		State parent;
		Action parentAction;
		private static final float EPS = 0.00001f;
		@Override
		public int compareTo(State o) {
			if (Math.abs(cost - o.cost) < EPS)
				return 0;
			if (cost > o.cost)
				return 1;
			return -1;
		}
		
		public State(World w, float cost, State parent, Action parentAction) {
			this.w = w;
			this.cost = cost;
			this.parent = parent;
			this.parentAction = parentAction;
		}
		
		@Override
		public boolean equals(Object other) {
			return w.equals(((State)other).w);
		}
		@Override
		public String toString() {
			return w.toString();
		}
		
	}
	/** Run A* to find the closest state that fulfills the predicate toFulfil.
	 * 
	 * @param currentStatus
	 * @param toFulfill
	 * @param g
	 * @return The resulting plan to achieve the Predicate.
	 */
	public static Plan findPlan(World currentStatus, List<Predicate> toFulfill, GoalEvaluator g, int botIdx) {
		Plan result = new Plan();
		
		PriorityQueue<State> open = new PriorityQueue<>();
		Set<State> closed = new HashSet<>();
		State initial = new State(currentStatus, g.evaluateDistance(currentStatus), null, null);
		open.add(initial);

		int requiredFulfilled = Math.max(toFulfill.size() / 2, 3);
		while (open.size() > 0) {
			State current = open.poll();
			boolean fulfilled = true;
			int numFulfilled = 0;
			for (int i = 0; i < toFulfill.size(); i++) {
				if (!toFulfill.get(i).evaluatePredicate(current.w)) {
					fulfilled = false;
				} else {
					numFulfilled++;
				}
			}
			if (fulfilled || numFulfilled >= requiredFulfilled) {
				/* Reconstruct path. */
				while (current.parent != null) {
					result.add(current.parentAction);
					current = current.parent;
				}
				Collections.reverse(result);
				break;
			}
			
			Iterator<State> iter = closed.iterator();
			boolean foundInClosed = false;
			while (iter.hasNext()) {
				if (iter.next().equals(current)) {
					foundInClosed = true;
					break;
				}
			}
			if (foundInClosed)
				continue;
			else
				closed.add(current);
			
			List<State> neighbors = getNeighbors(current, botIdx);
			for (State neighbor : neighbors) {
				if (closed.contains(neighbor))
					continue;


				boolean found = false;
				Iterator<State> it = open.iterator();
				while (it.hasNext()) {
					State s = it.next();
					if (s.equals(neighbor)) {
						found = true;
						if (s.cost > neighbor.cost) {
							it.remove(); 
							open.add(neighbor);
						}
						break;
					}
				}
				if (!found)
					open.add(neighbor);
			}
		}
		
		return result;
	}
	
	public static List<State> getNeighbors(State current, int botIdx) {
		List<State> result = new ArrayList<>();
		List<Action> possibleActions = getPossibleActions(current.w, botIdx);
		for (Action a : possibleActions) {
			World newWorld = current.w.clone();
			boolean execResult = a.executeAction(newWorld);
			assert(!execResult);
			State neighbor = new State(newWorld, current.cost + 1, current, a);
			result.add(neighbor);
		}
		return result;
	}
	
	public static List<Action> getPossibleActions(World w, int robotIdx) {
		List<Action> possibleActions = new LinkedList<Action>();
		for (Block a : w.blocks) {
			for (Action.Type t : Action.Type.values()) {
				Action act;
				try {
					act = new Action(t, robotIdx, a);
					if (act.canExecuteAction(w))
						possibleActions.add(act);
				} catch (Exception e) {
				}
				for (Block b : w.blocks) {
					try {
						act = new Action(t, robotIdx, a, b);
						if (act.canExecuteAction(w))
							possibleActions.add(act);
					} catch (Exception e) {
					}	
				}				
			}
		}
		return possibleActions;
	}
}
