package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;
   
  
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    @BeforeEach
    private void setUpForTest() {
    	parkingService= new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    	
    	
    }	 
		   
	@Test
    public void processIncomingVehicleSaveTicketTest(){ 	
    	try {
    		when(inputReaderUtil.readSelection()).thenReturn(1);
    		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
    		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("PEIO");
    		parkingService=new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);    		
    		parkingService.processIncomingVehicle();
    		verify(ticketDAO).saveTicket(any(Ticket.class));
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    			fail("Failed to set to set up test mock ticket");
    		}
   }
    
    @Test
    public void getNextParkingNumberIfAvailablePassTest () throws Exception {
    	when(inputReaderUtil.readSelection()).thenReturn(2);
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(3);
    	ParkingSpot spot = parkingService.getNextParkingNumberIfAvailable();
    	assertEquals(spot, new ParkingSpot(3, ParkingType.BIKE,true));
    }	
    
    @Test
    public void processExitingVehicleTrueTest(){
    	try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();
            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    		}
    		catch (Exception e) {
    			e.printStackTrace();
            	throw  new RuntimeException("Failed to set up test mock objects");
         }
    }
    	
    @Test
    public void processExitingVehicleFalseTest(){
    	try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();
            verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    			fail("test not working");
            	throw  new RuntimeException("Failed to set up test mock objects");
         }
      }
    }
