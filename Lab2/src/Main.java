import java.util.ArrayList;


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
		/*
			World clone = w.clone();
			System.out.println(clone);
			System.out.println(w.equals(clone));
			w.pickup(D);
			System.out.println(w.equals(clone));
			System.out.println(w);
			w.putdown(C);
			System.out.println(w);
			w.putdown(D);
			System.out.println(w.equals(clone));
			System.out.println(w);
			w.pickup(C);
			System.out.println(w);
			w.putdown(C);
			System.out.println(w);
			w.unstack(B, A);
			System.out.println(w);
			w.unstack(A, B);
			System.out.println(w);
			w.stack(B, D);
			System.out.println(w);
		*/
		PredicateAttribute pA = new PredicateAttribute(A);
		PredicateAttribute pB = new PredicateAttribute(B);
		PredicateAttribute pD = new PredicateAttribute(D);
		PredicateAttribute pC = new PredicateAttribute(C);
		ArrayList<Predicate> pList = new ArrayList<>();
		pList.add(new Predicate(Predicate.Type.ON, pD, pB));
		pList.add(new Predicate(Predicate.Type.ON, pB, pA));
		pList.add(new Predicate(Predicate.Type.ON, pC, pD));
		Plan p = Planner.findPlan(w, pList, new GoalEvaluator(w));
		System.out.println(p.size());
		System.out.println(w + "\n");
		
		for (int i = 0; i < p.size(); i++) {
			Action a = p.get(i);
			a.executeAction(w);
			System.out.println(w + "\n");
		}
	}
	 
}
