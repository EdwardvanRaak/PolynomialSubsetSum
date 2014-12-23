Subset Sum Problem, Strongly Polynomial Algorithm
===================

In this repo you'll find the fruits of an on-going effort to implement a polynomial-time algorithm to solve the subset sum problem.  

Note: this repo may eventually contain multiple algorithms.  This readme currently applies only to the current method being explored.

About the current method in iquestion:
The current big-O runtime is O(n^7).

RefactoredSubsetSum.java is the (somewhat) optimized implementation of the algorithm developed as the algorithm was analyzed for justification.  This uses the closed forms found in method.pdf.

SubsetSum.java is the original implementation requiring use of matrices and matrix operations.

If you use the code or the algorithm, great!  Some credit will be nice.  If you break it, even better!  Contact me to let me know how you did it.  (Note: worst case at n = 200 is 200^7, which is a pretty big number.)

UPDATE: 12/14: Justification/proof added to method.pdf; tracked code for n^7 implementation using only longs (note, sets containing large (>2^62 in value) will exceed LONG_MAX during execution: each input integer of p bits needs at most 2p bits once reached in the test procedure, so values of such magnitude can exceed long bit length.  Solution is to use BigInteger where this can occur); expanded discussion of complexity in method.pdf.

Questions?  Comments?  Suggestions?  Feel free to email me (aubrey.alston.1@gmail.com).
