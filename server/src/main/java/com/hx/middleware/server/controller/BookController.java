package com.hx.middleware.server.controller;

import com.hx.middleware.server.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jxlgcmh
 * @date 2020-02-06 20:07
 * @description
 */
@RestController
@RequestMapping("book")
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public Book info(Integer bookNo,String bookName) {
        Book book = new Book();
        book.setBookNo(bookNo);
        book.setBookName(bookName);
        log.info("=====测试成功=======");
        return book;
    }

}
