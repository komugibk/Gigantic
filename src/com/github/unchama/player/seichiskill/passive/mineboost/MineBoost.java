package com.github.unchama.player.seichiskill.passive.mineboost;

/**
 * @author tar0ss
 *
 */
public class MineBoost {

	//ブースト量
	private short amplifier;

	public MineBoost() {
		this.amplifier = 0;
	}

	public MineBoost(short amplifier) {
		this.amplifier = amplifier;
	}

	public void setAmplifier(short amplifier) {
		this.amplifier = amplifier;
	}

	public short getAmplifier() {
		return this.amplifier;
	}

}
