package fpt.aptech.projectbe.dto;

public class SizeDTO {
    private Integer id;
    private String name;
    private String catesize;
    public SizeDTO() {}

    public SizeDTO(Integer id, String name,String catesize) {
        this.id = id;
        this.name = name;
        this.catesize = catesize;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatesize() {
        return catesize;
    }

    public void setCatesize(String catesize) {
        this.catesize = catesize;
    }
}
