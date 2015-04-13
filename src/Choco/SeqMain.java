package Choco;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

/*
 * 
 * @������������Ⱥ�ָȻ��˳�����и�����Ⱥ��chocoԼ����̡�THRESHOLD�����ָ����������ֵ����������������Դ�����ɶ���������ɡ�
 * �������Դ�ǰ���˳��ͱ������䵽�������Ⱥ�ϵġ�
 * @��Ŀ���ƣ�ChocoProject
 * @������ algorithm
 * @�����ƣ�SeqMain
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class SeqMain {

public static void main(String[] agrs){
	
	//������������ﵽTRESHOLD����м�Ⱥ�ķָÿ����Ⱥ���TRESHOLD�����������
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
		
		int pmGap = TRESHOLD;         //gap��ʾ�������ٵ�������Ϳ��Խ��з��Σ��ָȺ
		

		int groupNum;
		if(PMN%pmGap == 0){
			groupNum = PMN/pmGap;
		}else{
			groupNum = PMN/pmGap+1;
		}
		
		int vmGap = VMN*pmGap/PMN;     //�������������������  

		/*
		 * ����for��������Ⱥ������Դ�����choco����
		 */
		
		for(int i = 0; i < groupNum;i++){
			
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
				segmentVMN = VMN - vmGap*i;
			}
			System.out.println("PMN: " + segmentPMN + "\tVMN: " + segmentVMN );
			
			
			System.out.print("vmCPU:");
                    
			segvmCPU = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                //���VMCPU
			
				segvmCPU[j] = vmCPU[VMN-i*vmGap-1-j];
				System.out.print(segvmCPU[j] + " ");
			}
			System.out.println();
			System.out.print("vmRAM:");
			segvmRAM = new int[segmentVMN];
			for(int j = 0; j<segmentVMN; j++){                //���VMRAM
				
				segvmRAM[j] = vmRAM[VMN-i*vmGap-1-j];
				System.out.print(segvmRAM[j] + " ");
			}
			System.out.println();
			System.out.print("pmCPU:");
			//������������

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
			
			//����segmentstaticPow
			segmentstaticPow = new int[segmentPMN];
			for (int j = 0; j < segmentPMN; j++){
				segmentstaticPow[j] = staticPow[i*pmGap+j];
			}
			
			//����segmentDYP
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
		System.out.println("�� ����ʱ�䣺" + (end - start));
		
	} catch (Exception ex) {
		System.out.println("file opening failed,exit");
		ex.printStackTrace();
		System.exit(0);
	}
	

}	
}
