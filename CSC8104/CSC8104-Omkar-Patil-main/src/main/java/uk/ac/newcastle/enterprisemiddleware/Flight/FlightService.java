package uk.ac.newcastle.enterprisemiddleware.Flight;

import uk.ac.newcastle.enterprisemiddleware.Customer.Customer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

@Dependent
public class FlightService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    FlightValidator validator;

    @Inject
    FlightRep crud;

    List<Flight> findAllOrdered() {
        return crud.findAllOrdered();
    }

   public Flight findById(Long id) {
        return crud.findById(id);
    }


    public Flight create(Flight flight) throws Exception {
        log.info(String.format("FlightService.create() - Creating %s", flight.getFlight_number()));

        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateFlight(flight);



        // Write the contact to the database.
        return crud.create(flight);
    }
    Flight delete(Flight flight) throws Exception {
        log.info("delete() - Deleting " + flight.toString());

        Flight deletedFlight = null;

        if (flight.getId() != null) {
            deletedFlight = crud.delete(flight);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedFlight;
    }


}
