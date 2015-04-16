/*
Point & Hit: A fast paced Android game
Copyright (C) 2015 Marien Raat

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
