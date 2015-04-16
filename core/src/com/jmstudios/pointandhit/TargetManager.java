package com.jmstudios.pointandhit;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class TargetManager {
	float targetLifeTime;
	int targetRadius;
	Target target;
	int screenX, screenY;
	int lowerX, lowerY
	, higherX, higherY;
	int score, lifes;
	boolean shoot;
	Random randomGenerator;
	UserPointer userPointer;
	BulletManager bulletManager;
	LoseLifeEffect loseLifeEffect;
	
	GameTheme currentTheme, blueTheme
		, purpleTheme, brownTheme
		, darkBlueTheme, greenTheme;
	GameScreen screen;
	
	public TargetManager(float targetLifeTime
			, int targetRadius
			, Vector2 screenSize
			, UserPointer userPointer
			, GameScreen screen) {
		this.screen = screen;
		this.targetLifeTime = targetLifeTime;
		this.targetRadius = targetRadius;
		this.userPointer = userPointer;
		this.shoot = false;
		this.screenX = (int) screenSize.x;
		this.screenY = (int) screenSize.y;
		int targetRadiusBound = targetRadius / 2;
		lowerX = targetRadiusBound;
		lowerY = targetRadiusBound;
		higherX = screenX - targetRadiusBound;
		higherY = screenY - targetRadiusBound;
		this.randomGenerator = new Random();
		spawnNewTarget();
		
		this.bulletManager = new BulletManager(0.4f, this, userPointer);
		
		this.loseLifeEffect = new LoseLifeEffect(0.4f, this);
		
		// Theme
		blueTheme = new GameTheme();
		blueTheme.background = new Color(0.2f, 0.4f, 0.5f, 1);
		blueTheme.target = new Color(0.9f, 0.35f, 0.1f, 1);
		blueTheme.userPointer = Color.WHITE;
		blueTheme.bullet = Color.LIGHT_GRAY;
		
		purpleTheme = new GameTheme();
		purpleTheme.background = new Color(0.2f, 0.2f, 0.5f, 1);
		purpleTheme.target = new Color(0.87f, 0.58f, 0.85f, 1);
		purpleTheme.userPointer = Color.WHITE;
		purpleTheme.bullet = Color.LIGHT_GRAY;
		
		darkBlueTheme = new GameTheme();
		darkBlueTheme.background = new Color(0.235f, 0.235f, 0.39f, 1);
		darkBlueTheme.target = new Color(0.39f, 0.69f, 0.29f, 1);
		darkBlueTheme.userPointer = Color.WHITE;
		darkBlueTheme.bullet = Color.LIGHT_GRAY;
		
		brownTheme = new GameTheme();
		brownTheme.background = new Color(0.45f, 0.28f, 0.24f, 1);
		brownTheme.target = new Color(0.51f, 0.75f, 0.81f, 1);
		brownTheme.userPointer = Color.WHITE;
		brownTheme.bullet = Color.LIGHT_GRAY;
		
		greenTheme = new GameTheme();
		greenTheme.background = new Color(0.34f, 0.53f, 0.27f, 1);
		greenTheme.target = new Color(0.42f, 0.33f, 0.55f, 1);
		greenTheme.userPointer = Color.WHITE;
		greenTheme.bullet = Color.LIGHT_GRAY;
		
		this.score = 0;

		this.currentTheme = getTheme(score);
		
		this.lifes = 3;
	}
	
	public void update() {
		bulletManager.update();
		loseLifeEffect.update();
		if (this.shoot) {
			shootBullet();
			this.shoot = false;
		}

		if (!loseLifeEffect.isActive()) {
			target.update();
			if (!target.exists()) {
				lifes--;
				loseLifeEffect.start();
			}
		}

		if (lifes <= 0 && screen != null) {
			loseLifeEffect.active = false;
			screen.gameOver();
		}
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		if (!loseLifeEffect.isActive()) {
			target.draw(shapeRenderer);
		}
		bulletManager.draw(shapeRenderer);
	}
	
	public void draw(SpriteBatch batch) {
		bulletManager.draw(batch);
		loseLifeEffect.draw(batch);
	}
	
	public void spawnTarget() {
		// Temp difficulty
		float lifeTime = (float) (targetLifeTime
				* (1.0f 
				- (float) 
				((Math.pow(score
						, (double) (1.0 / 3.0))
						/ 20.0))));
		target.reinnitialize(lifeTime
					, targetRadius
					, randomSpawnLocation()
					, Color.BLUE);

	}
	
	public void spawnNewTarget() {
		// Temp difficulty
		float lifeTime = (float) (targetLifeTime
				* (1 - (Math.pow(score, 1 / 3) / 10)));
		target = new Target(lifeTime
					, targetRadius
					, randomSpawnLocation()
					, this, 0.15f);

	}
	
	private void shootBullet() {
		bulletManager.shoot();
	}
	
	public int hit(Vector2 position) {
		Vector2 targetPosition = target.getCenterPosition();
		if (targetPosition.cpy().sub(position).len()
				< target.getRadius()
				+ userPointer.getRadius()
			&& !target.dying
			&& !loseLifeEffect.isActive()) {
			int extraScore = getHitScore((float)
					(target.getRadius())
					/ (float) (target.getStartRadius()));
			score += extraScore;
			currentTheme = getTheme(score);
			target.explode(position);
			return extraScore;
		} else {
			return 0;
		}
	}
	
	private int getHitScore(float ratio) {
		int hitScore = 0;
		if (ratio > 0.65f) {
			hitScore = 5;
		} else if (ratio > 0.55f) {
			hitScore = 3;
		} else if (ratio > 0.4f) {
			hitScore = 2;
		} else {
			hitScore = 1;
		}
		return hitScore;
	}
	
	public int getScore() {
		return score;
	}
	
	private Vector2 randomSpawnLocation() {
		int x = randomGenerator
				.nextInt(higherX - lowerX)
				+ lowerX;
		int y = randomGenerator
				.nextInt(higherY - lowerY)
				+ lowerY;
		return new Vector2(x, y);
	}
	
	public GameTheme getTheme(int score) {
		if (score < 50) {
			return blueTheme;
		} else if (score < 100){
			return darkBlueTheme;
		} else if (score < 150){
			return brownTheme;
		} else {
			return greenTheme;
		}
	}
	
	public void screenTouchDown() {
		this.shoot = true;
	}
}
