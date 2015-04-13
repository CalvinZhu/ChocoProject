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
 * @类描述：ChocoProcess类用于处理每个小集群的choco约束求解
 * @项目名称：ChocoProject
 * @包名： Choco
 * @类名称：ChocoProcess
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
		 * choco约束编程
		 */
		
		/*
		 * 1、model模型
		 */
		CPModel m = new CPModel();
		
		/*
		 * 2、variable变量
		 */
		IntegerVariable[] count = Choco.makeIntVarArray("count", PMN, 0,          //count[i]表示i编号的物理机上的虚拟机数量
				VMN);
		IntegerVariable[] x = Choco.makeBooleanVarArray("x", PMN);                //x[i]表示i编号物理机是否被使用

		IntegerVariable[][] pos = Choco.makeIntVarArray("pos", VMN, PMN, 0,       //pos[i][j]表示i编号的虚拟机是否在j编号的物理机上
				1);
		IntegerVariable[][] dulpos = Choco.makeIntVarArray("dulpos", PMN,         //dulpos[i][j]是pos[i][j]的转置矩阵，表示。。
				VMN, 0, 1);
		IntegerVariable sum = Choco.makeIntVar("sum", 0, PMN);                    //sum表示资源分配中使用了的物理机数量
		IntegerVariable power = Choco.makeIntVar("power", 0, 100000,              //power表示资源分配之后动态能耗和静态能耗的总能耗
				Options.V_OBJECTIVE);
		IntegerVariable DYAPOWER = Choco.makeIntVar("DYAPOWER", 0, 100000);       //动态能耗
		IntegerVariable STAPOWER = Choco.makeIntVar("STAPOWER", 0, 100000);       //静态能耗
		IntegerVariable[] DYNAMICPower = Choco.makeIntVarArray(                   //DYNAMICPower[i]表示i编号的物理机上的动态能耗
				"DYNAMICPower", PMN);
		
		
		/*
		 * 3、constraints约束
		 */
		for (int i = 0; i < VMN; i++){                                            //dulpos[j][i]与pos[i][j]相同
			for (int j = 0; j < PMN; j++) {
				m.addConstraint(Choco.eq(dulpos[j][i], pos[i][j]));
			}
		}
		for (int i = 0; i < VMN; i++) {                                           //i编号的虚拟机有且仅能放在一个物理机上
			m.addConstraint(Choco.eq(Choco.sum(pos[i]), 1));
		}

		for (int i = 0; i < PMN; i++) {
			m.addConstraint(Choco.leq(Choco.scalar(dulpos[i], vmCPU), pmCPU[i]));  //i编号物理机的CPU要大于分配给它的所有虚拟机总CPU
			m.addConstraint(Choco.leq(Choco.scalar(dulpos[i], vmRAM), pmRAM[i]));  //i编号物理机的RAM要大于分配给它的所有虚拟机总RAM
			m.addConstraint(Choco.eq(Choco.sum(dulpos[i]), count[i]));             
			m.addConstraint(Choco.ifThenElse(Choco.eq(count[i], 0),                //如果count[i]=0表示i编号物理机没有被使用，则对应的x[i]赋值0
					Choco.eq(x[i], 0), Choco.eq(x[i], 1)));
			m.addConstraint(Choco.eq(Choco.scalar(dulpos[i], DYPower[i]),          //求出i编号物理机上的动态能耗总和          
					DYNAMICPower[i]));
		}

		m.addConstraint(Choco.eq(STAPOWER, Choco.scalar(staticp, x)));
		m.addConstraint(Choco.eq(DYAPOWER, Choco.sum(DYNAMICPower)));
		m.addConstraint(Choco.eq(power, Choco.plus(DYAPOWER, STAPOWER)));          //power=DYAPOWER+STAPOWER
		m.addConstraint(Choco.eq(sum, Choco.sum(x)));
		

		
		/*
		 * 4、solver解决器
		 */
		Solver s = new CPSolver();
		s.read(m);
		//s.addGoal(new Assign(new MinDomain(s), new DecreasingDomain()));

		s.setTimeLimit(10000);                                                     //设置时间限制，如果超过时间则停止搜索
		s.setObjective(s.getVar(power));
		s.minimize(false);                                                          //
		
		ChocoLogging.toDefault();                                                   //打印默认搜索日志
		
		// Solve the model
		s.solve();
		
		System.out.println(s.getVar(sum));
		System.out.println(s.getVar(power));
		
	}

}
