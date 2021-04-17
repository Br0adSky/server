package org.pikIt.studentInit.repositorys;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.bid = :bid order by  c.messageDate desc ")
    List<Comment> getCommentSortedByMessageDate(Bid bid);
}
