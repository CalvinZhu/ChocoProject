package ThreadChocoProcess;
/*
 * 
 * @��������main����
 * @��Ŀ���ƣ�ChocoProject
 * @������ main
 * @�����ƣ�Main
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class Main {

		public static void main(String agrs[]){
			int threshold = 100;             //threshold��ʾ�ָ���С��Ⱥ�����ֻ���е����������
			
			LoadData ld = new LoadData();
			DivideAndRun dr = new DivideAndRun(threshold,ld.getPMN(), ld.getVMN(),ld.getvmCPU(),
					ld.getvmRAM(),ld.getpmCPU(),ld.getpmRAM(),ld.getStaticPow() ,ld.getDYPower());
			dr.threadRun();
			
		}

}
