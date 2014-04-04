/*
 *   CSCE311  Proj3 Resource 
 *   University of South Carolina
 *   authoer: Yixing Cheng
 *   date: Mar/31/2014
 *   email: cheng26@email.sc.edu
 *   ResourceCB.java
 *   
 *   if you find the code useful, please do me a favor
 *   go to my Linkedin page: https://www.linkedin.com/profile/view?id=106078871&trk=nav_responsive_tab_profile
 *   add endorse my Java skill
 *   Thanks!
 */


package osp.Resources;

import java.util.*;

import osp.Utilities.*;
import osp.IFLModules.*;
import osp.Threads.*;

/**
   The studends module for dealing with resource management. The methods 
   that have to be implemented are do_grant().

   @OSPProject Resources
*/

public class RRB extends IflRRB
{
    /** 
        constructor of class RRB 
        Creates a new RRB object. This constructor must have
        super() as its first statement.

        @OSPProject Resources
    */   
    public RRB(ThreadCB thread, ResourceCB resource,int quantity)
    {
       super(thread, resource, quantity);    // your code goes here

    }

    /**
       This method is called when we decide to grant an RRB.
       The method must update the various resource quantities
       and notify the thread waiting on the granted RRB.

        @OSPProject Resources
    */
    public void do_grant()
    {
        // your code goes here
       ThreadCB requestThread = this.getThread();                                        //retrive the thread that is requesting the resource

       ResourceCB requestRes = this.getResource();                                       // retrive which resource type the thread is being issued 

       int newAvailable = requestRes.getAvailable() - this.getQuantity();                //calcuate the new available instance of resource after request

       requestRes.setAvailable(newAvailable);                                            //set the avaiable instance of resource to the new value

       int newAllocated = requestRes.getAllocated(requestThread) + this.getQuantity();   //calcuate the instance of resource after allocation

       requestRes.setAllocated(requestThread, newAllocated);                             //set the new allocated instance of resource
       this.setStatus(Granted);

       this.notifyThreads();       
    }


    /*
       Feel free to add methods/fields to improve the readability of your code
    */

}

/*
      Feel free to add local classes to improve the readability of your code
*/
