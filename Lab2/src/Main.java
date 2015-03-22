import java.util.Random;


class Board {
	public int m[][];
	
	public static int CLEAR = 0;
	public static int WALL = 1;
	public static int FOOD = 2;
	public static int ROBOT = 3;
	public static Random r = new Random(42);
	
	private static int dx[] = { 0, 0, -1, -1, -1, 1,  1, 1};
	private static int dy[] = {-1, 1, -1,  1,  0, 0, -1,-1};
	
	public Board(int n, int m) {
		this.m = new int[n][m];
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				this.m[i][j] = r.nextInt(3);
				/* wall restriction */
				if (this.m[i][j] == WALL) {
					boolean cond = true;
					for (int k = 0; k < dx.length; k++) {
						if (i + dx[k] >= 0 && i + dx[k] < n &&
							j + dy[k] >= 0 && j + dy[k] < m)
							if (this.m[i + dx[k]][j + dy[k]] == WALL) {
								cond = false;
							}
					}
					if (!cond)
						j--;
				}
			}
		}
		
		this.m[0][0] = 3;
	}
	
	public String toString() {
		String res = "";
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++)
				res += m[i][j];
			res += "\n";
		}
		return res;
	}
	
	public boolean hasFood() {
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[0].length; j++)
				if (m[i][j] == FOOD)
					return true;
		return false;
	}
}

class RobotInfo {
	public static int[] NORTH = {-1, 0}; 
	public static int[] SOUTH = {1, 0};
	public static int[] WEST =  {0, -1};
	public static int[] EAST =  {0,  1};
	
	int[] dir = NORTH;
	int posX = 0;
	int posY = 0;
	
	public void rotateLeft(boolean yell, boolean change) {
		String dirName;
		if (dir == NORTH) {
			dir = WEST;
			dirName = "WEST";
		} else if (dir == SOUTH) {
			dir = EAST;
			dirName = "EAST";
		} else if (dir == WEST) {
			dir = SOUTH;
			dirName = "SOUTH";
		} else {// dir == EAST
			dir = NORTH;
			dirName = "NORTH";
		}
		
		if (yell && !change)
			System.out.println("Rotating Left, Looking: " + dirName);
		if (yell && change)
			System.out.println("Rotating Right, Looking: " + dirName);
	}
	
	public void rotateRight(boolean yell) {
		rotateLeft(false, false);
		rotateLeft(false, false);
		rotateLeft(yell, true);
	}
	
	public void moveForward(boolean yell) {
		
		this.posX += dir[0];
		this.posY += dir[1];
		
		if (yell)
			System.out.println("Moving Forward!");
	}
	
	public void moveBackward(boolean yell) {
		this.posX -= dir[0];
		this.posY -= dir[1];
		
		if (yell)
			System.out.println("Moving Backward!");
	}
}
public class Main {

	public static void main(String[] argv) throws InterruptedException {
		Board b = new Board(20, 20);
		RobotInfo r = new RobotInfo();
		System.out.println(b);
		int points = 0;
		while (b.hasFood() || !(r.posX == 0 && r.posY == 0)) {
			int choice = Board.r.nextInt(3);
			int deltaPoints = 0;
			if (choice == 0) {
				r.rotateLeft(true, false);
				deltaPoints = -1;
			}
			if (choice == 1) {
				r.rotateRight(true);
				deltaPoints = -1;
			}
			if (choice == 2) {
				assert(b.m[r.posX][r.posY] == Board.ROBOT);
				r.moveForward(false);
				if (r.posX < 0 || r.posX >= b.m.length ||
					r.posY < 0 || r.posY >= b.m[0].length ||
					b.m[r.posX][r.posY] == Board.WALL ) {
					r.moveBackward(false);
					continue;
				}
				r.moveBackward(false);
				b.m[r.posX][r.posY] = Board.CLEAR;
				r.moveForward(true);
				if (b.m[r.posX][r.posY] == Board.FOOD) {
					deltaPoints = 100;
				} else {
					deltaPoints = -1;
				}
				assert(b.m[r.posX][r.posY] != Board.WALL);
				b.m[r.posX][r.posY] = Board.ROBOT; 
			}
			points += deltaPoints;
			System.out.println("Current points: " + points);
			System.out.println("Current position: " + r.posX + " " + r.posY);
			System.out.println(b);
			//Thread.sleep(1000);
			
		}
	}
}
