import java.util.ArrayList;


public class Knowledge {
	public static int FOG = 0;
	public static int STEPPED = 3;
	public static int EXPLORED = 1;
	public static int WALL = 2;
	
	float points = 0;
	int memCount;
	int board[][];
	ArrayList<Pair> plan = null;
	
	public Knowledge(int n) {
		board = new int[n][n];
		board[0][0] = EXPLORED;
		memCount = 0;
	}
	
	public void clearMemCount() {
		memCount = 0;
	}
	
	public int getMemCount() {
		return memCount;
	}
	
	public void fillKnowledge(int x, int y, Board b) {
		board[x][y] = STEPPED;
		int dx[] = {-1, 1, 0, 0};
		int dy[] = {0, 0, -1, 1};
		for (int i = 0; i < dx.length; i++) {
			int posX = x + dx[i];
			int posY = y + dy[i];
			if (posX >= 0 && posX < board.length && posY >= 0 && posY < board.length) {
				if (b.m[posX][posY] == Board.WALL)
					board[posX][posY] = WALL;
				else if (board[posX][posY] != STEPPED)
					board[posX][posY] = EXPLORED;
			}
		}
		
	}
	
	public String showMap(int robotX, int robotY) {
		String res = "";
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (i == robotX && j == robotY)
					res +="R";
				else {
					if (board[i][j] == FOG)
						res += "F";
					else {
						if (board[i][j] == EXPLORED)
							res += "*";
						else if (board[i][j] == WALL)
							res += "#";
						else
							res += "_";
					}
				}
					
			}
			res += "\n";
		}
		return res;
	}
	
	public ArrayList<Pair> pathTo(RobotInfo info, int posX, int posY) {
		ArrayList<Pair> path = new ArrayList<>();
		ArrayList<Pair> stack = new ArrayList<>();
		stack.add(new Pair(info.posX, info.posY));
		Pair parents[][] = new Pair[board.length][board.length];
		parents[info.posX][info.posY]= new Pair(-1, -1); 
		while (!stack.isEmpty()) {
			points -= 0.1;
			int dx[] = {-1, 1, 0, 0};
			int dy[] = {0, 0, -1, 1};
			Pair p = stack.remove(0);
			for (int i = 0; i < dx.length; i++) {
				int newPosX = p.x + dx[i];
				int newPosY = p.y + dy[i];
				if (!(newPosX >= 0 && newPosX < board.length && newPosY >= 0 && newPosY < board.length)) {
					continue;
				}
				Pair newPair = new Pair(newPosX, newPosY);
				if (parents[newPosX][newPosY] != null || board[newPosX][newPosY] == WALL || board[newPosX][newPosY] == FOG) {
					continue;
				} else {
					if (newPosX == posX && newPosY == posY) {
						path.add(newPair);
						parents[newPosX][newPosY] = p;
						Pair current = newPair;
						while (parents[current.x][current.y].x != -1) {
							current = parents[current.x][current.y];
							path.add(0, current);
						}
						return path;
					} else {
						parents[newPosX][newPosY] = p;
						stack.add(newPair);
					}
				}
			}
		}
		
		return path;
	}
	
	public ArrayList<Pair> closestUnexplored(RobotInfo info) {
		
		ArrayList<Pair> path = new ArrayList<>();
		ArrayList<Pair> stack = new ArrayList<>();
		stack.add(new Pair(info.posX, info.posY));
		Pair parents[][] = new Pair[board.length][board.length];
		parents[info.posX][info.posY]= new Pair(-1, -1); 
		while (!stack.isEmpty()) {
			points -= 0.1;
			int dx[] = {-1, 1, 0, 0};
			int dy[] = {0, 0, -1, 1};
			Pair p = stack.remove(0);
			for (int i = 0; i < dx.length; i++) {
				int newPosX = p.x + dx[i];
				int newPosY = p.y + dy[i];
				if (!(newPosX >= 0 && newPosX < board.length && newPosY >= 0 && newPosY < board.length)) {
					continue;
				}
				Pair newPair = new Pair(newPosX, newPosY);
				if (parents[newPosX][newPosY] != null || board[newPosX][newPosY] == WALL) {
					continue;
				} else {
					if (board[newPosX][newPosY] == FOG || board[newPosX][newPosY] == EXPLORED) {
						path.add(newPair);
						parents[newPosX][newPosY] = p;
						Pair current = newPair;
						while (parents[current.x][current.y].x != -1) {
							current = parents[current.x][current.y];
							path.add(0, current);
						}
						return path;
					} else {
						parents[newPosX][newPosY] = p;
						stack.add(newPair);
					}
				}
			}
		}
		
		return path;
	}
	
	public int[] getRequiredHeading(int posX, int posY, int nX, int nY) {
		if (posX == nX) {
			if (nY > posY) 
				return RobotInfo.EAST;
			else
				return RobotInfo.WEST;
		} else {
			if (nX > posX) 
				return RobotInfo.SOUTH;
			else
				return RobotInfo.NORTH;
		}
	}
	
	boolean isDirLeft(int dir[], int req[]) {
		return true;
	}
	
	public void executePlanStep(RobotInfo info) {
		if (plan == null) 
			return;
		int[] reqHeading = getRequiredHeading(info.posX, info.posY, plan.get(0).x, plan.get(0).y);
		if (info.dir == reqHeading) {
			info.moveForward(true);
			plan.remove(0);
			if (plan.size() == 0)
				plan = null;
		} else {
			boolean dir = isDirLeft(info.dir, reqHeading);
			if (dir) {
				info.rotateLeft(true, false);
			} else {
				info.rotateRight(true);
			}
		}
	}
}