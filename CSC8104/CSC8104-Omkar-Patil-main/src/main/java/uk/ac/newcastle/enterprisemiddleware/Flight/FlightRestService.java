package uk.ac.newcastle.enterprisemiddleware.Flight;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.Booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.area.InvalidAreaCodeException;
import uk.ac.newcastle.enterprisemiddleware.contact.UniqueEmailException;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
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

@Path("/flight")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FlightRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    FlightService service;

    @GET
    @Operation(summary = "Fetch all Flights", description = "Returns a JSON array of all stored Contact objects.")
    public Response retrieveAllFlight(@QueryParam("source") String source) {//Create an empty collection to contain the intersection of Contacts to be returned
        List<Flight> flights;


        flights = service.findAllOrdered();

        return Response.ok(flights).build();


    }

    @POST
    @Operation(description = "Add a new Flight to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Flight created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Flight supplied in request body"),
            @APIResponse(responseCode = "409", description = "Flight supplied in request body conflicts with an existing Flight"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createFlight(
            @Parameter(description = "JSON representation of Contact object to be added to the database", required = true)
            Flight flight) {

        if (flight == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Clear the ID if accidentally set
            flight.setId(null);

            // Go add the new Contact.
            service.create(flight);

            // Create a "Resource Created" 201 Response and pass the contact back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(flight);


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

        log.info("createFlight completed. flight = " + flight);
        return builder.build();
    }

    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a Flight from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The Flight has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid Flight with id not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteFlight(
            @Parameter(description = "Id of Flight to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Flight flight = service.findById(id);
        if (flight == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Flight with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(flight);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deleteCustomer completed. Customer = " + flight);
        return builder.build();
    }


}
