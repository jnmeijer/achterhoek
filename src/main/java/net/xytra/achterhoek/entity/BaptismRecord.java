package net.xytra.achterhoek.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BAPTISM_RECORD")
public class BaptismRecord {
    /*private String fileName;

    @Column
    private int page;

    @Column
    private int entry;*/

    @EmbeddedId
    private RecordId recordId;

    @Column(name = "DOC_PARISH")
    private String docParish;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BIRTH_DATE_ID")
    private EventDate birthDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BAPTISM_DATE_ID")
    private EventDate baptismDate;

    @Column(name = "BAPT_LOCATION")
    private String baptismLocation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CHILD_PERSON_ID")
    private PersonIdentity child;

    @ElementCollection
    private String[] childQualifiers;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FATHER_PERSON_ID")
    private PersonIdentity parent1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MOTHER_PERSON_ID")
    private PersonIdentity parent2;

    @Column(name = "EVENT_LOCATION")
    private String location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ATTESTOR_PERSON_ID")
    private PersonIdentity attestor;

    private String getSerializedChildQualifiers() {
        if (childQualifiers == null || childQualifiers.length == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(childQualifiers[0]);

            for (int i=1; i<childQualifiers.length; i++) {
                sb.append('~').append(childQualifiers[i]);
            }

            return sb.toString();
        }
    }

    public String toString() {
        return recordId.toString() + ','
                + docParish + ','
                + birthDate + ','
                + baptismDate + ','
                + (baptismLocation == null ? "" : baptismLocation) + ','
                + child + ','
                + getSerializedChildQualifiers() + ','
                + (parent1 == null ? "" : parent1) + ','
                + (parent2 == null ? "" : parent2) + ','
                + (location == null ? "" : "\"" + location + '"') + ','
                + (attestor == null ? "" : attestor) + '\n';
    }
}
