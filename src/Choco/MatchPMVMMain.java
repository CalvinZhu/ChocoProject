package Choco;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

/*
 * 
 * @������������Ⱥ�ָȻ��˳�����и�����Ⱥ��chocoԼ����̡�THRESHOLD�����ָ����������ֵ����������������Դ�����ɶ�������ġ�˳��ָ��������
 * �������Ⱥ��Դ��С�ٽ������򡣽��������Դ�������ָ�ã�Ȼ�󽫸��ݽ���������󽫴�ݵ��������Դ���䵽��Դ����������Ⱥ�С�
 * @��Ŀ���ƣ�ChocoProject
 * @������ algorithm
 * @�����ƣ�SeqMain
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class MatchPMVMMain {

public static void main(String[] agrs){
	
	//������������ﵽTRESHOLD����м�Ⱥ�ķָÿ����Ⱥ���TRESHOLD�����������
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
		
		int pmGap = TRESHOLD;         //gap��ʾ�������ٵ�������Ϳ��Խ��з��Σ��ָȺ
		

		int groupNum;
		if(PMN%pmGap == 0){
			groupNum = PMN/pmGap;
		}else{
			groupNum = PMN/pmGap+1;
		}
		
		int vmGap = VMN*pmGap/PMN;     //�������������������  

		int pmRank[] = new int[groupNum];
		int vmRank[] = new int[groupNum];
		pmRank = rankPM(PMN,pmCPU,pmRAM,groupNum,TRESHOLD);
		vmRank = rankVM(PMN,VMN,vmCPU,vmRAM, groupNum, TRESHOLD);
		
		int matchPMVM[] = new int[groupNum];  //match[i]=k,��ʾ��i���������Ⱥ�����k���������Դ��
		for(int i = 0; i<groupNum; i++){
			for(int k = 0; k<groupNum; k++){
				if(pmRank[i]==vmRank[k])
					matchPMVM[i]=k;
			}
		}
		/*
		 * ����for��������Ⱥ������Դ�����choco����
		 */
		for(int i = 0; i< groupNum; i++){
			int[] segmentstaticPow; //��¼ÿ��segment�и���������ľ�̬�ܺ�
			int[][] segmentDYP;  //��¼ÿ��segment��Ӧ�������������Ķ�̬�ܺ�
			int []segvmCPU;
			int []segvmRAM;
			int []segpmCPU;
			int []segpmRAM;
			
			
			int segmentPMN = pmGap;           //segmentPMN��ʾÿ����list�������������Ĭ��ΪpmGap
			int segmentVMN = vmGap ;			
			if(i == groupNum - 1){
				segmentPMN = PMN - pmGap*i;
//				segmentVMN = VMN - vmGap*i;
			}
			if(matchPMVM[i]== groupNum-1)
				segmentVMN = VMN - vmGap*(groupNum-1);
			System.out.println("PMN: " + segmentPMN + "\tVMN: " + segmentVMN );
			
			/*
			 * �����CPU��RAM
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
				//list.add(pmRAM[pmflag+j]);//���������Դ��list��
				segpmRAM[j] = pmRAM[i*pmGap+j];
				System.out.print(segpmRAM[j] + " ");
			}
			System.out.println();
			
			/*
			 * �����CPU��RAM����̬�ܺ�
			 */
			System.out.print("vmCPU:");    
			if(matchPMVM[i]==groupNum-1){                     //����÷��������Դ������һ�ݣ�����Ӧ��ΪVMN - vmGap*(groupNum-1)
			segmentVMN = VMN - vmGap*(groupNum-1);
			segvmCPU = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                //���VMCPU
				segvmCPU[j] = vmCPU[vmGap*(groupNum-1)+j];
				System.out.print(segvmCPU[j] + " ");
			}
			System.out.println();
			System.out.print("vmRAM:");
			segvmRAM = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                //���VMRAM
				
				segvmRAM[j] = vmRAM[vmGap*(groupNum-1)+j];
				System.out.print(segvmRAM[j] + " ");
			}
			System.out.println();
			/*
			 * ��Ӷ�̬�ܺ�
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
			 * ��Ӷ�̬�ܺ�
			 */
			segmentDYP = new int[segmentPMN][segmentVMN];
			for (int j = 0;j < segmentPMN; j++){
				for(int k = 0; k < segmentVMN; k++){
					segmentDYP[j][k] = DYPower[i*pmGap+j][vmGap*matchPMVM[i]+k];
				}
			}
			}
						
			/*
			 * ��Ӿ�̬�ܺ�
			 */
			segmentstaticPow = new int[segmentPMN];
			for (int j = 0; j < segmentPMN; j++){
				segmentstaticPow[j] = staticPow[i*pmGap+j];
			}
			ChocoProcess obj = new ChocoProcess(segmentPMN,segmentVMN,segvmCPU,segvmRAM,segpmCPU,segpmRAM,segmentstaticPow,segmentDYP);	
			obj.runChoco();
			

		}
		
		long end = System.currentTimeMillis();
		System.out.println("�� ����ʱ�䣺" + (end - start));
		
	} catch (Exception ex) {
		System.out.println("file opening failed,exit");
		ex.printStackTrace();
		System.exit(0);
	}
	

}
	
	//��������Ⱥ�������Դ���������ɴ�С��������
	public static int [] rankPM(int PMN,int pmCPU[],int pmRAM[], int groupNum, int TRESHOLD){
		int pmGap = TRESHOLD;         //gap��ʾ�������ٵ�������Ϳ��Խ��з��Σ��ָȺ   
		
		int PMcpuAndram[] = new int[groupNum];
		int segmentPMN = pmGap;           //segmentPMN��ʾÿ����Ⱥ�������������Ĭ��ΪpmGap
		for(int i = 0; i<groupNum; i++){
			int pmsum=0;
			if(i == groupNum - 1){
				segmentPMN = PMN - pmGap*i;
			}
			for(int j = 0; j<segmentPMN;j++){
				pmsum += pmCPU[i*pmGap+j]+pmRAM[i*pmGap+j];
			}
			PMcpuAndram[i] = pmsum;              //�����������Ⱥ����Դ
		}
		
		//��PMcpuAndram[i]��VMcpuAndram[k]�������������Ӧ��i��k
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
	//��������Ⱥ�������Դ���������ɴ�С��������
	public static int [] rankVM(int PMN,int VMN,int vmCPU[],int vmRAM[], int groupNum, int TRESHOLD){
		int pmGap = TRESHOLD;         //gap��ʾ�������ٵ�������Ϳ��Խ��з��Σ��ָȺ
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
			VMcpuAndram[i] = vmsum;              //�����������Դ�ܺ�
		}
		
		//��PMcpuAndram[i]��VMcpuAndram[k]�������������Ӧ��i��k
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
