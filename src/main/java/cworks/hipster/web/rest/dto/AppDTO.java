package cworks.hipster.web.rest.dto;

import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the App entity.
 */
public class AppDTO implements Serializable {

    private String id;

    @NotNull
    private String name;

    @NotNull
    private LocalDate createDate;

    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AppDTO appDTO = (AppDTO) o;

        if ( ! Objects.equals(id, appDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AppDTO{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", createDate='" + createDate + "'" +
            ", url='" + url + "'" +
            '}';
    }
}
