package com.example.mypro2.controller;

import com.example.mypro2.domain.Board;
import com.example.mypro2.domain.Comment;
import com.example.mypro2.domain.Member;
import com.example.mypro2.domain.MemberType;
import com.example.mypro2.service.BoardService;
import com.example.mypro2.service.CommentService;
import com.example.mypro2.service.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/board")
    public String board(@CurrentUser Member member, Model model,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNum){

        List<Board> boards = boardService.getBoardList(pageNum);
        Integer [] pageList = boardService.getPageList(pageNum);

        if(member != null){
            model.addAttribute(member);
        }

        model.addAttribute("boards", boards);
        model.addAttribute("pageList", pageList);

        return "view/board/main";
    }

    @GetMapping("/board/detail")
    public String boardDetail(@CurrentUser Member member, Long id, Model model){

        Board board = boardService.getBoardById(id).get();
        List<Comment> comments = commentService.getBoardCommentById(id);

        if(board != null){
            model.addAttribute("board", board);
            model.addAttribute("comments", comments);
        }

        if(member != null){
            Boolean joa = boardService.checkJoayo(member, board);
            model.addAttribute(member);
            model.addAttribute("joa", joa);

            if(member.getType() == MemberType.ADMIN){
                model.addAttribute("admin", true);
            }
        }

        return "view/board/detail";
    }

    @GetMapping("/board/write")
    public String write(@CurrentUser Member member, Model model){

        model.addAttribute("member", member);

        return "view/board/write";
    }

    @GetMapping("/board/modify")
    public String modify(@CurrentUser Member member, Long id, Model model){
        Board board = boardService.findById(id);

        model.addAttribute("board", board);
        return "view/board/modify";
    }

}
