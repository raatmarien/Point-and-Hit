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
