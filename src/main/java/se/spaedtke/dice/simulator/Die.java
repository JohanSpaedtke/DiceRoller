package se.spaedtke.dice.simulator;

import java.util.concurrent.ThreadLocalRandom;

public class Die {
	
	private final int faces;
	private int lastRoll;
	
	public Die(int faces){
		this.faces = faces;
	}
	
	public int roll(){
		lastRoll = ThreadLocalRandom.current().nextInt(1, faces + 1);
		return lastRoll;
	}
	
	public int getLastRoll(){
		return lastRoll != 0 ? lastRoll : roll();
	}
	
	public int getFaces(){
		return faces;
	}
	
	@Override
	public String toString(){
		return "d" + faces + " - " + (lastRoll != 0 ? lastRoll : "not rolled");
	}
}
