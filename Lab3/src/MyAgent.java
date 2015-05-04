import java.awt.TextArea;
import java.util.ArrayList;

import javax.swing.JFrame;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


class ReaderBehaviour extends CyclicBehaviour {
	private static final long serialVersionUID = 1L;
	
	Agent who;
	public ReaderBehaviour(Agent who) {
		this.who = who;
	}
	
	public void action() {
		ACLMessage m = who.receive();
		if (m != null)
			System.out.println("Got message: " + m.getContent());
	}
}


class WriterBehaviour extends CyclicBehaviour {
	private static final long serialVersionUID = 1L;
	
	int timesSent = 0;
	Agent who;
	public WriterBehaviour(Agent who, String message) {
		this.who = who;
	}
	
	public void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("reader", AID.ISLOCALNAME));
		msg.setContent("Mesaj! " + timesSent++);
		who.send(msg);
		timesSent++;
	}
}


public class MyAgent extends Agent {
	static ArrayList<String> messages = new ArrayList<>();
	private static final long serialVersionUID = 1L;
	protected void setup() {
		System.out.println("Hello! " + getAID().getName() + " is ready.");
		Object[] args = getArguments();
		if (args != null) {
			ReaderBehaviour b = new ReaderBehaviour(this);
			addBehaviour(b);
		} else {
			WriterBehaviour b = new WriterBehaviour(this, "Hello!");
			addBehaviour(b);
		}
		
		JFrame frame = new JFrame("Agent: " + getAID().getName());
		TextArea textArea = new TextArea(getAID().getName());
		frame.add(textArea);
		frame.pack();
		frame.setVisible(true);
		
		
		
	}
	

}
