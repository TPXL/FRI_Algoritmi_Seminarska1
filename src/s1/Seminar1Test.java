package s1;

import java.util.Random;
import java.util.Scanner;

/**
 * Created by peter on 17. 03. 2016.
 */
public class Seminar1Test {
	public static void main(String[] args) {
        // radium of sphere
    	double r = 5;

        // points on sphere
        int nPoints = 1024 * 2;
        double[] xs = new double[nPoints];
        double[] ys = new double[nPoints];
        double[] zs = new double[nPoints];
        int[] values = new int[nPoints];

        // generate points
        Random random = new Random();
        {
	        int i = 0;
	        double x, y, z;
	        while (i < nPoints) {
	            x = (random.nextDouble() - 0.5) * r * 2;
	            y = (random.nextDouble() - 0.5) * r * 2;
	            z = (random.nextDouble() - 0.5) * r * 2;
	            // if points are outside of sphere, ignore them
	            double rn = Math.sqrt(x * x + y * y + z * z);
	            if (rn <= r || rn >= r / 2) {
	                // "normalize" points - move them to sphere
	                xs[i] = x * r / rn;
	                ys[i] = y * r / rn;
	                zs[i] = z * r / rn;
	                values[i] = random.nextInt(nPoints);
	                i++;
	            }
	        }
        }

        // test seminar
        int naivno=0, kdtree=0;
        double tnaivno, tnaivnobuild, tkdtree, tkdtreebuild;
        {
	        Seminar1Naive seminar1 = new Seminar1Naive();
	        double start = System.nanoTime();
	        seminar1.addPoints(xs, ys, zs, values);
	        tnaivnobuild = System.nanoTime() - start;
	        for(int i=0; i < nPoints; i++)
	        	for(int j=0; j < 100; j++)
	        		naivno = seminar1.getSum(xs[i], ys[i], zs[i], r/6);
	        tnaivno = System.nanoTime() - start - tnaivnobuild;
        }
        // ...
        {
        	Seminar1 seminar1 = new Seminar1();
        	double start = System.nanoTime();
        	seminar1.addPoints(xs,  ys,  zs,  values);
        	tkdtreebuild = System.nanoTime() - start;
	        for(int i=0; i < nPoints; i++)
	        	for(int j=0; j < 100; j++)
	        		kdtree = seminar1.getSum(xs[i], ys[i], zs[i], r/6);
        	tkdtree = System.nanoTime() - start - tkdtreebuild;
        }
        
        boolean pass = naivno==kdtree;
        System.out.println("pass: " + pass + " (" + naivno + ", " + kdtree + ")\nNaivno: " + (tnaivno/1_000_000d) + ", " + (tnaivnobuild/1_000_000d) + "\nKdtree: " + (tkdtree/1_000_000d) + ", " + (tkdtreebuild/1_000_000d));
    }
}
