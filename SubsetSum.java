import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Implementation of a polynomial-time algorithm solving instances of the
 * subset sum problem (accurately, as far as I can tell).
 * 
 * Feel free to use or modify this code so long as you give credit and link to 
 * the place you found it (hopefully my GitHub repo).
 * 
 * The above applies to the algorithm as well.
 * 
 * @author Aubrey Alston (aubrey.alston.1@gmail.com) (ada2145@columbia.edu)
 */

public class SubsetSum {
	
	// Double equivalence is fun.  Set a threshold.
	public static double DOUBLE_THRESHOLD = 0.0000000001;
	
	/**
	 * Returns true if the specified set contains a subset summing to sum.
	 */
	public static boolean satisfiesSubsetSum(double[] set, double sum){
		return subsetSum(set, sum).size() > 0;
	}
	
	/**
	 * If such a set exists, returns the subset within set summing to sum
	 * as a linked list of doubles.
	 */
	public static LinkedList<Double> subsetSum(double[] set, double sum){
		
		// Initialize a linked list of doubles to populate if/when
		// a subset is found.
		
		LinkedList<Double> subset = new LinkedList<Double>();
		
		// At a high level, this algorithm works by reducing the question
		// of subset sum membership to a question of the solution spaces
		// of a set of linear systems.  This method creates a four-element
		// window in which, if a subset exists, certain properties will
		// be apparent within the solution space.  When multiple such
		// subsets exist for choices within the window, choice exists outside
		// of the window, and no one subset can be chosen.
		// 
		// So far, I see no exploitable pattern to make such a choice; however,
		// interestingly, reconfiguring the order of the set allows for the window,
		// once moved, to make that choice when/if necessary.
		//
		// Two explored solutions include:
		//    1. Introduce another set of iterations for each of the nC4 possible
		//       length-four subsets of the original set.  Begin each iteration
		//       by swapping those subsets into the first four positions of the
		//       set (the window positions).  This method takes the total complexity
		//       of the algorithm to O(n^8).
		
		// Copy the set for easy readjustments
		double[] static_set = new double[set.length];
		for(int i = 0; i < set.length; i++)
			static_set[i] = set[i];
		
		// The window method introduced by this algorithm requires that the set
		// be at least 4 elements long.  Introduce base cases for sets less than four in length.
		double setSum = 0;
		
		/* Finding pairs */
		 double[][] pairs = new double[set.length*(set.length-1)/2][2];
		 int pairCursor = 0;
		 
		
		// Check for the sum among prefix subset of elements of the set.
		for(int i = 0; i < set.length; i++){
			// Populate subset where necessary and return it if found.
			if(dblEq(set[i], sum)){
				subset.add(set[i]);
				return subset;
			}
			setSum += set[i];
			if(dblEq(setSum, sum)){
				for(int j = 0; j <= i; j++){
					subset.add(set[j]);
				}
				return subset;
			}
		}
		
		
		// Check for the case that the subset summing to the sought value 
		// has length |set| - 1.
		for(int i = 0; i < set.length; i++){
			if(dblEq(setSum-set[i], sum)){
				for(int j = 0; j < set.length; j++){
					if(j != i) subset.add(set[j]);
				}
				return subset;
			}
		}
		
		// Check for length-2 subsets summing to the sought value.
		for(int i = 0; i < set.length; i++){
			for(int j = i+1; j < set.length; j++){
				 pairs[pairCursor++] = new double[]{ set[i], set[j] };
				 
				if(dblEq(set[i]+set[j], sum)){
					subset.add(set[i]);
					subset.add(set[j]);
					return subset;
				}
			}
		}
		
	    double[][] quads = new double[pairs.length*pairs.length][4];
		int quadCursor = 0;
		   
		for(int i = 0; i < pairs.length; i++){
		    for(int j = i + 1; j < pairs.length; j++){
		        //Only use pairs of pairs not containing common elements.
		        if(pairs[i][0] != pairs[j][0] && pairs[i][0] != pairs[j][1] && 
		            pairs[i][1] != pairs[j][0] && pairs[i][1] != pairs[j][1])
		               quads[quadCursor++] = new double[]
		                { pairs[i][0], pairs[i][1], pairs[j][0], pairs[j][1] };  
		       }
		}
		 
		
		double[][] system;
		
		for(int i = 0; i < quadCursor; i++){
			int qA = -1, qB = -1, qC = -1, qD = -1;
			for(int j = 0; j < set.length; j++){
	           if(set[j] == quads[i][0])
	               qA = j;
	           if(set[j] == quads[i][1])
	               qB = j;
	           if(set[j] == quads[i][2])
	               qC = j;
	           if(set[j] == quads[i][3])
	               qD = j;
	           if(qA != -1 && qB != -1 && qC != -1 && qD != -1)  break;
	       } 
	       
	       // Swap elements into the window
	       
	       double t = set[qA];
	       set[qA] = set[0];
	       set[0] = t;
	       t = set[qB];
	       set[qB] = set[1];
	       set[1] = t;
	       t = set[qC];
	       set[qC] = set[2];
	       set[2] = t;
	       t = set[qD];
	       set[qD] = set[3];
	       set[3] = t; 
		   for( int wShifts = 0; wShifts < 2; wShifts++){ // Shift the window once
			                                              // to ensure each pair
			                                              // appears once as balance/pivot.
				// This block tests the set against subset sum constraints
				// for the current window for subsets of length 3 . . . set.length-2.
				// Total complexity for this block: n^3.
				
				// Initialize the subset system configured for the current set
				// ordering and the sought sum.
				system = initSubsetSystem(set, sum); 
				for(int n = 3; n <= set.length - 2; n++){ 
					// Because the system is underdetermined (4 rows),
					// Matrix operations here are of the order 4n and 4n^2.
					// Unless I'm mistaken, this block is n^2.
					
					// Adjust the sought subset length.
					setAssumedSubsetLength(system, n);
					
					// Within the four-item window (first four items of the set)
					// there are four elements:
					// | a1 a2 a3 a4 | . . . . .
					// a1 and a2 are the balancing points for analyzing the solution space;
					// a3 and a4 are the pivots used to make membership choices of the set.
					
					// Test against the assumption that both pivots are in the subset.
					testSystem(system, subset, 1, 1, sum, n);
					if(subset.size() > 0) return subset;
					
					// Test against the assumption that the second but not the first element
					// is in the subset.
					testSystem(system, subset, 0, 1, sum, n);
					if(subset.size() > 0) return subset;
					
					// Test against the assumption that the first but not the second element is
					// in the subset.
					testSystem(system, subset, 1, 0, sum, n);
					if(subset.size() > 0) return subset;
					
					// Test against the assumption that neither the second nor the first element
					// is in the subset.
					testSystem(system, subset, 0, 0, sum, n);
					if(subset.size() > 0) return subset;		
				}
				shiftWindow(set);
			}
		}
		return subset;
	}
	
