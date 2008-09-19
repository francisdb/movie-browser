/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.somatik.moviebrowser.service;

public class LevenshteinDistance {
	// ****************************
	// Get minimum of three values
	// ****************************

	private static int min(int a, int b, int c) {
		int minimum;

		minimum = a;
		if (b < minimum) {
			minimum = b;
		}
		if (c < minimum) {
			minimum = c;
		}
		return minimum;

	}

	public static int distance(String s, String t) {
		int d[][]; // matrix
		int lengthS; // length of s
		int lengthT; // length of t
		int i; // iterates through s
		int j; // iterates through t
		char si; // ith character of s
		char tj; // jth character of t
		int cost; // cost
		// Step 1
		lengthS = s.length();
		lengthT = t.length();
		if (lengthS == 0) {
			return lengthT;
		}
		if (lengthT == 0) {
			return lengthS;
		}
		d = new int[lengthS + 1][lengthT + 1];
		// Step 2
		for (i = 0; i <= lengthS; i++) {
			d[i][0] = i;
		}
		for (j = 0; j <= lengthT; j++) {
			d[0][j] = j;
		}

		// Step 3

		for (i = 1; i <= lengthS; i++) {
			si = s.charAt(i - 1);
			// Step 4
			for (j = 1; j <= lengthT; j++) {
				tj = t.charAt(j - 1);
				// Step 5
				if (si == tj) {
					cost = 0;
				} else {
					cost = 1;
				}
				// Step 6
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]
						+ cost);
			}
		}
		// Step 7
		return d[lengthS][lengthT];

	}

}
