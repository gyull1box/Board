package com.example.mypro2.controller;

import com.example.mypro2.domain.Comment;
import com.example.mypro2.domain.Member;
import com.example.mypro2.domain.MemberType;
import com.example.mypro2.domain.SellBoard;
import com.example.mypro2.service.CommentService;
import com.example.mypro2.service.CurrentUser;
import com.example.mypro2.service.S3Service;
import com.example.mypro2.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ShopController {

    private final S3Service s3Service;
    private final ShopService shopService;
    private final CommentService commentService;

    @GetMapping("/shop/main")
    public String Shop(@CurrentUser Member member, Model model){

        List<SellBoard> sellBoard = shopService.sellBoardFindAll();

        if(member != null){
            model.addAttribute("member", member);
        }

        model.addAttribute("sellBoard", sellBoard);

        return "view/shop/main";
    }

    @GetMapping("/shop/upload")
    public String bike(){
        return "view/shop/upload";
    }

    @PostMapping(value = "/shop/upload", produces = "application/text; charset=utf8")
    public String upload(@RequestParam(value = "file" , required = false) MultipartFile multipartFile,
                         @RequestParam(value = "title" , required = false) String title,
                         @RequestParam(value = "content" , required = false) String content,
                         @RequestParam(value = "price", required = false) String price,
                         @CurrentUser Member member,
                         Model model) throws IOException {

        if(multipartFile.isEmpty() || title.trim().length() == 0 || content.trim().length() == 0 || price.trim().length() == 0){
            model.addAttribute("result_code", "notting.all.content");

            return "view/notify";
        }

        if(!multipartFile.getOriginalFilename().endsWith(".jpg") && !multipartFile.getOriginalFilename().endsWith(".png")){
            model.addAttribute("result_code", "not.type.img");

            return "view/notify";
        }

        String url = s3Service.upload(multipartFile);
        shopService.addSell(url, title, content, member, price);

        return "redirect:/shop/main";
    }

    @GetMapping("/shop/detail")
    public String shopDetail(@CurrentUser Member member, Long id, Model model){

        SellBoard sellBoard = shopService.sellFindById(id);
        List<Comment> comments = commentService.getShopCommentById(id);

        if(member != null){
            model.addAttribute("member", member);

            if(member.getType() == MemberType.ADMIN){
                model.addAttribute("admin", true);
            }
        }

        if(sellBoard != null){
            model.addAttribute("sellBoard", sellBoard);
            model.addAttribute("comments", comments);
        }

        if(member.getType() == MemberType.ADMIN){
            model.addAttribute("admin", true);
        }

        return "view/shop/detail";
    }

}
