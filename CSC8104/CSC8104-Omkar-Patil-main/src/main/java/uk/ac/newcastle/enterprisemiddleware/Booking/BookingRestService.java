package uk.ac.newcastle.enterprisemiddleware.Booking;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.hibernate.engine.query.spi.ParamLocationRecognizer;
import uk.ac.newcastle.enterprisemiddleware.Customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.Customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.Flight.Flight;
import uk.ac.newcastle.enterprisemiddleware.Flight.FlightService;
import uk.ac.newcastle.enterprisemiddleware.area.InvalidAreaCodeException;
import uk.ac.newcastle.enterprisemiddleware.contact.UniqueEmailException;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



@Path("/booking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingService bookingService;

    @Inject
    CustomerService customerService;

    @Inject
    FlightService flightService;

    @GET
    @Operation(summary = "Fetch all Customers", description = "Returns a JSON array of all stored Booking objects.")
    public Response retrieveAllBooking() {       //Create an empty collection to contain the intersection of Contacts to be returned
        List<Booking> bookings;


        bookings = bookingService.findAllOrderedByName();

        return Response.ok(bookings).build();
    }

    @POST
    @Operation(description = "Add a new Customer to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Bookkingcreated successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Booking supplied in request body"),
            @APIResponse(responseCode = "409", description = "Booking supplied in request body conflicts with an existing Booking"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createBooking(
            @Parameter(description = "JSON representation of Contact object to be added to the database", required = true)
            Booking booking) {

        Customer customer=customerService.findById(booking.getCustomer().getId());
        Flight flight= flightService.findById((booking.getFlight().getId()));

        if (booking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        } else if (customer == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        } else if (flight == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        booking.setId(null);
        booking.setCustomer(customer);
        booking.setFlight(flight);
        Response.ResponseBuilder builder;
        try {

            bookingService.create(booking);

            // Create a "Resource Created" 201 Response and pass the contact back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);


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

        log.info("createBooking completed. booking = " + booking);
        return builder.build();
    }

    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a booking from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The booking has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid booking id supplied"),
            @APIResponse(responseCode = "404", description = "Booking with id not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteBooking(
            @Parameter(description = "Id of Contact to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Booking booking = bookingService.findById(id);
        if (booking == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Contact with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }


        try {
            bookingService.delete(booking);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deleteCustomer completed. Customer = " + booking);
        return builder.build();
    }

}

