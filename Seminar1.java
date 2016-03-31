import java.util.ArrayList;
import java.util.function.ToDoubleFunction;
import java.util.Random;
import java.util.Arrays;

class Seminar1{
	static class Point{
		double x, y, z;
		int payload;
		public Point(double x, double y, double z){
			this.x = x;
			this.y = y;
			this.z = z;
			this.payload = 0;
		}

		public Point(double x, double y, double z, int payload){
			this.x = x;
			this.y = y;
			this.z = z;
			this.payload = payload;
		}

		static double getX(Point p){
			return p.x;
		}
		static double getY(Point p){
			return p.y;
		}
		static double getZ(Point p){
			return p.z;
		}
		public String toString(){
			return ((int)x) + " " + ((int)y) + " " + ((int)z) + " " + payload;
		}
	}

	static class KDTree<T>{

		ToDoubleFunction<T> getters[];
		private int k;
		T[] data;

		public KDTree(ToDoubleFunction<T>... getters) throws IllegalArgumentException{
			this.getters = getters;
			k = getters.length;
		}

		private double greatCircleToEulerSq(double d){
			if(data == null || data.length < 1)
				return Double.NaN;
			double radius = 0.d;
			for(int i=0; i < getters.length; i++){
				double c = getters[i].applyAsDouble(data[0]);
				radius += c*c;
			}
			double radiussq = radius;
			radius = Math.sqrt(radius);
			return (1.d-Math.cos(d/radius)) * radiussq * 2;
		}
		
		public int quickselect(T []parray, int left, int right, int n, int dim){
			while(true){
				if(left >= right){
					return left;
				}
				int pivotIndex = (left+right)/2;
				{
					T pivotValue = parray[pivotIndex];
					parray[pivotIndex] = parray[right];
					parray[right] = pivotValue;
					double cval = getters[dim%k].applyAsDouble(pivotValue);
					int storeIndex = left;
					for(int i=left; i < right; i++){
						if(getters[dim%k].applyAsDouble(parray[i]) < cval){
							T tmp = parray[storeIndex];
							parray[storeIndex] = parray[i];
							parray[i] = tmp;
							storeIndex++;
						}
					}
					pivotValue = parray[right];
					parray[right] = parray[storeIndex];
					parray[storeIndex] = pivotValue;
					pivotIndex = storeIndex;
				}
				if(n == pivotIndex){
					return n;
				}else if(n < pivotIndex){
					right = pivotIndex-1;
				}else{
					left = pivotIndex+1;
				}
			}
		}

		public void insert(T[] data){
			this.data = data;
			int mid = quickselect(data, 0, data.length-1, (data.length-1)/2, 0);
			insert(data, 0, data.length-1, 1);
		}

		private void insert(T[] data, int left, int right, int dim){
			if(left >= right)
				return;
			{
				int ll = left;
				int lr = (left+right)/2-1;
				if(ll <= lr){
					int mid = quickselect(data, ll, lr, (ll+lr)/2, dim);
					insert(data, ll, lr, (dim+1)%this.k);
				}
			}
			{
				int rl = (left+right)/2+1;
				int rr = right;
				if(rl <= rr){
					int mid = quickselect(data, rl, rr, (rl+rr)/2, dim);
					insert(data, rl, rr, (dim+1)%this.k);
				}
			}
		}

		public void print(){
			System.out.println("Tree: ");
			if(data.length < 1)
				return;
			print(0, data.length-1, "");
		}

		private void print(int left, int right, String prefix){
			if(left > right)
				return;
			print(left, (left+right)/2-1, prefix + " ");
			System.out.println(prefix + data[(left+right)/2]);
			print((left+right)/2+1, right, prefix + " ");
		}
	}

	public static void main(String args[]){
		KDTree kdtree = new KDTree<Point>(Point::getX, Point::getY, Point::getZ);
		System.out.println(kdtree.greatCircleToEulerSq(10));
		Random r = new Random(1234567l);
		int n = 31;
		Point pts[] = new Point[n];
		for(int i=0; i < n; i++){
			pts[i] = new Point(r.nextDouble()*100, r.nextDouble()*100, r.nextDouble()*100, i);
			System.out.println(pts[i]);
		}
		
		kdtree.insert(pts);
		kdtree.print();
		System.out.println();
		for(int i=0; i < pts.length; i++){
			System.out.println(pts[i]);
		}

	}

	public void addPoints(double[] x, double[] y, double[] z, int[] value){

	}


	public int getSum(double x, double y, double z, double d){
		return 0;
	}

	public void clear(){

	}

	public static String studentId(){
		return "63110293";
	}
}