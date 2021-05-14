package com.example.mypro2.service;

import com.example.mypro2.domain.*;
import com.example.mypro2.form.SignupForm;
import com.example.mypro2.form.SignupFormValidator;
import com.example.mypro2.repository.BoardRepository;
import com.example.mypro2.repository.JoayoRepository;
import com.example.mypro2.repository.MemberRepository;
import com.example.mypro2.repository.SellBoardRepository;
import com.example.mypro2.util.EmailMessage;
import com.example.mypro2.util.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService{

    private final MemberRepository memberRepository;

    private final SignupFormValidator signupFormValidator;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    
    private final BoardRepository boardRepository;

    private final SellBoardRepository sellBoardRepository;

    private final JoayoRepository joayoRepository;

    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signupFormValidator);

    }

    @Transactional
    public Member processNewMember(SignupForm signupForm){
        Member newMember = saveNewMember(signupForm);
        newMember.generateEmailCheckToken();
        newMember.setType(MemberType.USER);
        sendSignupConfirmEmail(newMember);
        return newMember;
    }

    public void sendSignupConfirmEmail(Member newMember) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(newMember.getEmail())
                .subject("My_pro : 이메일 인증을 위한 과정입니다. 본문의 링크를 복사해 붙여주세요.")
                .message("/check-email-token?token="+ newMember.getEmailCheckToken() + "&email=" + newMember.getEmail())
                .build();
        emailService.sendEmail(emailMessage);
    }

    public Member saveNewMember(SignupForm signupForm) {
        Member member = Member.builder()
                .email(signupForm.getEmail())
                .password(passwordEncoder.encode(signupForm.getPassword()))
                .address(Address.builder()
                        .zip(signupForm.getZipcode())
                        .city(signupForm.getCity())
                        .street(signupForm.getStreet())
                        .build())
                .build();
        Member newMember = memberRepository.save(member);
        return newMember;
    }


    public void login(Member member){
        MemberUser memberUser = new MemberUser(member);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                memberUser,
                memberUser.getMember().getPassword(),
                memberUser.getAuthorities()
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);
        if(member == null){
            throw new UsernameNotFoundException(username);
        }

        return new MemberUser(member);
    }

    public Member findByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        return member;
    }

    public void processResetPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        member.setPassword(passwordEncoder.encode(password));
    }


    public void sendMailResetPassword(String email) {
        Member member = memberRepository.findByEmail(email);

        if(member == null) {
            return;
        }

        String token = member.getEmailCheckToken();

        String url = "/reset-password?token=" + token + "&email=" + email;

        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject("My_pro : 비밀번호 재설정 링크입니다. 본문의 링크를 복사해 붙여주세요.")
                .message("링크 : " + url)
                .build();
        emailService.sendEmail(emailMessage);
    }

    public List<Board> findUserBoard(Member member) {
        List<Board> boards = boardRepository.findAll();
        List<Board> boardList = new ArrayList<>();

        for(Board b: boards){

            if(b.getAuthor().equals(member.getEmail())){
                boardList.add(b);
            }

        }
        return boardList;
    }

    public List<SellBoard> findUserSellBoard(Member member) {
        List<SellBoard> sellBoards = sellBoardRepository.findAll();
        List<SellBoard> sellBoardList = new ArrayList<>();
        
        for(SellBoard s: sellBoards){
            if(s.getAuthor().equals(member.getEmail())){
                sellBoardList.add(s);
            }
        }
        
        return sellBoardList;
    }

    public List<Board> findUserJoa(Member member) {
        List<Joayo> joayo = joayoRepository.findAll();
        List<Joayo> joayoList = new ArrayList<>();

        List<Board> boardList = new ArrayList<>();

        for(Joayo j: joayo){
            if(j.getMember().getId() == member.getId()){
                joayoList.add(j);
            }
        }

        for(Joayo j : joayoList){
            Board board = boardRepository.findById(j.getBoard().getId()).get();
            boardList.add(board);
        }

        return boardList;
    }
}
