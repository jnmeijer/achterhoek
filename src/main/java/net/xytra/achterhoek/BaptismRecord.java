package net.xytra.achterhoek;

public class BaptismRecord {
    private String docParish;
    private int page;
    private int entry;
    private EventDate birthDate;
    private EventDate baptismDate;
    private String baptismLocation;
    private PersonIdentity child;
    private String[] childQualifiers;
    private PersonIdentity parent1;
    private PersonIdentity parent2;
    private String location;
    private PersonIdentity attestor;

    public BaptismRecord(String docParish, int page, int entry,
            EventDate birthDate, EventDate baptismDate, String baptismLocation,
            PersonIdentity child, String[] childQualifiers, PersonIdentity parent1,
            PersonIdentity parent2, String location, PersonIdentity attestor) {
        this.docParish = docParish;
        this.page = page;
        this.entry = entry;
        this.birthDate = birthDate;
        this.baptismDate = baptismDate;
        this.baptismLocation = baptismLocation;
        this.child = child;
        this.childQualifiers = childQualifiers;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.location = location;
        this.attestor = attestor;
    }

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
        return "baptism,"
                + docParish + ','
                + page + ','
                + entry + ','
                + birthDate + ','
                + baptismDate + ','
                + (baptismLocation == null ? "" : baptismLocation) + ','
                + child + ','
                + getSerializedChildQualifiers() + ','
                + (parent1 == null ? "" : parent1) + ','
                + (parent2 == null ? "" : parent2) + ','
                + (location == null ? "" : location) + ','
                + (attestor == null ? "" : attestor) + '\n';
    }
}
