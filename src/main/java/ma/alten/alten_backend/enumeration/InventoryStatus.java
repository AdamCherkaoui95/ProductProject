package ma.alten.alten_backend.enumeration;

import lombok.Getter;

@Getter
public enum InventoryStatus {

    INSTOCK("inStock"),
    LOWSTOCK("lowStock"),
    OUTOFSTOCK("outOfStock");

    private final String value;
    InventoryStatus(String value) {
        this.value = value;
    }

}
