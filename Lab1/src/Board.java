import java.util.ArrayList;
import java.util.Random;


public class Board {
	public int m[][];
	
	public static int CLEAR = 0;
	public static int WALL = 1;
	public static int FOOD = 2;
	public static Random r = new Random(42);
	
	private static int dx[] = { 0, 0, -1, -1, -1, 1,  1, 1};
	private static int dy[] = {-1, 1, -1,  1,  0, 0, -1,-1};
	
	public Board(int n, int m, boolean empty) {
		
		this.m = new int[n][m];
		if (empty) {
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++)
					this.m[i][j] = 0;
			return;
		}

		
		int countWall = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				this.m[i][j] = r.nextInt(3);
				/* wall restriction */
				if (this.m[i][j] == WALL) {
					boolean cond = true;
					if (countWall == n)
						cond = false;
					for (int k = 0; k < dx.length; k++) {
						if (i + dx[k] >= 0 && i + dx[k] < n &&
							j + dy[k] >= 0 && j + dy[k] < m)
							if (this.m[i + dx[k]][j + dy[k]] == WALL) {
								cond = false;
							}
					}
					if (!cond)
						j--;
					else
						countWall++;
				}
			}
		}
		
		this.m[0][0] = 0;
	}
	
	public String showMap(ArrayList<RobotInfo> robots) {
		String res = "";
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				int foundRobot = 0;
				String addition = "";
				for (int k = 0; k < robots.size(); k++) {
					if (robots.get(k).posX == i && robots.get(k).posY == j) {
						if (foundRobot == 0) {
							if (robots.get(k).isCognitive)
								addition +="C";
							else
								addition +="R";
						} else {
							addition = (foundRobot + 1)+ ""; 
						}
						
						foundRobot ++;
					}
				}
				res += addition;
				if (foundRobot == 0) {
					if (m[i][j] == CLEAR)
						res += "_";
					else {
						if (m[i][j] == FOOD)
							res += "*";
						else
							res += "#";
					}
				}
					
			}
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