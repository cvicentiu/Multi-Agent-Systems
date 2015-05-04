
public class Pair<T1 extends Object, T2 extends Object> {
	T1 first;
	T2 second;
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	public String toString() {
		return first.toString() + " " + second.toString();
	}
}
