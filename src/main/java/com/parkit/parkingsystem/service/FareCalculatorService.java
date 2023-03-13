package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    private final TicketDAO ticketDAO;

    public FareCalculatorService(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = Math.round(((outHour - inHour) / 3600000.0) * 100.0) / 100.0;


        if (duration <= 0.5) {
            ticket.setPrice(0);
        }else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    // if the reg number exist already in the database the user get à 5 % discount

                    if(ticketDAO.getRegNumber(ticket.getVehicleRegNumber())){
                        ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR - (duration * Fare.CAR_RATE_PER_HOUR / 100) * 5);
                        break;
                    }else{
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    break;
                    }
                }
                case BIKE: {
                    // if the reg number exist already in the database the user get à 5 % discount
                    if (ticketDAO.getRegNumber(ticket.getVehicleRegNumber())){
                        ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR - (duration * Fare.BIKE_RATE_PER_HOUR / 100) * 5);
                        break;
                    }else {
                        ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                        break;
                    }
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}