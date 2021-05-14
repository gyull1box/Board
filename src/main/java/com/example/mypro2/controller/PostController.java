package com.example.mypro2.controller;

import com.example.mypro2.domain.Member;
import com.example.mypro2.service.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@AllArgsConstructor
public class PostController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final ShopService shopService;

    @PostMapping(value = "/board/write", produces = "application/text; charset=utf8")
    public String write(@CurrentUser Member member,
                        @RequestParam String title,
                        @RequestParam String content){

        return boardService.addBoard(member, title, content);
    }

    @PostMapping(value = "/board/modified", produces = "application/text; charset=utf8")
    public String modified(@RequestParam Long id,
                           @RequestParam String title,
                           @RequestParam String content ){

        return boardService.modiBoard(id, title, content);
    }

    @PostMapping(value = "/board/delete", produces = "application/text; charset=utf8")
    public String boardDelete(@RequestParam Long id){
        return boardService.deleteBoard(id);
    }

    @PostMapping(value = "/shop/delete", produces = "application/text; charset=utf8")
    public String shopDelete(@RequestParam Long id){
        return shopService.deleteBoard(id);
    }

    @PostMapping(value = "/board/addComment", produces = "application/text; charset=utf8")
    public String addBoardComment(@CurrentUser Member member,
                             @RequestParam Long id,
                             @RequestParam String content){

        return commentService.addBoardComment(member, id, content);
    }

    @PostMapping(value = "/board/deleteComment", produces = "application/text; charset=utf8")
    public String deleteBoardComment(@RequestParam Long comment_id){
        return commentService.deleteComment(comment_id);
    }

    @PostMapping(value = "/board/likes", produces = "application/text; charset=utf8")
    public String addLikes(@CurrentUser Member member , @RequestParam Long id){
        return boardService.addLikes(member, id);
    }

    @PostMapping(value = "/board/deleteLikes", produces = "application/text; charset=utf8")
    public String deleteLikes(@CurrentUser Member member , @RequestParam Long id){
        return boardService.deleteLikes(member, id);
    }

    @PostMapping(value = "/shop/addComment", produces = "application/text; charset=utf8")
    public String addShopComment(@CurrentUser Member member,
                             @RequestParam Long id,
                             @RequestParam String content){

        return commentService.addShopComment(member, id, content);
    }


    @PostMapping(value = "/shop/deleteComment", produces = "application/text; charset=utf8")
    public String deleteShopComment(@RequestParam Long comment_id){
        return commentService.deleteComment(comment_id);
    }

}
