import java.util.LinkedList;

public class RefactoredSubsetSum {
	
	public static boolean satisfiesSubsetSum(long[] set, long sum){
		return subsetSum(set, sum).size() > 0;
	}
	
	public static LinkedList<Long> subsetSum(long[] uSet, long sum){
		
		LinkedList<Long> subset = null;
		
		long weight = -1 * sum;
		boolean weighted = false;
		for(int i = 0; i < uSet.length; i++){
			if(uSet[i] == weight){
				weighted = true;
				break;
			}
		}
		long[] set = null;
		if(!weighted){
			set = new long[uSet.length+1];
			set[0] =  weight;
			for(int i = 0; i < uSet.length; i++){
				set[i+1] = uSet[i];
			}
		} else{
			set = uSet;
		}
		
		// Broken ordering
		for(int i = 0; i < set.length; i++){
			int min = i;
			for(int j = i+1; j < set.length; j++){
				if(set[j] < set[min])
					min = j;
			}
			long t = set[i];
			set[i] = set[min];
			set[min] = t;
		}
		for(int i = 0; i < set.length/2; i+=2){
			long t = set[i];
			set[i] = set[set.length - 1 - i];
			set[set.length - 1 - i] = t;
		}
		subset = partialSubsetSum(set, sum, !weighted, weight);
		if(subset.size() > 0) return subset;
		
		for(int i = 0; i < set.length; i++){
			int max = i;
			for(int j = i+1; j < set.length; j++){
				if(set[j] > set[max])
					max = j;
			}
			long t = set[i];
			set[i] = set[max];
			set[max] = t;
		}
		for(int i = 0; i < set.length/2; i+=2){
			long t = set[i];
			set[i] = set[set.length - 1 - i];
			set[set.length - 1 - i] = t;
		}
		subset = partialSubsetSum(set, sum, !weighted, weight);
		if(subset.size() > 0) return subset;
		
		// Descending absolute value
		for(int i = 0; i < set.length; i++){
			int max = i;
			for(int j = i+1; j < set.length; j++){
				if(Math.abs(set[j]) > Math.abs(set[max]))
					max = j;
			}
			long t = set[i];
			set[i] = set[max];
			set[max] = t;
		}
		subset = partialSubsetSum(set, sum, !weighted, weight);
		if(subset.size() > 0) return subset;
		
		// Descending scalar value
		for(int i = 0; i < set.length; i++){
			int max = i;
			for(int j = i+1; j < set.length; j++){
				if(set[j] > set[max])
					max = j;
			}
			long t = set[i];
			set[i] = set[max];
			set[max] = t;
		}
		subset = partialSubsetSum(set, sum, !weighted, weight);
		if(subset.size() > 0) return subset;
		
		// Ascending absolute value
		for(int i = 0; i < set.length; i++){
			int min = i;
			for(int j = i+1; j < set.length; j++){
				if(Math.abs(set[j]) < Math.abs(set[min]))
					min = j;
			}
			long t = set[i];
			set[i] = set[min];
			set[min] = t;
		}
		subset = partialSubsetSum(set, sum, !weighted, weight);
		if(subset.size() > 0) return subset;
		
		// Ascending absolute signed value
		for(int i = 0; i < set.length; i++){
			int min = i;
			for(int j = i+1; j < set.length; j++){
				if(set[j] < set[min])
					min = j;
			}
			long t = set[i];
			set[i] = set[min];
			set[min] = t;
		}
		subset = partialSubsetSum(set, sum, !weighted, weight);
		if(subset.size() > 0) return subset;
		
		return subset;
	}
	
