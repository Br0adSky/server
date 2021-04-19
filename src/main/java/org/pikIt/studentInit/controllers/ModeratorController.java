package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.BidStatus;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;

@Controller
@RequestMapping("/bids/bidList")
@PreAuthorize("hasAnyAuthority('MODERATOR', 'SUPER_USER')")
public class ModeratorController {
    private final BidService bidService;

    @Autowired
    public ModeratorController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping
    public String bidList(Model model) {
        bidService.replaceBidsByStatus(model, BidStatus.New);
        return "bids/bidList";
    }

    @PostMapping("/toUserPage")
    public String toUserPage() {
        return "redirect:/users/userPage";
    }

    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','MODERATOR', 'SUPER_USER')")
    @PostMapping("/delete")
    public String deleteBid(@RequestParam Bid bid,
                            Model model, @AuthenticationPrincipal User user) {
        return bidService.delete(bid, model, user);
    }
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','MODERATOR', 'SUPER_USER')")
    @GetMapping("{bid}")
    public String bidEditForm(@PathVariable Bid bid, Model model,
                              @AuthenticationPrincipal User user) {
        return bidService.editForm(model, bid, user);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','MODERATOR', 'SUPER_USER')")
    @PostMapping()
    public String bidSave(@Valid Bid bid, BindingResult bindingResult, Model model,
                          @RequestParam(required = false, value = "file") MultipartFile file,
                          @AuthenticationPrincipal User user) throws IOException {
        return bidService.saveBid(bid, bindingResult, model, file, user);
    }

    @PostMapping("/searchBidByAuthor")
    public String searchBid(@RequestParam String filterName,
                            @RequestParam String filterSurname,
            Model model) {
        bidService.searchByName(filterName, filterSurname, model, Collections.singletonList(BidStatus.New));
        return "bids/bidList";
    }

    @PostMapping("/searchBidByText")
    public String searchBidByText(@RequestParam String filterText,
                                  Model model) {
        bidService.searchByText(filterText, model, Collections.singletonList(BidStatus.New));
        return "bids/bidList";
    }
    @PostMapping("/bidDone")
    public String bidDone(Model model){
        bidService.bidDone(model);
        return "bids/bidList";
    }

    @GetMapping("/bidDone")
    public String confirm(@RequestParam Bid bid, Model model){
        bidService.confirmBid(bid);
        bidService.bidDone(model);
        return "bids/bidList";
    }



}
