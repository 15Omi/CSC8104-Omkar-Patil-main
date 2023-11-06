package uk.ac.newcastle.enterprisemiddleware.GuestBooking;

import uk.ac.newcastle.enterprisemiddleware.Booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.Customer.Customer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

public class GuestBooking implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull
    private Customer customer;


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }



    @NotNull
    private Booking booking;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
