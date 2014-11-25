Subset Sum Problem, Polynomial Algorithm
===================

In this repo you'll find the fruits of an on-going effort to implement a polynomial-time algorithm to solve the subset sum problem.  

About this project:
The current big-O runtime is O(n^8).

If you use the code or the algorithm, great!  Some credit will be nice.  If you break it, even better!  Contact me to let me know how you did it.  (Note: worst case at n = 200 is 200^8, which is a pretty big number.  50^8 is a pretty big number.)

Coming soon: Improvements (will transition to BigDecimal rather than double for greater range in inputs), coherent comments and explanations, fixes when/if people break it, and a proof of the method.

UPDATE: 11/25: the experimental method I used to reduce runtime to O(n^5) turned out to be a fluke, causing false negatives.  Reverted back to the old, slow O(n^8) algorithm; false negatives no longer seem to appear.  Going to investigate patterns to see if there is a true way to reduce this O(n^8) runtime.

Questions?  Comments?  Suggestions?  Feel free to email me (aubrey.alston.1@gmail.com).
