package pack;

import java.text.DecimalFormat;

public class Evaluation extends Polynomial {
	static String str(double d) {
		return Double.toString(d);
	}
	
	static String str(int i) {
		return Integer.toString(i);
	}
	
	static String bool(String u) {
		int Sign = -1;
		if (u.replaceAll("[^><=]", "").equals("<")) Sign = 0;
		else if (u.replaceAll("[^><=]", "").equals("=")) Sign = 1;
		else if (u.replaceAll("[^><=]", "").equals(">")) Sign = 2; 
		if (Sign == 0) {
			String LHS = semiFormat(u.substring(0, u.indexOf("<")));
			String RHS = semiFormat(u.substring(u.indexOf("<")+1, u.length()));
			if (eval(LHS) < eval(RHS))
				return "True";
			return "False";
		} else if (Sign == 1) {
			String LHS = semiFormat(u.substring(0, u.indexOf("=")));			
			String RHS = semiFormat(u.substring(u.indexOf("=")+1, u.length()));
			if (eval(LHS) == eval(RHS))
				return "True";
			return "False";
		} else {
			String LHS = semiFormat(u.substring(0, u.indexOf(">")));
			String RHS = semiFormat(u.substring(u.indexOf(">")+1, u.length()));
			if (eval(LHS) > eval(RHS))
				return "True";
			return "False";
		}
	}
	
	static String semiFormat(String u) { // u = userInput
		u = u.replaceAll(" ", "");
		if (u.charAt(u.length()-1) == '=')
			u = u.substring(0, u.length()-1);
		u = u.replace("pi", "(3.14159)");
		u = u.replace("e", "(2.71828)");
		u = u.replace(")(", ")*(");
		for (int i = 0; i < 9; i++) {
			u = u.replace(')'+str(i), ")*"+str(i));
			u = u.replace(str(i)+"(", str(i)+"*(");
		}
		return u;
	}
	
	//A semiformat method with addional steps
	static String format(String u) {
		u = u.replaceAll(" ", "");
		if (u.charAt(u.length()-1) == '=')
			u = u.substring(0, u.length()-1);
		u = u.replace("pi", "(3.14159)");
		u = u.replace("e", "(2.71828)");
		u = u.replace(")(", ")*(");
		for (int i = 0; i < 9; i++) {
			u = u.replace(')'+str(i), ")*"+str(i));
			u = u.replace(str(i)+"(", str(i)+"*(");
		}
		DecimalFormat format = new DecimalFormat("00000E0");
		if (abs(eval(u)) > 100_000)
			return format.format(eval(u));
		if (eval(u) == (int)eval(u))
			return str((int)(eval(u)));
		return str(round(eval(u), 5));
	}
	
	/* Evaluate expression using a recursive descent parser
	 * stackoverflow.com/questions/3422673 */
	static double eval(String u) {
		return new Object() {
			int ch, pos = -1;
			void nextChar() {
				ch = (++pos < u.length()) ? u.charAt(pos) : -1;
			}
			boolean eat(int charToEat) {
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}	
			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < u.length())
					throw new RuntimeException("Unexpected: " + (char)ch);
				return x;
			}			
			double parseExpression() {
				double x = parseTerm();
				while (true) {
					if 		(eat('+')) x += parseTerm();
					else if (eat('-')) x -= parseTerm();
					else return x;
				}
			}			
			double parseTerm() {
				double x = parseFactor();
				while (true) {
					if 		(eat('*')) x *= parseTerm();
					else if (eat('/')) x /= parseTerm();
					else if (eat('%')) x %= parseTerm();
					else return x;
				}
			}			
			double parseFactor() {
				if (eat('+')) return parseFactor();
				if (eat('-')) return -parseFactor();
				double x;
				int startPos = this.pos;
				if (eat('{') || eat('[') || eat('(')) {
					x = parseExpression();
					eat('}');
					eat(']');
					eat(')');
				} else if (('0' <= ch && ch <= '9') || ch == '.') {
					while (('0' <= ch && ch <= '9') || ch == '.')
						nextChar();
					x = Double.parseDouble(u.substring(startPos, this.pos));
				} else if ('a' <= ch && ch <= 'z') {
					while ('a' <= ch && ch <= 'z') nextChar();
					String func = u.substring(startPos, this.pos);
					x = parseFactor();
					if (func.equals("sqrt")) x = Math.sqrt(x);
					else if (func.equals("ln")) x = Math.log(x);
					else if (func.equals("abs")) x = Math.abs(x);
					else if (func.equals("log")) x = Math.log10(x);
					else if (func.equals("sin"))
						x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos"))
						x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan"))
						x = Math.tan(Math.toRadians(x));
					else throw new RuntimeException("Unknown function: " 
						+ func);
				} else {
					throw new RuntimeException("Unexpected: " + (char)ch);
				}
				if (eat('^')) x = Math.pow(x, parseFactor());
				return x;
			}
		}.parse();
	}
}