	/**
	 * Shifts the window by shifting the elements of the set, wrapping the first element
	 * around to be the last.
	 */
	public static void shift(double[] set){
		double t = set[0];
		for(int i = 1; i < set.length; i++){
			set[i-1] = set[i];
		}
		set[set.length-1] = t;
	}
	
	/**
	 * Shifts the window by shifting the elements of the set, wrapping the first element
	 * around to be the last.
	 */
	public static void shiftWindow(double[] set){
		double t = set[0];
		for(int i = 1; i < 4; i++){
			set[i-1] = set[i];
		}
		set[3] = t;
	}
	
	
	/**
	 * Initializes the underdetermined subset sum solving system for a 
	 * given set and sum.
	 */
	private static double[][] initSubsetSystem(double[] set, double sum){
		double[][] system = new double[4][set.length+1];
		for(int i = 0; i < system.length; i++){
			for(int j = 0; j < set.length; j++){
				if(i == 0) // Row with the values of the set 
					system[i][j] = set[j];
				if(i == 1) // Row en0coding subset length
					system[i][j] = 1;
				if(i == 2 && j == 2) // Row encoding assumed value 1
					system[i][j] = 1;
				if(i == 3 && j == 3) // Row encoding assumed value 2
					system[i][j] = 1;
			}
		}
		system[0][system[0].length - 1] = sum;
		return system;
	}
	
