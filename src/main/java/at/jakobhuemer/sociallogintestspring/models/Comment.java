//package at.jakobhuemer.sociallogintestspring.models;
//
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//
//@Entity( name = "Comment" )
//@Table(
//        name = "\"comments\"",
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "author_id_unique",
//                        columnNames = "author_id"
//                ),
//                @UniqueConstraint(
//                        name = "post_id_unique",
//                        columnNames = "post_id"
//                )
//        }
//)
//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
//public class Comment {
//    @Id
//    @SequenceGenerator(
//            name = "id_sequence",
//            sequenceName = "id_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "id_sequence"
//    )
//    private Long id;
//
//    @Column(
//            name = "content",
//            columnDefinition = "TEXT"
//    )
//    private String content;
//
//    @OneToOne
//    @JoinColumn(
//            name = "author_id",
//            nullable = false,
//            referencedColumnName = "id",
//            foreignKey = @ForeignKey(
//                    name = "author_id_fk"
//            )
//    )
//    @OnDelete( action = OnDeleteAction.CASCADE )
//    private User author;
//
//    @ManyToOne
//    @JoinColumn(
//            name = "post_id",
//            nullable = false,
//            referencedColumnName = "id",
//            foreignKey = @ForeignKey(
//                    name = "post_id_fk"
//            )
//    )
//    @OnDelete( action = OnDeleteAction.CASCADE )
//    private Post post;
//}
