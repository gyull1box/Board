package com.example.mypro2.service;

import com.example.mypro2.domain.*;
import com.example.mypro2.repository.BoardRepository;
import com.example.mypro2.repository.CommentRepository;
import com.example.mypro2.repository.JoayoRepository;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final JoayoRepository joayoRepository;

    private final CommentRepository commentRepository;

    private static final int BLOCK_PAGE_NUM_COUNT  = 100;
    private static final int PAGE_POST_COUNT = 10;

    @PostConstruct
    public void boardList(){
        for(int i = 1; i <= 100; ++i){
            Board board = Board.builder()
                    .author("a@a.a")
                    .writeAt(LocalDateTime.now())
                    .liked(135)
                    .content("qq")
                    .title("오늘의 한 일")
                    .build();

            boardRepository.save(board);
        }
    }

    public List<Board> getBoardList() {
        return boardRepository.findAll();
    }

    public List<Board> getBoardList(Integer page){
        int currentPage = 0;
        if(page == null){
            currentPage = 1;
        } else {
            currentPage = page;
        }

        List<Board> boardList = boardRepository.findAll(Sort.by("id").descending());

        int start = 10 * (currentPage -1);
        int fin = 10 * currentPage;

        List<Board> board = new ArrayList<>();

        for(int i =start; i <fin; i++){
            if(i >= boardList.size()){
                break;
            }
            board.add(boardList.get(i));
        }

        return board;
    }


    public Optional<Board> getBoardById(Long id){
        return boardRepository.findById(id);
    }

    @Transactional
    public String addBoard(Member member, String title, String content) {
        String name = member.getEmail();

        Board board = Board.builder()
                .title(title)
                .content(content)
                .writeAt(LocalDateTime.now())
                .author(name)
                .build();

        boardRepository.save(board);
        return "게시물이 저장되었습니다.";
    }


    public Board findById(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        return board.get();
    }

    public String modiBoard(Long id, String title, String content) {
       Board board = boardRepository.findById(id).get();
        board.setTitle(title);
        board.setContent(content);
        board.setModifiedAt(LocalDateTime.now());

        boardRepository.save(board);

        return "게시물이 수정되었습니다.";
    }

    public String deleteBoard(Long id) {
        Board board = boardRepository.findById(id).get();
        List<Comment> commentList = commentRepository.findAllByBoard(board);
        List<Joayo> joayoList = joayoRepository.findAllByBoard(board);

        if(commentList != null || !commentList.isEmpty()){
            for(Comment c: commentList){
                c.setBoard(null);
                commentRepository.delete(c);
            }
        }

        if(joayoList != null || !joayoList.isEmpty()){
            for(Joayo j: joayoList){
                j.setBoard(null);
                joayoRepository.delete(j);
            }
        }

        boardRepository.delete(board);
        return "게시물이 삭제되었습니다.";
    }

    @Transactional
    public String addLikes(Member member, Long id) {
        Board board = boardRepository.findById(id).get();
        Joayo joayo = Joayo.builder()
                .member(member)
                .board(board)
                .build();

        board.setLiked(board.getLiked() + 1);
        boardRepository.save(board);

        joayoRepository.save(joayo);

        return "해당 게시물에 좋아요를 눌렀습니다.";
    }

    public Boolean checkJoayo(Member member, Board board) {
        Boolean result = false;
        List<Joayo> joayo = joayoFindByMem(member);

        if(joayo.isEmpty()){
            return result = false;
        }

        for(Joayo j: joayo){
            if(j.getBoard() == board){
                 result = true;
            }
        }

        return result;
    }

    public List<Joayo> joayoFindByMem(Member member){
        List<Joayo> joayo = joayoRepository.findAll();
        List<Joayo> joayoMem = new ArrayList<>();

        for(Joayo j: joayo){
            if(j.getMember().getId() == member.getId()){
                joayoMem.add(j);
            }
        }

        return joayoMem;
    }

    public Joayo joayoFindByBoardId(Member member, Long id){
        List<Joayo> joayo =  joayoFindByMem(member);

        for(Joayo j: joayo){
            if(j.getBoard().getId() == id){
                return j;
            }
        }

        return null;
    }

    @Transactional
    public String deleteLikes(Member member, Long id) {
        Board board = boardRepository.findById(id).get();
        Joayo joayo = joayoFindByBoardId(member, id);

            if( joayo != null){
                joayoRepository.delete(joayo);
            }
            board.setLiked(board.getLiked() - 1);
            boardRepository.save(board);

        return "좋아요가 취소되었습니다.";
    }

    public Integer[] getPageList(Integer currentPage) {
        Integer[] pageList = new Integer[BLOCK_PAGE_NUM_COUNT];

        List<Board> count = boardRepository.findAll();
        Double totalList = Double.valueOf(count.size());

        Integer totalLastPageNum = (int)(Math.ceil(totalList/PAGE_POST_COUNT));

        Integer blockLastPageNum = (totalLastPageNum > currentPage + BLOCK_PAGE_NUM_COUNT)?
        currentPage + BLOCK_PAGE_NUM_COUNT : totalLastPageNum;

        currentPage = (currentPage <= 3) ? 1: currentPage -2;


        for(int val=currentPage, i = 0; val <= blockLastPageNum; val++, i++){
            pageList[i] = val;
        }
        return pageList;
    }

    public List<Board> getPopularBoard(){
        List<Board> boardList = boardRepository.findAll(Sort.by("liked").descending());
        List<Board> board = new ArrayList<>();

        for(Board b: boardList){
            if(boardList.isEmpty()){
                return null;
            }

            if(board.size() == 10){
                return board;
            }
            board.add(b);
        }
        return board;
    }

    public List<Board> searchByContent(String content) {
        List<Board> boardList = boardRepository.findAll();
        List<Board> board = new ArrayList<>();

        if(content.length() == 0){
            return null;
        }

        for(Board b: boardList){
            if(b.getContent().contains(content)){
                board.add(b);
            }
        }

        return board;
    }
}
