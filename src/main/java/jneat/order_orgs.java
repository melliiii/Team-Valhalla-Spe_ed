   package jneat;

/**
 *
 *
 *
 */
	public class order_orgs implements java.util.Comparator {
   	  	  /**
   * order_orgs constructor comment.
   */
	   public order_orgs() {
	  //super();
	  }
   /**
	*/
	   public int compare(Object o1, Object o2) {
	  
	  
		 Organism _ox = (Organism) o1;
		 Organism _oy = (Organism) o2;

		   return Double.compare(_oy.fitness, _ox.fitness);
	   }
   }