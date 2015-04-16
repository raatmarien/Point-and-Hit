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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;

public class OneShotGame extends Game {
	MainMenuScreen mainMenu;
	GameScreen gameScreen;
	Texture buttons;
	Preferences preferences;
	float scale;
	
	@Override
	public void create() {
		// Preferences
		preferences = Gdx.app.getPreferences("prefs");
		
		scale = findScale();
		
		// Loading global textures
		buttons = new Texture(Gdx.files
				.internal("buttons/buttons.png"));
		if (!Gdx.input.isPeripheralAvailable(Peripheral.Compass)) {
			this.setScreen(new NoCompassScreen(scale));
		} else {
			mainMenu = new MainMenuScreen(this);
			this.setScreen(mainMenu);
		}
	}
	
	public void render() {
		super.render();
	}
	
	public int getHighScore() {
		return preferences.getInteger("highscore"
				, 0);
	}
	
	public void setHighScore(int score) {
		preferences.putInteger("highscore"
				, score);
		preferences.flush();
	}

	public float getScale() {
		return scale;
	}
	
	private float findScale() {
		int screenX = Gdx.graphics.getWidth();
		if (screenX < 470) {
			return (float) (1.0f / 3.0f);
		} else if (screenX < 700) {
			return (float) (1.0f / 2.0f);
		} else if (screenX < 900) {
			return (float) (3.0f / 4.0f);
		} else {
			return 1;
		}
	}
	
	public GameScreen getGameScreen() {
		if (gameScreen == null) {
			gameScreen = new GameScreen(this);
		}
		return gameScreen;
	}
}
