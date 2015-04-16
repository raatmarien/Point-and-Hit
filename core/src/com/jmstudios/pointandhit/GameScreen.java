package com.jmstudios.pointandhit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jmstudios.pointandhit.UserPointer;

public class GameScreen implements Screen, InputProcessor {
	
	public enum GameState { Running, Paused, GameOver, Calibrating };
	GameState gameState;
	
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Texture img;
	UserPointer userPointer;
	TargetManager targetManager;
	BitmapFont scoreFont;
	Vector2 screenSize;
	OneShotGame game;
	
	// GameState calibrating
	float calibrationTime = 0.1f
			, calibrationTimeLeft;
	float roll, pitch, azimuth;
	int frames;
	
	// InputMultiplexer's
	InputMultiplexer gameRunningMultiplexer
		, gamePausedMultiplexer
		, gameOverMultiplexer;
	
	// Pause menu
	float scale;
	Stage pauseStage, gameOverStage, gameRunningStage;
	Table pauseTable, gameOverTable
		, gameOverButtonsTable;
	BitmapFont textFont;
	ImageButton resumeButton
		, retryButton, menuButton
		, pauseButton;
	Texture transparentShader, heartTex;
	Label gameOverText
		, scoreTextLabel, scoreLabel
		, highScoreTextLabel
		, highScoreLabel;
	ImageButton gameOverRetryButton
		, gameOverMenuButton;


	public GameScreen(final OneShotGame game) {
		this.game = game;
		this.scale = game.scale;
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		scoreFont = new BitmapFont(Gdx.files.internal("fonts/deja_vu_sans_large.fnt"));
		scoreFont.setColor(Color.WHITE);
		scoreFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		scoreFont.setScale(scale);
		heartTex = new Texture(Gdx.files.internal("heart.png"));
		screenSize = new Vector2(Gdx.graphics.getWidth()
				, Gdx.graphics.getHeight());
		
		setupMenus();
		
		// Set up multiplexers
		gameRunningMultiplexer = new InputMultiplexer();
		gameRunningMultiplexer.addProcessor(gameRunningStage);
		gameRunningMultiplexer.addProcessor(this);
		
		gamePausedMultiplexer = new InputMultiplexer();
		gamePausedMultiplexer.addProcessor(pauseStage);
		gamePausedMultiplexer.addProcessor(this);
		
		gameOverMultiplexer = new InputMultiplexer();
		gameOverMultiplexer.addProcessor(gameOverStage);
		gameOverMultiplexer.addProcessor(this);
	}
	
