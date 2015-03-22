import java.util.ArrayList;


public class Action {
	
	public static enum Type {
		STACK, UNSTACK, PICKUP, PUTDOWN 
	}
	
	public Action(Type t, Block... operators) {
		Block b1 = null, b2 = null;
		switch (t) {
		case STACK:
		case UNSTACK:
			if (operators.length != 2)
				throw new RuntimeException();
			b1 = operators[0];
			b2 = operators[1];
			break;
		default:
			if (operators.length != 1)
				throw new RuntimeException();
			b1 = operators[0];
		}
		op1 = new PredicateAttribute(b1);
		op2 = new PredicateAttribute(b2);
		this.setType(t);
	}
	public Block getOp1() {
		return op1.getBlock();
	}

	public Block getOp2() {
		return op2.getBlock();
	}
	
	public void setOp1(Block op1) {
		this.op1.setBlock(op1);
	}

	public void setOp2(Block op2) {
		this.op2.setBlock(op2);
	}

	public ArrayList<Predicate> getRequiredConditions() {
		return requiredConditions;
	}

	public ArrayList<Predicate> getInvalidatingConditions() {
		return invalidatingConditions;
	}

	public ArrayList<Predicate> getPosteriorConditions() {
		return posteriorConditions;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type t) {
		type = t;
		switch (type) {
		case STACK:
			requiredConditions = new ArrayList<Predicate>();
			requiredConditions.add(new Predicate(Predicate.Type.CLEAR, op2));
			requiredConditions.add(new Predicate(Predicate.Type.HOLD,  op1));
			invalidatingConditions = new ArrayList<Predicate>();
			invalidatingConditions.add(new Predicate(Predicate.Type.CLEAR, op2));
			invalidatingConditions.add(new Predicate(Predicate.Type.HOLD,  op1));
			posteriorConditions = new ArrayList<Predicate>();
			posteriorConditions.add(new Predicate(Predicate.Type.ON, op1, op2));
			posteriorConditions.add(new Predicate(Predicate.Type.ARMEMPTY));
			return;
		case UNSTACK:
			requiredConditions = new ArrayList<Predicate>();
			requiredConditions.add(new Predicate(Predicate.Type.ON, op1, op2));
			requiredConditions.add(new Predicate(Predicate.Type.CLEAR,  op1));
			requiredConditions.add(new Predicate(Predicate.Type.ARMEMPTY));
			invalidatingConditions = new ArrayList<Predicate>();
			invalidatingConditions.add(new Predicate(Predicate.Type.ON, op1, op2));
			invalidatingConditions.add(new Predicate(Predicate.Type.ARMEMPTY));
			posteriorConditions = new ArrayList<Predicate>();
			posteriorConditions.add(new Predicate(Predicate.Type.HOLD, op1));
			posteriorConditions.add(new Predicate(Predicate.Type.CLEAR, op2));
			return;
		case PICKUP:
			requiredConditions = new ArrayList<Predicate>();
			requiredConditions.add(new Predicate(Predicate.Type.CLEAR, op1));
			requiredConditions.add(new Predicate(Predicate.Type.ONTABLE,  op1));
			requiredConditions.add(new Predicate(Predicate.Type.ARMEMPTY));
			invalidatingConditions = new ArrayList<Predicate>();
			invalidatingConditions.add(new Predicate(Predicate.Type.ONTABLE, op1));
			invalidatingConditions.add(new Predicate(Predicate.Type.ARMEMPTY));
			posteriorConditions = new ArrayList<Predicate>();
			posteriorConditions.add(new Predicate(Predicate.Type.HOLD, op1));
			return;
		case PUTDOWN:
			requiredConditions = new ArrayList<Predicate>();
			requiredConditions.add(new Predicate(Predicate.Type.HOLD, op1));
			invalidatingConditions = new ArrayList<Predicate>();
			invalidatingConditions.add(new Predicate(Predicate.Type.HOLD, op1));
			posteriorConditions = new ArrayList<Predicate>();
			posteriorConditions.add(new Predicate(Predicate.Type.ONTABLE, op1));
			posteriorConditions.add(new Predicate(Predicate.Type.ARMEMPTY));
			return;
		}
	}

	private Type type;
	private PredicateAttribute op1;
	private PredicateAttribute op2;
	private ArrayList<Predicate> requiredConditions;
	private ArrayList<Predicate> invalidatingConditions;
	private ArrayList<Predicate> posteriorConditions;
		
	public boolean executeAction(World w) {
		switch (type) {
		case STACK:
			return w.stack(op1.getBlock(), op2.getBlock());
		case UNSTACK:
			return w.unstack(op1.getBlock(), op2.getBlock());
		case PICKUP:
			return w.pickup(op1.getBlock());
		case PUTDOWN:
			return w.putdown(op1.getBlock());
		}
		
		return true;
	}
	
	public boolean canExecuteAction(World w) {
		for (Predicate p : requiredConditions) {
			if (!p.evaluatePredicate(w))
				return false;
		}
		return true;
	}
	
	public String toString() {
		String result =  type + "(" + op1;
		if (type == Type.PICKUP || type == Type.PUTDOWN) {
			result += ")";
		} else {
			result += ", " + op2 + ")";
		}
		return result;
	}
	
}
