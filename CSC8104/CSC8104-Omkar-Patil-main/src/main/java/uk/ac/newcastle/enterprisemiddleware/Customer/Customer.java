package uk.ac.newcastle.enterprisemiddleware.Customer;


import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.Booking.Booking;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;


/**
     * <p>This is a the Domain object. The Contact class represents how contact resources are represented in the application
     * database.</p>
     *
     * <p>The class also specifies how a contacts are retrieved from the database (with @NamedQueries), and acceptable values
     * for Contact fields (with @NotNull, @Pattern etc...)<p/>
     *
     * @author Joshua Wilson
     */
    /*
     * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
     * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
     */
    @Entity
    @NamedQueries({
            @NamedQuery(name = Customer.FIND_ALL, query = "SELECT c FROM Customer c ORDER BY c.name ASC")

    })
    @XmlRootElement
    @Table(name = "Customer", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
    public class Customer implements Serializable {

        private static final long serialVersionUID = 1L;

        public static final String FIND_ALL = "Customer.findAll";
        public static final String FIND_BY_EMAIL = "Customer.findByEmail";

        @JsonIgnore
        @OneToMany(fetch = FetchType.LAZY,mappedBy ="customer",cascade = CascadeType.REMOVE)
        private List<Booking> booking;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull
        @Size(min = 1, max = 49)
        @Pattern(regexp = "[A-Za-z']+", message = "Please use a name without numbers or specials")
        @Column(name = "name")
        private String name;


        @NotNull
        @NotEmpty
        @Email(message = "The email address must be in the format of name@domain.com")
        private String email;

        @NotNull
        @Pattern(regexp ="^0[0-9]\\d{9}")
        @Column(name = "phone_number")
        private String phoneNumber;



    public List<Booking> getBooking() {
        return booking;
    }

    public void setBooking(List<Booking> booking) {
        this.booking = booking;
    }

    public String getEmail() {
            return email;
        }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
            this.email = email;


        }
    }


