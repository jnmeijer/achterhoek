package net.xytra.achterhoek;

public class PersonIdentity {
    private String[] forenames;
    private String[] surnames;
    private String role;

    private PersonIdentity(String[] forenames, String[] surnames, String role) {
        this.forenames = forenames;
        this.surnames = surnames;
        this.role = role;
    }

    public static PersonIdentity parseFirstNames(String firstNames) {
        return new PersonIdentity(normalizeCapitalization(firstNames.split(" ")), new String[] {}, null);
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
            String endPart = fullName.substring(fullName.indexOf(')')+1, fullName.length()).trim();

            // rebuild fullName
            fullName = startPart + ' ' + endPart;
        }

        String[] parts = fullName.split(" ");
        int surnameIndex;

        if (parts.length < 2) {
            throw new RuntimeException("Too few names in full name:" + parts.length);
        } else if (parts.length == 2) {
            surnameIndex = 1; // 2nd name is always surname
        } else { // over 2
            // special case: van d* X
            if ("van".equals(parts[parts.length-3])) {
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

        return new PersonIdentity(forenames, surnames, role);
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
