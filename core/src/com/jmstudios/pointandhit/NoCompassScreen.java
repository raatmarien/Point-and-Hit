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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class NoCompassScreen implements Screen {
	Stage stage;
	Table table;
	Label messageLabel;
	float scale;
	String text = "Your device doesn't have a compass, which is required to play this game.";
	
	public NoCompassScreen(float scale) {
		this.scale = scale;
	}
	
	@Override
	public void show() {
		stage = new Stage();
		table = new Table();
		float padding = 50 * scale;
		BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/deja_vu_sans_medium.fnt"));
		Vector2 screenSize = new Vector2(Gdx.graphics.getWidth()
					, Gdx.graphics.getHeight());
		font.setScale(scale);
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
		messageLabel = new Label(text, labelStyle);
		messageLabel.setWrap(true);
		messageLabel.setWidth(screenSize.x - padding * 2);
		messageLabel.setAlignment(Align.center);
		
		table.setFillParent(true);
		table.defaults().pad(padding);
		table.add(messageLabel)
			.width(screenSize.x - padding * 2);
		stage.addActor(table);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
