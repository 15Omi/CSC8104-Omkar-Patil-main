package uk.ac.newcastle.enterprisemiddleware.Flight;

import uk.ac.newcastle.enterprisemiddleware.Customer.Customer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;
@RequestScoped
public class FlightRep {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    List<Flight> findAllOrdered() {
        TypedQuery<Flight> query = em.createNamedQuery(Flight.FIND_ALL, Flight.class);
        return query.getResultList();
    }


    Flight findById(Long id) { return em.find(Flight.class, id);
    }


    Flight create(Flight flight) throws Exception {
        log.info(String.format("FlightRepository.create() - Creating %s", flight.getFlight_number()));

        // Write the contact to the database.
        em.persist(flight);

        return flight;

    }
    Flight delete(Flight flight) throws Exception {
        log.info("FlightRepository.delete() - Deleting " + flight.getFlight_number());

        if (flight.getId() != null) {
            /*
             * The Hibernate session (aka EntityManager's persistent context) is closed and invalidated after the commit(),
             * because it is bound to a transaction. The object goes into a detached status. If you open a new persistent
             * context, the object isn't known as in a persistent state in this new context, so you have to merge it.
             *
             * Merge sees that the object has a primary key (id), so it knows it is not new and must hit the database
             * to reattach it.
             *
             * Note, there is NO remove method which would just take a primary key (id) and a entity class as argument.
             * You first need an object in a persistent state to be able to delete it.
             *
             * Therefore we merge first and then we can remove it.
             */
            em.remove(em.merge(flight));

        } else {
            log.info("FlightRepository.delete() - No ID was found so can't Delete.");
        }

        return flight;
    }
}
