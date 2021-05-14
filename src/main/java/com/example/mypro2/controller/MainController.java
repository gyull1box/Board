package com.example.mypro2.controller;

import com.example.mypro2.domain.Board;
import com.example.mypro2.domain.Member;
import com.example.mypro2.domain.SellBoard;
import com.example.mypro2.service.BoardService;
import com.example.mypro2.service.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final BoardService boardService;

    @GetMapping("/")
    public String index(@CurrentUser Member member, Model model){

        List<Board> board = boardService.getPopularBoard();

        if(member != null){
            model.addAttribute(member);
        }

        if(board != null){
            model.addAttribute("board", board);
        }
        return "view/index";
    }

    @GetMapping("/login")
    public String login(){
        return "view/user/login";
    }

    @PostMapping("/result")
    public String result(String content, Model model){

        if(content.trim().length() == 0){
            model.addAttribute("result_code", "notting.result");
            return "view/search";
        }

        List<Board> boardList = boardService.searchByContent(content);

        if(boardList.isEmpty()){
            model.addAttribute("result_code", "notting.result");
            return "view/search";
        }

        model.addAttribute("boardList", boardList);

        return "view/search";
    }

}
