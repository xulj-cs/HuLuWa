package game;

import creature.Creature;

public class Tile {

	// the creature who stands on the tile
	private Creature creature = null;
	
	public Tile(){  }

	public void clearCreature(){
		setCreature(null);
	}
	
	public void setCreature(Creature creature) {
	    this.creature = creature;
	}

	public Creature getCreature() {
		return creature;
	}
	
	public boolean isEmpty(){
		return creature == null;
	}


}
