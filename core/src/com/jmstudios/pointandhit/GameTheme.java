package com.jmstudios.pointandhit;

import com.badlogic.gdx.graphics.Color;

public class GameTheme {
	Color background, target, userPointer
	, bullet;
	public GameTheme() {
		background = null;
		target = null;
		userPointer = null;
		bullet = null;
	}
	
	public GameTheme(Color background
			, Color target, Color userPointer
			, Color bullet) {
		this.background = background;
		this.target = target;
		this.userPointer = userPointer;
		this.bullet = bullet;
	}
}
