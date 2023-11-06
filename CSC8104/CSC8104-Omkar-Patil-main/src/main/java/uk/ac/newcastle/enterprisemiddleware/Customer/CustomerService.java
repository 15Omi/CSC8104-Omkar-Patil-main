package uk.ac.newcastle.enterprisemiddleware.Customer;



import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Dependent
public class CustomerService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    CustomerValidator validator;

    @Inject
    CustomerRep crud;
    List<Customer> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    Customer findByEmail(String email) {
        return crud.findByEmail(email);
    }

    public Customer findById(Long id) {
        return crud.findById(id);
    }

   public Customer create(Customer customer) throws Exception {
        log.info("CustomerService.create() - Creating " + customer.getName());

        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateCustomer(customer);



        // Write the contact to the database.
        return crud.create(customer);
    }

    Customer delete(Customer customer) throws Exception {
        log.info("delete() - Deleting " + customer.toString());

        Customer deletedCustomer = null;

        if (customer.getId() != null) {
            deletedCustomer = crud.delete(customer);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedCustomer;
    }

}
