package  com.xiaoqing.flight.util;
public class LineMeetUtils {

	static double eps = 1e-6;

	double Pi = Math.PI;

	public static class Seg {
		Point p1, p2;
	}

	public static int sgn(double x) {
		return x < -eps ? -1 : 1;
	}

	public static double Cross(Point p1, Point p2, Point p3, Point p4) {
		return (p2.x - p1.x) * (p4.y - p3.y) - (p2.y - p1.y) * (p4.x - p3.x);
	}

	public static double Area(Point p1, Point p2, Point p3) {
		return Cross(p1, p2, p1, p3);
	}

	public static double fArea(Point p1, Point p2, Point p3) {
		return Math.abs(Area(p1, p2, p3));
	}


	public static boolean Meet(Point p1, Point p2, Point p3, Point p4) {
		return Math.max(Math.min(p1.x, p2.x), Math.min(p3.x, p4.x)) <= Math
				.min(Math.max(p1.x, p2.x), Math.max(p3.x, p4.x))
				&& Math.max(Math.min(p1.y, p2.y), Math.min(p3.y, p4.y)) <= Math
						.min(Math.max(p1.y, p2.y), Math.max(p3.y, p4.y))
				&& sgn(Cross(p3, p2, p3, p4) * Cross(p3, p4, p3, p1)) >= 0
				&& sgn(Cross(p1, p4, p1, p2) * Cross(p1, p2, p1, p3)) >= 0;
	}


	public static Point Inter(Point p1, Point p2, Point p3, Point p4) {
		double k = fArea(p1, p2, p3) / fArea(p1, p2, p4);
		return new Point((p3.x + k * p4.x) / (1 + k), (p3.y + k * p4.y)
				/ (1 + k));
	}

	public static Point Inter2(Point p1, Point p2, Point p3, Point p4) {
		double s1 = fArea(p1, p2, p3), s2 = fArea(p1, p2, p4);
		return new Point((p4.x * s1 + p3.x * s2) / (s1 + s2), (p4.y * s1 + p3.y
				* s2)
				/ (s1 + s2));
	}

	public static class Point {
		public double x, y;
		public  Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

	}

	public static void main(String[] args) {
		Point point = Inter(new Point(1, 1), new Point(4, 4), new Point(0, 0),
				new Point(0, 0));
		boolean meet = Meet(new Point(1, 1), new Point(4, 4), new Point(1, 0),
				new Point(0, 1));
		System.out.println(point.x + "\t" + point.y + "\t" + meet);
	}

}
