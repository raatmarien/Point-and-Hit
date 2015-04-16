package com.jmstudios.pointandhit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

public class TutorialScreen
	implements Screen, InputProcessor {
	public enum TutorialState { Calibrating, Step1, Step2, Done };
	OneShotGame game;
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	UserPointer userPointer;
	TargetManager targetManager;
	
	Vector2 screenSize;
	BitmapFont textFont;
	TutorialState state;
	
	boolean automatic;
	
	// Calibration
	float roll, pitch, azimuth;
	int frames;
	float calibrationTimeLeft
	, calibrationTime = 0.1f;
	
	Label.LabelStyle infoLabelStyle
		, tapToLabelStyle;
	
	// Step 1
	float scale;
	Stage step1Stage;
	Table step1Table;
	CharSequence tiltInfo = "Tilt your phone to move the white pointer."
			, tapInfo = "Tap the screen to shoot."
			, tapToContinue = "Tap the screen to continue...";
	Label step1TiltInfo, step1TapInfo, step1TapToContinue;
	
	// Step 2
	Stage step2Stage;
	Table step2Table;
	CharSequence objectiveInfo = "Shoot the targets before they dissapear."
			, shootToContinueInfo = "Shoot a target to continue...";
	Label step2ObjectiveInfo, step2ShootToContinue;
	
	// Done
	Stage doneStage;
	Table doneTable;
	CharSequence endMessage = "That's all. Have fun!"
			, tapToPlay = "Tap to play...";
	Label doneEndMessage, doneTapToInfo;
	
	public TutorialScreen(final OneShotGame game, boolean automatic) {
		this.game = game;
		this.automatic = automatic;
		this.scale = game.scale;
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		screenSize = new Vector2(Gdx.graphics.getWidth()
				, Gdx.graphics.getHeight());
		textFont = new BitmapFont(Gdx.files.internal("fonts/deja_vu_sans_medium.fnt"));
		textFont.setScale(scale);
		
		infoLabelStyle = new Label.LabelStyle(textFont, Color.WHITE);
		
		tapToLabelStyle = new Label.LabelStyle(textFont, Color.LIGHT_GRAY);
		
		// Step 1
		float padding = 50 * scale;
		step1Stage = new Stage();
		step1Table = new Table();
		step1Table.setFillParent(true);
		step1Table.align(Align.bottomLeft);
		
		step1TiltInfo = new Label(tiltInfo, infoLabelStyle);
		step1TiltInfo.setWrap(true);
		step1TiltInfo.setWidth(screenSize.x - padding * 2);
		step1TiltInfo.setAlignment(Align.center);
		
		step1TapInfo = new Label(tapInfo, infoLabelStyle);
		step1TapInfo.setWrap(true);
		step1TapInfo.setWidth(screenSize.x - padding * 2);
		step1TapInfo.setAlignment(Align.center);
		
		step1TapToContinue = new Label(tapToContinue, tapToLabelStyle);
		step1TapToContinue.setWrap(true);
		step1TapToContinue.setWidth(screenSize.x - padding * 2);
		step1TapToContinue.setAlignment(Align.center);
		
		step1Table.defaults().pad(padding);
		step1Table.add(step1TiltInfo)
			.width(screenSize.x - padding * 2);
		step1Table.row();
		step1Table.add(step1TapInfo)
			.width(screenSize.x - padding * 2);
		step1Table.row();
		step1Table.add(step1TapToContinue)
			.width(screenSize.x - padding * 2);
		step1Stage.addActor(step1Table);
		
		// Step 2
		step2Stage = new Stage();
		step2Table = new Table();
		step2Table.setFillParent(true);
		step2Table.align(Align.bottomLeft);
		
		step2ObjectiveInfo = new Label(objectiveInfo, infoLabelStyle);
		step2ObjectiveInfo.setWrap(true);
		step2ObjectiveInfo.setWidth(screenSize.x - padding * 2);
		step2ObjectiveInfo.setAlignment(Align.center);
		
		step2ShootToContinue = new Label(shootToContinueInfo, tapToLabelStyle);
		step2ShootToContinue.setWrap(true);
		step2ShootToContinue.setWidth(screenSize.x - padding * 2);
		step2ShootToContinue.setAlignment(Align.center);
		
		step2Table.defaults().pad(padding);
		step2Table.add(step2ObjectiveInfo)
			.width(screenSize.x - padding * 2);
		step2Table.row();
		step2Table.add(step2ShootToContinue)
			.width(screenSize.x - padding * 2);
		step2Stage.addActor(step2Table);
		
		// Done
		doneStage = new Stage();
		doneTable = new Table();
		doneTable.setFillParent(true);
		doneTable.align(Align.left);
		
		doneEndMessage = new Label(endMessage, infoLabelStyle);
		doneEndMessage.setWrap(true);
		doneEndMessage.setWidth(screenSize.x - padding * 2);
		doneEndMessage.setAlignment(Align.center);
		
		if (automatic) {
			tapToPlay = "Tap to finish...";
		}
		
		doneTapToInfo = new Label(tapToPlay, tapToLabelStyle);
		doneTapToInfo.setWrap(true);
		doneTapToInfo.setWidth(screenSize.x - padding * 2);
		doneTapToInfo.setAlignment(Align.center);
		
		doneTable.defaults().pad(padding);
		doneTable.add(doneEndMessage)
			.width(screenSize.x - padding * 2);
		doneTable.row();
		doneTable.add(doneTapToInfo)
			.width(screenSize.x - padding * 2);
		doneStage.addActor(doneTable);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		userPointer = new UserPointer((int) (screenSize.x / 40), screenSize
				, game.preferences);
		targetManager = new TargetManager(3f, (int) (screenSize.x * 0.255f)
				, screenSize, userPointer, null);
		
		// Calibration 
		roll = Gdx.input.getRoll();
		pitch = Gdx.input.getPitch();
		azimuth = Gdx.input.getAzimuth();
		frames = 1;
		calibrationTimeLeft = calibrationTime;
		state = TutorialState.Calibrating;
		
		// Tutorial has been done
		game.preferences.putBoolean("tutorial", true);
		game.preferences.flush();
	}

	@Override
	public void render(float delta) {
		if (state == TutorialState.Calibrating) {
			clearScreen();
			updateCalibrations();
		}
		else if (state == TutorialState.Step1) {
			userPointer.update(); 
			
			clearScreen();
			
			shapeRenderer.begin(ShapeType.Filled);
			userPointer.draw(shapeRenderer
					, targetManager.currentTheme);
			shapeRenderer.end();
			
			step1Stage.act();
			step1Stage.draw();
		}
		else if (state == TutorialState.Step2) {
			userPointer.update();
			targetManager.update();
			
			clearScreen();
			
			shapeRenderer.begin(ShapeType.Filled);
			targetManager.draw(shapeRenderer);
			userPointer.draw(shapeRenderer, targetManager.currentTheme);
			shapeRenderer.end();
			
			batch.begin();
			targetManager.draw(batch);
			batch.end();
			
			step2Stage.act();
			step2Stage.draw();
			
			if (targetManager.score > 0) {
				startDone();
			}
		}
		else if (state == TutorialState.Done) {
			clearScreen();
			
			doneStage.act();
			doneStage.draw();
		}
	}
	
	public void clearScreen() {
		Color backgroundColor = targetManager.currentTheme
				.background;
		Gdx.gl.glClearColor(backgroundColor.r
				, backgroundColor.g
				, backgroundColor.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	public void updateCalibrations() {
		roll += Gdx.input.getRoll();
		pitch += Gdx.input.getPitch();
		azimuth += Gdx.input.getAzimuth();
		frames++;
		calibrationTimeLeft -= Gdx.graphics.getDeltaTime();
		if (calibrationTimeLeft < 0) {
			userPointer.calibrate(roll / frames
					, pitch / frames
					, azimuth / frames);
			startStep1();
		}
	}

	public void startStep1() {
		
		state = TutorialState.Step1;
	}

	public void startStep2() {
		
		state = TutorialState.Step2;
	}
	
	public void startDone() {
		
		state = TutorialState.Done;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK) {
			if (state == TutorialState.Calibrating
				|| state == TutorialState.Step1
				|| state == TutorialState.Done) {
				game.setScreen(game.mainMenu);
			} else if (state == TutorialState.Step2) {
				startStep1();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (state == TutorialState.Step1) {
			startStep2();
			return true;
		}
		if (state == TutorialState.Step2) {
			targetManager.screenTouchDown();
		}
		if (state == TutorialState.Done) {
			if (automatic) {
				game.setScreen(game.mainMenu);
			} else {
				game.setScreen(game.getGameScreen());
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}