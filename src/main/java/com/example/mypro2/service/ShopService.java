package com.example.mypro2.service;

import com.example.mypro2.domain.*;
import com.example.mypro2.repository.CommentRepository;
import com.example.mypro2.repository.SellBoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ShopService {
    private final SellBoardRepository sellBoardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void addSell(String url, String title, String content, Member member, String price) {
        int p = Integer.parseInt(price);

        SellBoard sellBoard = SellBoard.builder()
                .url(url)
                .title(title)
                .uptTime(LocalDateTime.now())
                .content(content)
                .author(member.getEmail())
                .price(p)
                .build();

        sellBoardRepository.save(sellBoard);
    }

    public SellBoard sellFindById(Long id){
        SellBoard sellBoard = sellBoardRepository.findById(id).get();

        return sellBoard;
    }

    public List<SellBoard> sellBoardFindAll(){
        List<SellBoard> sellBoards = sellBoardRepository.findAll();

        return sellBoards;
    }

    public String deleteBoard(Long id) {
        SellBoard sellBoard = sellBoardRepository.findById(id).get();
        List<Comment> commentList = commentRepository.findAllBySellBoard(sellBoard);

        if(commentList != null || !commentList.isEmpty()){
            for(Comment c: commentList){
                c.setSellBoard(null);
                commentRepository.delete(c);
            }
        }

        sellBoardRepository.delete(sellBoard);
        return "게시물이 삭제되었습니다.";
    }
}
