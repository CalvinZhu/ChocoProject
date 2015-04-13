package ThreadChocoProcess;
/*
 * 
 * @类描述：main函数
 * @项目名称：ChocoProject
 * @包名： main
 * @类名称：Main
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class Main {

		public static void main(String agrs[]){
			int threshold = 100;             //threshold表示分割后的小集群中最多只能有的物理机数量
			
			LoadData ld = new LoadData();
			DivideAndRun dr = new DivideAndRun(threshold,ld.getPMN(), ld.getVMN(),ld.getvmCPU(),
					ld.getvmRAM(),ld.getpmCPU(),ld.getpmRAM(),ld.getStaticPow() ,ld.getDYPower());
			dr.threadRun();
			
		}

}
