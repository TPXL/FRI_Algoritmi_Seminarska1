package s1;

import java.util.Random;

//data generator for testing
class TestSeminar1{
	static class Point implements Comparable<Point>{
		public static int currDim = 0;
		
		private double x, y, z;
		private int value;
		
		public Point(double x, double y, double z, int value){
			this.x = x;
			this.y = y;
			this.z = z;
			this.value = value;
		}
		
		public double dist(Point a){
			double dx = x - a.x;
			double dy = y - a.y;
			double dz = z - a.z;
			
			return norm(dx, dy, dz);
		}
		
		@Override
		public int compareTo(Point a){
			switch(currDim){
				case 0: return (int)Math.signum(x - a.x);
				case 1: return (int)Math.signum(y - a.y);
				case 2: return (int)Math.signum(z - a.z);
			}
			return 0;
		}
		
		@Override
		public String toString(){
			return x + " " + y + " " + z;
		}
	}
	
	private double r;
	private int n;
	private double[] x, y, z;
	private int[] values, answers;
	private double[][] queries;
	
	public TestSeminar1(double r, int n){
		this.r = r;
		this.n = n;
		x = new double[n];
		y = new double[n];
		z = new double[n];
		values = new int[n];
		queries = new double[4][100 * n];
		answers = new int[100 * n];
		
		Random rand = new Random(42); //seeded random
		
		//edge cases
		int idx = 0;
		for(int i = -1; i <= 1; i++){
			for(int j = -1; j <= 1; j++){
				for(int k = -1; k <= 1; k++){
					if(i != 0 || j != 0 || k != 0){
						x[idx] = i;
						y[idx] = j;
						z[idx] = k;
						idx++;
					}
				}
			}
		}
		
		//random cases
		for(int i = idx; i < n; i++){
			x[i] = rand.nextDouble() * 2 - 1;
			y[i] = rand.nextDouble() * 2 - 1;
			z[i] = rand.nextDouble() * 2 - 1;
		}
		
		//normalization
		for(int i = 0; i < n; i++){
			double d = norm(x[i], y[i], z[i]);
			x[i] *= r / d;
			y[i] *= r / d;
			z[i] *= r / d;
			values[i] = rand.nextInt(1000);
		}
		
		for(int i = 0; i < 100 * n; i++){
			for(int j = 0; j < 4; j++){
				queries[j][i] = rand.nextDouble();
				if(j < 3) queries[j][i] = queries[j][i] * 2 - 1;
			}
			double d = norm(queries[0][i], queries[1][i], queries[2][i]);
			queries[0][i] *= this.r / d;
			queries[1][i] *= this.r / d;
			queries[2][i] *= this.r / d;
			queries[3][i] *= 0.5 * Math.PI * r;
			answers[i] = getSum(queries[0][i], queries[1][i], queries[2][i], queries[3][i]);
		}
	}
	
	public int getSum(double x, double y, double z, double d){
		int sum = 0;
		Point a = new Point(x, y, z, 0);
		d = Math.sin(d / (2 * r)) * 2 * r; //convert to euclidean dist
		
		for(int i = 0; i < this.n; i++){
			if(a.dist(new Point(this.x[i], this.y[i], this.z[i], 0)) <= d){
				sum += this.values[i];
			}
		}
		
		return sum;
	}
	
	public boolean compare(int guesses[]){
		for(int i = 0; i < 100 * n; i++){
			if(answers[i] != guesses[i]){
				System.err.println("TEST FAILED:");
				System.err.println("\t" + guesses[i] + " does not equal the right answer " + answers[i] + ".");
				System.err.println("\t" + "Query index: " + i);
				System.err.println("\t" + "Query data: " + queries[0][i] + ", " + queries[1][i] + ", " + queries[2][i] + ", " + queries[3][i]);
				return false;
			}
		}
		System.out.println("TESTS PASSED");
		return true;
	}
	
	public static double norm(double x, double y, double z){
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public double[] getx(){
		return this.x;
	}
	
	public double[] gety(){
		return this.y;
	}
	
	public double[] getz(){
		return this.z;
	}
	
	public int[] getValues(){
		return this.values;
	}
	
	public double[][] getQueries(){
		return this.queries;
	}
	
	public static void main(String[] args){
		//generate data
		int n = 2000;
		long s = System.nanoTime();
		TestSeminar1 data = new TestSeminar1(1, n);
		System.out.println((System.nanoTime() - s) / 1000000000.0);
		double[][] queries = data.getQueries();
		
		//main
		s = System.nanoTime();
		Seminar1 obj = new Seminar1();
		obj.addPoints(data.getx(), data.gety(), data.getz(), data.getValues());
		int[] ans = new int[100 * n];
		
		for(int i = 0; i < 100 * n; i++){
			ans[i] = obj.getSum(queries[0][i], queries[1][i], queries[2][i], queries[3][i]);
		}
		System.out.println((System.nanoTime() - s) / 1000000000.0);
		data.compare(ans);
	}
}