package com.github.wolfie.datagame;

import java.awt.event.KeyEvent;

import com.github.wolfie.engine.KeyData;

public class DataKeys extends KeyData {

	public final Key pause = new Key();

	public DataKeys() {
		add(KeyEvent.VK_ESCAPE, pause);
	}

}
