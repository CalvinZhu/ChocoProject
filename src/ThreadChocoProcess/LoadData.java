package ThreadChocoProcess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoadData {
	private int PMN;
	private int VMN;
	private int []vmCPU;
	private int []vmRAM;
	private int []pmCPU;
	private int []pmRAM;
	private int [][]DYPower;
	private int []staticp;
	
	public LoadData() {
		// TODO Auto-generated constructor stub
		//读取物理机、虚拟机资源数据，CPU、RAM、静态能耗和动态能耗

		try {
		BufferedReader readPm = new BufferedReader(new FileReader(
				"new_data/pm_10"));                                //物理机CPU、RAM
		BufferedReader readVm = new BufferedReader(new FileReader(
				"new_data/vm_10"));                                //虚拟机CPU、RAM
		BufferedReader readStaticp = new BufferedReader(new FileReader(
				"new_data/StaticPow_10"));                         //静态能耗
		BufferedReader readDy = new BufferedReader(new FileReader(
				"new_data/DY_10"));                                //动态能耗

		//将文件数据读取到变量中
		String data = readPm.readLine();
		this.PMN = Integer.parseInt(data);
		this.pmCPU = new int[PMN];
		this.pmRAM = new int[PMN];
		String [] stemp;
		for( int i = 0; i < PMN; i++ ){
			data = readPm.readLine();
			stemp = data.split(" ");
			pmCPU[i] = Integer.parseInt(stemp[0]);
			pmRAM[i] = Integer.parseInt(stemp[1]);
		}
		
		data = readVm.readLine();
		this.VMN = Integer.parseInt(data);
		this.vmCPU = new int[VMN];
		this.vmRAM = new int[VMN];
		for( int i = 0; i < VMN; i++ ){
			data= readVm.readLine();
			stemp = data.split(" ");
			vmCPU[i] = Integer.parseInt(stemp[0]);
			vmRAM[i] = Integer.parseInt(stemp[1]);
		} 
		
		data = readStaticp.readLine();
		int staticNum = Integer.parseInt(data);
		this.staticp = new int[staticNum];
		for( int i = 0; i < staticNum; i++ ){
			data = readStaticp.readLine();
			stemp = data.split(" ");
			staticp[i] = Integer.parseInt(stemp[0]);
		}
		
		this.DYPower = new int[PMN][VMN];
		for (int i = 0; i < PMN; i++) {
			data = readDy.readLine();
			stemp = data.split(" ");
			for (int j = 0; j < VMN; j++) {
				DYPower[i][j] = Integer.parseInt(stemp[j]);
			}
		}
		
		readPm.close();
		readVm.close();
		readStaticp.close();
		readDy.close();
    	} catch (Exception ex) {
    		System.out.println("file opening failed,exit");
    		ex.printStackTrace();
    		System.exit(0);
    	}
	}
	public void printData() throws IOException{
		//将pm、vm、dy和staticpow数据读出
		System.out.println("PMN: " + PMN + "\tVMN: " + VMN);	
		System.out.print("pmCPU:");
		for (int i = 0; i < this.PMN; i++) {
			System.out.print(this.pmCPU[i] + " ");
		}
		System.out.print("\npmRAM:");
		for (int i = 0; i < this.PMN; i++) {
			System.out.print(this.pmRAM[i] + " ");
		}
		
		System.out.print("\nvmCPU:");
		for (int i = 0; i < this.VMN; i++) {
			System.out.print(this.vmCPU[i] + " ");
		}
		System.out.println();
		System.out.print("vmRAM:");
		for (int i = 0; i < this.VMN; i++) {
			System.out.print(this.vmRAM[i] + " ");
		}		
		System.out.println();
	}

	//返回pm、vm资源
	public int getPMN(){
		return this.PMN;
	}
	public int getVMN(){
		return this.VMN;
	}
	public int[] getpmCPU(){
		return this.pmCPU;
	}
	public int[] getpmRAM(){
		return this.pmRAM;
	}
	public int[] getvmCPU(){
		return this.vmCPU;
	}
	public int[] getvmRAM(){
		return this.vmRAM;
	}
	public int[] getStaticPow(){
		return this.staticp;
	}
	public int[][] getDYPower(){
		return this.DYPower;
	}

}