	public void setupMenus() {
		Texture buttonsTex = game.buttons;
		
		// Game running menu
		gameRunningStage = new Stage();
		TextureRegion pauseButtonTex = new TextureRegion(buttonsTex
				, 512, 256, 128, 128);
		pauseButton = new ImageButton(new TextureRegionDrawable
				(pauseButtonTex));
		pauseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				pauseGame();
			}
		});
		Table pauseButtonTable = new Table();
		pauseButtonTable.setPosition(screenSize.x 
				- (pauseButton.getWidth() * scale)
				, screenSize.y 
				- (pauseButton.getHeight() * scale));
		pauseButtonTable.add(pauseButton)
			.size(pauseButton.getWidth() * scale);
		gameRunningStage.addActor(pauseButtonTable);
		
		// Set up font
		textFont = new BitmapFont(Gdx.files.internal("fonts/deja_vu_sans_medium.fnt"));
		textFont.setColor(Color.WHITE);
		textFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		textFont.setScale(scale);
		
		// Set up transparent shader
		Pixmap onePixTransparent = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		onePixTransparent.setColor(new Color(0,0,0,0.6f));
		onePixTransparent.fill();
		transparentShader = new Texture(onePixTransparent);
		
		// Set up buttons
		TextureRegion resumeTex = new TextureRegion(buttonsTex
				, 0, 0, 256, 256)
			, retryTex = new TextureRegion(buttonsTex
				, 256, 0, 256, 256)
			, menuTex = new TextureRegion(buttonsTex
				, 512, 0, 256, 256);
		TextureRegionDrawable resumeDrawable = new 
				TextureRegionDrawable(resumeTex)
			, retryDrawable = new TextureRegionDrawable
				(retryTex)
			, menuDrawable = new TextureRegionDrawable
				(menuTex);
		resumeButton = new ImageButton(resumeDrawable);
		retryButton = new ImageButton(retryDrawable);
		menuButton = new ImageButton(menuDrawable);
		
		// Click listeners
		resumeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameState = GameState.Running;
				giveFocus();
			}
		});
		retryButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				retry();
			}
		});
		menuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				goToMainMenu();
			}
		});
		
		// Set up pauseTable
		float padding = 20 * scale;
		this.pauseStage = new Stage();
		Table wholePauseTable = new Table();
		wholePauseTable.setFillParent(true);
		pauseTable = new Table();
		pauseTable.defaults().pad(padding).padTop(padding * 3)
			.padBottom(padding * 3); // Different screen sizes
		pauseTable.add(resumeButton)
			.size(resumeButton.getWidth() * scale);
		pauseTable.add(retryButton)
			.size(retryButton.getWidth() * scale);
		pauseTable.add(menuButton)
			.size(menuButton.getWidth() * scale);
		wholePauseTable.add(pauseTable).fill(10, 1f);
		pauseStage.addActor(wholePauseTable);

		// Set up game over menu
		gameOverStage = new Stage();
		gameOverTable = new Table();
		gameOverStage.addActor(gameOverTable);
		
		// Labels
		Label.LabelStyle scoreLabelStyle = new Label.LabelStyle(textFont
				, Color.WHITE);
		
		Label.LabelStyle gameOverLabelStyle = new Label.LabelStyle
				(scoreFont, Color.WHITE);
		gameOverText = new Label("Game over", gameOverLabelStyle);
		scoreTextLabel = new Label("Score: ", scoreLabelStyle);
		highScoreTextLabel = new Label("Highscore: ", scoreLabelStyle);
		scoreLabel = new Label("null", gameOverLabelStyle);
		highScoreLabel = new Label("null", scoreLabelStyle);
		
		// Buttons	
		gameOverRetryButton = new ImageButton(retryDrawable);
		gameOverMenuButton = new ImageButton(menuDrawable);
		gameOverRetryButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				retry();
			}
		});
		gameOverMenuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				goToMainMenu();
			}
		});
		
		gameOverButtonsTable = new Table();
		gameOverButtonsTable.defaults().pad(padding * 2);
		gameOverButtonsTable.add(gameOverRetryButton).left()
			.size(gameOverRetryButton.getWidth() * scale);
		gameOverButtonsTable.add(gameOverMenuButton).right()
			.size(gameOverMenuButton.getHeight() * scale);
		
		gameOverTable.setFillParent(true);
		gameOverTable.setDebug(false);
		gameOverTable.defaults().pad(padding * 2);
		gameOverTable.add(gameOverText).colspan(3).center();
		gameOverTable.row();
		gameOverTable.add(scoreLabel).colspan(3).center()
			.pad(8 * padding);
		gameOverTable.row();
		gameOverTable.add(highScoreLabel).colspan(3).center()
			.padTop(0).padBottom(padding * 4);
		gameOverTable.row();
		gameOverTable.add(gameOverButtonsTable).center()
			.fill(10f, 1f).padTop(padding * 8);
	}
	
	protected void goToMainMenu() {
		game.setScreen(game.mainMenu);
	}

	protected void retry() {
		gameState = GameState.Running;
		show();
	}

	public void giveFocus() {
		Gdx.input.setInputProcessor(gameRunningMultiplexer);
	}

	@Override
	public void show() {
		giveFocus();
        Gdx.input.setCatchBackKey(true);
		userPointer = new UserPointer((int) (screenSize.x / 40)
				, screenSize, game.preferences);
		targetManager = new TargetManager(2.1f
				, (int) (screenSize.x * 0.255f)
				, screenSize, userPointer, this);
		
		// Calibration 
		roll = Gdx.input.getRoll();
		pitch = Gdx.input.getPitch();
		azimuth = Gdx.input.getAzimuth();
		frames = 1;
		calibrationTimeLeft = calibrationTime;
		gameState = GameState.Calibrating;
	}
	
	public void updateGame() {
		userPointer.update();
		targetManager.update();
	}

	@Override
	public void render(float delta) {
		if (gameState == GameState.Calibrating) {
			updateCalibrations();
			renderUIOnly();
		}
		else if (gameState == GameState.Running) {
			updateGame();
			renderGame();
		}
		else if (gameState == GameState.Paused) {
			renderGame();
			renderPauseMenu();
		}
		else if (gameState == GameState.GameOver) {
			renderGameOverMenu();
		}
	}
	
	public void renderGame() {
		Color backgroundColor = targetManager.currentTheme
				.background;
		Gdx.gl.glClearColor(backgroundColor.r
				, backgroundColor.g
				, backgroundColor.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Shape drawing
		shapeRenderer.begin(ShapeType.Filled);

		targetManager.draw(shapeRenderer);
		userPointer.draw(shapeRenderer,
				targetManager.currentTheme);

		shapeRenderer.end();
		
		// Sprite drawing
		batch.begin();
		
		targetManager.draw(batch);
		CharSequence scoreString = "" 
				+ targetManager.getScore();
		scoreFont.draw(batch, scoreString
				, screenSize.x / 20, screenSize.y 
				- screenSize.x / 20);
		drawHearts(targetManager.lifes, batch);
		
		batch.end();
		
		gameRunningStage.act();
		gameRunningStage.draw();
	}
	
	public void renderUIOnly() {
		Color backgroundColor = targetManager.currentTheme
				.background;
		Gdx.gl.glClearColor(backgroundColor.r
				, backgroundColor.g
				, backgroundColor.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		CharSequence scoreString = "" 
				+ targetManager.getScore();
		scoreFont.draw(batch, scoreString
				, screenSize.x / 20, screenSize.y 
				- screenSize.x / 20);
		drawHearts(targetManager.lifes, batch);
		
		batch.end();
		
		gameRunningStage.act();
		gameRunningStage.draw();
	}
	
	public void renderPauseMenu() {
		batch.begin();
		batch.draw(transparentShader, 0, 0
				, screenSize.x, screenSize.y);
		batch.end();
		pauseStage.act();
		pauseStage.draw();
	}
	
	public void renderGameOverMenu() {
		// Clear screen
		Color backgroundColor = targetManager.currentTheme
				.background;
		Gdx.gl.glClearColor(backgroundColor.r
				, backgroundColor.g
				, backgroundColor.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		gameOverStage.act();
		gameOverStage.draw();
	}
	
	public void gameOver() {
		gameState = GameState.GameOver;
		
		Pixmap gameOverButtonsBackground = new Pixmap
				(1, 1, Format.RGBA8888);
		Color gameOverButtonsBackgroundColor = targetManager
				.currentTheme.target.cpy();
		gameOverButtonsBackgroundColor.a = 0.8f;
		gameOverButtonsBackground.setColor
			(gameOverButtonsBackgroundColor);
		gameOverButtonsBackground.fill();
		TextureRegionDrawable gameOverButtonsBackgroundDrawable
			= new TextureRegionDrawable(new TextureRegion
					(new Texture(gameOverButtonsBackground)));
		gameOverButtonsTable.setBackground
			(gameOverButtonsBackgroundDrawable);
		
		scoreLabel.setText("" + targetManager.score);
		scoreLabel.setColor(targetManager.currentTheme
				.target.cpy().mul(1.2f));
		int highscore = game.getHighScore();
		if (targetManager.score > highscore) {
			game.setHighScore(targetManager.score);
			highscore = targetManager.score;
		}
		highScoreLabel.setText("Best: " + highscore); // Get HighScore
		highScoreLabel.setColor(targetManager.currentTheme
				.bullet);
		
		Gdx.input.setInputProcessor(gameOverMultiplexer);
	}
	
	public void pauseGame() {
		gameState = GameState.Paused;
		Gdx.input.setInputProcessor(gamePausedMultiplexer);
		Pixmap pauseMenuBackground = new Pixmap(1,1, Format.RGBA8888);
		Color pauseMenuColor = targetManager.currentTheme.target.cpy();
		pauseMenuColor.a = 0.8f;
		pauseMenuBackground.setColor(pauseMenuColor);
		pauseMenuBackground.fill();
		pauseTable.setBackground(new TextureRegionDrawable
				(new TextureRegion(new Texture(pauseMenuBackground))));
	}
	
	public void drawHearts(int hearts
			, SpriteBatch batch) {
		float padding = (screenSize.x / 20) * scale
				, heartSize = 64 * scale
				, xMid = screenSize.x / 2
				, y = screenSize.y
				- heartSize - padding;
		switch (hearts) {
		case 0:
			break;
		case 1:
			float x = screenSize.x / 2
					  - heartSize / 2;
			batch.draw(heartTex, x, y
					, heartSize, heartSize);
			break;
		case 2:
			float x2_1 = xMid - (padding / 2)
						- heartSize
				, x2_2 = xMid + (padding / 2);
			batch.draw(heartTex, x2_1, y
					, heartSize, heartSize);
			batch.draw(heartTex, x2_2, y
					, heartSize, heartSize);
			break;
		default:
			float x3_1 = xMid - (1.5f * heartSize)
						- padding
				, x3_2 = screenSize.x / 2
						  - heartSize / 2
				, x3_3 = xMid + (heartSize / 2)
							+ padding;
			batch.draw(heartTex, x3_1, y
					, heartSize, heartSize);
			batch.draw(heartTex, x3_2, y
					, heartSize, heartSize);
			batch.draw(heartTex, x3_3, y
					, heartSize, heartSize);
			break;
		}
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
			gameState = GameState.Running;
		}
	}

	@Override
	public void dispose() {
		// TODO: Make dispose function
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		if (gameState == GameState.Running) {
			pauseGame();
		}
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
        Gdx.input.setCatchBackKey(false);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK) {
			if (gameState == GameState.Running) {
				pauseGame();
			}
			else if (gameState == GameState.Paused) {
				gameState = GameState.Running;
				giveFocus();
			}
			else if (gameState == GameState.GameOver) {
				goToMainMenu();
			}
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
		if (gameState == GameState.Running) {
			targetManager.screenTouchDown();
		}
		else if (gameState == GameState.Paused) {
			if (screenY < pauseTable.getY()
					|| screenY > pauseTable.getY()
								+ pauseTable.getHeight()) {
				gameState = GameState.Running;
				giveFocus();
			}
		}
		return true;
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
