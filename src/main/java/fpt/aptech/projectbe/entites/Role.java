package fpt.aptech.projectbe.entites;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // Nếu muốn thấy danh sách user trong 1 role:
    @OneToMany(mappedBy = "role")
    private List<User> users;

    // getter/setter...
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

