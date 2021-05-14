package com.example.mypro2.repository;

import com.example.mypro2.domain.Board;
import com.example.mypro2.domain.Comment;
import com.example.mypro2.domain.SellBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByBoard(Board board);
    boolean existsBySellBoard(SellBoard sellBoard);
    List<Comment> findAllBySellBoard(SellBoard sellBoard);
    List<Comment> findAllByBoard(Board board);
}
