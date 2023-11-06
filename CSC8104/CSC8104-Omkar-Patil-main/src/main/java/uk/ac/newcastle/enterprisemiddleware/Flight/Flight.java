package uk.ac.newcastle.enterprisemiddleware.Flight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.Booking.Booking;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = Flight.FIND_ALL, query = "SELECT c FROM Flight c ORDER BY c.flight_number ASC")

})
@XmlRootElement
@Table(name = "Flight", uniqueConstraints = @UniqueConstraint(columnNames = "flight_number"))
public class Flight implements Serializable {

    public static final String FIND_ALL = "Flight.findAll";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy ="flight",cascade = CascadeType.REMOVE)
    private List<Booking> booking;

    public List<Booking> getBooking() {
        return booking;
    }

    public void setBooking(List<Booking> booking) {
        this.booking = booking;
    }

    @NotNull
    @Size(min = 1, max = 49)
    @Pattern(regexp = "[A-Za-z']+", message = "Please use a name without numbers or specials")
    @Column(name = "destination")
    private String destination;

    @Size(min = 1, max = 49)
    @Pattern(regexp = "[A-Za-z']+", message = "Please use a name without numbers or specials")
    @Column(name = "source")
    private String source;

    @NotNull
   // @Pattern(regexp ="[0-9]\\d{10}")
    @Column(name = "flight_number")
    private String flight_number;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFlight_number() {
        return flight_number;
    }

    public void setFlight_number(String flight_number) {
        this.flight_number = flight_number;
    }

}
