   package jneat;

   /**
 * 
 * 
 * 
 */
	public class order_species implements java.util.Comparator {
   	  	  /**
   * order_species constructor comment.
   */
	   public order_species() {
	  //super();
	  }
   /**
   */
	   public int compare(Object o1, Object o2) {
	  
		 Species _sx = (Species) o1;
		 Species _sy = (Species) o2;
	  
		 Organism _ox = (Organism) _sx.organisms.firstElement();
		 Organism _oy = (Organism) _sy.organisms.firstElement();

		   return Double.compare(_oy.orig_fitness, _ox.orig_fitness);

	   }
   }