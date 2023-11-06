package uk.ac.newcastle.enterprisemiddleware.Booking;


import uk.ac.newcastle.enterprisemiddleware.Customer.Customer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;



@RequestScoped
public class BookingRep {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    List<Booking> findAllOrdered() {
        TypedQuery<Booking> query = em.createNamedQuery(Customer.FIND_ALL, Booking.class);
        return query.getResultList();
    }
    Booking findById(Long id) { return em.find(Booking.class, id);
    }

    List<Booking> findAllOrderedByName() {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class);
        return query.getResultList();
    }

    Booking create(Booking booking) throws Exception {
        log.info("BookingRepository.create() - Creating " + booking.getId());

        // Write the contact to the database.
        em.persist(booking);

        return booking;

    }

    Booking delete(Booking booking) throws Exception {
        log.info("BookingRepository.delete() - Deleting " + booking.getId());

        if (booking.getId() != null) {
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
            em.remove(em.merge(booking));

        } else {
            log.info("BookingRepository.delete() - No ID was found so can't Delete.");
        }

        return booking;
    }

}