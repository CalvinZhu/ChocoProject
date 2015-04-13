package Choco;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

/*
 * 
 * @��������SingleProcess���ָȺ��ֻ����һ��choco�Ĵ���������������ܴ��ʱ�򣬴����ʱ����������ࡣ
 * @��Ŀ���ƣ�ChocoProject
 * @������ algorithm
 * @�����ƣ�SingleProcess
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class SingleProcessMain {

	public static void main(String[] args) throws IOException {
		//��ȡ��������������Դ���ݣ�CPU��RAM����̬�ܺĺͶ�̬�ܺ�
		BufferedReader readPm = new BufferedReader(new FileReader(
				"new_data/pm_10"));                                //�����CPU��RAM
		BufferedReader readVm = new BufferedReader(new FileReader(
				"new_data/vm_10"));                                //�����CPU��RAM
		BufferedReader readStaticp = new BufferedReader(new FileReader(
				"new_data/StaticPow_10"));                         //��̬�ܺ�
		BufferedReader readDy = new BufferedReader(new FileReader(
				"new_data/DY_10"));                                //��̬�ܺ�
		
		//���ļ����ݶ�ȡ��������
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
		
		data = readStaticp.readLine();
		int staticNum = Integer.parseInt(data);
		int[] staticp = new int[staticNum];
		for( int i = 0; i < staticNum; i++ ){
			data = readStaticp.readLine();
			stemp = data.split(" ");
			staticp[i] = Integer.parseInt(stemp[0]);
		}
		
		int[][] DYPower = new int[PMN][VMN];
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
		
		//��pm��vm��dy��staticpow���ݶ���
		System.out.println("PMN: " + PMN + "\tVMN: " + VMN);	
		System.out.print("pmCPU:");
		for (int i = 0; i < PMN; i++) {
			System.out.print(pmCPU[i] + " ");
		}
		System.out.print("\npmRAM:");
		for (int i = 0; i < PMN; i++) {
			System.out.print(pmRAM[i] + " ");
		}
		
		System.out.print("\nvmCPU:");
		for (int i = 0; i < VMN; i++) {
			System.out.print(vmCPU[i] + " ");
		}
		System.out.println();
		System.out.print("vmRAM:");
		for (int i = 0; i < VMN; i++) {
			System.out.print(vmRAM[i] + " ");
		}
		
		
		System.out.println();
		/*
		 * chocoԼ�����
		 */
		
		/*
		 * 1��modelģ��
		 */
		CPModel m = new CPModel();
		
		/*
		 * 2��variable����
		 */
		IntegerVariable[] count = Choco.makeIntVarArray("count", PMN, 0,          //count[i]��ʾi��ŵ�������ϵ����������
				VMN);
		IntegerVariable[] x = Choco.makeBooleanVarArray("x", PMN);                //x[i]��ʾi���������Ƿ�ʹ��

		IntegerVariable[][] pos = Choco.makeIntVarArray("pos", VMN, PMN, 0,       //pos[i][j]��ʾi��ŵ�������Ƿ���j��ŵ��������
				1);
		IntegerVariable[][] dulpos = Choco.makeIntVarArray("dulpos", PMN,         //dulpos[i][j]��pos[i][j]��ת�þ��󣬱�ʾ����
				VMN, 0, 1);
		IntegerVariable sum = Choco.makeIntVar("sum", 0, PMN);                    //sum��ʾ��Դ������ʹ���˵����������
		IntegerVariable power = Choco.makeIntVar("power", 0, 100000,              //power��ʾ��Դ����֮��̬�ܺĺ;�̬�ܺĵ����ܺ�
				Options.V_OBJECTIVE);
		IntegerVariable DYAPOWER = Choco.makeIntVar("DYAPOWER", 0, 100000);       //��̬�ܺ�
		IntegerVariable STAPOWER = Choco.makeIntVar("STAPOWER", 0, 100000);       //��̬�ܺ�
		IntegerVariable[] DYNAMICPower = Choco.makeIntVarArray(                   //DYNAMICPower[i]��ʾi��ŵ�������ϵĶ�̬�ܺ�
				"DYNAMICPower", PMN);
		
		
		/*
		 * 3��constraintsԼ��
		 */
		for (int i = 0; i < VMN; i++){                                            //dulpos[j][i]��pos[i][j]��ͬ
			for (int j = 0; j < PMN; j++) {
				m.addConstraint(Choco.eq(dulpos[j][i], pos[i][j]));
			}
		}
		for (int i = 0; i < VMN; i++) {                                           //i��ŵ���������ҽ��ܷ���һ���������
			m.addConstraint(Choco.eq(Choco.sum(pos[i]), 1));
		}

		for (int i = 0; i < PMN; i++) {
			m.addConstraint(Choco.leq(Choco.scalar(dulpos[i], vmCPU), pmCPU[i]));  //i����������CPUҪ���ڷ�������������������CPU
			m.addConstraint(Choco.leq(Choco.scalar(dulpos[i], vmRAM), pmRAM[i]));  //i����������RAMҪ���ڷ�������������������RAM
			m.addConstraint(Choco.eq(Choco.sum(dulpos[i]), count[i]));             
			m.addConstraint(Choco.ifThenElse(Choco.eq(count[i], 0),                //���count[i]=0��ʾi��������û�б�ʹ�ã����Ӧ��x[i]��ֵ0
					Choco.eq(x[i], 0), Choco.eq(x[i], 1)));
			m.addConstraint(Choco.eq(Choco.scalar(dulpos[i], DYPower[i]),          //���i���������ϵĶ�̬�ܺ��ܺ�          
					DYNAMICPower[i]));
		}

		m.addConstraint(Choco.eq(STAPOWER, Choco.scalar(staticp, x)));
		m.addConstraint(Choco.eq(DYAPOWER, Choco.sum(DYNAMICPower)));
		m.addConstraint(Choco.eq(power, Choco.plus(DYAPOWER, STAPOWER)));          //power=DYAPOWER+STAPOWER
		m.addConstraint(Choco.eq(sum, Choco.sum(x)));
		

		
		/*
		 * 4��solver�����
		 */
		Solver s = new CPSolver();
		s.read(m);
		//s.addGoal(new Assign(new MinDomain(s), new DecreasingDomain()));

		s.setTimeLimit(100000);                                                     //����ʱ�����ƣ��������ʱ����ֹͣ����
		s.setObjective(s.getVar(power));
		s.minimize(false);                                                          //true��ʾ�Ӹ��ڵ�����������false��ʾ��solution leafͨ�����ݼ���������
		
		ChocoLogging.toDefault();                                                   //��ӡĬ��������־
		
		// Solve the model
		s.solve();
		System.out.println(s.getVar(sum));
		System.out.println(s.getVar(power));		
	}
}

