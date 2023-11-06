package uk.ac.newcastle.enterprisemiddleware.Booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.Customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.Flight.Flight;
import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
@Entity
@NamedQueries({
        @NamedQuery(name = Booking.FIND_ALL, query = "SELECT c FROM Booking c ORDER BY c.id ASC"),
        @NamedQuery(name = Booking.FIND_BY_ID, query = "SELECT c FROM Booking c WHERE c.id = :id")
})
@XmlRootElement
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_ID = "Booking.findById";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   /* @NotNull
    @Column(name="Customer Id")
    private long customerId;

    @NotNull
    @Column(name="Flight Id")
    private long flightId;

    */

    public Customer getCustomer() {
        return customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @OneToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    private Flight flight;

    /* public long getFlightId() {
        return flightId;
    }

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }




    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
   // @JoinColumn(name="Flight Id")
    private Flight flight;


    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

     */





    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @NotNull
    @Future
    @Column(name = "date")
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
