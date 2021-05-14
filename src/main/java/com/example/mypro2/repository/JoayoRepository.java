package com.example.mypro2.repository;

import com.example.mypro2.domain.Board;
import com.example.mypro2.domain.Comment;
import com.example.mypro2.domain.Joayo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface JoayoRepository extends JpaRepository<Joayo,Long> {
    List<Joayo> findAllByBoard(Board board);
}
