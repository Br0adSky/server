package org.pikIt.studentInit.services;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.Comment;
import org.pikIt.studentInit.model.Role;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.repositorys.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Date;

@Service
public class CommentsService {
    private final CommentsRepository commentsRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    public String createNewComment(Model model, Bid bid, User user){
        Comment comment = new Comment();
        comment.setBid(bid);
        model.addAttribute("bid",bid);
        return getAttributes(model, user, comment, bid);
    }

    private String getAttributes(Model model, User user, Comment comment, Bid bid) {
        model.addAttribute("newComment", comment);
        model.addAttribute("comments", commentsRepository.getCommentSortedByMessageDate(bid));
        model.addAttribute("user", user);
        model.addAttribute("userRoles", user.getRoles());
        model.addAttribute("super", Role.SUPER_USER);
        model.addAttribute("moder", Role.MODERATOR);
        return "comments/commentPage";
    }

    public void delete(Comment comment){
        commentsRepository.delete(comment);
    }

    public String send(Comment newComment, BindingResult bindingResult, Model model, User user, Bid bid){
        if (bindingResult.hasErrors()) {
            return getAttributes(model, user, newComment, bid);
        } else {
            newComment.setBid(bid);
            newComment.setUser(user);
            newComment.setMessageDate(new Date());
            commentsRepository.save(newComment);
            return "redirect:/comments/commentPage/" + bid.getId();
        }
    }
}
