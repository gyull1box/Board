package com.example.mypro2.controller;

import com.example.mypro2.domain.Board;
import com.example.mypro2.domain.Member;
import com.example.mypro2.domain.SellBoard;
import com.example.mypro2.form.SignupForm;
import com.example.mypro2.repository.MemberRepository;
import com.example.mypro2.service.CurrentUser;
import com.example.mypro2.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/signup")
    public String join(Model model){
        model.addAttribute(new SignupForm());
        return "view/user/signup";
    }

    @PostMapping("/signup")
    @Transactional
    public String signupSubmit(@Valid SignupForm signupForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            return "view/user/signup";
        }

        Member checkMember = memberService.findByEmail(signupForm.getEmail());

        if(checkMember != null){
            model.addAttribute("result_code", "already.joined.member");
            memberService.login(checkMember);
            return "view/notify";
        }

        Member newMember = memberService.processNewMember(signupForm);

        memberService.login(newMember);
        return "redirect:/";
    }


    @GetMapping("check-email-token")
    @Transactional
    public String checkEmailToken(String token, String email, Model model){
        Member member = memberService.findByEmail(email);

        if(member == null){
            model.addAttribute("error", "wrong.email");
            return "view/user/check-email";
        }

        if(!member.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return "view/user/checked-email";
        }

        member.completeSignup();
        memberService.login(member);
        model.addAttribute("email", member.getEmail());

        return "view/user/checked-email";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "view/user/change-password";
    }

    @PostMapping("/change-password")
    public String changePasswordSubmit(String email, Model model) {
        memberService.sendMailResetPassword(email);

        model.addAttribute("email", email);
        model.addAttribute("result_code", "password.reset.send");

        return "view/notify";
    }


    @GetMapping("/reset-password")
    public String resetPasswordForm(String token, String email, Model model){
        Member member = memberService.findByEmail(email);
        if(member == null) {
            model.addAttribute("result", false);
            return "view/user/reset-password";
        }

        String emailCheckToken = member.getEmailCheckToken();

        if(!token.equals(emailCheckToken)){
            model.addAttribute("result", false);
            return "view/notify";
        }

        model.addAttribute("result", true);
        model.addAttribute("email", email);
        return "view/user/reset-password";
    }

    @PostMapping("/reset-password")
    @Transactional
    public String resetPasswordSubmit(String email, String password, Model model){
        memberService.processResetPassword(email, password);

        model.addAttribute("result_code", "password.reset.complete");

        Member member = memberService.findByEmail(email);
        memberService.login(member);

        return "view/notify";
    }

    @GetMapping("/myPage")
    public String myPage(@CurrentUser Member member, Model model){

        List<Board> memBoard =  memberService.findUserBoard(member);
        List<SellBoard> memSellBoard = memberService.findUserSellBoard(member);
        List<Board> memJoaBoard = memberService.findUserJoa(member);

        if(member != null){
            model.addAttribute("member", member);
        }

        if(!memBoard.isEmpty()){
            model.addAttribute("memBoard", memBoard);
        }

        if(!memSellBoard.isEmpty()){
            model.addAttribute("memSellBoard", memSellBoard);
        }

        if(!memJoaBoard.isEmpty()){
            model.addAttribute("memJoaBoard", memJoaBoard);
        }

        return "view/user/my-page";
    }

}
