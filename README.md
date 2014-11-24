Subset Sum Problem, Polynomial Algorithm
===================

In this repo you'll find the fruits of an on-going effort to implement a polynomial-time algorithm to solve the subset sum problem.  I haven't put any effort into formal proofs yet; however, the algorithm has held its own against its exponential counterpart from n = 1 to 20, 10,000,000 trials each with a 100% success rate, and I've also reduced and solved quite a few 3-SAT problems using this implementation of the algorithm.

About this project:
I think it works; I haven't proven anything.  No warranties.
The current big-O runtime is O(n^5); however, I'm applying an alternative to a more brutish method running in O(n^8).  (Code for the n^8 method is included, commented out with instructions if you want to see how it feels to watch a Java console for minutes on end.)

If you use the code or the algorithm, great!  Some credit will be nice.  If you break it, even better!  Contact me to let me know how you did it.  (Note: worst case at n = 200 is 200^5 (neglecting lower-order terms and constants), so if it feels like it's hanging for a large input, it's probably not: it's just your computer attempting to execute some multiple of 200^5 operations.)

Coming soon:
-Improvements
-Coherent comments and explanations.
-Fixes when/if people break it.

Questions?  Comments?  Suggestions?  Feel free to email me (aubrey.alston.1@gmail.com).



