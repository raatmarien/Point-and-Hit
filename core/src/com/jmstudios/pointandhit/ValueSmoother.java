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

public class ValueSmoother {
	int AMMOUNT_VALUES_KEPT = 12;
	float previousValues[];
	boolean populated[];
	int currentIndex;
	
	public ValueSmoother() {
		currentIndex = 0;
		previousValues = new float[AMMOUNT_VALUES_KEPT];
		populated = new boolean[AMMOUNT_VALUES_KEPT];
		for (int i = 0; i < AMMOUNT_VALUES_KEPT; i++) {
			previousValues[i] = 0.0f;
			populated[i] = false;
		}
	}
	
	public void addValue(float value) {
		populated[currentIndex] = true;
		previousValues[currentIndex++] = value;
		if (currentIndex >= AMMOUNT_VALUES_KEPT)
			currentIndex = 0;
	}
	
	public float getValue() {
		int totalPopulated = 0;
		float value = 0.0f;
		for (int i = 0; i < AMMOUNT_VALUES_KEPT; i++) {
			if (populated[i]) {
				value += previousValues[i];
				totalPopulated++;
			}
		}
		if (totalPopulated != 0)
			return value / totalPopulated;
		return 0;
	}
}
