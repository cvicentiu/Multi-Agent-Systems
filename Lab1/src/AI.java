import java.util.ArrayList;


public class AI {
	int id;
	boolean cognitive;
	Knowledge kn;
	public AI(int id, boolean cognitive, int bSize) {
		this.id = id;
		kn = new Knowledge(bSize);
		this.cognitive = cognitive;
	}
	
	int[] dx = {0, 0, -1, 1};
	int[] dy = {1, -1, 0, 0};
	
	public Action getNextMove(Board b, ArrayList<RobotInfo> r) {
		if (!cognitive)
			return getNextMoveReactive(b, r);
		return getNextMoveActive(b, r);
		//return Action.STAY;
	}
	
	public Action getNextMoveActive(Board b, ArrayList<RobotInfo> robots) {
		RobotInfo self = robots.get(id);

		ArrayList<Pair> available = new ArrayList<>();
		for (int i = 0; i < dx.length; i++) {
			int newX = self.posX + dx[i];
			int newY = self.posY + dy[i];
			try {
				if (b.m[newX][newY] != Board.WALL) {
					available.add(new Pair(newX, newY));
				}
			} catch (Exception e) {}
		}
		
		if (kn.plan == null || kn.plan.size() == 0) {
			kn.plan = kn.closestUnexplored(self);
			if (kn.plan.size() > 0)
				kn.plan.remove(0);
		}
		
		if (!b.hasFood() && !self.inInitialPosition()) {
			kn.plan = kn.pathTo(self, self.initX, self.initY);
			if (kn.plan.size() > 0)
				kn.plan.remove(0);
		}
		if (!b.hasFood() && self.inInitialPosition())
			return Action.STAY;
		
		for (int i = 0; i < robots.size(); i++) { 
			if (i != id) {
				RobotInfo other = robots.get(i);
				if (isNear(self, other) && !other.isCognitive()) {
					Pair best = available.get(0);
					int bestDist = Math.abs(best.x - other.posX) + Math.abs(best.y - other.posY);
					for (int j = 0; j < available.size(); j++) {
						int dist = Math.abs(available.get(j).x - other.posX) + Math.abs(available.get(j).y - other.posY);
						if (dist > bestDist) {
							bestDist = dist;
							best = available.get(j);
						}
					}
					available.clear();
					available.add(best);
				}
			}
		}
		
		if (kn.plan.size() != 0) {
			self.posX = kn.plan.get(0).x;
			self.posY = kn.plan.get(0).y;
			System.out.println(kn.plan);
			kn.plan.remove(0);
			
			kn.fillKnowledge(self.posX, self.posY, b);
			return Action.MOVE;
		}
		
		if (available.size() == 1) {
			self.posX = available.get(0).x;
			self.posY = available.get(0).y;
			return Action.MOVE;
		}
		
		return Action.MOVE;
	}
	
	public Action getNextMoveReactive(Board b, ArrayList<RobotInfo> robots) {
		RobotInfo self = robots.get(id);
		ArrayList<Pair> available = new ArrayList<>();
		
		if (!b.hasFood() && self.inInitialPosition())
			return Action.STAY;
		
		for (int i = 0; i < dx.length; i++) {
			int newX = self.posX + dx[i];
			int newY = self.posY + dy[i];
			try {
				if (b.m[newX][newY] != Board.WALL) {
					available.add(new Pair(newX, newY));
				}
			} catch (Exception e) {}
		}
		
		
		for (int i = 0; i < robots.size(); i++) { 
			if (i != id) {
				RobotInfo other = robots.get(i);
				if (isNear(self, other) && other.isCognitive()) {
					if (other.inInitialPosition() && !b.hasFood())
						continue;
					Pair best = available.get(0);
					int bestDist = Math.abs(best.x - other.posX) + Math.abs(best.y - other.posY);
					for (int j = 0; j < available.size(); j++) {
						int dist = Math.abs(available.get(j).x - other.posX) + Math.abs(available.get(j).y - other.posY);
						if (dist < bestDist) {
							bestDist = dist;
							best = available.get(j);
						}
					}
					available.clear();
					available.add(best);
				}
			}
		}
		
		int nextPos = Main.r.nextInt(available.size()); // Random move selection.
		self.posX = available.get(nextPos).x;
		self.posY = available.get(nextPos).y;
		return Action.MOVE;
		
	}
	
	public boolean isNear(RobotInfo r1, RobotInfo r2) {
		return Math.abs(r1.posX - r2.posX) <= 2 && Math.abs(r1.posY - r2.posY) <= 2;
	}
	
	public float getInfoUsed() {
		// TODO Auto-generated method stub
		return 0;
	}
}