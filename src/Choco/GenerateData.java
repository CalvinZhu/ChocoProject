package Choco;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

/*
 * 
 * @类描述：随机生成物理机、虚拟机的相关资源参数，存储在文件中，生成资源范围如下面显示。
 * @项目名称：ChocoProject
 * @包名： main
 * @类名称：GenerateData
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
		System.out.print("请输入文件序号：");
		String FileNum = sc.nextLine();	
//		int FileNum = sc.nextInt();
		System.out.print("请输入物理机个数：");
		int PMN = sc.nextInt();
        System.out.print("请输入虚拟机个数：");
        int VMN = sc.nextInt();

		/*
		 * 生成物理机资源CPU、RAM
		 */
		int pmcpu[] = new int[PMN];
		int pmram[] = new int[PMN];
		for(int i = 0; i<PMN;i++){
			pmcpu[i] = (int)(Math.random()*32)+4;               //PMCPU范围4~32GHZ
			pmram[i] = (int)(Math.random()*32)+4;				//PMRAM范围4~32GHZ
		}
		PrintWriter writer1 = new PrintWriter(new BufferedWriter(new FileWriter("../ChocoProject/new_data/pm_"+ FileNum)));
		writer1.write(PMN+"\n");
		for(int i = 0; i<PMN; i++){
			writer1.write(pmcpu[i]+" "+pmram[i]+"\n");
		}
		writer1.close();
		
		/*
		 * 生成虚拟机资源CPU、RAM，数量是PM的两倍
		 */
		int vmcpu[] = new int[VMN];
		int vmram[] = new int[VMN];
		for(int i = 0; i<VMN;i++){
			vmcpu[i] = (int)(Math.random()*8)+1;                //VMCPU范围1~8GB
			vmram[i] = (int)(Math.random()*8)+1;				//VMRAM范围1~8GB
		}
		PrintWriter writer2 = new PrintWriter(new BufferedWriter(new FileWriter("../ChocoProject/new_data/vm_"+ FileNum)));
		writer2.write(VMN+"\n");
		for(int i = 0; i<VMN; i++){
			writer2.write(vmcpu[i]+" "+vmram[i]+"\n");
		}
		writer2.close();
		/*
		 * 生成动态能耗DynamicPower[PMN][VMN]，根据虚拟机资源大小决定，DynamicPower=cpu+ram。
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
         * 生成静态能耗，staticPower[PMN]，根据物理机资源大小而定——staticPower=cpu+ram。
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
        System.out.println("数据生成完成…………！");
        sc.close();
		}catch(Exception ex){
            System.out.println("file opening failed,exit");
            ex.printStackTrace();
            System.exit(0);
        }
	}

}
