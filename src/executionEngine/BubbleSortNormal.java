package executionEngine;

public class BubbleSortNormal {
	
/**
 * @author Administrator
 * ð�����򳣹��
 * 
 */
	public static void main (String []args){
		int [] list = {5,22,55,57,54,2};
		int tmp =0;// ����һ����ʱ�ռ䣬��Ž������м�ֵ
		// Ҫ�����Ĵ���
		for (int i = 0; i < list.length -1; i++) {
			// ���εıȽ������������Ĵ�С������һ�κ�������е�iС�������ڵ�i��λ����
			for (int j = 0; j < list.length -1-i; j++) {
				//�Ƚ����ڵ�Ԫ�أ����ǰ�����С�ں�������ͽ���λ��
				if (list[j] < list[j+1]) {
					
					tmp = list[j+1];
					list[j+1] = list[j];
					list[j] = tmp;
					
				}
				
				System.out.format("��%d ��ĵ�%d �ν�����",i+1,j+1);
				for (int count:list) {
					System.out.print(count);
					
				}
				System.out.println("");
			}
			System.out.format("��%d �����ս��", i+1);
			for (int count:list) {
				System.out.print(count);
				
			}
			System.out.println("\n##############");
			
		}
		
		
	}

}
