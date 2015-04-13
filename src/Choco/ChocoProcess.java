package Choco;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
/*
 * 
 * @��������ChocoProcess�����ڴ���ÿ��С��Ⱥ��chocoԼ�����
 * @��Ŀ���ƣ�ChocoProject
 * @������ Choco
 * @�����ƣ�ChocoProcess
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @user Calvin
 *
 */
public class ChocoProcess {
	private int PMN;
	private int VMN;
	private int []vmCPU;
	private int []vmRAM;
	private int []pmCPU;
	private int []pmRAM;
	private int [][]DYPower;
	private int []staticp;
		
	public ChocoProcess(int segmentPMN, int segmentVMN,int []segvmCPU,int []segvmRAM,int []segpmCPU,int []segpmRAM,int []staticpower , int [][]DYP){
		this.PMN = segmentPMN;
		this.VMN = segmentVMN;
		this.vmCPU = segvmCPU;
		this.vmRAM = segvmRAM;
		this.pmCPU = segpmCPU;
		this.pmRAM = segpmRAM;
		this.DYPower = DYP;
		this.staticp = staticpower;
	}
	
	public void runChoco(){
		
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

		s.setTimeLimit(10000);                                                     //����ʱ�����ƣ��������ʱ����ֹͣ����
		s.setObjective(s.getVar(power));
		s.minimize(false);                                                          //
		
		ChocoLogging.toDefault();                                                   //��ӡĬ��������־
		
		// Solve the model
		s.solve();
		
		System.out.println(s.getVar(sum));
		System.out.println(s.getVar(power));
		
	}

}
