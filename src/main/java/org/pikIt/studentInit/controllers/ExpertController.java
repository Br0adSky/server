package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.BidStatus;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.BidService;
import org.pikIt.studentInit.services.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
@PreAuthorize("hasAnyAuthority('EXPERT', 'SUPER_USER')")
public class ExpertController {
    private final VotingService votingService;
    private final BidService bidService;

    @Autowired
    public ExpertController(VotingService votingService, BidService bidService) {
        this.votingService = votingService;
        this.bidService = bidService;
    }

    @GetMapping("users/expertPage")
    public String main(Model model, @AuthenticationPrincipal User user) {
        bidService.replaceBidsByStatus(model, BidStatus.Voting_expert);
        model.addAttribute("message", "Доступные голосования");
        model.addAttribute("user", user);
        return "users/expertPage";
    }


    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','MODERATOR', 'SUPER_USER')")
    @GetMapping("votes/voting/{bid}")
    public String voting(Model model, @PathVariable Bid bid, @AuthenticationPrincipal User user) {
        return votingService.checkRoleInVoting(bid, user, model);
    }

    @PostMapping("users/expertPage/replaceBids")
    public String replaceBids() {
        return "redirect:/users/expertPage";
    }

    @PostMapping("users/expertPage/filterText")
    public String filterText(@RequestParam String filterText, Model model) {
        bidService.searchByText(filterText, model, Collections.singletonList(BidStatus.Voting_expert));
        return "users/expertPage";
    }

    @PostMapping("users/expertPage/allBidsByName")
    public String allBidsByName(@AuthenticationPrincipal User user, Model model) {
        bidService.allBidsByName(model, user);
        return "users/expertPage";
    }

    @PostMapping("users/expertPage/expertVote")
    public String expertVoting(@AuthenticationPrincipal User user, @RequestParam boolean vote,
                               @RequestParam Bid bid) {
        votingService.voting(user, bid, BidStatus.Working, vote);
        return "redirect:/users/expertPage";
    }

}
