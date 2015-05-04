import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Random;


class PupilBehaviour extends Behaviour {

	private static final long serialVersionUID = 1L;
	enum State {
		WAIT_QUESTION,
		ANSWER_QUESTION,
		FINISHED
	};
	State currentState = State.WAIT_QUESTION;
	String teacherName;
	String currentQuestion;
	PupilAgent pupil;
	public PupilBehaviour(PupilAgent pupil, String teacherName) {
		this.teacherName = teacherName;
		this.pupil = pupil;
	}
	
	@Override
	public void action() {
		switch (currentState) {
		case WAIT_QUESTION: {
			ACLMessage msg = pupil.receive();
			if (msg == null) {
				block();
				return;
			}
			if (msg.getPerformative() == ACLMessage.REQUEST) {
				currentState = State.ANSWER_QUESTION;
				String question = msg.getContent();
				currentQuestion = question;
				return;
			} else {
				System.out.println("Got the test results:");
				System.out.println(msg.getContent());
				currentState = State.FINISHED;
				break;
			}
		}
		case ANSWER_QUESTION: {
			Float answer = pupil.computeEquation(currentQuestion);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID(teacherName, AID.ISLOCALNAME));
			String answerMsg = currentQuestion.replaceAll("\\?", "" + answer);
			msg.setContent(answerMsg);
			pupil.send(msg);
			currentState = State.WAIT_QUESTION;
			block();
			break;
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
public class PupilAgent extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Random r = new Random(42);
	private float ERR_RATE = 0.15f;
	public Float computeEquation(String eq) {
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
		return r.nextFloat() < ERR_RATE ? result - 1 : result;
		
	}
	
	protected void setup() {
		Behaviour b = new PupilBehaviour(this, "teacher");
		addBehaviour(b);
	}
}
