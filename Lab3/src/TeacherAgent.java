import java.util.ArrayList;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;


class TeacherBehaviour extends Behaviour {

	private static final long serialVersionUID = 1L;
	int nQuestions;
	int questionsAsked = 0;
	enum State {
		ASK_QUESTION,
		WAIT_ANSWER,
		SEND_TEST_RESULTS,
		FINISHED
	};
	State currentState = State.ASK_QUESTION;
	Float expectedAnswer;
	String studentName;
	TeacherAgent teacher;
	ArrayList<Pair<String, Boolean>> results;
	public TeacherBehaviour(TeacherAgent teacher, int nQuestions, String studentName) {
		this.nQuestions = nQuestions;
		this.studentName = studentName;
		this.teacher = teacher;
		this.results = new ArrayList<>();
		
	}
	
	@Override
	public void action() {
		System.out.println("Teacher current state:" + currentState);
		switch (currentState) {
		case ASK_QUESTION: {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			Pair<String, Float> eq = teacher.generateEquation();
			msg.addReceiver(new AID(studentName, AID.ISLOCALNAME));
			msg.setContent(eq.first);
			teacher.send(msg);
			expectedAnswer = eq.second;
			currentState = State.WAIT_ANSWER;
			questionsAsked++;
			block();
			break;
		}
		case WAIT_ANSWER: {
			ACLMessage msg = teacher.receive();
			String reply = msg.getContent();
			System.out.println("Teacher got answer: " + reply);
			String[] split = reply.split(" ");
			Float answer = Float.parseFloat(split[4]);
			if (Math.abs(answer - expectedAnswer) < 0.0001)
				results.add(new Pair<>(reply, true));
			else
				results.add(new Pair<>(reply, false));
			if (questionsAsked == nQuestions)
				this.currentState = State.SEND_TEST_RESULTS;
			else
				this.currentState = State.ASK_QUESTION;
			break;
		}
		case SEND_TEST_RESULTS: {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID(studentName, AID.ISLOCALNAME));
			int totalScore = 0;
			for (Pair<String, Boolean> q : results) {
				if (q.second)
					totalScore++;
			}
			msg.setContent(results.toString() + " " + totalScore);
			teacher.send(msg);
			this.currentState = State.FINISHED;
		}
		default:
			break;
		}
		
		
	}

	@Override
	public boolean done() {
		return currentState == State.FINISHED;
	}
	
}
public class TeacherAgent extends Agent {

	private static final long serialVersionUID = 1L;
	public static Random r = new Random(42);
	
	public Pair<String, Float> generateEquation() {
		Pair<String, Float> result;
		char[] opList = { '+', '-', '*', '/' };
		char op = opList[r.nextInt(4)];
		int t1 = r.nextInt(500);
		int t2 = r.nextInt(500)	;
		if (op == '/' && t2 == 0) {
			t2 += 1;
		}
		String eq = t1 + " " + op + " " + t2 + " = " + "?"; 
		result = new Pair<String, Float>(eq, computeEquation(eq));
		return result;
	}
	
	private Float computeEquation(String eq) {
		String[] split = eq.split(" ");
		int t1 = Integer.parseInt(split[0]);
		int t2 = Integer.parseInt(split[2]);
		String op = split[1];
		Float result = 0.0f;
		switch (op) {
		case "+": result = (float) (t1 + t2);
				  break;
		case "-": result = (float) (t1 - t2);
				  break;
		case "*": result = (float) (t1 * t2);
				  break;
		case "/": result = (float) (t1 / (float)t2);
				  break;
		}
		return result;
		
	}
	protected void setup() {
		Behaviour b = new TeacherBehaviour(this, 20, "pupil");
		addBehaviour(b);
	}

}
