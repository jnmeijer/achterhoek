package net.xytra.achterhoek.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NonNull;

@Embeddable
@Data
public class RecordId {
    @NonNull private String fileName;
    @NonNull private int page;
    @NonNull private int entry;
}
