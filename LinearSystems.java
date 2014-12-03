/**
 * Implementation of base linear operations.  Matrices
 * are represented as two-dimensional arrays of doubles.
 * @author Aubrey Alston
 */
public class LinearSystems {

	public static double[][] concatenateColumns(double[][] a, double[][] b){
		double[][] result = new double[a.length][a[0].length+b[0].length];
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length + b[0].length; j++){
				if(j < a[0].length)
					result[i][j] = a[i][j];
				else
					result[i][j] = b[i][j-a[0].length];
			}
		}
		return result;
	}
	
	/**
	 * Returns the particular solution for a system in rref.
	 * @param sys
	 * @return
	 */
	public static double[][] getParticularSolution(double[][] sys){
		double[][] solution = new double[sys[0].length-1][1];
		for(int i = sys.length-1; i >= 0; i--){
			double v = sys[i][sys[0].length-1];
			for(int j = 0; j < sys[0].length - 1; j++){
				v -= solution[j][0] * sys[i][j];
			}
			solution[i][0] = v / sys[i][i];
		}
		return solution;
	}
	
	/**
	 * Returns the special solutions for a system in rref.
	 * @return
	 */
	public static double[][][] getSpecialSolutions(double[][] sys){
		double[][][] solutions = 
				new double[sys[0].length-sys.length-1][sys[0].length-1][1];
		int solIndex = 0;
	
		for(int current = sys.length; current < sys[0].length-1; current++){
			double[][] solution = new double[sys[0].length-1][1];
			solution[current][0] = 1;
			// pls fix
			for(int i = sys.length-1; i >= 0; i--){
				double v = 0;
				for(int j = 0; j < sys[0].length - 1; j++){
					if(i != j){
						v -= solution[j][0] * sys[i][j];
					}
				}
				solution[i][0] = v / sys[i][i];
			}
			solutions[solIndex++] = solution;
		}
		
		return solutions;
	}
	
	public static double[][][] getOtherSolutions(double[][] sys){
		double[][][] solutions = 
				new double[sys[0].length-sys.length-1][sys[0].length-1][1];
		int solIndex = 0;
	
		for(int current = sys.length; current < sys[0].length-1; current++){
			double[][] solution = new double[sys[0].length-1][1];
			solution[current][0] = 1;
			// pls fix
			for(int i = sys.length-1; i >= 0; i--){
				double v = sys[i][sys[0].length-1];
				for(int j = 0; j < sys[0].length - 1; j++){
					if(i != j){
						v -= solution[j][0] * sys[i][j];
					}
				}
				solution[i][0] = v / sys[i][i];
			}
			solutions[solIndex++] = solution;
		}
		
		return solutions;
	}
	
	public static double[][] eliminate(double[][] a){
		double[][] work = new double[a.length][a[0].length];
		
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				work[i][j] = a[i][j];
			}
		}
		
		// Forward elimination (n^3)
		for(int i = 0; i < a.length; i++){
			for(int j = i+1; j < a.length; j++){
				double l_value = work[j][i] / work[i][i];
				for(int k = i; k < work[0].length; k++){
					work[j][k] -= work[i][k] * l_value;
				}
			}
		}
		
		
		return work;
	}
	
	public static double[][] getOrthogonalizedSpace(double[][] a){
		double[][] result = new double[a.length][a[0].length];
		double[][][] vectors = getVectors(a);
		for(int i = 0; i < vectors.length; i++){
			vectors[i] = getUnitVector(vectors[i]);
			double[][] proj = getVectorProjectionMatrix(vectors[i]);
			for(int j = i +1; j < vectors.length; j++){
				vectors[j] = subtract(vectors[j], multiply(proj, vectors[j]));
			}
		}
		return toMatrix(vectors);
	}
	
	public static double[][][] getVectors(double[][] a){
		double[][][] vectors = new double[a[0].length][a.length][1];
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				vectors[j][i][0] = a[i][j];
			}
		}
		return vectors;
	}
	
	public static double[][] toMatrix(double[][][] vectors){
		double[][] matrix = new double[vectors[0].length][vectors.length];
		for(int i = 0; i < matrix.length; i++){
			for(int j = 0; j < matrix[0].length; j++){
				 matrix[i][j] = vectors[j][i][0];
			}
		}
		return matrix;
	}
	
	/**
	 * Returns the resulting projection of b onto a.
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[][] project(double[][] a, double[][] b){
		return multiply(getMatrixProjectionMatrix(a), b);
	}
	
	/**
	 * Returns the projection matrix for a vector.
	 */
	public static double[][] getVectorProjectionMatrix(double[][] a){
		double[][] transpose = transpose(a);
		return divideByNumber(multiply(a,transpose),multiply(transpose,a)[0][0]);
	}
	
	/**
	 * Returns the projection matrix of a matrix.
	 * @param a
	 * @return
	 */
	public static double[][] getMatrixProjectionMatrix(double[][] a){
		double[][] transpose = transpose(a);
		double[][] ata_inv = inverse(multiply(transpose, a));
		return multiply(multiply(a, ata_inv), transpose);
	}
	
	/**
	 * Returns the sum of two matrices.
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[][] add(double[][] a, double[][] b){
		double[][] result = new double[a.length][a[0].length];
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[0].length; j++)
				result[i][j] = a[i][j] + b[i][j];
		return result;
	}
	
	/**
	 * Returns the difference between two matrices.
	 */
	public static double[][] subtract(double[][] a, double[][] b){
		double[][] result = new double[a.length][a[0].length];
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[0].length; j++)
				result[i][j] = a[i][j] - b[i][j];
		return result;
	}
	
	/**
	 * Returns the inverse of a. (n^3)
	 * @return
	 */
	public static double[][] inverse(double[][] a){
		double[][] work = new double[a.length][a.length*2];
		double[][] result = new double[a.length][a.length];
		
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				work[i][j] = a[i][j];
			}
			work[i][i+a[0].length] = 1;
		}
		
		// Forward elimination (n^3)
		for(int i = 0; i < a.length; i++){
			for(int j = i+1; j < a.length; j++){
				double l_value = work[j][i] / work[i][i];
				for(int k = i; k < work[0].length; k++){
					work[j][k] -= work[i][k] * l_value;
				}
			}
		}
		
		// Backward elimination (n^3)
		for(int i = a.length-1; i > 0; i--){
			for(int j = i-1; j >= 0; j--){
				double l_value = work[j][i] / work[i][i];
				for(int k = i; k < work[0].length; k++){
					work[j][k] -= work[i][k] * l_value;
				}
			}
		}
		
		// Divide by pivots
		for(int i = 0; i < a.length; i++){
			for(int j = a[0].length; j < work[0].length; j++)
				work[i][j] /= work[i][i];
			work[i][i] = 1;
		}
		
		for(int i = 0; i < result.length; i++){
			for(int j = 0; j < a[0].length; j++){
				result[i][j] = work[i][j+a[0].length];
			}
		}
		
		return result;
	}
	
	public static double[][] getUnitVector(double[][] a){
		return divideByNumber(a, getNorm(a));
	}
	
	public static double getNorm(double[][] a){
		return Math.sqrt(getSquaredNorm(a));
	}
	
	public static double getSquaredNorm(double[][] a){
		double n = 0;
		for(int i = 0; i < a.length; i++){
			n += a[i][0] * a[i][0];
		}
		return n;
	}
	
	/**
	 * Returns the transpose of a.
	 */
	public static double[][] transpose(double[][] a){
		double[][] result = new double[a[0].length][a.length];
		
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[0].length; j++)
				result[j][i] = a[i][j];
		return result;
	}
	
	/**
	 * Vanilla nmr matrix multiplication implementation
	 * of A * B.
	 * @param a
	 * @param b
	 */
	public static double[][] multiply(double[][] a, double[][] b){
		double[][] result  = new double[a.length][b[0].length];
		
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < b[0].length; j++)
				for(int k = 0; k < a[0].length; k++)
					result[i][j] += a[i][k] * b[k][j];
		
		return result;
	}
	
	public static void print2D(double[][] a){
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++)
				System.out.print(a[i][j]+" ");
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * Divides every element of the matrix a by a constant. 
	 * NOTE: THIS CHANGES THE PASSED MATRIX
	 * @param a
	 * @param n
	 */
	public static double[][] divideByNumber(double[][] a, double n){
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[0].length; j++)
				a[i][j] /= n;
		return a;
	}
	
	/**
	 * Multiplies every element of the matrix by a constant.
	 * NOTE: THIS CHANGES THE PASSED MATRIX
	 * @param a
	 * @param n
	 */
	public static double[][] multiplyByNumber(double[][] a, double n){
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[0].length; j++)
				a[i][j] *= n;
		return a;
	}
}
