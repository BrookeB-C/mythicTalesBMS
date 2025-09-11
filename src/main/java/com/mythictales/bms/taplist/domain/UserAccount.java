package com.mythictales.bms.taplist.domain;
import jakarta.persistence.*; import jakarta.validation.constraints.NotBlank;
@Entity @Table(name="users")
public class UserAccount {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(unique=true) private String username;
    @NotBlank private String password;
    @Enumerated(EnumType.STRING) private Role role;
    @ManyToOne private Brewery brewery; @ManyToOne private Bar bar; @ManyToOne private Taproom taproom;
    public UserAccount(){} public UserAccount(String u,String p,Role r){ username=u; password=p; role=r; }
    public Long getId(){ return id; } public void setId(Long id){ this.id=id; }
    public String getUsername(){ return username; } public void setUsername(String username){ this.username=username; }
    public String getPassword(){ return password; } public void setPassword(String password){ this.password=password; }
    public Role getRole(){ return role; } public void setRole(Role role){ this.role=role; }
    public Brewery getBrewery(){ return brewery; } public void setBrewery(Brewery b){ this.brewery=b; }
    public Bar getBar(){ return bar; } public void setBar(Bar b){ this.bar=b; }
    public Taproom getTaproom(){ return taproom; } public void setTaproom(Taproom t){ this.taproom=t; }
}