	/**
	 * Tests a subset sum system against assumptions.
	 * @param system system to test
	 * @param workingSubset working subset containing result
	 * @param m1 membership (0 or 1) of first pivot element
	 * @param m2 membership (0 or 1) of second pivot element
	 * @param sum sought sum
	 * @param n subset length for current test
	 */
	public static void testSystem(double[][] system, 
			LinkedList<Double> workingSubset, int m1, int m2, double sum, int n){
		
		double[][] rref, particular_solution, special_solutions;
		
		setAssumedMembership(system, m1, m2);
		
		// Perform direct forward elimination on the system.
		rref = LinearSystems.eliminate(system);
		
		// Derive the particular solution using back substitution.
		particular_solution = LinearSystems.getParticularSolution(rref);
		
		// for |set| = 4, only check the particular solution
		if(particular_solution.length == 4){
			for(int i = 0; i < particular_solution.length; i++){
				if(!dblEq(particular_solution[i][0], 0) && 
						!dblEq(particular_solution[i][0], 1))
					return;
			}
			for(int i = 0; i < particular_solution.length; i++){
				if(dblEq(particular_solution[i][0],1))
					workingSubset.add(system[0][i]);
			}
			return;
		}
		
		// Calculate the special solutions by finding the null space
		// for the eliminated subset system.
		special_solutions = LinearSystems.toMatrix(
				LinearSystems.getSpecialSolutions(rref));
		
		// Attempts to balance the particular solution against the null space.
		// A balanced particular solution encodes a valid subset summing to the sought
		// value in n element.  It will contain only 0s and 1s.
		if(enforceConstraints(particular_solution, special_solutions, n, m1+m2)){
			for(int i = 0; i < particular_solution.length; i++){
				if(dblEq(particular_solution[i][0], 1)){
					workingSubset.add(system[0][i]);
				}
			}
		}
	}
	
	/**
	 * Enforces subset sum constraints by considering the properties of the
	 * yielded solution space.
	 */
	public static boolean enforceConstraints(double[][] particular_solution,
			double[][] null_space, int n, int nMod){
		boolean sufficient = true;
		for(int i = 0; i < 4; i++){
			if(!dblEq(particular_solution[i][0],0) 
					&& !dblEq(particular_solution[i][0],1))
				sufficient = false;
		}
		if(sufficient)
			return true;
		
		// Find difference table.
		
		double[][] differenceTable = 
				getDifferenceTable(particular_solution, null_space);
		
		if(!checkSumConstraint(particular_solution, null_space, differenceTable,
				0, 0, n, nMod) &&
		   !checkSumConstraint(particular_solution, null_space, differenceTable,
				1, 1, n, nMod) &&
		   !checkSumConstraint(particular_solution, null_space, differenceTable,
				0, 1, n, nMod) &&
		   !checkSumConstraint(particular_solution, null_space, differenceTable,
				1, 0, n, nMod)){ return false; }
		return true;
	}
	
