import java.util.ArrayList;
import java.util.HashSet;


public class World {
	ArrayList<ArrayList<Block>> table;
	HashSet<Block> blocks;
	Block inHand;
	
	public World() {
		table = new ArrayList<>();
		blocks = new HashSet<>();
		inHand = null;
	}
	
	public boolean addBlock(Block newBlock, Block onTopOf) {
		if (blocks.contains(newBlock))
			return true;
		blocks.add(newBlock);
		if (onTopOf == null) {
			ArrayList<Block> s = new ArrayList<Block>();
			s.add(newBlock);
			table.add(s);
			return false;
		}
		
		for (int i = 0; i < table.size(); i++) {
			if (onTopOf.equals(table.get(i).get(0))) {
				table.get(i).add(0, newBlock);
				return false;
			}
		}
		return true;
	}
	
	public boolean stack(Block source, Block destination) {
		if (inHand == null || !source.equals(inHand)) {
			return true;
		}
		
		for (int i = 0; i < table.size(); i++) {
			try {
				if (destination.equals(table.get(i).get(0))) {
					table.get(i).add(0, source);
					inHand = null;
					return false;
				}
			} catch (Exception e) {
				continue;
			}
		}
		return true;
	}
	
	public boolean unstack(Block source, Block destination) {
		if (inHand != null)
			return true;
		
		for (int i = 0; i < table.size(); i++) {
			try {
				if (source.equals(table.get(i).get(0)) && destination.equals(table.get(i).get(1))) {
					inHand = source;
					table.get(i).remove(0);
					return false;
				}
			} catch (Exception e) {
				continue;
			}
		}
		return true;
	}
	
	public boolean pickup(Block source) {
		if (inHand != null)
			return true;
		
		for (int i = 0; i < table.size(); i++) {
			if (table.get(i).size() == 1 && table.get(i).get(0).equals(source)) {
				table.remove(i);
				inHand = source;
				return false;
			}
		}
		
		return true;
	}
	
	public boolean putdown(Block source) {
		if (inHand == null || !inHand.equals(source))
			return true;
		
		ArrayList<Block> newStack = new ArrayList<>();
		newStack.add(source);
		table.add(newStack);
		inHand = null;
		return false;
	}
	
	public boolean on(Block a, Block b) {
		for (ArrayList<Block> s : table) {
			for (int i = 0; i < s.size() - 1; i++) {
				if (s.get(i).equals(a) && s.get(i + 1).equals(b))
					return true;
			}
		}
		
		return false;
	}
	
	public boolean onTable(Block a) {
		for (ArrayList<Block> s : table) {
			if (s.get(s.size() - 1).equals(a))
				return true;
		}
		return false;
	}
	
	public boolean clear(Block a) {
		for (ArrayList<Block> s : table) {
			if (s.get(0).equals(a))
				return true;
		}
		return false;
	}
	
	public boolean hold(Block a) {
		if (inHand != null && inHand.equals(a))
			return true;
		return false;
	}
	
	public boolean armempty() {
		return inHand == null;
	}
	
	public String toString() {
		String result = "";
		for (int i = 0; i < table.size(); i++)
			result += table.get(i) + "\n";
		result += "Arm = " + inHand;
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public World clone() {
		World w = new World();
		w.blocks = (HashSet<Block>) this.blocks.clone();
		w.inHand = this.inHand;
		w.table = new ArrayList<>();
		for (int i = table.size() - 1; i >= 0; i--) {
			w.table.add(0, (ArrayList<Block>)table.get(i).clone());
		}
		return w;
	}
	@Override
	public boolean equals(Object other) {
		World o = (World)other;
		if ((inHand == null && o.inHand != null) ||
			(inHand != null && o.inHand == null) ||	
			(inHand != null && o.inHand != null && !inHand.equals(o.inHand)))
			return false;
		for (int i = 0; i < table.size(); i++) {
			boolean found = false;
			for (int j = 0; j < o.table.size(); j++) {
				if (o.table.get(j).get(0).equals(table.get(i).get(0))) {
					ArrayList<Block> first = table.get(i);
					ArrayList<Block> second = o.table.get(j);
					if (first.size() != second.size()) {
						return false;
					}
					for (int k = 0; k < second.size(); k++) {
						if (!first.get(k).equals(second.get(k))) {
							return false;
						}
					}
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		
		return true;
	}
}
