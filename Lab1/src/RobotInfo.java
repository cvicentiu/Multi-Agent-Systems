
public class RobotInfo {
	public static int[] NORTH = {-1, 0}; 
	public static int[] SOUTH = {1, 0};
	public static int[] WEST =  {0, -1};
	public static int[] EAST =  {0,  1};
	
	int[] dir = NORTH;
	int posX = 0;
	int posY = 0;
	int initX = -1, initY = -1;
	boolean isCognitive = false;
	int points;
	
	public RobotInfo(int x, int y, boolean cognitive) {
		posX = x;
		posY = y;
		initX = x;
		initY = y;
		isCognitive = cognitive;
	}
	
	boolean isCognitive() {
		return isCognitive;
	}
	boolean inInitialPosition() {
		return posX == initX && posY == initY;
	}
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