package uk.ac.newcastle.enterprisemiddleware.Flight;

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
public class FlightValidator {

    @Inject
    Validator validator;

    @Inject
    FlightRep crud;

    void validateFlight(Flight flight) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Flight>> violations = validator.validate(flight);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }
}
