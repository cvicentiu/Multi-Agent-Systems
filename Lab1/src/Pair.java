public class Pair {
	int x, y;
	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public boolean equals(Object p) {
		return ((Pair)p).x == x && ((Pair)p).y == y; 
	}
	public String toString() {
		return "(" + x + " " + y + ")";
	}
}