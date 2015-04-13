package Choco;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

/*
 * 
 * @类描述：将大集群分割，然后顺序运行各个集群的choco约束编程。THRESHOLD决定分割物理机的阈值。物理机、虚拟机资源的生成都是随机的。顺序分割物理机，
 * 计算各集群资源大小再进行排序。将虚拟机资源按比例分割好，然后将各份进行排序。最后将大份的虚拟机资源分配到资源大的物理机集群中。
 * @项目名称：ChocoProject
 * @包名： algorithm
 * @类名称：SeqMain
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class MatchPMVMMain {

public static void main(String[] agrs){
	
	//当总物理机数达到TRESHOLD则进行集群的分割，每个集群最多TRESHOLD个数的物理机
	final int TRESHOLD = 50;

	try{
		long start = System.currentTimeMillis();
		BufferedReader readPm = new BufferedReader(new FileReader(
				"new_data/pm_14"));
		BufferedReader readVm = new BufferedReader(new FileReader(
				"new_data/vm_14"));
		BufferedReader readStaticPow = new BufferedReader(new FileReader(
				"new_data/StaticPow_14"));
		BufferedReader readDy = new BufferedReader(new FileReader(
				"new_data/DY_14"));
		
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

		int pmRank[] = new int[groupNum];
		int vmRank[] = new int[groupNum];
		pmRank = rankPM(PMN,pmCPU,pmRAM,groupNum,TRESHOLD);
		vmRank = rankVM(PMN,VMN,vmCPU,vmRAM, groupNum, TRESHOLD);
		
		int matchPMVM[] = new int[groupNum];  //match[i]=k,表示第i个物理机集群适配第k份虚拟机资源。
		for(int i = 0; i<groupNum; i++){
			for(int k = 0; k<groupNum; k++){
				if(pmRank[i]==vmRank[k])
					matchPMVM[i]=k;
			}
		}
		/*
		 * 如下for将各个集群进行资源分配和choco计算
		 */
		for(int i = 0; i< groupNum; i++){
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
//				segmentVMN = VMN - vmGap*i;
			}
			if(matchPMVM[i]== groupNum-1)
				segmentVMN = VMN - vmGap*(groupNum-1);
			System.out.println("PMN: " + segmentPMN + "\tVMN: " + segmentVMN );
			
			/*
			 * 物理机CPU与RAM
			 */
			System.out.print("pmCPU:");

			segpmCPU = new int[segmentPMN];
			for( int j = 0; j < segmentPMN; j++ ){
				segpmCPU[j] = pmCPU[i*pmGap+j];
				System.out.print(segpmCPU[j] + " ");
			}
			System.out.println();
			System.out.print("pmRAM:");
			segpmRAM = new int[segmentPMN];
			for( int j = 0; j < segmentPMN; j++ ){
				//list.add(pmRAM[pmflag+j]);//添加物理资源到list中
				segpmRAM[j] = pmRAM[i*pmGap+j];
				System.out.print(segpmRAM[j] + " ");
			}
			System.out.println();
			
			/*
			 * 虚拟机CPU与RAM，动态能耗
			 */
			System.out.print("vmCPU:");    
			if(matchPMVM[i]==groupNum-1){                     //如果该份虚拟机资源是最后的一份，个数应该为VMN - vmGap*(groupNum-1)
			segmentVMN = VMN - vmGap*(groupNum-1);
			segvmCPU = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                //添加VMCPU
				segvmCPU[j] = vmCPU[vmGap*(groupNum-1)+j];
				System.out.print(segvmCPU[j] + " ");
			}
			System.out.println();
			System.out.print("vmRAM:");
			segvmRAM = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                //添加VMRAM
				
				segvmRAM[j] = vmRAM[vmGap*(groupNum-1)+j];
				System.out.print(segvmRAM[j] + " ");
			}
			System.out.println();
			/*
			 * 添加动态能耗
			 */
			segmentDYP = new int[segmentPMN][segmentVMN];
			for (int j = 0;j < segmentPMN; j++){
				for(int k = 0; k < segmentVMN; k++){
					segmentDYP[j][k] = DYPower[i*pmGap+j][vmGap*(groupNum-1)+k];
				}
			}
			}
			else{
			segvmCPU = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                
				segvmCPU[j] = vmCPU[matchPMVM[i]*vmGap+j];
				System.out.print(segvmCPU[j] + " ");
			}
			System.out.println();
			System.out.print("vmRAM:");
			segvmRAM = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                
				segvmRAM[j] = vmRAM[matchPMVM[i]*vmGap+j];
				System.out.print(segvmRAM[j] + " ");
			}
			System.out.println();
			/*
			 * 添加动态能耗
			 */
			segmentDYP = new int[segmentPMN][segmentVMN];
			for (int j = 0;j < segmentPMN; j++){
				for(int k = 0; k < segmentVMN; k++){
					segmentDYP[j][k] = DYPower[i*pmGap+j][vmGap*matchPMVM[i]+k];
				}
			}
			}
						
			/*
			 * 添加静态能耗
			 */
			segmentstaticPow = new int[segmentPMN];
			for (int j = 0; j < segmentPMN; j++){
				segmentstaticPow[j] = staticPow[i*pmGap+j];
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
	
	//将各个集群物理机资源进行排序，由大到小排序下来
	public static int [] rankPM(int PMN,int pmCPU[],int pmRAM[], int groupNum, int TRESHOLD){
		int pmGap = TRESHOLD;         //gap表示超过多少的物理机就可以进行分治，分割集群   
		
		int PMcpuAndram[] = new int[groupNum];
		int segmentPMN = pmGap;           //segmentPMN表示每个集群中物理机个数，默认为pmGap
		for(int i = 0; i<groupNum; i++){
			int pmsum=0;
			if(i == groupNum - 1){
				segmentPMN = PMN - pmGap*i;
			}
			for(int j = 0; j<segmentPMN;j++){
				pmsum += pmCPU[i*pmGap+j]+pmRAM[i*pmGap+j];
			}
			PMcpuAndram[i] = pmsum;              //各个物理机集群总资源
		}
		
		//对PMcpuAndram[i]、VMcpuAndram[k]进行排序，配对相应的i和k
		int pmRank[] = new int[groupNum];

		for(int i= 0; i<groupNum; i++){
			pmRank[i] = PMcpuAndram[i];
		}
		Arrays.sort(PMcpuAndram);
		
		for(int i = 0;i<groupNum;i++){
			for(int j = 0; j<groupNum; j++){
				if(pmRank[i]==PMcpuAndram[j])
					pmRank[i] = j;
			}
		}
		
		return pmRank;
	}
	//将各个集群物理机资源进行排序，由大到小排序下来
	public static int [] rankVM(int PMN,int VMN,int vmCPU[],int vmRAM[], int groupNum, int TRESHOLD){
		int pmGap = TRESHOLD;         //gap表示超过多少的物理机就可以进行分治，分割集群
		int vmGap = VMN*pmGap/PMN;     
		

		int VMcpuAndram[] = new int[groupNum];
		int segmentVMN = vmGap ;	
		for(int i = 0; i<groupNum; i++){

			int vmsum=0;
			if(i == groupNum - 1){
				segmentVMN = VMN - vmGap*i;
			}

			for(int k = 0; k<segmentVMN; k++){
				vmsum += vmCPU[i*vmGap+k]+vmRAM[i*vmGap+k];
			}
			VMcpuAndram[i] = vmsum;              //各份虚拟机资源总和
		}
		
		//对PMcpuAndram[i]、VMcpuAndram[k]进行排序，配对相应的i和k
		int vmRank[] = new int[groupNum];

		for(int i= 0; i<groupNum; i++){
			vmRank[i] = VMcpuAndram[i];
		}
		Arrays.sort(VMcpuAndram);
		
		for(int i = 0;i<groupNum;i++){
			for(int j = 0; j<groupNum; j++){
				if(vmRank[i]==VMcpuAndram[j])
					vmRank[i] = j;
			}
		}
		
		return vmRank;
	}
	
}
