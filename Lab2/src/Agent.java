import java.util.ArrayList;

@SuppressWarnings("serial")
class Beliefs extends ArrayList<Predicate> {
	
}

@SuppressWarnings("serial")
class Desires extends ArrayList<Predicate> {
	
}

@SuppressWarnings("serial")
class Intentions extends ArrayList<Predicate> {
	
}
public class Agent {
	Beliefs beliefs;
	Desires desires;
	Intentions intentions;
	World currentState;
	Plan currentPlan;
	
	public Agent(World state) {
		this.currentState = state.clone();
		this.currentPlan = null;
	}
	
	public void setDesires(Desires s) {
		desires = s;
	}
	
	public void setIntentions(Intentions i) {
		intentions = i;
	}
	
	public void makePlan() {
		// TODO: Choose random intention predicate.
		//       Search for action that when accomplished, fulfills that intention.
		//       Add the action to the plan.
	}
	
	public void executePlan() {
		// TODO: Get next action in plan.
		//       Try to execute the action.
		//       If it can not be executed, search for action to fulfill the required conditions.
		//       Add the action before the 	previous action.
		//       Repeat.
		 
	}
}
