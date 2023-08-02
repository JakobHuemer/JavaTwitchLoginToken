//package at.jakobhuemer.sociallogintestspring.services;
//
//import at.jakobhuemer.sociallogintestspring.models.post.Post;
//import at.jakobhuemer.sociallogintestspring.models.user.User;
//import at.jakobhuemer.sociallogintestspring.repository.PostRepository;
//import at.jakobhuemer.sociallogintestspring.repository.UserRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//@Service
//public class PostService {
//    PostRepository postRepository;
//    UserRepository userRepository;
//
//    public PostService( PostRepository postRepository, UserRepository userRepository ) {
//        this.postRepository = postRepository;
//        this.userRepository = userRepository;
//    }
//
//    public Post createPost( String authToken, String content ) {
//
//        User postUser = userRepository.findDistinctFirstByToken( authToken ).orElseThrow(() -> {
//            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Invalid Token" );
//        });
//
//        Post postToCreate = Post.builder()
//                .author( postUser )
//                .content( content )
//                .build();
//
//        Post post = postRepository.save( postToCreate );
//        return post;
//    }
//
//    public Post updatePost( Post post, String content ) {
//        post.setContent( content );
//        return postRepository.save( post );
//    }
//}
