package com.singlesoft.web.model;

//import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User{

	//@Id 
	//@GeneratedValue(generator = "UUID")
	//@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	//@Column(name = "id", updatable = false, nullable = false)
	//private UUID id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="Type", nullable = false)
    private String userType;

    @Column(name="Name", nullable = false, unique = true)
    private String name;

    @Column(name="password", nullable = false)
    private String password;

	@Transient
    private int numServices;
    /*
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
	    if (userType.equals("ADMIN") || userType.equals("USER")) {
	        this.userType = userType;
	    } else {
	        throw new IllegalArgumentException("Invalid user type");
	    }
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public int getNumServices() {
		return numServices;
	}

	public void setNumServices(int numServices) {
		this.numServices = numServices;
	}
    
    
}
