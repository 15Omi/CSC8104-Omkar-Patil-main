package uk.ac.newcastle.enterprisemiddleware.Booking;

import uk.ac.newcastle.enterprisemiddleware.Customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.Customer.CustomerRep;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class BookingValodator {

    @Inject
    Validator validator;

    @Inject
    BookingRep crud;

    void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }


    }
}
