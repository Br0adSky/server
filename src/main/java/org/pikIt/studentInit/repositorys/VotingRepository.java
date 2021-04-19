package org.pikIt.studentInit.repositorys;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingRepository extends JpaRepository<Vote, Integer> {

    @Query("select sum(votesFor) from Vote where Vote.bid = :bid")
    Integer sumVotesFor(Bid bid);

    @Query("select sum(votesFor) from Vote where Vote.bid = :bid")
    Integer sumVotesAgainst(Bid bid);

    //    @Query("select v from Vote v where v.bid = :bid and v.user = :user")
    Vote findVoteByUserAndBid(User user, Bid bid);

    List<Vote> findVoteByBid(Bid bid);
}