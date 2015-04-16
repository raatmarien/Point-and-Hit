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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class Target {
	float lifeTime, timeLeft
	, dyingTimeLeft, dyingDuration; // seconds
	int radius, currentRadius; // pixels
	Vector2 centerPosition; // pixels
	Vector2 dyingPosition; // pixels
	int maxDyingRadius, currentDyingRadius; // pixels
	TargetManager targetManager;
	boolean dying;
	
	public Target(float lifeTime, int radius
			, Vector2 centerPosition
			, TargetManager targetManager
			, float dyingDuration) {
		this.lifeTime = lifeTime;
		this.radius = radius;
		this.timeLeft = this.lifeTime;
		this.centerPosition = centerPosition;
		this.currentRadius = radius;
		this.dyingTimeLeft = 0;
		this.dying = false;
		this.dyingDuration = dyingDuration;
		this.targetManager = targetManager;
	}
	
	public void reinnitialize(float lifeTime
			, int radius, Vector2 centerPosition
			, Color color) {
		this.lifeTime = lifeTime;
		this.radius = radius;
		this.timeLeft = this.lifeTime;
		this.centerPosition = centerPosition;
		this.currentRadius = radius;
		this.dying = false;
		this.dyingTimeLeft = 0;
	}
	
	public void update() {
		timeLeft -= Gdx.graphics.getDeltaTime();
		currentRadius = (int) (radius * (timeLeft / lifeTime));
		if (dying) {
			this.dyingTimeLeft -= Gdx.graphics.getDeltaTime();
			currentDyingRadius = (int)
					(((dyingDuration - dyingTimeLeft) 
							/ dyingDuration)
					* (float) (maxDyingRadius));
			if (this.dyingTimeLeft <= 0) {
				dying = false;
				targetManager.spawnTarget();
			}
		}
	}
	
	// Call ShapeRenderer.begin() before calling this method
	// And call ShapeRenderer.end() when you're done with it
	public void draw(ShapeRenderer shapeRenderer) {
		ShapeType previousType = shapeRenderer.getCurrentType();
		shapeRenderer.set(ShapeType.Filled);
		if (currentRadius > 0) {
			shapeRenderer.setColor(targetManager.currentTheme.target);
			shapeRenderer.circle(centerPosition.x
					, centerPosition.y, getRadius());
		}
		if (dying) {
			shapeRenderer.setColor(targetManager.currentTheme.background); // Background color
			shapeRenderer.circle(centerPosition.x
						 , centerPosition.y
						 , currentDyingRadius);
		}
		shapeRenderer.set(previousType);
	}
	
	public void explode(Vector2 position) {
		this.dying = true;
		this.dyingTimeLeft = dyingDuration;
		this.dyingPosition = position;
		this.maxDyingRadius = this.radius;
		this.currentDyingRadius = 0;
	}
	
	public boolean exists() {
		return currentRadius > 0 || dying;
	}
	
	public int getRadius() {
		return currentRadius;
	}
	
	public int getStartRadius() {
		return radius;
	}
	
	public Vector2 getCenterPosition() {
		return centerPosition;
	}
}
