package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.service.BoardService;
import com.example.demo.vo.BoardVO;

@Controller
public class BoardController {
	
	@Autowired
	BoardService boardService;
	
	//글 목록 가져오기
	@RequestMapping("boardList")
	public ModelAndView boardList() {
		//System.out.println("boardList 호출");
		List<BoardVO> list=boardService.boardList();
		ModelAndView mav=new ModelAndView("boardList");//jsp의 이름
		mav.addObject("boardList", list);//list의 이름
		return mav;
	}
	
	//글쓰기 화면 제공
	@RequestMapping("boardWriteForm")	
	public String boardWriteForm() {			
		return "boardWriteForm";		
	}
	
	//글 등록
	@RequestMapping("boardWrite")
	public RedirectView boardWrite(BoardVO boardVO, MultipartFile file) {
		
		String fileName=file.getOriginalFilename();
		if(!fileName.equals("")) {
			boardVO.setFileName(fileName);
			try {
				file.transferTo(new File("C:\\0AI\\5_backend\\springBoot_workspace\\Day1103_01\\src\\main\\webapp\\uploading\\"+fileName));
			} catch (IllegalStateException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println(boardVO);
		boardService.boardWrite(boardVO);
		
		return new RedirectView("boardList");
	}
	
	//글 상세보기
	@RequestMapping("viewArticle")
	public String viewArticle(@RequestParam int no, HttpSession session) {//글 번호 받아오기
		System.out.println(no+"번 글 보기");
		BoardVO boardVO=boardService.viewArticle(no);
		System.out.println(boardVO);
		session.setAttribute("article", boardVO);
		return "viewArticle";
	}
	
	//답글쓰기 화면 제공
	@RequestMapping("replyWriteForm")	
	public String replyWriteForm() {			
		return "replyWriteForm";		
	}
	
	//답글 등록
	@RequestMapping("replyWrite")
	public RedirectView replyWrite(@RequestParam int parentNo, BoardVO replyVO, HttpSession session) {
		BoardVO parentVO = (BoardVO)session.getAttribute("article");
		System.out.println(parentVO);
		System.out.println(replyVO);
		if(parentVO.getNo()==parentNo) {
			replyVO.setNo(parentNo);
			replyVO.setGrp(parentVO.getGrp());
			replyVO.setSeq(parentVO.getSeq()+1);
			replyVO.setLvl(parentVO.getLvl()+1);
			boardService.replyWrite(replyVO);
			return new RedirectView("boardList");
		}else {
			return new RedirectView("error");
		}
	}
}
