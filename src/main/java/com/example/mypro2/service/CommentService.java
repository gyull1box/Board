package com.example.mypro2.service;

import com.example.mypro2.domain.Board;
import com.example.mypro2.domain.Comment;
import com.example.mypro2.domain.Member;
import com.example.mypro2.domain.SellBoard;
import com.example.mypro2.repository.BoardRepository;
import com.example.mypro2.repository.CommentRepository;
import com.example.mypro2.repository.SellBoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final SellBoardRepository sellBoardRepository;

    @Transactional
    public String addBoardComment(Member member, Long id, String content) {
        Board board = boardRepository.findById(id).get();
        Comment comment =  Comment.builder()
                .board(board)
                .content(content)
                .member(member)
                .uptTime(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return "댓글이 입력되었습니다.";
    }

    public List<Comment> getBoardCommentById(Long id) {
        List<Comment> comments = commentRepository.findAll();
        List<Comment> co = new ArrayList<>();

        Board board = boardRepository.findById(id).get();

        if(!commentRepository.existsByBoard(board)){
            return null;
        }

        for(Comment c: comments){
            if(c.getBoard() != null && c.getBoard().getId() == id){
                co.add(c);
            }
        }
        return co;
    }

    public List<Comment> getShopCommentById(Long id) {
        List<Comment> comments = commentRepository.findAll();
        List<Comment> co = new ArrayList<>();

        SellBoard sellBoard = sellBoardRepository.findById(id).get();

        if(!commentRepository.existsBySellBoard(sellBoard)){
            return null;
        }

        for(Comment c: comments){
            if(c.getSellBoard() != null && c.getSellBoard().getId() == id){
                co.add(c);
            }
        }

        return co;
    }



    public String deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).get();
        commentRepository.delete(comment);
        return "댓글이 삭제되었습니다.";
    }

    public String addShopComment(Member member, Long id, String content) {
        SellBoard sellBoard = sellBoardRepository.findById(id).get();

        Comment comment =  Comment.builder()
                .sellBoard(sellBoard)
                .content(content)
                .member(member)
                .uptTime(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return "댓글이 입력되었습니다.";
    }
}
