
public class Block {
	String id;
	
	public Block(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.id.equals(((Block) obj).id);
	}
	@Override
	public String toString() {
		return this.id;
	}
}