	public static LinkedList<Long> partialSubsetSum(long[] set, long sum, boolean weighted,
			long weight){
		LinkedList<Long> subset = new LinkedList<Long>();
		
		long setSum = 0;
		// in-place window swap?
		// desc, asc, stripe desc, stripe asc		
		
		/* Finding pairs */
		long[][] pairs = new long[set.length*(set.length-1)/2][2];
		int pairCursor = 0;
		
		// Check for the sum among prefix subset of elements of the set.
		for(int i = 0; i < set.length; i++){
			// Populate subset where necessary and return it if found.
			if(weighted && set[i] == weight) continue;
			if(set[i] == sum){
				subset.add(set[i]);
				return subset;
			}
			setSum += set[i];
			if(sum == setSum){
				for(int j = 0; j <= i; j++){
					if(!weighted && set[j] == weight)
						subset.add(set[j]);
				}
				return subset;
			}
		}
		
		// Check for the case that the subset summing to the sought value 
		// has length |set| - 1.
		for(int i = 0; i < set.length; i++){
			if(weighted && set[i] == weight) continue;
			if(setSum-set[i] == sum){
				for(int j = 0; j < set.length; j++){
					if(j != i && !(weighted && set[j]==weight)) subset.add(set[j]);
				}
				if(subset.size() > 0)
					return subset;
			}
		}
		
		// Check for length-2 subsets summing to the sought value.
		for(int i = 0; i < set.length; i++){
			for(int j = i+1; j < set.length; j++){
				 if(set[i] != set[j])
					pairs[pairCursor++] = new long[]{ set[i], 
						 set[j] };
				 
				if(weighted && (set[i] == weight || set[j] == weight))
					continue;
				
				if(set[i] + set[j] == sum){
					subset.add(set[i]);
					subset.add(set[j]);
					return subset;
				}
			}
		}
		
		
	    long[][] quads = new long[pairs.length*(pairs.length-1)/2][4];
	    
	    int quadCursor = 0;
		   
		for(int i = 0; i < pairCursor; i++){
			for(int j = i + 1; j < pairCursor; j++){
		        //Only use pairs of pairs not containing common elements.
		        if(pairs[i][0] != pairs[j][0] && pairs[i][0] != pairs[j][1] && 
		            pairs[i][1] != pairs[j][0] && pairs[i][1] != pairs[j][1])
		               quads[quadCursor++] = new long[]
		                { pairs[i][0], pairs[i][1], pairs[j][0], pairs[j][1] };  
		       }
		}
		
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
	       
	       long t = set[qA];
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
		   for( int wShifts = 0; wShifts < 2; wShifts++){
			   
			   for(int n = 3; n < set.length - 1; n++){
				   balance(subset, set, sum, n, 1, 1, weighted, weight);
				   if(subset.size() > 0) return subset;
				   balance(subset, set, sum, n, 0, 0, weighted, weight);
				   if(subset.size() > 0) return subset;
				   balance(subset, set, sum, n, 1, 0, weighted, weight);
				   if(subset.size() > 0) return subset;
				   balance(subset, set, sum, n, 0, 1, weighted, weight);
				   if(subset.size() > 0) return subset;
			   }
			   
			   t = set[1];
			   set[1] = set[2];
			   set[2] = t;
		   }
		}
		   
		return subset;
	}
	
	public static boolean balance(LinkedList<Long> workingSubset,
			long[] set, long c, int t, int v_r, int v_s, boolean weighted, long weight){
		
		if(!test(workingSubset, set, c, t, 0, 0, v_r, v_s, weighted, weight) &&
				!test(workingSubset, set, c, t, 1, 1, v_r, v_s, weighted, weight) &&
				!test(workingSubset, set, c, t, 1, 0, v_r, v_s, weighted, weight) && 
				!test(workingSubset, set, c, t, 0, 1, v_r, v_s, weighted, weight)){
			return false;
		}
		
		return true;
	}
	
	public static boolean test(LinkedList<Long> workingSubset,
			long[] set, long c, int t, int t_1, int t_2, int v_r, int v_s, boolean weighted,
			long weight){
		LinkedList<Integer> valid = new LinkedList<Integer>();
		
		long d1 = set[1]*t - c;
		long d2 = c - set[0] * t;
		
		if(t_1 == 1){
			if(weighted && set[0] == weight) return false;
			valid.add(0);
			d1 += set[0] - set[1];
		}
		if(t_2 == 1){
			if(weighted && set[1] == weight) return false;
			valid.add(1);
			d2 += set[0] - set[1];
		}
		if(v_r == 1){
			if(weighted && set[2] == weight) return false;
			valid.add(2);
			d1 += set[2] - set[1];
			d2 -= set[2] - set[0];
		}
		if(v_s == 1){
			if(weighted && set[3] == weight) return false;
			valid.add(3);
			d1 += set[3] - set[1];
			d2 -= set[3] - set[0];
		}
		
		long testSum = 0;
		for(int i : valid){
			testSum += set[i];
			if(testSum == c){
				for(int j : valid)
					workingSubset.add(set[j]);
				return true;
			}
		}
		
		if(d1 == 0 || d2 == 0)
			return false;
		
		// No doubles needed!  Watch out for Long.MAX_VALUE, though!
		// Space required is polynomial; however, for values near 2^63,
		// with a constant of 2, d1d2 will appear beyond LONG_MAX.
		long s1 = 0;
		long s2 = 0;
		long d1d2 = d1*d2;
		int sgn = d1d2 < 0 ? -1 : 1;
		int sgn_d1 = d1 < 0 ? -1 : 1;
		int sgn_d2 = d2 < 0 ? -1 : 1;
		
		d1d2*= sgn;
		d1 *= sgn_d1;
		d2 *= sgn_d2;
		
		//{ -1, 0, + 1 } if a < b, a = b, or a > b
		for(int i = 4; i < set.length; i++){
			if(set[i] == weight && weighted) continue;
			long dir1 = (set[1] - set[i])*d2*sgn_d1;
			long dir2 = (set[i] - set[0])*d1*sgn_d2;
			long dir3 = Math.abs(dir1 - dir2);
			//if(dir1 < 0 || dir2 >= d1d2 || dir2 < 0)
				//continue;
			if(dir3 < d1d2 && dir3 > 0){
				valid.add(i);
				s1 += dir1;
				s2 += dir2;
				if(s1 == d1d2 && s2 == d1d2){
					for(int j : valid){
						workingSubset.add(set[j]);
					}
					return true;
				}
			}
		}
		
		return false;
	}
}
