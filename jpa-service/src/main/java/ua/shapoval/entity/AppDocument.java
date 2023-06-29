package ua.shapoval.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString(exclude = "id")
@Table(name = "app_document")
public class AppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFieldId;
    private String documentName;

    @OneToOne
    private  BinaryContent binaryContent;
    private String type;
    private Long fileSize;
}
