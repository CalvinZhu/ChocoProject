package main;
import java.text.MessageFormat;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/*
 * 
 * @类描述：测试choco2.1.5，Magic square程序，n*n矩阵中横、列或者对角线的元素相加都等于一个数(n(n^2 +1)/2)。
 * @项目名称：ChocoProject
 * @包名： 
 * @类名称：TestChoco
 * @version v1.0
 * @see [nothing]
 * @bug [nothing]
 * @Copyright go3c
 * @mail *@qq.com
 */
public class TestChoco {

	public TestChoco() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String agrs[]){
		// Constant declaration
		int n = 3; // Order of the magic square
		int magicSum = n * (n * n + 1) / 2; // Magic sum
		
		// Build the model
		CPModel m = new CPModel();
		
		// Creation of an array of variables
		IntegerVariable[][] var = new IntegerVariable[n][n];
		// For each variable, we define its name and the boundaries of its domain.
		for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j ++) {
		var[i][j ] = Choco. makeIntVar("var_" + i + "_" + j , 1, n * n);
		// Associate the variable to the model.
		m.addVariable(var[i][j ]);
		}
		}
		
		// All cells of the matrix must be different
		for (int i = 0; i < n * n; i++) {
		for (int j = i + 1; j < n * n; j ++) {
		Constraint c = (Choco.neq(var[i / n][i % n], var[j / n][j % n]));
		m.addConstraint(c);
		}
		}
		
		// All row’ s sum has to be equal to the magic sum
		for (int i = 0; i < n; i++) {
		m.addConstraint(Choco.eq(Choco. sum(var[i]), magicSum));
		}
		IntegerVariable[][] varCol = new IntegerVariable[n][n];
		for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j ++) {
		// Copy of var in the column order
		varCol[i][j ] = var[j ][i];
		}
		
		// All column’ s sum is equal to the magic sum
		m.addConstraint(Choco.eq(Choco.sum(varCol[i]), magicSum));
		}
		IntegerVariable[] varDiag1 = new IntegerVariable[n];
		IntegerVariable[] varDiag2 = new IntegerVariable[n];
		for (int i = 0; i < n; i++) {
		varDiag1[i] = var[i][i]; // Copy of var in varDiag1
		varDiag2[i] = var[(n - 1) - i][i]; // Copy of var in varDiag2
		}
		
		// All diagonal’ s sum has to be equal to the magic sum
		m.addConstraint(Choco.eq(Choco.sum(varDiag1), magicSum));
		m.addConstraint(Choco.eq(Choco.sum(varDiag2), magicSum));
		
		// Build the solver
		CPSolver s = new CPSolver();
		
		// Read the model
		s.read(m);
		
		//set the chocologging		
		//ChocoLogging. toVerbose();
		ChocoLogging.toDefault();
		// Solve the model
		s.solve();
		// Print the solution
		for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j ++) {
		System.out.print(MessageFormat. format("{0} ", s.getVar(var[i][j ]).getVal()));
		}
		System.out.println();
		}
		
	}

}
