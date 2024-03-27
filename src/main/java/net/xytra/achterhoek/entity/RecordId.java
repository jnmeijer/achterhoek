package net.xytra.achterhoek.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NonNull;

@Embeddable
@Data
public class RecordId {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DOC_FILE_ID")
    @NonNull
    private DocumentFile file;

    @NonNull private int page;
    @NonNull private int entry;
}
