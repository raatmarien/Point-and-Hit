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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class OptionsScreen implements Screen
	, InputProcessor{
	OneShotGame game;
	float scale;
	
	// Checkbox
	BitmapFont textFont;
	CheckBox.CheckBoxStyle checkBoxStyle;
	ButtonGroup<CheckBox> sensitivityGroup;
	
	// UI
	Stage mainStage;
	Table optionsTable, mainTable;
	
	// Input
	InputMultiplexer inputMultiplexer;
	
	
	public OptionsScreen(final OneShotGame game) {
		this.game = game;
		this.scale = game.scale;
		Vector2 screenSize = new Vector2(Gdx.graphics.getWidth()
				, Gdx.graphics.getHeight());
		
		// Font
		textFont = new BitmapFont(Gdx.files.internal("fonts/deja_vu_sans_medium.fnt"));
		textFont.setScale(scale);
		BitmapFont titleFont = new BitmapFont(Gdx.files.internal("fonts/deja_vu_sans_large.fnt"));
		titleFont.setScale(scale);
		
		// Checkbox style
		Texture checkBoxes = new Texture(Gdx.files.internal("buttons/radiobutton.png"));
		TextureRegionDrawable checkBoxUnchecked = new TextureRegionDrawable(
				new TextureRegion(checkBoxes, 0, 0, 64, 64));
		TextureRegionDrawable checkBoxChecked = new TextureRegionDrawable(
				new TextureRegion(checkBoxes, 64, 0, 64, 64));
		checkBoxStyle = new CheckBox.CheckBoxStyle(checkBoxUnchecked
				, checkBoxChecked, textFont, Color.WHITE);
		
		CheckBox verySensitive = newRadioButton("Very sensitive")
				, sensitive = newRadioButton("Sensitive")
				, normal = newRadioButton("Normal")
				, forgiving = newRadioButton("Forgiving")
				, veryForgiving = newRadioButton("Very forgiving")
                    , invertControls = newRadioButton("Invert the controls");
		
		sensitivityGroup = new ButtonGroup<CheckBox>(verySensitive
                                 , sensitive, normal, forgiving, veryForgiving
                                 , invertControls);
		
		int startSetting = game.preferences.getInteger("sensitivity", 2);
		sensitivityGroup.uncheckAll();
		sensitivityGroup.getButtons().get(startSetting).setChecked(true);

		float padding = 20 * scale;
		
		// Title
		Table titleTable = new Table();
		titleTable.align(Align.topLeft);
		Pixmap backgroundPixmap = new Pixmap(1, 1, Format.RGBA8888);
		backgroundPixmap.setColor(new Color(0.9f, 0.35f, 0.1f, 1));
		backgroundPixmap.fill();
		titleTable.setBackground(new TextureRegionDrawable
				(new TextureRegion(new Texture(backgroundPixmap))));
		Label.LabelStyle titleLabelStyle 
			= new Label.LabelStyle(titleFont, Color.WHITE);
		Label titleLabel = new Label("Control sensitivity", titleLabelStyle);
		titleLabel.setWrap(true);
		titleLabel.setWidth(screenSize.x - padding * 2);
		titleLabel.setAlignment(Align.center);
		titleTable.add(titleLabel).align(Align.topLeft).pad(2 * padding)
			.width(screenSize.x - padding * 2);
		
		// Checkboxes
		optionsTable = new Table();
		optionsTable.align(Align.topLeft);
		optionsTable.defaults().align(Align.topLeft)
			.pad(padding).padBottom(0).padLeft(2 * padding);
		optionsTable.row();
		optionsTable.add(verySensitive);
		optionsTable.row();
		optionsTable.add(sensitive);
		optionsTable.row();
		optionsTable.add(normal);
		optionsTable.row();
		optionsTable.add(forgiving);
		optionsTable.row();
		optionsTable.add(veryForgiving);
                optionsTable.row();
                optionsTable.add(invertControls);
		
		optionsTable.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				int newSensitivity = sensitivityGroup.getCheckedIndex();
				if (newSensitivity == -1) newSensitivity = 2;
				game.preferences.putInteger("sensitivity", newSensitivity);
				game.preferences.flush();
			}
		});

		mainTable = new Table();
		mainTable.setFillParent(true);
		mainTable.align(Align.top);
		mainTable.add(titleTable).pad(0)
			.padBottom(padding * 4).fill(10, 1)
			.align(Align.topLeft);
		mainTable.row();
		mainTable.add(optionsTable).align(Align.left);
		
		mainStage = new Stage();
		mainStage.addActor(mainTable);
		
		// Input
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(mainStage);
		inputMultiplexer.addProcessor(this);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.4f, 0.5f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mainStage.act();
		mainStage.draw();
	}
	
	private CheckBox newRadioButton(String text) {
		CheckBox radioButton = new CheckBox(text, checkBoxStyle);
		radioButton.getImageCell().pad(20 * scale);
		radioButton.getImageCell().size(
				radioButton.getImageCell().getPrefWidth()
				* scale);
		return radioButton;
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
			game.setScreen(game.mainMenu);
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
		// TODO Auto-generated method stub
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
