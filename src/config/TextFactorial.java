package config;

public class TextFactorial {
	
	
	public static void main (String [] args){
		
		int x= TextFactorial.simpleCircle(6);
		System.out.println(x);
	
	}
	
	
	
	
	public static  int simpleCircle (int num){
		
		int sum =1;
		if (num<0) {
			throw new IllegalArgumentException("����Ϊ������");
			
		}
		for (int i = 1; i <=num; i++) {
			
			sum *=i;
			
		}

		return sum;

	
	}	

}
