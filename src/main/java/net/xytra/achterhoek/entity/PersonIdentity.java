package net.xytra.achterhoek.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PERSON")
public class PersonIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ElementCollection
    private String[] forenames;

    @Column
    private String patronym;

    @ElementCollection
    private String[] surnames;

    @Column
    private String role;

    private PersonIdentity(String[] forenames, String patronym, String[] surnames, String role) {
        this.forenames = forenames;
        this.patronym = patronym;
        this.surnames = surnames;
        this.role = role;
    }

    public static PersonIdentity parseFirstNames(String firstNames) {
        return new PersonIdentity(normalizeCapitalization(firstNames.split(" ")), null, new String[] {}, null);
    }

    private static String[] normalizeCapitalization(String[] name) {
        for (int i=0; i<name.length; i++) {
            name[i] = normalizeCapitalization(name[i]);
        }
        return name;
    }

    // ABCdef -> Abcdef
    private static String normalizeCapitalization(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
    }

    public static PersonIdentity parseFullName(String fullName) {
        // Split off the role
        String role = null;
        String[] parts = fullName.split(", ");

        if (parts.length > 1) {
            role = parts[1];
        }

        return parseFullName(parts[0], role);
    }

    public static PersonIdentity parseFullName(String fullName, String role) {
        // First filter out parentheses, if any
        int parenthesisIndex = fullName.indexOf('(');
        if (parenthesisIndex >= 0) {
            String startPart = fullName.substring(0, parenthesisIndex).trim();
            String endPart = fullName.substring(fullName.indexOf(')')+1).trim();

            // rebuild fullName
            fullName = startPart + ' ' + endPart;
        }

        // Filter out a trailing " uit ..."
        int uitIndex = fullName.indexOf(" uit ");
        if (uitIndex > 0) {
            fullName = fullName.substring(0, uitIndex);
        }

        // Filter out trailing " Lz."
        int lzIndex = fullName.indexOf(" Lz.");
        if (lzIndex > 0) {
            fullName = fullName.substring(0, lzIndex);
        }

        // Filter out "wijlen "
        if (fullName.startsWith("wijlen ")) {
            fullName = fullName.substring(7);
        }
        String[] parts = fullName.split(" ");
        int surnameIndex;

        if (parts.length < 2) {
            throw new RuntimeException("Too few names in full name:" + parts.length);
        } else if (parts.length == 2) {
            surnameIndex = 1; // 2nd name is always surname
        } else { // over 2
            // special case: van/op d*/het X
            if ("op".equals(parts[parts.length-3]) || "van".equals(parts[parts.length-3])) {
                surnameIndex = parts.length-3;
            } else {
                switch (parts[parts.length-2]) {
                    case "Groot":
                    case "Klein":
                    case "de":
                    case "te":
                    case "ten":
                    case "ter":
                    case "van":
                        surnameIndex = parts.length-2;
                        break;
                    default:
                        surnameIndex = parts.length-1;
                }
            }
        }

        String[] forenames = new String[surnameIndex];
        String[] surnames = new String[parts.length-surnameIndex];

        for (int i=0; i<surnameIndex; i++) {
            forenames[i] = parts[i];
        }
        for (int i=surnameIndex; i<parts.length; i++) {
            surnames[i-surnameIndex] = parts[i];
        }

        return new PersonIdentity(forenames, null, surnames, role);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        // forenames
        for (int i=0; i<forenames.length; i++) {
            if (i>0) {
                sb.append(' ');
            }
            sb.append(forenames[i]);
        }

        // separator
        sb.append('|');

        if (patronym != null) {
            sb.append(patronym);
        }

        // separator
        sb.append('|');

        // surnames
        for (int i=0; i<surnames.length; i++) {
            if (i>0) {
                sb.append(' ');
            }
            sb.append(surnames[i]);
        }

        // separator and role
        sb.append('|');

        if (role != null) {
            sb.append(role);
        }

        return sb.toString();
    }
}
