public class Predicate {

	private Type type;
	private PredicateAttribute[] attributes;
	private int botNumber;
	enum Type {
		CLEAR, HOLD, ON, ARMEMPTY, ONTABLE
	};
	public Predicate(Type t, PredicateAttribute... attributes) {
		
		this.type = t;
		this.attributes = attributes;
		this.botNumber = -1;
		checkAttributes();
	}
	
	public void checkAttributes() {
		switch (type) {
		case ARMEMPTY:
			if (attributes.length != 0 || botNumber < 0)
				throw new RuntimeException();
			break;
		case CLEAR:
		case ONTABLE:
			if (attributes.length != 1)
				throw new RuntimeException();
			break;
		case HOLD:
			if (attributes.length != 1 || botNumber < 0)
				throw new RuntimeException();
			break;
		case ON:
			if (attributes.length != 2)
				throw new RuntimeException();
			break;
		}		
	}
	
	public Predicate(Type t, int botNumber, PredicateAttribute... attributes) {
		this.type = t;
		this.botNumber = botNumber;
		this.attributes = attributes;
		checkAttributes();
	}
	
	public PredicateAttribute getAttribute(int idx) {
		return attributes[idx];
	}
	
	public int getNumAttributes() {
		return attributes.length;
	}
	
	public boolean evaluatePredicate(World w) {
		switch (type) {
		case CLEAR:
			return w.clear(attributes[0].getBlock());
		case HOLD:
			return w.hold(attributes[0].getBlock(), botNumber);
		case ONTABLE:
			return w.onTable(attributes[0].getBlock());
		case ON:
			return w.on(attributes[0].getBlock(), attributes[1].getBlock());
		case ARMEMPTY:
			return w.armempty(botNumber);
		default:
			throw new RuntimeException();
		}
	}
	
	public String toString() {
		String s = this.type + "";
		if ((type == Type.HOLD || type == Type.ARMEMPTY) && botNumber != -1)
			s +="_" + botNumber;
			
		if (attributes.length > 0) {
			s += "(";
			for (PredicateAttribute p : attributes)
				s += p.getBlock() + ",";
			s = s.substring(0, s.length() - 1);
			s += ")";
		}
		
		return s;
	}
	@Override
	public boolean equals(Object other) {
		Predicate p = (Predicate) other;
		if (!p.type.equals(type))
			return false;
		if ((type == Type.HOLD || type == Type.ARMEMPTY) && botNumber != p.botNumber)
			return false;

		if (!(p.attributes.length == attributes.length))
			return false;
		for (int i = 0; i < attributes.length; i++) {
			if (!attributes[i].equals(p.attributes[i]))
				return false;
		}
		return true;
	}
}