	/**
	 * Checks the properties of the solution space, modifying the particular_solution
	 */
	private static boolean checkSumConstraint(double[][] particular_solution,
			double[][] null_space, double[][] differenceTable, int sumA, int
			sumB, int n, int nMod){
		int indA = sumA;  int indB = sumB+2;
		double[][] work = new double[3][differenceTable[0].length];
		for(int i = 1; i < differenceTable[0].length; i++){
			work[0][i-1] = differenceTable[indA][i];
			work[1][i-1] = differenceTable[indB][i];
		}
		boolean pos_a = false;
		boolean pos_b = false;
		for(int i = 0; i < work[0].length-1; i++){
			if(dblEq(work[0][i],1) && dblEq(work[1][i],1)){
				for(int j = 0; j < particular_solution.length; j++){
					particular_solution[j][0] += null_space[j][i];
				}
				
				return true;
			}	
			if(work[0][i] > 0) pos_a = true;
			if(work[1][i] > 0) pos_b = true;
		}
		
		if(!pos_a && !pos_b){
			return false;
		}
		
		for(int i = 0; i < work[0].length; i++){
			work[2][i] = Math.abs(work[0][i]-work[1][i]);
			work[0][work[0].length-1] +=work[0][i];
			work[1][work[0].length-1] +=work[1][i];
		}
		
		ArrayList<Integer> valid = new ArrayList<Integer>();
		
		// If one of the elements of the particular solution is
		// already one or zero (but not the other since it's reached this
		// point), simply take the elements of the null space corresponding
		// to the positive proportions in the work table
		double[][] possible_solution = new double[particular_solution.length][1];
		for(int i = 0; i < particular_solution.length; i++) 
			possible_solution[i][0] = particular_solution[i][0];
		if(particular_solution[0][0] == sumA){
			double oSum = 0;
			for(int i = 0; i < work[0].length-1; i++){
				if(work[1][i] > 0){
					valid.add(i);
					oSum += work[1][i];
					if(dblEq(oSum,1)){
						for(Integer index : valid){
							for(int j = 0; j < particular_solution.length; j++){
								possible_solution[j][0] += null_space[j][index];
							}
						}
						boolean valid_solution = true;
						// test the solution 
						for(int k = 0; k < possible_solution.length; k++){
							if(!dblEq(possible_solution[k][0],1) &&
									!dblEq(possible_solution[k][0],0)){
								valid_solution = false;
								break;
							}
						}
						if(valid_solution){
							for(int j = 0; j < particular_solution.length; j++){
								particular_solution[j][0] = possible_solution[j][0];
							}
						}
						return valid_solution;
					}
				}
			}
		}
		
		valid.clear();
		
		if(particular_solution[1][0] == sumB){
			double oSum = 0;
			for(int i = 0; i < work[0].length-1; i++){
				if(work[0][i] > 0){
					valid.add(i);
					oSum += work[0][i];
					if(dblEq(oSum,1)){
						for(Integer index : valid){
							for(int j = 0; j < particular_solution.length; j++){
								possible_solution[j][0] += null_space[j][index];
							}
						}
						boolean valid_solution = true;
						// test the solution 
						for(int k = 0; k < possible_solution.length; k++){
							if(!dblEq(possible_solution[k][0],1) &&
									!dblEq(possible_solution[k][0],0)){
								valid_solution = false;
								break;
							}
						}
						if(valid_solution){
							for(int j = 0; j < particular_solution.length; j++){
								particular_solution[j][0] = possible_solution[j][0];
							}
						}
						return valid_solution;
					}
				}
			}
		}
		
		valid.clear();
		// For an affirmative (1) check, valid components in the null
		// space have equal difference proportions
		for(int i = 0; i < work[0].length-1; i++){
			valid.add(i);
			for(int j = i+1; j < work[0].length-1; j++){
				if((!dblEq(work[1][i], 0) || !dblEq(work[0][i], 0)) && 
						dblEq(work[2][i],work[2][j]))
					valid.add(j);
			}
			double fracA = 0;
			double fracB = 0;
			for(Integer index : valid){
				fracA += work[0][index];
				fracB += work[1][index];
			}
			if(dblEq(fracA, 1) && dblEq(fracB, 1)){
				for(Integer index : valid){
					for(int j = 0; j < particular_solution.length; j++){
						particular_solution[j][0] += null_space[j][index];
					}
				}
				return true;
			}	
			valid.clear();
		}
		// For a negative affirmation (2) check, the valid components
		// in the null space will have proportion differences < 1.
		double sumTop = 0;
		double sumBottom = 0;
		for(int i = 0; i < work[0].length-1; i++){
			if(work[2][i] > 1){ }
			else{
				valid.add(i);
				sumTop += work[0][i];
				sumBottom += work[1][i];
				
				if(dblEq(sumTop,1) && dblEq(sumBottom,1)){
					for(Integer index : valid){
						for(int j = 0; j < particular_solution.length; j++){
							particular_solution[j][0] += null_space[j][index];
						}
					}
					return true;
				}
			}
		}
		
		valid.clear();
		// 2 0s -> constant difference between elements in null space
		double[] cpy = new double[work[0].length-1];
		int cpyLen = 0;
		ArrayList<Integer> minEq = new ArrayList<Integer>();
		for(int i = 0; i < work[0].length-1; i++){
			if(!dblEq(work[0][i],0))
				cpy[cpyLen++] = work[2][i];
		}
		for(int i = 0; i < cpyLen; i++){
			int min = i;
			for(int j = i+1; j < cpyLen; j++){
				if(cpy[j] < cpy[min])
					min = j;
			}
			double t = cpy[i];
			cpy[i] = cpy[min];
			cpy[min] = t;
		}
		for(int i = 0; i < cpyLen; i++){
			for(int j = 0; j < work[0].length-1; j++){
				if(work[2][j] == cpy[i] && !minEq.contains(j)){
					minEq.add(j);
					break;
				}
			}
			if(minEq.size() >= n-nMod-sumA-sumB){ 
				break;
			}
		}
		sumTop = 0;
		sumBottom = 0;
		for(Integer i : minEq){
			sumTop += work[0][i];
			sumBottom += work[1][i];
			valid.add(i);
			if(dblEq(sumTop,1) && dblEq(sumBottom,1)){
				for(Integer index : valid){
					for(int j = 0; j < particular_solution.length; j++){
						particular_solution[j][0] += null_space[j][index];
					}
				}
				return true;
			}
		}
		valid.clear();
		
		// At times, it's possible for there to be choices within the null space.
		/*HashSet<Integer> seenAsChoice = new HashSet<Integer>();
		for(int i = 0; i < work[0].length-1; i++){
			// pass; make binary decisions
			if(seenAsChoice.contains(i) || work[2][i] == -1) continue;
			LinkedList<Integer> choices = new LinkedList<Integer>();
			for(int j = 0; j < work[0].length-1; j++){
				if(i == j) continue;
				double dA = work[0][j] - work[0][i];
				double dB = work[1][j] - work[1][i];
				if(dblEq(dA, dB)){
					choices.add(j);
				}
			}
			// choose the first choice 
			if(choices.size() == 1){
				valid.add(i);
				
				work[2][i] = -1;
				work[2][choices.get(0)] = -1;
				work[0][choices.get(0)] = 0;
				work[1][choices.get(0)] = 0;
				for(int j = 0; j < work[0].length-1; j++){
					if(work[2][j] == -1) continue;
					work[0][j] /= 1 - work[0][i];
					work[1][j] /= 1 - work[1][i];
					if(work[2][j] != -1)
						work[2][j] = work[0][j] - work[1][j];
				}
			} else{
				seenAsChoice.addAll(choices);
				seenAsChoice.add(i);
			}
			
		}
		
		// now remaining elements of the subset are determined
		int nRemaining = n - nMod - sumA - sumB - valid.size();

		LinkedList<Integer> eqDiff = new LinkedList<Integer>();
		if(nRemaining > 1){
			for(int i = 0; i < work[0].length - 1; i++){
				eqDiff.clear();
				eqDiff.add(i);
				if(work[2][i] == -1) continue;
				for(int j = i+1; j < work[0].length - 1; j++){
					// Need more precision: BigDecimal implementation becoming necessary
					if(dblEq(1000*work[2][i], 1000*work[2][j])){
						eqDiff.add(j);
						if(eqDiff.size() == nRemaining) break;
					}
				}
				if(eqDiff.size() == nRemaining){
					valid.addAll(eqDiff);
					break;
				}
			}
		} else{
			for(int i = 0; i < work[0].length - 1; i++){
				if(dblEq(work[0][i],1) && dblEq(work[1][i],1)){
					valid.add(i);
				}
			}
		}
		
		for(Integer index : valid){
			for(int j = 0; j < particular_solution.length; j++){
				possible_solution[j][0] += null_space[j][index];
			}
		}
		
		boolean valid_solution = true;
		// test the solution 
		for(int k = 0; k < possible_solution.length; k++){
			if(!dblEq(possible_solution[k][0],1) &&
					!dblEq(possible_solution[k][0],0)){
				valid_solution = false;
				break;
			}
		}
		if(valid_solution){
			for(int j = 0; j < particular_solution.length; j++){
				particular_solution[j][0] = possible_solution[j][0];
			}
			System.out.println("lol");
		}*/
		
		return false;//valid_solution;
	}
	
