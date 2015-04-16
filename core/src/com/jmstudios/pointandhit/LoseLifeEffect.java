package com.jmstudios.pointandhit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoseLifeEffect {
	boolean active;
	float effectDuration, timeLeft;
	Texture drawTex;
	int width, height;
	TargetManager targetManager;
	
	public LoseLifeEffect(float effectDuration
			, TargetManager targetManager) {
		this.effectDuration = effectDuration;
		this.timeLeft = 0;
		this.active = false;
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.drawTex = new Texture(Gdx.files.internal("lose_life_effect.png"));
		this.targetManager = targetManager;
	}
	
	public void update() {
		if (active) {
			timeLeft -= Gdx.graphics.getDeltaTime();
			if (timeLeft <= 0) {
				active = false;
				targetManager.spawnTarget();
			}
		}
	}
	
	public void draw(SpriteBatch batch) {
		if (active) {
			batch.draw(drawTex, 0, 0, width, height);
		}
	}

	public void start() {
		active = true;
		timeLeft = effectDuration;
	}
	
	public boolean isActive() {
		return active;
	}
}
