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

import com.badlogic.gdx.graphics.Color;

public class GameTheme {
	Color background, target, userPointer
	, bullet;
	public GameTheme() {
		background = null;
		target = null;
		userPointer = null;
		bullet = null;
	}
	
	public GameTheme(Color background
			, Color target, Color userPointer
			, Color bullet) {
		this.background = background;
		this.target = target;
		this.userPointer = userPointer;
		this.bullet = bullet;
	}
}
