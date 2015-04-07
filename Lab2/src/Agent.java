import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Agent {
	List<Predicate> desires;
	List<Predicate> intentions;
	World currentState;
	World desiredState;
	Plan currentPlan;
	int botIdx;
	
	public Agent(World state, int idx) {
		this.currentState = state;//state.clone();
		this.currentPlan = null;
		this.botIdx = idx;
	}
	
	public void setDesiredState(World w) {
		desiredState = w;
	}
	
	public void setDesires(List<Predicate> s) {
		desires = s;
	}
		
	public List<Predicate> getUnsatisfiedDesires() {
		List<Predicate> unsatisfiedDesires = new LinkedList<>();
		for (Predicate p : desires) {
			boolean remove = false;
			ArrayList<Block> unavailableBlocks = new ArrayList<>();
			for (int i = 0; i < currentState.inHand.length; i++) {
				if (i != botIdx && currentState.inHand[i] != null)
					unavailableBlocks.add(currentState.inHand[i]);
			}
			for (int i = 0; i < unavailableBlocks.size(); i++) {
				for (int j = 0; j < p.getNumAttributes(); j++)
					if (p.getAttribute(j).getBlock().equals(unavailableBlocks.get(i)))
						remove = true;
			}
			
			if (!p.evaluatePredicate(currentState) && !remove)
				unsatisfiedDesires.add(p);
		}
		return unsatisfiedDesires;
	}
	
	public boolean areDesiresStatisfied() {
		for (Predicate p : desires) {
			if (!p.evaluatePredicate(currentState))
				return false;
		}
		return true;
	}
	
	public static List<Predicate> createDesiresBasedOnWorld(World w, int botIdx) {
		List<Predicate> d = new LinkedList<>();
			for (Block b1 : w.blocks) {
				PredicateAttribute p1 = new PredicateAttribute(b1);
				for (Predicate.Type t : Predicate.Type.values()) {
					Predicate pred;
					try {
						pred = new Predicate(t, botIdx);
						if (d.contains(pred))
							continue;
						if (pred.evaluatePredicate(w))
							d.add(pred);
						
						continue;
					} catch (Exception e) {
					}
	
					try {
						pred = new Predicate(t, botIdx, p1);
						if (d.contains(pred))
							continue;
						if (pred.evaluatePredicate(w))
							d.add(pred);
						continue;
					} catch (Exception e) {
					}
					for (Block b2 : w.blocks) {
						PredicateAttribute p2 = new PredicateAttribute(b2);
						try {
							pred = new Predicate(t, botIdx, p1, p2);
							if (d.contains(pred))
								continue;
							if (pred.evaluatePredicate(w))
								d.add(pred);
						} catch (Exception e) {
						}
					}
				}
			}

		return d;
	}
	
	public void makePlan(List<Predicate> intentions) {
		currentPlan = Planner.findPlan(currentState, intentions, new GoalEvaluator(desiredState), botIdx);
	}
	
	public boolean executePlan() {
		 if (currentPlan == null || currentPlan.size() == 0)
			 return true;
		 Action a = currentPlan.get(0);
		 if (a.canExecuteAction(currentState)) {
			 return a.executeAction(currentState);
		 }
		 return true;
	}
	
	public Plan getCurrentPlan() {
		return currentPlan;
	}
}
