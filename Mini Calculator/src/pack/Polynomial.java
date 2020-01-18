package pack;

public class Polynomial extends Main {
	
	static String setChar(String s, int i, String New) {
		return s.substring(0, i) + New + s.substring(i+1);
	}
	
	// Generate any non-zero integer from min to max inclusive
	static int rng(int min, int max) {
		int result = (int)(Math.random()*(max - min + 1)) + min;
		return (result == 0) ? rng(min, max) : result;
	}
	// Simplify Math.pow syntax
	static double pow(double a, double b) {
		return Math.pow(a, b);
	}
	// Simplify Math.abs syntax
	static double abs(double a) {
		return Math.abs(a);
	}
	// Round double up to n decimal degits
	// round(3.1415, 3) = 3.142, work best when n >= 1
	static double round(double a, int n) {
		return Math.round(a*pow(10, n)) / pow(10, n);
	}
	// Remove the 'bugged' element in 2d array polys  
	static String[] remove(String[] poly, int index) {
		String[] result = new String[poly.length - 1];
		for (int i = 0, j = 0; i < poly.length; i++) {
			if (i == index) continue;
			result[j++] = poly[i];
		}
		return result;
	}
	
	static double[][] remove(double[][] poly, int index) {
		double[][] result = new double[poly.length - 1][2];
		for (int i = 0, j = 0; i < poly.length; i++) {
			if (i == index) continue;
			result[j][0] = poly[i][0];
			result[j++][1] = poly[i][1];
		}
		return result;
	}
	
	static double[][] derivative(double[][] poly) {
		double[][] result = new double[poly.length][2];
		for (int i = 0; i < result.length; i++) {
			result[i][0] = poly[i][0];
			result[i][1] = poly[i][1];
		}
		for (int i = 0; i < result.length; i++) {
			result[i][0] *= result[i][1];
			result[i][1] -= 1;
		}
		return result;
	}
	
	static double[][] format(double[][] poly) { // Remove 0*x^i in poly
		for (int i = 0; i < poly.length - 1; i++)
			for (int j = i+1; j < poly.length; j++)
				if (poly[i][1] == poly[j][1]) {
					poly[i][0] += poly[j][0];
					poly = remove(poly, j);
				}
		for (int i = 0; i < poly.length; i++)
			if (poly[i][0] == 0)
				poly = remove(poly, i);
		return poly;
	}
	
	static String format(String u) { // u = userInput
		u = u.replace(" ", "");
		if (u.substring(u.length()-2).equals("=0"))
			u = u.substring(0, u.length()-2);
		else {
			int equalIndex = u.indexOf("=");
			u = u.replace("=-", "+");
			u = u.replace("=", "-");
			for (int i = equalIndex+1 ; i < u.length(); i++) {
				if (u.charAt(i) == '-')
					u = setChar(u, i, "+");
				else if (u.charAt(i) == '+')
					u = setChar(u, i, "-");
			}
		}
		u = u.replace("+x", "+1*x");
		u = u.replace("-x", "-1*x");
		u = u.replace("x+", "x^+1+");
		u = u.replace("x-", "x^+1-");
		// "2x" => "2*x"
		for (int i = '0'; i < '9'; i++)
			u = u.replace((char)i + "x", (char)i + "*x");
		// changing to StringBuilder for easier manipulation
		StringBuilder sb = new StringBuilder(u);
		if (sb.charAt(0) == 'x')
			sb.insert(0, "+1*");
		if (Character.isDigit(sb.charAt(0)))
			sb.insert(0, "+");
		if (sb.charAt(sb.length()-1) == 'x')
			sb.insert(sb.length(), "^+1");
		// "...8" => "...8*x^+0"
		for (int i = sb.length()-1, step = 0; i >= 0; i--)
			if (step==0 && !Character.isDigit(sb.charAt(i))) {
				break;
			} else if (step<=1 && Character.isDigit(sb.charAt(i))) {
				step = 1;
			} else if (step==1 && (sb.charAt(i)=='+' ||
								   sb.charAt(i)=='-')) {
				step = 2;
			} else if (step==2 && Character.isDigit(sb.charAt(i))) {
				step = 3;
			} else if (step==3 && (sb.charAt(i)=='+' || 
								   sb.charAt(i)=='-' ||
								   sb.charAt(i)=='^')) {
				sb.insert(sb.length(), "*x^+0");
				break;
			} else step = 0;
		// "8..." => "8*x^+0..."
		for (int i = 0, step = 0; i < sb.length(); i++)
			if (step==0 && (sb.charAt(i)=='+' || sb.charAt(i)=='-')) {
				//step checking, do nothing here
			} else if (step==0 && !Character.isDigit(sb.charAt(i))) {
				break;
			} else if (step==0 && Character.isDigit(sb.charAt(i))) {
				step = 1;
			} else if (step==1 && (sb.charAt(i)=='*')) {
				break;
			} else if (step==1 && (sb.charAt(i)=='+' || sb.charAt(i)=='-')) {
				step = 2;
			} else if (step==2 && Character.isDigit(sb.charAt(i))) {
				sb.insert(i-1, "*x^+0");
				break;
			}
		// "..8.." => "..8*x^+0.."
		for (int i = 0, step = 0, g = 0; i < sb.length()-1; i++)
			if (step == 0 && Character.isDigit(sb.charAt(i))) {
				step = 1;
			} else if (step==1 && (sb.charAt(i)=='+' || sb.charAt(i)=='-')) {
				step = 2;
			} else if (step==2 && Character.isDigit(sb.charAt(i))) {
				step = 3; 
				g = i;
			} else if (step==3 && (sb.charAt(i)=='+' || sb.charAt(i)=='-')) {
				step = 4;
			} else if (step==4 && Character.isDigit(sb.charAt(i))) {
				sb.insert(g+1, "*x^+0");
			} else step = 0;
		// "x^3" => "x^+3"
		for (int i = 0; i < sb.length()-1; i++)
			if (sb.charAt(i) == '^')
				if (Character.isDigit(sb.charAt(i+1)))
					sb.insert(i+1, '+');
		u = sb.toString();
		/* Split userInput into smaller parts to store
		 * polynomial's coherent and exponent in 2D array elements */
		String co[] = u.split("\\*x\\^[+-]\\d+");
		String ex[] = u.split("[+-]\\d+\\*x\\^");
		ex = remove(ex, 0);
		double[][] poly = new double[ex.length][2];
		for (int i = 0; i < ex.length; i++) {
			poly[i][0] = Double.valueOf(co[i]);
			poly[i][1] = Double.valueOf(ex[i]);
		}
		poly = format(poly);
		return find1Root(poly);
	}
	
	static String find1Root(double [][]poly) {
		double[][] dPoly = derivative(poly);
		dPoly = format(dPoly);
		// Find a solution with Newton-Raphson method
		// Formula: Ans = Ans - f(Ans) / f'(Ans)
		double f;
		double df;
		double Ans = rng(-10, 10);
		for (int i = 0; i < 100; i++) {
			f = 0;
			for (int j = 0; j < poly.length; j++) // Calculate f(Ans)
				f += poly[j][0]*pow(Ans, poly[j][1]);
			df = 0;
			for (int j = 0; j < dPoly.length; j++) // Calculate f'(Ans)
				df += dPoly[j][0]*pow(Ans, dPoly[j][1]);
			Ans -= f/df;
		}
		// Check again if it's really a solution
		double value = 0;
		for (int i = 0; i < poly.length; i++)
			value += poly[i][0]*pow(Ans, poly[i][1]);
		if (abs(value) > 1E-3)
			return NOUT;
		return "x = " + round(Ans, 5);
	}
}