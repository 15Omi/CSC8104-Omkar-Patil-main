package uk.ac.newcastle.enterprisemiddleware.GuestBooking;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.Booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.Customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.Customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.Flight.FlightService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/guestbooking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GuestBookingRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingService bookingService;

    @Inject
    CustomerService customerService;

    @Inject
    FlightService flightService;

    @Inject
    UserTransaction userTransaction;


    @POST
    @Operation(summary = "Add a new booking")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Flight created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Flight supplied in request body"),
            @APIResponse(responseCode = "409", description = "Flight supplied in request body conflicts with an existing Flight"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })

    public Response GuestBooking(@Parameter(description = "JSON representation of booking  object to be added to the database") GuestBooking guestBooking) throws SystemException {

        if (guestBooking == null){
            throw  new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            userTransaction.begin();
            guestBooking.getCustomer().setId(null);
            customerService.create(guestBooking.getCustomer());
            guestBooking.getBooking().setCustomer(guestBooking.getCustomer());
            guestBooking.getBooking().getFlight().setId(null);
            guestBooking.getBooking().setId(null);
            flightService.create(guestBooking.getBooking().getFlight());
            bookingService.create(guestBooking.getBooking());
            builder=Response.status(Response.Status.CREATED);
            userTransaction.commit();

        } catch (Exception e) {
             userTransaction.rollback();
            throw new RestServiceException(e);
        }

      return  builder.build();
    }











}
