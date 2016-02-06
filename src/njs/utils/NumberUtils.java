package njs.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class NumberUtils {

	public static int getGCD(int num1, int num2) {
		if(num2 == 0) {
			return num1;
		} else {
			return getGCD(num2, num1%num2);
		}
	}
	
	public static int getLCM(int num1, int num2) {
		return num1*num2/getGCD(num1, num2);
	}
	
	public static Map<Integer,Integer> getPrimeFactorsWithPower(int num) {
		Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();
		int pow2 = 0;
		while(num%2 == 0) {
			pow2++;
			num = num/2;
		}
		if(pow2 != 0) {
			map.put(2, pow2);
		}
		for (int i = 3; i <= (int)Math.pow(num, 0.5); i = i+2) {
			int powI = 0;
			while(num%i == 0) {
				powI++;
				num = num/i;
			}
			if(powI != 0) {
				map.put(i, powI);
			}
		}
		if(num > 2) {
			map.put(num, 1);
		}
		return map;
	}
	
	public static void main(String[] args) {
		/*int gcd = getGCD(10, 2);
		System.out.println(gcd);
		gcd = getGCD(22, 25);
		System.out.println(gcd);
		gcd = getGCD(18, 48);
		System.out.println(gcd);
		gcd = getGCD(31, 28);
		System.out.println(gcd);*/
		Map<Integer, Integer> primeFactorsWithPower = getPrimeFactorsWithPower(1260);
		System.out.println(primeFactorsWithPower);
		primeFactorsWithPower = getPrimeFactorsWithPower(31);
		System.out.println(primeFactorsWithPower);
		primeFactorsWithPower = getPrimeFactorsWithPower(1023);
		System.out.println(primeFactorsWithPower);
		primeFactorsWithPower = getPrimeFactorsWithPower(49);
		System.out.println(primeFactorsWithPower);
		
	}
	
}
