package net.ddns.falcoboss.integration.test.factorization;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

public class FactorBigIntegerTest {

	public static LinkedList<BigInteger> tdFactors(BigInteger n) {
		BigInteger tmp = n;
		BigInteger two = BigInteger.valueOf(2);
		LinkedList<BigInteger> factors = new LinkedList<BigInteger>();
		if (tmp.compareTo(two) < 0) {
			throw new IllegalArgumentException("Liczba musi by wieksza niz 1!");
		}
		while (tmp.mod(two).equals(BigInteger.ZERO)) {
			factors.add(two);
			tmp = tmp.divide(two);
		}
		if (tmp.compareTo(BigInteger.ONE) > 0) {
			BigInteger f = BigInteger.valueOf(3);
			while (f.multiply(f).compareTo(tmp) <= 0) {
				if (tmp.mod(f).equals(BigInteger.ZERO)) {
					factors.add(f);
					tmp = tmp.divide(f);
				} else {
					f = f.add(two);
				}
			}
			factors.add(tmp);
		}
		return factors;
	}

	@Test
	public void testFactorization() {
		BigInteger n = BigInteger.valueOf(10);
		long startTime;
		long endTime;
		System.out.println("start...");
		for (BigInteger i = BigInteger.valueOf(2); n.compareTo(i) >
			0; i = i.add(BigInteger.valueOf(1))) {
			long tt = 0;
			for(int j=0; j<6; j++)
			{
				BigInteger p = BigInteger.probablePrime(i.intValue(), new Random());
				BigInteger q = BigInteger.probablePrime(i.intValue(), new Random());
				n = p.multiply(q);
				startTime = System.currentTimeMillis();
				LinkedList<BigInteger> lst = tdFactors(n);
				endTime = System.currentTimeMillis();
				tt += endTime - startTime;
			}
			System.out.println("(" + n.bitLength() + "," + tt/6 + ")");
		}
	}
}