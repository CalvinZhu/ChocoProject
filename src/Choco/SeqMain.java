package Choco;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

/*
 * 
 * @类描述：将大集群分割，然后顺序运行各个集群的choco约束编程。THRESHOLD决定分割物理机的阈值。物理机、虚拟机资源的生成都是随机生成。
 * 虚拟机资源是按照顺序和比例分配到物理机集群上的。
 * @项目名称：ChocoProject
 * @包名： algorithm
 * @类名称：SeqMain
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class SeqMain {

public static void main(String[] agrs){
	
	//当总物理机数达到TRESHOLD则进行集群的分割，每个集群最多TRESHOLD个数的物理机
	int TRESHOLD = 40;

	try{
		long start = System.currentTimeMillis();
		BufferedReader readPm = new BufferedReader(new FileReader(
				"new_data/pm_10"));
		BufferedReader readVm = new BufferedReader(new FileReader(
				"new_data/vm_10"));
		BufferedReader readStaticPow = new BufferedReader(new FileReader(
				"new_data/StaticPow_10"));
		BufferedReader readDy = new BufferedReader(new FileReader(
				"new_data/DY_10"));
		
		String data = readPm.readLine();
		int PMN = Integer.parseInt(data);
		int[] pmCPU = new int[PMN];
		int[] pmRAM = new int[PMN];
		String [] stemp;
		for( int i = 0; i < PMN; i++ ){
			data = readPm.readLine();
			stemp = data.split(" ");
			pmCPU[i] = Integer.parseInt(stemp[0]);
			pmRAM[i] = Integer.parseInt(stemp[1]);
		}
		
		data = readVm.readLine();
		int VMN = Integer.parseInt(data);
		int[] vmCPU = new int[VMN];
		int[] vmRAM = new int[VMN];
		for( int i = 0; i < VMN; i++ ){
			data= readVm.readLine();
			stemp = data.split(" ");
			vmCPU[i] = Integer.parseInt(stemp[0]);
			vmRAM[i] = Integer.parseInt(stemp[1]);
		} 
		
		data = readStaticPow.readLine();
		int staticPowNum = Integer.parseInt(data);
		int[] staticPow = new int[staticPowNum];
		for( int i = 0; i < staticPowNum; i++ ){
			data = readStaticPow.readLine();
			stemp = data.split(" ");
			staticPow[i] = Integer.parseInt(stemp[0]);
		}
		
		int[][] DYPower = new int[PMN][VMN];
		for (int i = 0; i < PMN; i++) {
			data = readDy.readLine();
			stemp = data.split(" ");
			for (int j = 0; j < VMN; j++) {
				DYPower[i][j] = Integer.parseInt(stemp[j]);
			}
		}		
		
		readDy.close();
		readStaticPow.close();
		readVm.close();
		readPm.close();
		
		int pmGap = TRESHOLD;         //gap表示超过多少的物理机就可以进行分治，分割集群
		

		int groupNum;
		if(PMN%pmGap == 0){
			groupNum = PMN/pmGap;
		}else{
			groupNum = PMN/pmGap+1;
		}
		
		int vmGap = VMN*pmGap/PMN;     //按比例分配虚拟机数量  

		/*
		 * 如下for将各个集群进行资源分配和choco计算
		 */
		
		for(int i = 0; i < groupNum;i++){
			
			int[] segmentstaticPow; //记录每个segment中各个物理机的静态能耗
			int[][] segmentDYP;  //记录每个segment对应虚拟机、物理机的动态能耗
			int []segvmCPU;
			int []segvmRAM;
			int []segpmCPU;
			int []segpmRAM;
			
			
			int segmentPMN = pmGap;           //segmentPMN表示每个子list中物理机个数，默认为pmGap
			int segmentVMN = vmGap ;			
			if(i == groupNum - 1){
				segmentPMN = PMN - pmGap*i;
				segmentVMN = VMN - vmGap*i;
			}
			System.out.println("PMN: " + segmentPMN + "\tVMN: " + segmentVMN );
			
			
			System.out.print("vmCPU:");
                    
			segvmCPU = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                //添加VMCPU
			
				segvmCPU[j] = vmCPU[VMN-i*vmGap-1-j];
				System.out.print(segvmCPU[j] + " ");
			}
			System.out.println();
			System.out.print("vmRAM:");
			segvmRAM = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                //添加VMRAM
				
				segvmRAM[j] = vmRAM[VMN-i*vmGap-1-j];
				System.out.print(segvmRAM[j] + " ");
			}
			System.out.println();
			System.out.print("pmCPU:");
			//添加物理机参数

			segpmCPU = new int[segmentPMN];
			for( int j = 0; j < segmentPMN; j++ ){
				segpmCPU[j] = pmCPU[i*pmGap+j];
				System.out.print(segpmCPU[j] + " ");
			}
			System.out.println();
			System.out.print("pmRAM:");
			segpmRAM = new int[segmentPMN];
			for( int j = 0; j < segmentPMN; j++ ){

				segpmRAM[j] = pmRAM[i*pmGap+j];
				System.out.print(segpmRAM[j] + " ");
			}
			System.out.println();
			
			//设置segmentstaticPow
			segmentstaticPow = new int[segmentPMN];
			for (int j = 0; j < segmentPMN; j++){
				segmentstaticPow[j] = staticPow[i*pmGap+j];
			}
			
			//设置segmentDYP
			segmentDYP = new int[segmentPMN][segmentVMN];
			for (int j = 0;j < segmentPMN; j++){
				for(int k = 0; k < segmentVMN; k++){
					segmentDYP[j][k] = DYPower[i*pmGap+j][VMN-i*vmGap-1-k];
				}
			}
			
			ChocoProcess obj = new ChocoProcess(segmentPMN,segmentVMN,segvmCPU,segvmRAM,segpmCPU,segpmRAM,segmentstaticPow,segmentDYP);	
			obj.runChoco();
		}
		

		long end = System.currentTimeMillis();
		System.out.println("总 运行时间：" + (end - start));
		
	} catch (Exception ex) {
		System.out.println("file opening failed,exit");
		ex.printStackTrace();
		System.exit(0);
	}
	

}	
}
