package s1;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class Seminar1 {
	static class Point {
		double x, y, z;
		int payload;

		public Point(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.payload = 0;
		}

		public Point(double x, double y, double z, int payload) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.payload = payload;
		}

		static double getX(Point p) {
			return p.x;
		}

		static double getY(Point p) {
			return p.y;
		}

		static double getZ(Point p) {
			return p.z;
		}

		static int getPayload(Point p) {
			return p.payload;
		}

		public String toString() {
			return ((int) x) + " " + ((int) y) + " " + ((int) z) + " " + payload;
		}
	}

	static class KDTree<T> {

		ToDoubleFunction<T> getters[];
		ToIntFunction<T> getPayload;
		private int k;
		T[] data;

		@SafeVarargs
		public KDTree(ToIntFunction<T> getPayload, ToDoubleFunction<T>... getters) throws IllegalArgumentException {
			this.getters = getters;
			this.getPayload = getPayload;
			k = getters.length;
		}

		private double greatCircleToEulerSq(double d) {
			double radius = 0.d;
			for (int i = 0; i < getters.length; i++) {
				double c = getters[i].applyAsDouble(data[0]);
				radius += c * c;
			}
			double radiussq = radius;
			radius = Math.sqrt(radius);
			return (1.d - Math.cos(d / radius)) * radiussq * 2;
		}

		public void quickselect(T[] parray, int left, int right, int n, int dim) {
			while (true) {
				if (left >= right) {
					return;
				}
				int pivotIndex = (left + right) / 2;
				{
					T pivotValue = parray[pivotIndex];
					parray[pivotIndex] = parray[right];
					parray[right] = pivotValue;
					double cval = getters[dim % k].applyAsDouble(pivotValue);
					int storeIndex = left;
					for (int i = left; i < right; i++) {
						if (getters[dim % k].applyAsDouble(parray[i]) < cval) {
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
				if (n == pivotIndex) {
					return;
				} else if (n < pivotIndex) {
					right = pivotIndex - 1;
				} else {
					left = pivotIndex + 1;
				}
			}
		}

		public void insert(T[] data) {
			this.data = data;
			quickselect(data, 0, data.length - 1, (data.length - 1) / 2, 0);
			insert(data, 0, data.length - 1, 1);
		}

		private void insert(T[] data, int left, int right, int dim) {
			if (left >= right)
				return;
			{
				int ll = left;
				int lr = (left + right) / 2 - 1;
				if (ll <= lr) {
					quickselect(data, ll, lr, (ll + lr) / 2, dim);
					insert(data, ll, lr, (dim + 1) % this.k);
				}
			}
			{
				int rl = (left + right) / 2 + 1;
				int rr = right;
				if (rl <= rr) {
					quickselect(data, rl, rr, (rl + rr) / 2, dim);
					insert(data, rl, rr, (dim + 1) % this.k);
				}
			}
		}

		private double eulerSq(T t1, T t2) {
			double ret = 0d;
			for (int i = 0; i < getters.length; i++) {
				double d1 = getters[i].applyAsDouble(t1);
				double d2 = getters[i].applyAsDouble(t2);
				double diff = d1 - d2;
				ret += diff * diff;
			}
			return ret;
		}

		
		class Job{
			public T p;
			public int left;
			public int right;
			public double dist;
			public int dim;
			public int ret;
			public Job(T p, int left, int right, double dist, int dim, int ret){
				this.p = p;
				this.left = left;
				this.right = right;
				this.dist = dist;
				this.dim = dim;
				this.ret = ret;
			}
		}
		
		BlockingQueue<Job> jobs;
		

		class Worker implements Runnable{
			Integer result = 0;
			@Override
			public void run() {
			    try {
			        while (!Thread.currentThread().isInterrupted()) {
			        	Job job = jobs.take();
			        	if (job.left > job.right){
			        		synchronized (result) {
			        			result += job.ret;
							}
							continue;
			        	}
						int mid = (job.left + job.right) / 2;
						T curr = data[mid];
						double cdimp = getters[job.dim].applyAsDouble(job.p);
						double cdimc = getters[job.dim].applyAsDouble(curr);
						double diff = Math.abs(cdimp - cdimc);
						if (eulerSq(job.p, curr) <= job.dist)
							job.ret += getPayload.applyAsInt(curr);
						if (cdimp < cdimc || diff * diff < job.dist) {
							//ret += find(job.p, job.left, mid - 1, job.dist, (job.dim + 1) % k);
							Job nj = new Job(job.p, job.left, mid-1, job.dist, (job.dim+1) % k, job.ret);
							jobs.put(nj);
						}
						if (cdimp >= cdimc || diff * diff < job.dist) {
							//ret += find(p, mid + 1, right, dist, (dim + 1) % k);
							Job nj = new Job(job.p, mid+1, job.right, job.dist, (job.dim+1) % k, job.ret);
							jobs.put(nj);
						}
			        }
			      } catch (Exception e) {
			        
			      }
			}
		}
		
		public int find(T p, double dist) {
			if(jobs == null)
				jobs = new ArrayBlockingQueue<>(1000);
			return find(p, 0, data.length - 1, greatCircleToEulerSq(dist), 0);
		}
		
		private int find(T p, int left, int right, double dist, int dim) {
			if (left > right)
				return 0;
			int mid = (left + right) / 2;
			T curr = data[mid];
			double cdimp = getters[dim].applyAsDouble(p);
			double cdimc = getters[dim].applyAsDouble(curr);
			double diff = Math.abs(cdimp - cdimc);
			int ret = 0;
			if (cdimp < cdimc || diff * diff < dist) {
				ret += find(p, left, mid - 1, dist, (dim + 1) % k);
			}
			if (cdimp >= cdimc || diff * diff < dist) {
				ret += find(p, mid + 1, right, dist, (dim + 1) % k);
			}
			if (eulerSq(p, curr) <= dist)
				ret += getPayload.applyAsInt(curr);
			return ret;
		}

		public void print() {
			System.out.println("Tree: ");
			if (data.length < 1)
				return;
			print(0, data.length - 1, "");
		}

		private void print(int left, int right, String prefix) {
			if (left > right)
				return;
			print(left, (left + right) / 2 - 1, prefix + " ");
			System.out.println(prefix + data[(left + right) / 2]);
			print((left + right) / 2 + 1, right, prefix + " ");
		}
	}

	private static KDTree<Point> kdtree;

	static {
		kdtree = new KDTree<Point>(Point::getPayload, Point::getX, Point::getY, Point::getZ);
	}

	public void addPoints(double[] x, double[] y, double[] z, int[] value) {
		Point points[] = IntStream.range(0, x.length).parallel()
				.mapToObj(ind -> new Point(x[ind], y[ind], z[ind], value[ind])).toArray(Point[]::new);
		kdtree.insert(points);
	}

	public int getSum(double x, double y, double z, double d) {
		return kdtree.find(new Point(x, y, z), d);
	}

	public void clear() {
		kdtree.data = null;
	}

	public static String studentId() {
		return "63110293";
	}
}