package org.pikIt.studentInit.services;

import org.pikIt.studentInit.model.*;
import org.pikIt.studentInit.repositorys.BidRepository;
import org.pikIt.studentInit.repositorys.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class VotingService {
    private final VotingRepository votingRepository;
    private final BidRepository bidRepository;
    private final Integer VOTES_FOR_STUDENT = 2;
    private final Integer VOTES_AGAINST_STUDENT = 200;
    private final Integer VOTES_FOR_EXPERT = VOTES_FOR_STUDENT + 2;
    private final Integer VOTES_AGAINST_EXPERT = VOTES_AGAINST_STUDENT + 200;

    @Autowired
    public VotingService(VotingRepository votingRepository, BidRepository bidRepository) {
        this.votingRepository = votingRepository;
        this.bidRepository = bidRepository;
    }

    public void voting(User user, Bid bid, BidStatus status, boolean yes) {
        Vote vote;

        if (yes) {

            if (votingRepository.findVoteByUserAndBid(user, bid) == null) {
                vote = new Vote(bid, user);

            } else {
                vote = votingRepository.findVoteByUserAndBid(user, bid);
                vote.setVotesAgainst(null);
            }
            vote.setVotesFor(1);
            votingRepository.save(vote);
            if (bid.getStatus() == BidStatus.Voting_stud) {
                if (votingRepository.sumVotesFor(bid) != null && (votingRepository.sumVotesFor(bid).equals(VOTES_FOR_STUDENT))) {
                    bid.setStatus(status);
                    bidRepository.save(bid);
                }
            } else {
                if (votingRepository.sumVotesFor(bid) != null && votingRepository.sumVotesFor(bid).equals(VOTES_FOR_EXPERT)) {
                    bid.setStatus(status);
                    bidRepository.save(bid);
                }
            }
        } else {
            if (votingRepository.findVoteByUserAndBid(user, bid) == null) {
                vote = new Vote(bid, user);

            } else {
                vote = votingRepository.findVoteByUserAndBid(user, bid);
                vote.setVotesFor(null);
            }
            vote.setVotesAgainst(1);
            votingRepository.save(vote);
            if (bid.getStatus() == BidStatus.Voting_stud) {
                if (votingRepository.sumVotesAgainst(bid) != null && votingRepository.sumVotesAgainst(bid) >= VOTES_AGAINST_STUDENT) {
                    for (Vote v : votingRepository.findVoteByBid(bid)) {
                        votingRepository.delete(v);
                    }
                    bidRepository.delete(bid);
                }
            } else {
                if (votingRepository.sumVotesAgainst(bid) != null && votingRepository.sumVotesAgainst(bid) >= VOTES_AGAINST_EXPERT) {
                    for (Vote v : votingRepository.findVoteByBid(bid)) {
                        votingRepository.delete(v);
                    }
                    bidRepository.delete(bid);
                }
            }
        }
    }

    public String checkRoleInVoting(Bid bid, User user, Model model) {
        if ((user.getRoles().contains(Role.EXPERT) || user.getRoles().contains(Role.SUPER_USER)) && bid.getStatus() == BidStatus.Voting_expert) {
            model.addAttribute("bidExpertVote", bid);
            model.addAttribute("student", false);
        } else {
            model.addAttribute("bidStudVote", bid);
            model.addAttribute("student", true);
        }
        return "votes/voting";
    }
}
