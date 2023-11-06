package uk.ac.newcastle.enterprisemiddleware.Booking;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;



@Dependent
public class BookingService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingValodator valodator;

    @Inject
    BookingRep crud;


    public Booking findById(Long id) {
        return crud.findById(id);
    }
    List<Booking> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    public Booking create(Booking booking) throws Exception {
        log.info("BookingService.create() - Creating " + booking.getId());

        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        valodator.validateBooking(booking);



        // Write the contact to the database.
        return crud.create(booking);
    }


    Booking delete(Booking booking) throws Exception {
        log.info("delete() - Deleting " + booking.toString());

      Booking deletedBooking = null;

        if (booking.getId() != null) {
            deletedBooking = crud.delete(booking);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedBooking;
    }


}