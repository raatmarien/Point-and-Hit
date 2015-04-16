package com.jmstudios.pointandhit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class ShootAnimation {
	int maxRadius, minRadius;
	int width, height;
	int currentRadius;
	int targetX, targetY;
	int currentX, currentY;
	int scoreSize;
	float shootingDuration, shootingTimeLeft
	, scoreDuration, scoreTimeLeft;
	boolean shooting, active;
	TargetManager targetManager;
	Texture[] scoreTextures;
	Texture currentScoreTexture;
	
	public ShootAnimation(float shootingDuration
			, float scoreDuration
			, Color bulletColor, int minRadius
			, TargetManager targetManager
			, Texture[] scoreTextures) {
		this.shootingDuration = shootingDuration;
		this.shootingTimeLeft = 0;
		this.scoreDuration = scoreDuration;
		this.scoreTimeLeft = 0;
		this.targetManager = targetManager;
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.minRadius = minRadius;
		this.maxRadius = (width / 6);
		this.currentRadius = 0;
		this.shooting = false;
		this.active = false;
		this.scoreTextures = scoreTextures;
		
		scoreSize = width / 8;
	}
	
	public void update() {
		if (shooting) {
			shootingTimeLeft -= Gdx.graphics.getDeltaTime();
			currentRadius = minRadius
					+ (int) ((float) (maxRadius - minRadius)
					* Math.pow((shootingTimeLeft / shootingDuration), 2));
			int centerX = width / 2
			, centerY = height / 2;
			currentX = targetX + (int)((float) (centerX - targetX)
					* Math.pow((shootingTimeLeft / shootingDuration), 2));
			currentY = targetY + (int)((float) (centerY - targetY)
					* Math.pow((shootingTimeLeft / shootingDuration), 2));
		} else if (active) {
			scoreTimeLeft  -= Gdx.graphics.getDeltaTime();
		}
		if (shootingTimeLeft <= 0 && shooting && active) {
			shooting = false;
			int extraScore = targetManager
					.hit(new Vector2(targetX, targetY));
			if (extraScore != 0) {
				int index = extraScore - (extraScore > 3 ? 2 : 1);
				currentScoreTexture = scoreTextures[index];
				scoreTimeLeft = scoreDuration;
				active = true;
			} else {
				active = false;
			}
		}
		if (scoreTimeLeft <= 0 && !shooting && active) {
			active = false;
		}
	}
	
	// Call ShapeRenderer.begin() before calling this method
	// And call ShapeRenderer.end() when targetYou're done with it
	public void draw(ShapeRenderer shapeRenderer) {
		if (shooting) {
			ShapeType previousType = shapeRenderer.getCurrentType();
			shapeRenderer.set(ShapeType.Filled);
			shapeRenderer.setColor(targetManager.currentTheme.bullet);
			shapeRenderer.circle(currentX
					, currentY, currentRadius);
			shapeRenderer.set(previousType);
		}
	}
	
	public void draw(SpriteBatch batch) {
		if (active && !shooting) {
			batch.draw(currentScoreTexture, targetX - scoreSize
					, targetY - scoreSize, scoreSize, scoreSize);
		}
	}
	
	public void startShoot(int targetX, int targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
		this.currentX = width / 2;
		this.currentY = height / 2;
		this.shootingTimeLeft = shootingDuration;
		this.currentRadius = maxRadius;
		this.shooting = true;
		this.active = true;
	}

	public boolean isShooting() {
		return shooting; 
	}

	public boolean isActive() {
		return active;
	}
}
