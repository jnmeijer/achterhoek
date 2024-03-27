package net.xytra.achterhoek.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NonNull;

@Entity
@Table(name = "DOC_FILE")
@Data
public class DocumentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "FILE_NAME")
    @NonNull
    private String fileName;

    private static List<DocumentFile> findAllDocumentFile(Session session) {
        return session.createQuery("SELECT a FROM DocumentFile a", DocumentFile.class).getResultList();
    }

    private static Map<String, DocumentFile> fileMap;

    private static Map<String, DocumentFile> getFileMap(Session session) {
        if (fileMap == null) {
            fileMap = new HashMap<String, DocumentFile>();

            for (DocumentFile file: findAllDocumentFile(session)) {
                fileMap.put(file.getFileName(), file);
            }
        }

        return fileMap;
    }

    public static DocumentFile getFileForName(Session session, String fileName) {
        Map<String, DocumentFile> map = getFileMap(session);
        DocumentFile newFile = map.get(fileName);

        if (!map.containsKey(fileName)) {
            newFile = new DocumentFile(fileName);
            session.persist(newFile);

            map.put(fileName, newFile);
        }

        return newFile;
    }

}
