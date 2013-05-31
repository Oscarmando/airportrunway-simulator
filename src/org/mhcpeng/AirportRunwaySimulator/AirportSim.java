//*******************************************************
// AirportSim.java		Author: Michael Peng
//
// Represents Airport Simulation
//*******************************************************

package org.mhcpeng.AirportRunwaySimulator;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;

public class AirportSim {
	private static final int MINTUES_PER_HOUR = 60;
	private static final int PLANE_ARRIVAL_HOURLY_AVG = 8;
	private static final int PLANE_DEPART_HOURLY_AVG = 8;
	private static final int TIME_TO_LAND = 2;
	private static final int TIME_TO_DEPART = 3;
	
	private int arrivalCount = 0;
	private int departureCount = 0;
	private float avgArriveWait = 0;
	private float avgDepartWait = 0;
	private int maxWaitingToDepart = 0;
	private int maxWaitingToLand = 0;
	private int totalDepartingWaitTime = 0;
	private int totalLandingWaitTime = 0;
	private int planeNumber = 1;
	private int planeArrivalNumber;
	private int planeDepartNumber;
	private int waitingToLand = 0;
	private int waitingToDepart = 0;
	private int simPeriod;
	private int runwayFree = 0;
	
	private int minute;
	
	private String planeEvent;
	private String outputPlane;
	private String onRunway;
	private boolean planeHasArrived = false;
	private String departEvent = "";
	private Queue<Integer> landingQueue = new LinkedList<Integer>();
	private Queue<Integer> departingQueue = new LinkedList<Integer>();
	
	AirportSim(){
		simPeriod = 480;
	}
	
	public void run(){
		
		System.out.printf(" Time plane  event       ");
		System.out.printf("runway   waiting   waiting");
		System.out.printf("    num     num     runway\n");
		
		System.out.printf("        #                ");
		System.out.printf("status   to land   to depart");
		System.out.printf("  arriv   dpart    free\n");
		
		for( minute = 1; minute<=simPeriod; minute++){
			
			planeEvent = new String();
			outputPlane = new String();
			onRunway = new String();
			departEvent="";
			planeHasArrived=false;
			
			if(planeArrives()){				
				waitingToLand++;
				planeEvent = "#" + planeNumber + " lands";
				planeArrivalNumber = planeNumber;
				outputPlane = String.valueOf(planeNumber);
				landingQueue.add(planeNumber);
				planeNumber++;
				planeHasArrived = true;
			}
			if(planeDeparts()){
				waitingToDepart++;
				if(planeHasArrived){
					// create a string message for this
					// event. Print it in showResultMinute
					// if it is non-blank.
					StringBuilder sb = new StringBuilder();
					Formatter formatter = new Formatter(sb);
					formatter.format(" %3d %4s  #%3s\n", minute,
								planeNumber, planeNumber + " departs");
					departEvent = formatter.toString();
				}else{
					planeEvent = "#" + planeNumber + " departs";
				}
				planeDepartNumber = planeNumber;
				outputPlane = String.valueOf(planeNumber);
				departingQueue.add(planeNumber);
				planeNumber++;
			}			
			
			if(minute >= runwayFree){
					
				if(waitingToLand > 0){
					waitingToLand--;
					runwayFree = minute + TIME_TO_LAND;
					arrivalCount++;
					planeArrivalNumber = landingQueue.poll();
					onRunway = planeArrivalNumber + "L";
				}else if (waitingToDepart > 0){
					waitingToDepart--;
					runwayFree = minute + TIME_TO_DEPART;
					departureCount++;
					planeDepartNumber = departingQueue.poll();
					onRunway = planeDepartNumber + "D";
				}
			
			}else{
				totalLandingWaitTime = totalLandingWaitTime + waitingToLand;
				totalDepartingWaitTime = totalDepartingWaitTime + waitingToDepart;
			}
			
			
			
			if(maxWaitingToLand < waitingToLand){
				maxWaitingToLand = waitingToLand;
			}
			if(maxWaitingToDepart < waitingToDepart){
				maxWaitingToDepart = waitingToDepart;
			}
			
			showMinuteResults();
		}
		showEndSimResults();
	}
	
	/* Return a boolean value indicating whether or
	 * not a plane has arrived for this minute.
	 * 
	 * This is a static method because the constants
	 * it uses are static.
	 * 
	 * @return true if a plane arrives for landing
	 */
	private static boolean planeArrives(){
		return (Math.random()* MINTUES_PER_HOUR 
				< PLANE_ARRIVAL_HOURLY_AVG);
	}
	/* Return a boolean value indicating whether or
	 * not a plane has arrived for this minute.
	 * 
	 * This is a static method because the constants
	 * it uses are static.
	 * 
	 * @return true if a plane ready for departing
	 */	
	private static boolean planeDeparts(){
		return (Math.random()* MINTUES_PER_HOUR 
				< PLANE_DEPART_HOURLY_AVG); 
		
	}
	
	public void showMinuteResults(){
		
		System.out.printf(" %3d  %3s", minute, outputPlane);
		System.out.printf("  %-12s",    planeEvent);
		System.out.printf("%5s",        onRunway);
		System.out.printf("        %d", waitingToLand);
		System.out.printf("          %d", waitingToDepart);
		System.out.printf("%9d", arrivalCount);
		System.out.printf("%8d", departureCount);
		System.out.printf("%9d", runwayFree);
		System.out.println("");
		/* If a plane departs this minute as well, print the event
		 * after the message about the plane arriving.
		 */
		if(departEvent !=""){
			System.out.print( departEvent);
		}
		
	}
	
	
	
	private void showEndSimResults(){
		avgArriveWait = ((float)totalLandingWaitTime / (float)arrivalCount);
		avgDepartWait = ((float)totalDepartingWaitTime / (float)departureCount);
		System.out.println("Simulation length: " + (simPeriod/MINTUES_PER_HOUR)+ " hours");
		System.out.println("Average rate of arriving plane is " + PLANE_ARRIVAL_HOURLY_AVG +" / hour");
		System.out.println("Average rate of departing plane is "+ PLANE_DEPART_HOURLY_AVG +" / hour" );
		System.out.println("Time for a plane to land is " + TIME_TO_LAND + " mins");
		System.out.println("Time for a plane to depart is " + TIME_TO_DEPART + " mins");
		System.out.println("Number of planes landed is " + arrivalCount);
		System.out.println("Number of planes departed is " + departureCount);
		System.out.println("Average wait time for arrivals is " + avgArriveWait + " mins");
		System.out.println("Average wait time for departure is " + avgDepartWait + " mins");
		System.out.println("Maximum number of planes waiting to depart is "+ maxWaitingToDepart);
		System.out.println("Maximum number of planes waiting to land is "+ maxWaitingToLand);
		
	}
	
}
