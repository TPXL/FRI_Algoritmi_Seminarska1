package s1;

import java.util.HashSet;

/**
 * Created by peter on 16. 03. 2016.
 */
public class Seminar1Naive {
	private HashSet<Point> points = new HashSet<>();

	public Seminar1Naive() {
	}

	public int getSum(double x, double y, double z, double d) {
		// return sum of all values of points, that are away for d or closer of
		// (x, y, z).
		int sum = 0;
		Point p = new Point(x, y, z, 0);
		d = greatCircleToEuclid(x, y, z, d);
		for (Point pp : points) {
			if (pp.distance(p) <= d) {
				sum += pp.value;
			}
		}
		return sum;
	}

	public void addPoints(double[] x, double[] y, double[] z, int[] value) {
		// v strukturo doda točke (x,y,z) z vrednostjo value; lahko
		// predpostavljamo, da so vsa štiri polja enake dolžine
		for (int i = 0; i < x.length; i++) {
			points.add(new Point(x[i], y[i], z[i], value[i]));
		}
	}

	public void clear() {
		// remove all points from structure
		points.clear();
	}

	public static String studentId() {
		return "63120356";
	}

	private double greatCircleToEuclid(double x, double y, double z, double d) {
		double r = Math.sqrt(x * x + y * y + z * z);
		return r * Math.sqrt(2 * (1 - Math.cos(d / r)));
	}

	private class Point {
		double x;
		double y;
		double z;
		int value;

		public Point(double x, double y, double z, int value) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.value = value;
		}

		public double distance(Point p) {
			return Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2) + Math.pow(p.z - z, 2));
		}
	}
}
