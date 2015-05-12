/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/


import java.util.ArrayList;
import java.util.Arrays;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BookBuyerAgent extends Agent {
	// The title of the book to buy
	private String[] targetBookTitles;
	private int[] maximumBudget;
	// The list of known seller agents
	private AID[] sellerAgents;

	// Put agent initializations here
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Buyer-agent "+getAID().getName()+" is ready.");

		// Get the title of the book to buy as a start-up argument
		Object[] args = getArguments();
		if (args != null && args.length > 0 && args.length % 2 == 0) {
			targetBookTitles = new String[args.length / 2];
			maximumBudget = new int[args.length / 2];
			for (int i = 0; i < args.length; i += 2) {
				targetBookTitles[i / 2] = (String) args[i];
				maximumBudget[i / 2] = Integer.parseInt((String)args[i + 1]);
				System.out.println("Target book " + i + " is "+targetBookTitles[i / 2] + " " + maximumBudget[i / 2]);
			}

			// Add a TickerBehaviour that schedules a request to seller agents every minute
			addBehaviour(new TickerBehaviour(this, 20000) {
				int book = 0;
				protected void onTick() {
					
					System.out.println("Trying to buy " + targetBookTitles[book]);
					
					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("book-selling");
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template); 
						System.out.println("Found the following seller agents:");
						sellerAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							sellerAgents[i] = result[i].getName();
							System.out.println(sellerAgents[i].getName());
						}
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					myAgent.addBehaviour(new RequestPerformer(book));
					book = (book + 1) % targetBookTitles.length; // For next book.
				}
			} );
		}
		else {
			// Make the agent terminate
			System.out.println("No target book title specified");
			doDelete();
		}
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
	}

	/**
	   Inner class RequestPerformer.
	   This is the behaviour used by Book-buyer agents to request seller 
	   agents the target book.
	 */
	private class RequestPerformer extends Behaviour {
		private AID bestSeller; // The agent who provides the best offer 
		private int bestPrice;  // The best offered price
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		private int offerCnt = 0;
		private ArrayList<AID> sellers;
		private int bookNumber;
		private boolean askedTwice = false;

		public RequestPerformer(int bookNumber) {
			this.bookNumber = bookNumber;
			this.sellers = new ArrayList<AID>();
		}
		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < sellerAgents.length; ++i) {
					cfp.addReceiver(sellerAgents[i]);
				} 
				cfp.setContent(targetBookTitles[bookNumber]);
				cfp.setConversationId("book-trade");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						int price = Integer.parseInt(reply.getContent());
						System.out.println("Got offer from:" + reply.getSender() + " " + price);
						
						if (!sellers.contains(reply.getSender()))
								sellers.add(reply.getSender());
						if (bestSeller == null || price < bestPrice) {
							// This is the best offer at present
							bestPrice = price;
							bestSeller = reply.getSender();
						}
						offerCnt ++;
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length || offerCnt >= 3) {
						// We received all replies
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			case 2:
					
				if (bestPrice > maximumBudget[bookNumber]) {
					if (askedTwice) { // No money at all.
						step = 5;
						System.out.println("Buyer gives up, budget exceeded.");
						break;
					}
					askedTwice = true;
					repliesCnt = 0;
					offerCnt = 0;
					// Send the cfp to all sellers
					ACLMessage cfp2 = new ACLMessage(ACLMessage.CFP);
					for (int i = 0; i < sellers.size(); ++i) {
						cfp2.addReceiver(sellers.get(i));
					} 
					cfp2.setContent("[]" + targetBookTitles[bookNumber]);
					cfp2.setConversationId("book-trade");
					cfp2.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
					myAgent.send(cfp2);
					// Prepare the template to get proposals
					mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
							MessageTemplate.MatchInReplyTo(cfp2.getReplyWith()));
					step = 1;
					break;
				} else {
					step = 3;
				}
			case 3:
				// Send the purchase order to the seller that provided the best offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestSeller);
				order.setContent(targetBookTitles[bookNumber]);
				order.setConversationId("book-trade");
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 4;
				break;
			case 4:      
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println(targetBookTitles[bookNumber]+" successfully purchased from agent "+reply.getSender().getName());
						System.out.println("Price = "+bestPrice);
						//myAgent.doDelete();
					}
					else {
						System.out.println("Attempt failed: requested book already sold.");
					}

					step = 5;
				}
				else {
					block();
				}
				break;
			}        
		}

		public boolean done() {
			if (step == 2 && bestSeller == null) {
				System.out.println("Attempt failed: "+targetBookTitles[bookNumber]+" not available for sale");
			}
			return ((step == 2 && bestSeller == null) || step == 5);
		}
	}  // End of inner class RequestPerformer
}
