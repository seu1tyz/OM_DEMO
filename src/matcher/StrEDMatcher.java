/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Peng Wang
 * Author email       PWangSeu@gmail.com
 * Package            Jena 2.4
 * Web                http://sourceforge.net/projects/jena/
 * Created            12-Aug-2006
 * Filename           $RCSfile: OWLClassParse.java.html,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2006/9/13 09:38:31 $
 *               by   $Author: Peng Wang $
 *
 * (c) Copyright 2006 CS Department, Southeast University
 *****************************************************************************/
package matcher;

/**
 * »ùÓÚ×Ö·û´®±à¼­¾àÀëÏàËÆ¶ÈµÄÆ¥ÅäÆ÷
 * @author seu1tyz
 */

public class StrEDMatcher {
	
	public static int getLevenshteinDistance (String s, String t) {
	
		  if (s == null || t == null) {
		    throw new IllegalArgumentException("Strings must not be null");
		  }	
		  
		  /*
		    The difference between this impl. and the previous is that, rather 
		     than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
		     we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
		     is the 'current working' distance array that maintains the newest distance cost
		     counts as we iterate through the characters of String s.  Each time we increment
		     the index of String t we are comparing, d is copied to p, the second int[].  Doing so
		     allows us to retain the previous cost counts as required by the algorithm (taking 
		     the minimum of the cost count to the left, up one, and diagonally up and to the left
		     of the current cost count being calculated).  (Note that the arrays aren't really 
		     copied anymore, just switched...this is clearly much better than cloning an array 
		     or doing a System.arraycopy() each time  through the outer loop.)
		
		     Effectively, the difference between the two implementations is this one does not 
		     cause an out of memory condition when calculating the LD over two very large strings.  		
		  */				
		  int n = s.length(); // length of s
		  int m = t.length(); // length of t
				
		  if (n == 0) {
		    return m;
		  } else if (m == 0) {
		    return n;
		  }
		
		  int p[] = new int[n+1]; //'previous' cost array, horizontally
		  int d[] = new int[n+1]; // cost array, horizontally
		  int _d[]; //placeholder to assist in swapping p and d
		
		  // indexes into strings s and t
		  int i; // iterates through s
		  int j; // iterates through t
		
		  char t_j; // jth character of t
		
		  int cost; // cost
		
		  for (i = 0; i<=n; i++) {
		     p[i] = i;
		  }
		  
		  for (j = 1; j<=m; j++) {
		     t_j = t.charAt(j-1);
		     d[0] = j;
				
		     for (i=1; i<=n; i++) {
		        cost = s.charAt(i-1)==t_j ? 0 : 1;
		        // minimum of cell to the left+1, to the top+1, diagonally left and up +cost				
		        d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);  
		     }
		
		   // copy current distance counts to 'previous row' distance counts
		     _d = p;
		     p = d;
		     d = _d;
		  } 
				
		  // our last action in the above loop was to switch d and p, so p now 
		  // actually has the most recent cost counts
		  return p[n];
		}

	/*******************************************************************
	 * I adapt the method proposed by LI Yu-jian. In the paper [Normalized 
	 * Distance Metrics Between Symbolic Sequences, Journal of Beijing 
	 * University of Technology, 2005, 31(4)], Liu present a formula, which 
	 * can assure that the distance value is between [0,1].
	 * 
     * The formula is defined as follows:
     * DE(x,y) denotes the edit distance between x and y;
     * SE(x,y)=[|x|+|y|-DE(x,y)]/2 denotes the edit similarity between x and y; 
     * The Normalized Edit Distance DNE(x,y) is:
     * DNE(x,y)=DE(x,y)/[DE(x,y)+SE(x,y)]=[|x|+|y|-2*SE(x,y)]/[|x|+|y|-SE(x,y)].
     * when x and y are empty strings, DNE(x,y)=0. 
	 */	
	public static double getNormEDSim(String source, String target){
		int de, s_len, t_len;
		double se, dne;
		s_len = source.length();
		t_len = target.length();
		
		if (!(s_len==0 && t_len == 0)){
			de = getLevenshteinDistance(source, target);
			se = (double) ((double)(s_len + t_len - de)/2.0);
			dne = (double)de /(de + se);
			dne=1-dne;
			double  temp = (double)(Math.round(dne*1000))/1000;
			return temp;
		}
		else
		{
			return 1.0;
		}
	}
}