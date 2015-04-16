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
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.jmstudios.pointandhit.ValueSmoother;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class UserPointer {
	Vector2 cornerPosition, screenSize
		, startValue;
	ValueSmoother rollSmoother, pitchSmoother
		, azimuthSmoother;
	int screenSizeInDegrees = 23;
	int radius;
	
	public UserPointer(int radius
			, Vector2 screenSize
			, Preferences prefs) {
		rollSmoother = new ValueSmoother();
		pitchSmoother = new ValueSmoother();
		azimuthSmoother = new ValueSmoother();
		this.radius = radius;
		this.screenSize = screenSize;
		float roll = Gdx.input.getRoll();
		float pitch = Gdx.input.getPitch();
		float azimuth = Gdx.input.getAzimuth();
		this.startValue = mapToScreenAbsolute(roll, pitch, azimuth);
		this.screenSizeInDegrees = getScreenSizeInDegrees(
				prefs.getInteger("sensitivity", 2));
	}
	
	public void update() {
		float roll = Gdx.input.getRoll();
		float pitch = Gdx.input.getPitch();
		float azimuth = Gdx.input.getAzimuth();
		addAngles(roll, pitch, azimuth);
	}
	
	public void addAngles(float roll, float pitch
			, float azimuth) {
		rollSmoother.addValue(roll);
		pitchSmoother.addValue(pitch);
		azimuthSmoother.addValue(azimuth);
	}
	
	public Vector2 getCenterPosition() {
		Vector2 centerPosition = mapToScreen(rollSmoother.getValue()
					, pitchSmoother.getValue()
					, azimuthSmoother.getValue());
		return centerPosition;
	}
	
	private Vector2 mapToScreen(float roll, float pitch
			, float azimuth) {
		Vector2 absolute = mapToScreenAbsolute(roll
				, pitch, azimuth);
		float x = (absolute.x - startValue.x)
				+ (screenSize.x / 2);
		float y = (absolute.y - startValue.y)
				+ (screenSize.y / 2);
		x = x < 0.0f ? 0.0f :
			(x > screenSize.x ? screenSize.x : x);
		y = y < 0.0f ? 0.0f :
			(y > screenSize.y ? screenSize.y : y);
		return new Vector2(x, y);
	}
	
	private Vector2 mapToScreenAbsolute(float roll, float pitch
			, float azimuth) {
		return new Vector2((roll / screenSizeInDegrees)
				* screenSize.x
				, (pitch / screenSizeInDegrees)
				* screenSize.y);
	}
	
	public int getRadius() {
		return this.radius;
	}
	
	// Call ShapeRenderer.begin() before calling this method
	// And call ShapeRenderer.end() when you're done with it
	public void draw(ShapeRenderer shapeRenderer
			, GameTheme gameTheme) {
		ShapeType previousType = shapeRenderer.getCurrentType();
		shapeRenderer.set(ShapeType.Filled);
		shapeRenderer.setColor(gameTheme.userPointer);
		Vector2 centerPosition = getCenterPosition();
		shapeRenderer.circle(centerPosition.x
				, centerPosition.y, radius);
		shapeRenderer.set(previousType);
	}
	
	public void calibrate(float roll, float pitch
							, float azimuth) {
		this.startValue = mapToScreenAbsolute(roll, pitch, azimuth);
	}
	
	public int getScreenSizeInDegrees(int sensitivity) {
		switch (sensitivity) {
		case 0:
			return 17;
		case 1:
			return 23;
		case 2:
			return 25;
		case 3:
			return 27;
		case 4:
			return 33;
		}
		return 25;
	}
}
