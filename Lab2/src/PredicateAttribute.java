
public class PredicateAttribute {
	public Block getBlock() {
		return innerBlock;
	}
	
	public void setBlock(Block b) {
		this.innerBlock = b;
	}
	
	public PredicateAttribute(Block innerBlock) {
		this.innerBlock = innerBlock;
	}
	
	private Block innerBlock;
	
	public String toString() {
		return innerBlock.toString();
	}
	@Override
	public boolean equals(Object o) {
		return innerBlock.equals(((PredicateAttribute)o).innerBlock);
	}
}
