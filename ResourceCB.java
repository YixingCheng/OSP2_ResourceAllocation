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
    protected static Hashtable<ThreadCB, RRB> threadRRBTable;             //use a hash table as the data structure for implement banks's algorithm
    protected static RRB nullRRB;                                         // a null RRB object for place holder
    //protected static Vector<ThreadCB> vector;
    protected static int resourceNum;                                     // number of resource type

    /**
       Creates a new ResourceCB instance with the given number of 
       available instances. This constructor must have super(qty) 
       as its first statement.

       @OSPProject Resources
    */
    public ResourceCB(int qty)
    {
        // your code goes here
       super(qty);                                                       //number of instance of this resource
    
    }

    /**
       This method is called once, at the beginning of the
       simulation. Can be used to initialize static variables.

       @OSPProject Resources
    */
    public static void init()
    {
        // your code goes here
       threadRRBTable = new Hashtable<ThreadCB, RRB>();                   //initialize hashtable and null RRB object
       nullRRB = new RRB(null, null, 0);
       //vector = new Vector<ThreadCB>();
       resourceNum = ResourceTable.getSize();
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

       TaskCB currentTask = null;                                                                 //get the requesting thread
       ThreadCB currentThread = null;

       try {
          currentTask = MMU.getPTBR().getTask();                                            
          currentThread = currentTask.getCurrentThread();
        } catch (NullPointerException e){        
         }

       if( (quantity + this.getAllocated(currentThread) > this.getTotal())){                     //if the request + allocated is more than total, return null
              return null;
           }

       if(!threadRRBTable.containsKey(currentThread)){                                           //if the requesting thread is not in hashtable, put it in the hashtable
             threadRRBTable.put(currentThread, nullRRB); 
          } 

       request = new RRB(currentThread, this, quantity);       

       //here we determine which deadlock strategy we employ
       int deadlockStrat = getDeadlockMethod();
       
       switch(deadlockStrat){
                  case Detection:
                         //   deadlock detection strategy
                         break;
                  case Avoidance:
                         //  need to fix here
                         if(isGrant(request) == Granted){
                                 request.grant();
                             }                   
      
                         if((request.getStatus() == Suspended) && (!threadRRBTable.containsValue(request))){
                                 threadRRBTable.put(currentThread, request);
                            }
                       
                      //   RRBStatus = isGrant(request);
                      //   switch(RRBStatus){
                      //          case Granted:
                      //             request.grant();
                      //             break;
                      //          case Suspended:
                      //             if(!threadRRBTable.containsValue(request)){
                      //                    threadRRBTable.put(currentThread, request);
                      //                 }
                      //          default:
                      //             break;
                      //       }
                         break;
                  default: break;
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

       return null;
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
        if(!threadRRBTable.containsKey(thread))
               return;

        for(int resourceID = 0; resourceID < resourceNum; resourceID++ ){
                 ResourceCB checkResource = ResourceTable.getResourceCB(resourceID);
                 if(checkResource.getAllocated(thread) != 0){
                        checkResource.setAvailable(checkResource.getAvailable() + checkResource.getAllocated(thread));
                    }
                 checkResource.setAllocated(thread, 0);
           }

        threadRRBTable.remove(thread);

        Collection<RRB> suspendedThreads = threadRRBTable.values();
        Iterator<RRB> threadIterator = suspendedThreads.iterator();
 
        
        while(threadIterator.hasNext()){
                RRB checkRRB = threadIterator.next();
                if(bankerAlgo(checkRRB)){
                       checkRRB.setStatus(Granted);
                       //System.out.println("debug");
                       ThreadCB checkThread = checkRRB.getThread();
                       if (checkThread != null){
                           if (checkThread.getStatus() != ThreadKill){
                                  checkRRB.grant();
                                //  System.out.println("debug");
                                  threadRRBTable.put(checkThread, nullRRB);
                            }
                         }
                        
                  }
         }

    }

    /**
        Release a previously acquired resource.

	@param quantity

        @OSPProject Resources
    */
    public void do_release(int quantity)
    {
        // your code goes here
        TaskCB currentTask = null;                                                                 //get the requesting thread
        ThreadCB currentThread = null;

        try {
            currentTask = MMU.getPTBR().getTask();                                            
            currentThread = currentTask.getCurrentThread();
           } catch (NullPointerException e){        
          }

        int allocatedInst = this.getAllocated(currentThread);
        if (quantity > allocatedInst){
              quantity = allocatedInst;
          }
        this.setAllocated(currentThread, allocatedInst - quantity);
        this.setAvailable(this.getAvailable() + quantity);
        
        Collection<RRB> suspendedThreads = threadRRBTable.values();
        Iterator<RRB> threadIterator = suspendedThreads.iterator();
 
        while(threadIterator.hasNext()){
                RRB checkRRB = threadIterator.next();
                if(bankerAlgo(checkRRB)){
                       checkRRB.setStatus(Granted);
                      // System.out.println("debug");
                       ThreadCB checkThread = checkRRB.getThread();
                       if (checkThread != null){
                           if (checkThread.getStatus() != ThreadKill){
                                  checkRRB.grant();
                                //  System.out.println("debug");
                                  threadRRBTable.put(checkThread, nullRRB);
                            }
                         }
                        
                  }
         }
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
    private static int isGrant(RRB request){
         
         ThreadCB requestThread = request.getThread();                                                //get the requesting thread
         ResourceCB requestResource = request.getResource();
         int requestNeed, max, availableInst;

         requestNeed = requestResource.getAllocated(requestThread) + request.getQuantity();
         max = requestResource.getMaxClaim(requestThread);

         availableInst = requestResource.getAvailable();

         if(requestNeed > max){                                                                      //deny request if need more than MAX 
                request.setStatus(Denied);
                return Denied;
           }
                                                                                                    //if the thread is requesting more than what currently have, suspend it and put it on the waiting queue of this RRB, also set status of this RRB to suspended
         if(request.getQuantity() > availableInst){
              if( (requestThread.getStatus() != ThreadWaiting) && (request.getStatus() != Suspended)){
                     requestThread.suspend(request);
                 }

              if(!threadRRBTable.containsValue(request)){
                     threadRRBTable.put(requestThread, request);
                 } 
             
              request.setStatus(Suspended);
              return Suspended;                 
           }

         if(bankerAlgo(request)){
               request.setStatus(Granted);
               return Granted;
           }          
         else{
             if((requestThread.getStatus() != ThreadWaiting) && (requestThread.getStatus() != ThreadKill) && (request.getStatus() != Suspended))                {
                   requestThread.suspend(request);

                   if(threadRRBTable.containsValue(request)){
                           threadRRBTable.put(requestThread, request); 
                      } 
                   
                   request.setStatus(Suspended);
                   return Suspended;
                }
             }

        return Suspended;     
    }

    private static boolean bankerAlgo(RRB request){

            //ThreadCB requestThread = request.getThread();
    
            int[] availableInstArray = new int[resourceNum];
            for(int i = 0; i < resourceNum; i++){
                  availableInstArray[i] = ResourceTable.getResourceCB(i).getAvailable();
               }            

            Enumeration<ThreadCB> enumThreads = threadRRBTable.keys();
            Vector<ThreadCB> threadVector = new Vector<ThreadCB>();
            
            while(enumThreads.hasMoreElements()){
                   threadVector.addElement(enumThreads.nextElement());
               }
            
            boolean goodThread = true;
            
            while(!threadVector.isEmpty()){
                  
                   int size = threadVector.size();
                   for(int threadID = 0; threadID < threadVector.size(); threadID++ ){
                    
                         ThreadCB checkThread = threadVector.get(threadID);
                         goodThread = true;
                   
                         for(int resourceID = 0; resourceID < resourceNum; resourceID++){
                           
                                ResourceCB checkResource = ResourceTable.getResourceCB(resourceID);
                                if(checkResource.getMaxClaim(checkThread)-checkResource.getAllocated(checkThread) >= checkResource.getAvailable()){
                                      goodThread = false;
                                      break;
                                 }
                           }
                         
                         if(goodThread){
                                 for(int i = 0; i < resourceNum; i++){
                                       ResourceCB checkResource = ResourceTable.getResourceCB(i);
                                       checkResource.setAvailable(checkResource.getAllocated(checkThread) + checkResource.getAvailable()); 
                                     }
 
                                threadVector.remove(checkThread);
                                break;
                             }
                      }

                    if(size == threadVector.size()){
                           for(int i = 0; i < resourceNum; i++){
                                   ResourceTable.getResourceCB(i).setAvailable(availableInstArray[i]); 
                               }
                           return false;
                      }
              }

           for(int i = 0; i < resourceNum; i++){
                    ResourceTable.getResourceCB(i).setAvailable(availableInstArray[i]); 
               }
           return true;
       }
    
}

/*
      Feel free to add local classes to improve the readability of your code
*/
