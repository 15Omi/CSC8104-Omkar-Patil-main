package uk.ac.newcastle.enterprisemiddleware.Customer;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.Cache;
import uk.ac.newcastle.enterprisemiddleware.Booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.Booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.Flight.Flight;
import uk.ac.newcastle.enterprisemiddleware.area.InvalidAreaCodeException;
import uk.ac.newcastle.enterprisemiddleware.contact.UniqueEmailException;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/customer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    CustomerService service;



    @GET
    @Operation(summary = "Fetch all Customers", description = "Returns a JSON array of all stored Customer objects.")
    public Response retrieveAllCustomer() {       //Create an empty collection to contain the intersection of Customer to be returned
        List<Customer> customers;


        customers = service.findAllOrderedByName();

        return Response.ok(customers).build();

    }

    @GET
    @Cache
    @Path("/email/{email:.+[%40|@].+}")
    @Operation(
            summary = "Fetch a Contact by Email",
            description = "Returns a JSON representation of the Contact object with the provided email."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Contact found"),
            @APIResponse(responseCode = "404", description = "Contact with email not found")
    })
    public Response retrieveContactsByEmail(
            @Parameter(description = "Email of Customer to be fetched", required = true)
            @PathParam("email")
            String email) {

        Customer customer;
        try {
            customer = service.findByEmail(email);
        } catch (NoResultException e) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Customer with the email " + email + " was found!", Response.Status.NOT_FOUND);
        }
        return Response.ok(customer).build();
    }

    @GET
    @Cache
    @Path("/{id:[0-9]+}")
    @Operation(
            summary = "Fetch a Customer by id",
            description = "Returns a JSON representation of the Customer object with the provided id."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Customer found"),
            @APIResponse(responseCode = "404", description = "Customer with id not found")
    })
    public Response retrieveContactById(
            @Parameter(description = "Id of Customer to be fetched")
            @Schema(minimum = "0", required = true)
            @PathParam("id")
            long id) {

        Customer customer = service.findById(id);
        if (customer == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Customer with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Customer = " + customer);

        return Response.ok(customer).build();
    }

    @POST
    @Operation(description = "Add a new Customer to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Customer supplied in request body"),
            @APIResponse(responseCode = "409", description = "Customer supplied in request body conflicts with an existing Customer"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createCustomer(
            @Parameter(description = "JSON representation of Contact object to be added to the database", required = true)
            Customer customer) {



        if (customer == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Clear the ID if accidentally set
            customer.setId(null);

            // Go add the new Contact.
            service.create(customer);

            // Create a "Resource Created" 201 Response and pass the contact back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(customer);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (UniqueEmailException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
        } catch (InvalidAreaCodeException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("area_code", "The telephone area code provided is not recognised, please provide another");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("createCustomer completed. Customer = " + customer);
        return builder.build();
    }


    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a Contact from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The contact has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid Contact id supplied"),
            @APIResponse(responseCode = "404", description = "Contact with id not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteContact(
            @Parameter(description = "Id of Contact to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Customer customer = service.findById(id);

        if (customer == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Contact with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }


        try {
            service.delete(customer);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deleteCustomer completed. Customer = " + customer);
        return builder.build();
    }

}


