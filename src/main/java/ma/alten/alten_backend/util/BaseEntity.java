package ma.alten.alten_backend.util;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    protected Instant createdAt;

    @UpdateTimestamp
    protected Instant updatedAt;

    /**
     * Indicates whether the object has already been persisted or not
     *
     * @return true if the object has not yet been persisted
     */

    public boolean isNew() {
        return getId() == null;
    }

}
