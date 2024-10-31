package ma.alten.alten_backend.exceptions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GlobalException  extends Exception {
    public GlobalException(String message) {
        super(message);
    }

}