	/**
	 * Returns the difference table for a particular solution and null space.
	 * A difference table is a table tabulating the contributed distance
	 * from a particular solution cell value to an expected value (0 or 1).
	 * @return
	 */
	public static double[][] getDifferenceTable(double[][] particular_solution,
			double[][] null_space){
		double[][] table = new double[4][null_space[0].length+1]; 
		
		table[0][0] = ( 0 - particular_solution[0][0] );
		table[1][0] = ( 1 - particular_solution[0][0] );
		table[2][0] = ( 0 - particular_solution[1][0] );
		table[3][0] = ( 1 - particular_solution[1][0] );
		
		for(int j = 0; j < null_space[0].length; j++){
			table[0][j+1] = null_space[0][j] / table[0][0];
			table[1][j+1] = null_space[0][j] / table[1][0];
			table[2][j+1] = null_space[1][j] / table[2][0];
			table[3][j+1] = null_space[1][j] / table[3][0];
		}
		
		return table;
	}
	
	public static void setAssumedSubsetLength(double[][] system, int r){
		system[1][system[0].length-1] = r;
	}
	
	public static void setAssumedMembership(double[][] system, int m1, int m2){
		system[2][system[0].length-1] = m1;
		system[3][system[0].length-1] = m2;
	}
	
	/**
	 * Determines whether or not two doubles are equal.
	 */
	public static boolean dblEq(double a, double b){
		return Math.abs(a - b) < DOUBLE_THRESHOLD;
	}
}
