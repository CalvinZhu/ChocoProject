package Choco;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

/*
 * 
 * @����������������������������������Դ�������洢���ļ��У�������Դ��Χ��������ʾ��
 * @��Ŀ���ƣ�ChocoProject
 * @������ main
 * @�����ƣ�GenerateData
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class GenerateData {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		try{
		System.out.print("�������ļ���ţ�");
		String FileNum = sc.nextLine();	
//		int FileNum = sc.nextInt();
		System.out.print("�����������������");
		int PMN = sc.nextInt();
        System.out.print("�����������������");
        int VMN = sc.nextInt();

		/*
		 * �����������ԴCPU��RAM
		 */
		int pmcpu[] = new int[PMN];
		int pmram[] = new int[PMN];
		for(int i = 0; i<PMN;i++){
			pmcpu[i] = (int)(Math.random()*32)+4;               //PMCPU��Χ4~32GHZ
			pmram[i] = (int)(Math.random()*32)+4;				//PMRAM��Χ4~32GHZ
		}
		PrintWriter writer1 = new PrintWriter(new BufferedWriter(new FileWriter("../ChocoProject/new_data/pm_"+ FileNum)));
		writer1.write(PMN+"\n");
		for(int i = 0; i<PMN; i++){
			writer1.write(pmcpu[i]+" "+pmram[i]+"\n");
		}
		writer1.close();
		
		/*
		 * �����������ԴCPU��RAM��������PM������
		 */
		int vmcpu[] = new int[VMN];
		int vmram[] = new int[VMN];
		for(int i = 0; i<VMN;i++){
			vmcpu[i] = (int)(Math.random()*8)+1;                //VMCPU��Χ1~8GB
			vmram[i] = (int)(Math.random()*8)+1;				//VMRAM��Χ1~8GB
		}
		PrintWriter writer2 = new PrintWriter(new BufferedWriter(new FileWriter("../ChocoProject/new_data/vm_"+ FileNum)));
		writer2.write(VMN+"\n");
		for(int i = 0; i<VMN; i++){
			writer2.write(vmcpu[i]+" "+vmram[i]+"\n");
		}
		writer2.close();
		/*
		 * ���ɶ�̬�ܺ�DynamicPower[PMN][VMN]�������������Դ��С������DynamicPower=cpu+ram��
		 */     
        
        int [][]a = new int[PMN][VMN];
        for(int i = 0; i < PMN; i++){
        	for(int j = 0; j < VMN; j++ ){
//        			a[i][j] = (int)(Math.random() * 50) + 10;
        		a[i][j] = vmcpu[j]+vmram[j];
        	}
        }
        PrintWriter writer3 = new PrintWriter(new BufferedWriter(new FileWriter("../ChocoProject/new_data/DY_"+ FileNum)));
        for( int i = 0; i < PMN; i++ ){
        	for(int j = 0;j < VMN; j++){
        		writer3.write(a[i][j] + " ");
        	}
        	writer3.write("\n");
        }
        writer3.close();
        
        /*
         * ���ɾ�̬�ܺģ�staticPower[PMN]�������������Դ��С��������staticPower=cpu+ram��
         */
        int staticPower[] = new int[PMN];
        PrintWriter writer4 = new PrintWriter(new BufferedWriter(new FileWriter("../ChocoProject/new_data/StaticPow_"+ FileNum)));
        writer4.write(PMN+"\n");
        for(int i = 0; i<PMN; i++){
//        	staticPower[i] = (int)(Math.random()*50)+10;
        	staticPower[i] = pmcpu[i]+pmram[i];
        	writer4.write(staticPower[i]+"\n");
        }
        writer4.close();
        System.out.println("����������ɡ���������");
        sc.close();
		}catch(Exception ex){
            System.out.println("file opening failed,exit");
            ex.printStackTrace();
            System.exit(0);
        }
	}

}
