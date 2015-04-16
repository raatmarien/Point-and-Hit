package com.jmstudios.pointandhit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MainMenuScreen implements Screen {
	OneShotGame game;
	Stage stage;
	Table buttonsTable, table;
	ImageButton tutorialButton, playButton
	, optionsButton, exitButton;
	Label titleText;
	float scale;

	public MainMenuScreen(final OneShotGame game) {
		this.game = game;
		this.stage = new Stage();
		this.scale = game.scale;
		
		this.table = new Table();
		this.buttonsTable = new Table();
		table.setFillParent(true);
		//buttonsTable.setDebug(true);
		stage.addActor(table);
		
		float padding = 40 * scale;

		Vector2 screenSize = new Vector2(Gdx.graphics.getWidth()
				, Gdx.graphics.getHeight());
		
		BitmapFont titleFont = new BitmapFont(Gdx.files.internal("fonts/deja_vu_sans_large.fnt"));
		titleFont.setScale(scale);
		Label.LabelStyle titleLabelStyle = new Label.LabelStyle(titleFont, Color.WHITE);
		titleText = new Label("Point & Hit", titleLabelStyle);
		titleText.setWrap(true);
		titleText.setWidth(screenSize.x - padding * 2);
		titleText.setAlignment(Align.center);
		
		// Set up buttons
		Texture buttonsTex = game.buttons;
		Texture bigPlayButton = new Texture(
				Gdx.files.internal("buttons/play_button.png"));
		TextureRegion playTex = new TextureRegion(bigPlayButton)
			, optionsTex = new TextureRegion(buttonsTex
				, 0, 256, 256, 256)
			, tutorialTex = new TextureRegion(buttonsTex
				, 768, 0, 256, 256)
			, exitTex = new TextureRegion(buttonsTex
				, 256, 256, 256, 256);
		TextureRegionDrawable playDrawable = new 
				TextureRegionDrawable(playTex)
			, optionsDrawable = new TextureRegionDrawable
				(optionsTex)
			, tutorialDrawable = new TextureRegionDrawable
				(tutorialTex)
			, exitDrawable = new TextureRegionDrawable
				(exitTex);
		
		
		playButton = new ImageButton(playDrawable);
		optionsButton = new ImageButton(optionsDrawable);
		tutorialButton = new ImageButton(tutorialDrawable);
		exitButton = new ImageButton(exitDrawable);
		
		// Set up buttonsTable
		buttonsTable.defaults().pad(padding);
		
		buttonsTable.add(playButton).colspan(3).center()
			.padBottom(2.5f * padding)
			.width(scale * playButton.getWidth())
			.height(scale * playButton.getHeight());
		buttonsTable.row();
		buttonsTable.add(tutorialButton).width(scale * tutorialButton.getWidth())
		.height(scale * tutorialButton.getHeight());
		buttonsTable.add(optionsButton).width(scale * optionsButton.getWidth())
		.height(scale * optionsButton.getHeight());
		
		// Title
		table.defaults().pad(padding);
		table.add(titleText).width(screenSize.x - padding * 2)
			.center();
		table.row();
		table.add(buttonsTable).center();
		
		// Listeners
		tutorialButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new TutorialScreen(game, false));
			}
		});
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (game.preferences.getBoolean("tutorial", false)) {
					game.setScreen(game.getGameScreen());
				} else {
					game.setScreen(new TutorialScreen(game, false));
				}
			}
		});
		optionsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new OptionsScreen(game));
			}
		});
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new CreditsScreen(game));
			}
		});
	}	

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.4f, 0.5f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
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

	}

	@Override
	public void dispose() {

	}
}
