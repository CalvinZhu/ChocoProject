package ThreadChocoProcess;

import java.util.ArrayList;


/*
 * 
 * @类描述：根据物理机的数量，将一个大的集群分割成几个小的集群，多线程执行几个集群的choco约束求解
 * @项目名称：ChocoProject
 * @包名： chocoProcess
 * @类名称：SeqSplitPm
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class DivideAndRun {
	private int groupNum;                                 //表示集群分割数量
	private int segmentPmNum;                             //每个小集群最多的物理机数量
	
	private int PMN;
	private int VMN;
	private int []vmCPU;
	private int []vmRAM;
	private int []pmCPU;
	private int []pmRAM;
	private int []staticp;
	private int [][]DYPower;

	
	public DivideAndRun(int thres,int pPMN, int pVMN,int []pvmCPU,int []pvmRAM,int []ppmCPU,int []ppmRAM,int []staticpower , int [][]DYP){
		
		this.segmentPmNum = thres;
		this.PMN = pPMN;
		this.VMN = pVMN;
		this.vmCPU = pvmCPU;
		this.vmRAM = pvmRAM;
		this.pmCPU = ppmCPU;
		this.pmRAM = ppmRAM;
		this.staticp = staticpower;
		this.DYPower = DYP;
		
		//求得集群分割的个数
		if(pPMN%thres == 0){
			this.groupNum = pPMN/thres;
		}else{
			this.groupNum = pPMN/thres+1;
		}
	}
	
	public void threadRun(){
		
		ThreadProcess tp[] = new ThreadProcess[this.groupNum];            //tp[i]为一个ThreadProcess对象，多线程处理多个小集群的choco约束
		
		for(int i = 0; i < this.groupNum;i++){
			
			int[] segmentStaticP;                                         //记录每个小集群中各个物理机的静态能耗
			int[][] segmentDYP;                                          //记录每个小集群对应虚拟机、物理机的动态能耗
			int []segvmCPU;
			int []segvmRAM;
			int []segpmCPU;
			int []segpmRAM;			

			
			int segmentPMN = this.segmentPmNum;                         //segmentPMN表示每个小集群中物理机个数，默认为segmentPmNum			
			//int segmentVMN = ;
			
			if(i == this.groupNum - 1){
				segmentPMN = PMN - this.segmentPmNum*i;
				//segmentVMN = VMN - vmGap*i;
			}
			System.out.println("\n集群"+i+"的PMN: " + segmentPMN +"\tVMN:"+getSegVmNum(i));
			
			//向各个集群分配虚拟机资源
			
			
			System.out.print("pmCPU:");
			//添加物理机参数
			segpmCPU = new int[segmentPMN];
			for( int j = 0; j < segmentPMN; j++ ){
				segpmCPU[j] = pmCPU[i*this.segmentPmNum+j];
				System.out.print(segpmCPU[j] + " ");
			}
			System.out.println();
			System.out.print("pmRAM:");
			segpmRAM = new int[segmentPMN];
			for( int j = 0; j < segmentPMN; j++ ){
				segpmRAM[j] = pmRAM[i*this.segmentPmNum+j];
				System.out.print(segpmRAM[j] + " ");
			}
			System.out.println();
			
			int segVMGap = getSegVmNum(0);                                 //segVMGap表示
			
			int segmentVMN = getSegVmNum(i);
			segvmCPU = getSegVmCPU(i,segVMGap,segmentVMN);
			segvmRAM = getSegVmRAM(i,segVMGap,segmentVMN);
			segmentStaticP = getStaticPow(i,segmentPMN);
			segmentDYP = getDyPow(i,segmentPMN,segmentVMN,segVMGap);
			//运行每个小集群的choco约束编程
			tp[i] = new ThreadProcess(segmentPMN,segmentVMN,segvmCPU,segvmRAM,segpmCPU,segpmRAM,segmentStaticP,segmentDYP);
		}
		
		Thread[] th = new Thread[this.groupNum];
		for(int j = 0; j < this.groupNum; j++){
			th[j] = new Thread(tp[j]);
		}
		
		for(int j = 0;j < this.groupNum; j++){
			th[j].start();
		}
	}
	
	/*
	 * 虚拟机资源顺序分配，以下函数返回每个小集群对应的VMN、CPU、RAM、staticPower和DYPower
	 */
	public int getSegVmNum(int i){                                            
		//暂时选择顺序分割虚拟机
		int vmGap = this.VMN*this.segmentPmNum/this.PMN;     //按比例分配虚拟机数量 
		int segmentVMN = vmGap;
		if(i == this.groupNum - 1)
			segmentVMN = this.VMN - vmGap*i;
		
		return segmentVMN;
	}
	public int[] getSegVmCPU(int i,int segVMNGap,int segVMN){
		//(暂时是顺序分配）因为VM的cpu和ram在文件中的大小是递减的，为了配合物理机资源的分配，所以分配vm资源时采用逆序赋值
		System.out.print("vmCPU:");
		if(i==this.groupNum-1){
			int cpuTemp[] = new int[segVMN];
			for(int j = 0; j<segVMN; j++){                
				
				cpuTemp[j] = this.vmCPU[this.VMN-i*segVMNGap-1-j];
				System.out.print(cpuTemp[j] + " ");
			}
			return cpuTemp;
		}
		else{
			int cpuTemp1[] = new int[segVMNGap];
			for(int j = 0; j<segVMNGap; j++){                
				
				cpuTemp1[j] = this.vmCPU[this.VMN-i*segVMNGap-1-j];
				System.out.print(cpuTemp1[j] + " ");
			}
			return cpuTemp1;
		}
		

	}
	public int[] getSegVmRAM(int i,int segVMNGap,int segVMN){
		System.out.print("\nvmRAM:");   
		if(i==this.groupNum-1){
			int ramTemp[] = new int[segVMN];
			for(int j = 0; j<segVMN; j++){                
				
				ramTemp[j] = this.vmRAM[this.VMN-i*segVMNGap-1-j];
				System.out.print(ramTemp[j] + " ");
			}
			System.out.println();
			return ramTemp;
		}
		else{
			int ramTemp1[] = new int[segVMNGap];
			for(int j = 0; j<segVMNGap; j++){                
				
				ramTemp1[j] = this.vmRAM[this.VMN-i*segVMNGap-1-j];
				System.out.print(ramTemp1[j] + " ");
			}
			System.out.println();
			return ramTemp1;
		}
		
	}
	public int[] getStaticPow(int i,int segPMN){
		if(i==this.groupNum-1){
			int staticPTemp[] = new int[segPMN];
			for(int j =0;j<segPMN;j++){
				staticPTemp[i] = this.staticp[i*this.segmentPmNum+j];
			}
			
			return staticPTemp;
		}
		else{
			int staticPTemp[] = new int[this.segmentPmNum];
			for(int j =0;j<this.segmentPmNum;j++){
				staticPTemp[i] = this.staticp[i*this.segmentPmNum+j];
			}
			
			return staticPTemp;
		}

	}
	public int[][] getDyPow(int i,int segPMN,int segVMN,int segVMNGap){
		int [][] segmentDYPTemp = new int[segPMN][segVMN];
		for (int j = 0;j < segPMN; j++){
			for(int k = 0; k < segVMN; k++){
				segmentDYPTemp[j][k] = this.DYPower[i*this.segmentPmNum+j][this.VMN-i*segVMNGap-1-k];
			}
		}
		
		return segmentDYPTemp;
	}
	
	
	/*
	 * 以下函数随机分配虚拟机
	 */

	public int[] getSegVmCPURandomly(int i,int segVMNGap,int segVMN){
		System.out.print("vmCPU:");
		if(i==this.groupNum-1){
			int cpuTemp[] = new int[segVMN];
			for(int j = 0; j<segVMN; j++){                
				
				cpuTemp[j] = this.vmCPU[this.VMN-i*segVMNGap-1-j];
				System.out.print(cpuTemp[j] + " ");
			}
			return cpuTemp;
		}
		else{
			int cpuTemp1[] = new int[segVMNGap];
			for(int j = 0; j<segVMNGap; j++){                
				
				cpuTemp1[j] = this.vmCPU[this.VMN-i*segVMNGap-1-j];
				System.out.print(cpuTemp1[j] + " ");
			}
			return cpuTemp1;
		}
	}
	public int[] getSegVmRAMRandomly(int i,int segVMNGap,int segVMN){
		System.out.print("\nvmRAM:");   
		if(i==this.groupNum-1){
			int ramTemp[] = new int[segVMN];
			for(int j = 0; j<segVMN; j++){                
				
				ramTemp[j] = this.vmRAM[this.VMN-i*segVMNGap-1-j];
				System.out.print(ramTemp[j] + " ");
			}
			System.out.println();
			return ramTemp;
		}
		else{
			int ramTemp1[] = new int[segVMNGap];
			for(int j = 0; j<segVMNGap; j++){                
				
				ramTemp1[j] = this.vmRAM[this.VMN-i*segVMNGap-1-j];
				System.out.print(ramTemp1[j] + " ");
			}
			System.out.println();
			return ramTemp1;
		}
	}
	public int[] getStaticPowRandomly(int i,int segPMN){
		if(i==this.groupNum-1){
			int staticPTemp[] = new int[segPMN];
			for(int j =0;j<segPMN;j++){
				staticPTemp[i] = this.staticp[i*this.segmentPmNum+j];
			}
			
			return staticPTemp;
		}
		else{
			int staticPTemp[] = new int[this.segmentPmNum];
			for(int j =0;j<this.segmentPmNum;j++){
				staticPTemp[i] = this.staticp[i*this.segmentPmNum+j];
			}
			
			return staticPTemp;
		}
	}
	public int[][] getDyPowRandomly(int i,int segPMN,int segVMN,int segVMNGap){
		int [][] segmentDYPTemp = new int[segPMN][segVMN];
		return segmentDYPTemp;
	}
}
