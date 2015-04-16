 package com.jmstudios.pointandhit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class BulletManager {
	float shootCoolDownTime, shootCoolDownTimeLeft
	, animationTime = 0.2f, textTime = 3f;
	TargetManager targetManager;
	ShootAnimation[] shootAnimations;
	int ammountShootAnimations;
	UserPointer userPointer;
	Texture[] scoreSprites;
	
	public BulletManager(float shootCoolDownTime
			, TargetManager targetManager
			, UserPointer userPointer) {
		this.shootCoolDownTime = shootCoolDownTime;
		this.shootCoolDownTimeLeft = 0;
		this.targetManager = targetManager;
		this.userPointer = userPointer;
		
		// Textures
		scoreSprites = new Texture[4];
		for (int i = 1, j = 0; j < 4; i += i == 3 ? 2 : 1, j++) {
			scoreSprites[j] = new Texture(Gdx.files.internal("+" + i + "points.png"));
		}
		
		int shootAnimationsNeeded = (int) Math.ceil(
				(animationTime + textTime) / shootCoolDownTime);
		this.ammountShootAnimations = shootAnimationsNeeded;
		Color bulletColor = new Color(0.76f
				, 0.58f, 0.11f, 1f);
		this.shootAnimations = new ShootAnimation
				[shootAnimationsNeeded];
		for (int i = 0; i < shootAnimationsNeeded; i++) {
			shootAnimations[i] = new ShootAnimation(0.2f, 1f
					, bulletColor, userPointer.getRadius()
					, targetManager, scoreSprites);
		}
	}
	
	public void update() {
		if (shootCoolDownTimeLeft > 0) {
			shootCoolDownTimeLeft -= Gdx.graphics.getDeltaTime();
		}
		for (ShootAnimation shootAnimation : shootAnimations) {
			shootAnimation.update();
		}
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		for (ShootAnimation shootAnimation : shootAnimations) {
			shootAnimation.draw(shapeRenderer);
		}
	}
	
	public void draw(SpriteBatch batch) {
		for (ShootAnimation shootAnimation : shootAnimations) {
			shootAnimation.draw(batch);
		}
	}
	
	public void shoot() {
		if (shootCoolDownTimeLeft <= 0) {
			Vector2 targetPosition = userPointer.getCenterPosition();
			for (int i = 0; i < ammountShootAnimations; i++) {
				if (!shootAnimations[i].isActive()) {
					shootAnimations[i].startShoot((int) targetPosition.x
							, (int) targetPosition.y);
					break;
				}
			}
			shootCoolDownTimeLeft = shootCoolDownTime;
		}
	}
}
