package ThreadChocoProcess;

import java.util.ArrayList;


/*
 * 
 * @���������������������������һ����ļ�Ⱥ�ָ�ɼ���С�ļ�Ⱥ�����߳�ִ�м�����Ⱥ��chocoԼ�����
 * @��Ŀ���ƣ�ChocoProject
 * @������ chocoProcess
 * @�����ƣ�SeqSplitPm
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class DivideAndRun {
	private int groupNum;                                 //��ʾ��Ⱥ�ָ�����
	private int segmentPmNum;                             //ÿ��С��Ⱥ�������������
	
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
		
		//��ü�Ⱥ�ָ�ĸ���
		if(pPMN%thres == 0){
			this.groupNum = pPMN/thres;
		}else{
			this.groupNum = pPMN/thres+1;
		}
	}
	
	public void threadRun(){
		
		ThreadProcess tp[] = new ThreadProcess[this.groupNum];            //tp[i]Ϊһ��ThreadProcess���󣬶��̴߳�����С��Ⱥ��chocoԼ��
		
		for(int i = 0; i < this.groupNum;i++){
			
			int[] segmentStaticP;                                         //��¼ÿ��С��Ⱥ�и���������ľ�̬�ܺ�
			int[][] segmentDYP;                                          //��¼ÿ��С��Ⱥ��Ӧ�������������Ķ�̬�ܺ�
			int []segvmCPU;
			int []segvmRAM;
			int []segpmCPU;
			int []segpmRAM;			

			
			int segmentPMN = this.segmentPmNum;                         //segmentPMN��ʾÿ��С��Ⱥ�������������Ĭ��ΪsegmentPmNum			
			//int segmentVMN = ;
			
			if(i == this.groupNum - 1){
				segmentPMN = PMN - this.segmentPmNum*i;
				//segmentVMN = VMN - vmGap*i;
			}
			System.out.println("\n��Ⱥ"+i+"��PMN: " + segmentPMN +"\tVMN:"+getSegVmNum(i));
			
			//�������Ⱥ�����������Դ
			
			
			System.out.print("pmCPU:");
			//������������
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
			
			int segVMGap = getSegVmNum(0);                                 //segVMGap��ʾ
			
			int segmentVMN = getSegVmNum(i);
			segvmCPU = getSegVmCPU(i,segVMGap,segmentVMN);
			segvmRAM = getSegVmRAM(i,segVMGap,segmentVMN);
			segmentStaticP = getStaticPow(i,segmentPMN);
			segmentDYP = getDyPow(i,segmentPMN,segmentVMN,segVMGap);
			//����ÿ��С��Ⱥ��chocoԼ�����
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
	 * �������Դ˳����䣬���º�������ÿ��С��Ⱥ��Ӧ��VMN��CPU��RAM��staticPower��DYPower
	 */
	public int getSegVmNum(int i){                                            
		//��ʱѡ��˳��ָ������
		int vmGap = this.VMN*this.segmentPmNum/this.PMN;     //������������������� 
		int segmentVMN = vmGap;
		if(i == this.groupNum - 1)
			segmentVMN = this.VMN - vmGap*i;
		
		return segmentVMN;
	}
	public int[] getSegVmCPU(int i,int segVMNGap,int segVMN){
		//(��ʱ��˳����䣩��ΪVM��cpu��ram���ļ��еĴ�С�ǵݼ��ģ�Ϊ������������Դ�ķ��䣬���Է���vm��Դʱ��������ֵ
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
	 * ���º���������������
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
