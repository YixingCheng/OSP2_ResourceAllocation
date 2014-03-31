/*
 *   CSCE311  Proj3 Resource 
 *   University of South Carolina
 *   authoer: Yixing Cheng
 *   date: Mar/31/2014
 *   ResourceCB.java
 */


package osp.Resources;

import java.util.*;
import osp.IFLModules.*;
import osp.Tasks.*;
import osp.Threads.*;
import osp.Utilities.*;
import osp.Memory.*;

/**
    Class ResourceCB is the core of the resource management module.
    Students implement all the do_* methods.
    @OSPProject Resources
*/
public class ResourceCB extends IflResourceCB
{
    private static Hashtable<ThreadCB, RRB> threadRRBTable;
    private static RRB nullRRB;

    /**
       Creates a new ResourceCB instance with the given number of 
       available instances. This constructor must have super(qty) 
       as its first statement.

       @OSPProject Resources
    */
    public ResourceCB(int qty)
    {
        // your code goes here
       super(qty);
    
    }

    /**
       This method is called once, at the beginning of the
       simulation. Can be used to initialize static variables.

       @OSPProject Resources
    */
    public static void init()
    {
        // your code goes here
       threadRRBTable = new Hashtable<ThreadCB, RRB>();
       nullRRB = new RRB(null, null, 0);
    }

    /**
       Tries to acquire the given quantity of this resource.
       Uses deadlock avoidance or detection depending on the
       strategy in use, as determined by ResourceCB.getDeadlockMethod().

       @param quantity
       @return The RRB corresponding to the request.
       If the request is invalid (quantity+allocated>total) then return null.

       @OSPProject Resources
    */
    public RRB  do_acquire(int quantity) 
    {
        // your code goes here
       RRB request;

       TaskCB currentTask = null;                                                                 //get the currently running thread
       ThreadCB currentThread = null;
       try {
          currentTask = MMU.getPTBR().getTask();
          currentThread = currentTask.getCurrentThread();
        } catch (NullPointerException e){
        
         }

       if( (quantity + this.getAllocated(currentThread) > this.getTotal())){
               return null;
           }
       else{
           if(threadRRBTable.containsKey(currentThread)){
 
               } 
           else{
              threadRRBTable.put(currentThread, nullRRB);
              }

            request = new RRB(currentThread, this, quantity);
          }       


       return request;
    }

    /**
       Performs deadlock detection.
       @return A vector of ThreadCB objects found to be in a deadlock.

       @OSPProject Resources
    */
    public static Vector do_deadlockDetection()
    {
        // your code goes here
       Vector vector = new Vector();

       return vector;
    }

    /**
       When a thread was killed, this is called to release all
       the resources owned by that thread.

       @param thread -- the thread in question

       @OSPProject Resources
    */
    public static void do_giveupResources(ThreadCB thread)
    {
        // your code goes here

    }

    /**
        Release a previously acquired resource.

	@param quantity

        @OSPProject Resources
    */
    public void do_release(int quantity)
    {
        // your code goes here

    }

    /** Called by OSP after printing an error message. The student can
	insert code here to print various tables and data structures
	in their state just after the error happened.  The body can be
	left empty, if this feature is not used.
	
	@OSPProject Resources
    */
    public static void atError()
    {
        // your code goes here

    }

    /** Called by OSP after printing a warning message. The student
	can insert code here to print various tables and data
	structures in their state just after the warning happened.
	The body can be left empty, if this feature is not used.
     
	@OSPProject Resources
    */
    public static void atWarning()
    {
        // your code goes here

    }


    /*
       Feel free to add methods/fields to improve the readability of your code
    */
    private boolean decideGrant(){
         

     }

}

/*
      Feel free to add local classes to improve the readability of your code
*/
