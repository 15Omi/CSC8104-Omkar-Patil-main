package uk.ac.newcastle.enterprisemiddleware.Customer;

import uk.ac.newcastle.enterprisemiddleware.Customer.CustomerRep;
import uk.ac.newcastle.enterprisemiddleware.contact.Contact;
import uk.ac.newcastle.enterprisemiddleware.contact.UniqueEmailException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class CustomerValidator {

    @Inject
    Validator validator;

    @Inject
    CustomerRep crud;

    void validateCustomer(Customer customer) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
       /* if (emailAlreadyExists(customer.getEmail(), customer.getId())) {
            throw new UniqueEmailException("Unique Email Violation");
        }

        */


    }

    boolean emailAlreadyExists(String email, Long id) {
        Customer customer = null;
        Customer customerWithID = null;
        try {
            customer = crud.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }

        if (customer != null && id != null) {
            try {
                customerWithID = crud.findById(id);
                if (customerWithID != null && customerWithID.getEmail().equals(email)) {
                    customerWithID = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return customer != null;

    }
}
