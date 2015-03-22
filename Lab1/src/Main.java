import java.util.ArrayList;
import java.util.Random;

public class Main {

	public static void resolveActions(float []points) {
		
	}
	
	public static Random r = new Random(42);
	public static void main(String[] argv) throws InterruptedException {
		int bSize = 10;
		int n = bSize;
		Board b = new Board(bSize, bSize, false);
		System.out.println(b);

		
		ArrayList<RobotInfo> robots = new ArrayList<>();
		ArrayList<AI> ais = new ArrayList<>();
		
		float []points = new float[4];
		float []info = new float[4];
		for (int i = 0; i < points.length; i++) {
			boolean cognitive = i % 2 == 0;
			ais.add(new AI(i, cognitive, bSize));
			points[i] = 0;
			info[i] = 0;
			
		}
		
		robots.add(new RobotInfo(0, 0, true));
		robots.add(new RobotInfo(0, n - 1 , false));
		robots.add(new RobotInfo(n - 1, n - 1 , true));
		robots.add(new RobotInfo(n - 1, 0, false));
		
		while (b.hasFood() 
				|| !(robots.get(0).inInitialPosition())
				|| !(robots.get(1).inInitialPosition())
				|| !(robots.get(2).inInitialPosition())
				|| !(robots.get(3).inInitialPosition())) 
		{
			ArrayList<Action> actions = new ArrayList<Action>();
			for (int i = 0; i < robots.size(); i++) {
				actions.add(ais.get(i).getNextMove(b, robots));
				info[i] = ais.get(i).getInfoUsed();
			}
			
			
			resolveActions(actions, b, robots, points, info);
			System.out.println("Points: ");
			for (int i = 0; i < ais.size(); i++) {
				//points[i] -= ais.get(i).kn.points;
				System.out.print(i + ": " + Math.round(points[i]) + "; ");
			}
			System.out.println();
			System.out.println(b.showMap(robots));
			Thread.sleep(400);
			/*
			if (b.m[r.posX][r.posY] == Board.FOOD) {
				b.m[r.posX][r.posY] = Board.CLEAR;
				System.out.println("Picked up food");
				kn.points += 100;
				continue;
			}
			kn.fillKnowledge(r.posX, r.posY, b);
			System.out.println(kn.showMap(r.posX, r.posY));
			if (kn.plan == null) {
				kn.plan = kn.closestUnexplored(r);
				kn.plan.remove(0);
			}
			System.out.println(kn.plan);
			kn.executePlanStep(r);
			kn.points -= 1;
			System.out.println(r.posX + " " + r.posY);
			System.out.println(b.showMap(r.posX, r.posY));
			System.out.println(kn.points);
			Thread.sleep(1000);
			*/
		}
	}
	private static void resolveActions(ArrayList<Action> actions, Board b,
			ArrayList<RobotInfo> robots, float[] points, float mem[]) {
		System.out.println(actions);
		for (int i = 0; i < actions.size(); i++) {
			//System.out.println("Robot " + i);
			if (actions.get(i) == Action.MOVE) {
				points[i] -= 1;
			}
			points[i] -= mem[i];
			//System.out.println(mem[i]);
		}
		
		
		for (int i = 0; i < robots.size(); i++) {
			RobotInfo r = robots.get(i);
			if (b.m[r.posX][r.posY] == Board.FOOD) {
				points[i] += 10;
				b.m[r.posX][r.posY] = Board.CLEAR;
				System.out.println("Added 10 points to " + i + "=" + points[i]);
			}
		}
		
		for (int i = 0; i < robots.size(); i++) {
			for (int j = 0; j < robots.size(); j++) {
				if (i == j)
					continue;
				if (!(robots.get(i).posX == robots.get(j).posX && robots.get(i).posY == robots.get(j).posY))
					continue;
				if (i % 2 == j % 2) {
					float avg = (points[i] + points[j]) / 2;
					points[i] = avg;
					points[j] = avg;
				}
				if (i % 2 != j % 2) {
					int winner = i;
					int looser = j;
					if (j % 2 == 1) {
						winner = j;
						looser = i;
					}
					if (!robots.get(looser).inInitialPosition()) {
						if (points[looser] > 0) {
							points[winner] += points[looser];
							points[looser] = 0;
						}
					}
				}
			}
		}
	}
}
