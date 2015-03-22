
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
		System.out.println(w);
		w.pickup(D);
		System.out.println(w);
		w.putdown(C);
		System.out.println(w);
		w.putdown(D);
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
		
	}
	 
}
