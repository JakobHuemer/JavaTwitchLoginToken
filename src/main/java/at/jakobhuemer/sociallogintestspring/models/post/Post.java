//package at.jakobhuemer.sociallogintestspring.models.post;
//
//
//import at.jakobhuemer.sociallogintestspring.models.user.User;
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//
//@Entity( name = "Post" )
//@Table(
//        name = "\"posts\"",
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "author_id_unique",
//                        columnNames = "author_id"
//                )
//        }
//)
//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
//@Builder(access = AccessLevel.PUBLIC)
//public class Post {
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
//    @ManyToOne
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
//    @Column(
//            name = "content",
//            columnDefinition = "TEXT"
//    )
//    private String content;
//
//    public Post( PostBuilder builder ) {
//        this.id = builder.id;
//        this.author = builder.author;
//        this.content = builder.content;
//    }
//}
